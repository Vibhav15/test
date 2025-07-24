package com.halodoc.batavia.controller.api.pharmacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halodoc.audit.sdk.Auditor;
import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.PrescriptionUpload;
import com.halodoc.batavia.entity.buru.PharmacyOrder;
import com.halodoc.batavia.entity.cms.Pharmacy;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.oms.*;
import com.halodoc.batavia.entity.oms.order_refund.OrderRefundPayload;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.BuruService;
import com.halodoc.batavia.service.OrderMerchantService;
import com.halodoc.batavia.service.PharmacyService;
import com.halodoc.batavia.service.scrooge.PaymentsService;
import com.halodoc.batavia.service.timor.TimorOmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.halodoc.batavia.entity.gojek.OrderTracking;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("api/v1/orders")
@Slf4j
public class OrderApiController extends HalodocBaseApiController {

    private final PharmacyService pharmacyService;
    private final TimorOmsService orderService;
    private final OrderMerchantService orderGroupService;
    private final BuruService buruService;
    private final PaymentsService paymentsService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private Auditor auditor;

    @Autowired
    public OrderApiController(PharmacyService pharmacyService, TimorOmsService orderService, OrderMerchantService orderGroupService, BuruService buruService, PaymentsService paymentsService) {
        this.pharmacyService = pharmacyService;
        this.orderService = orderService;
        this.orderGroupService = orderGroupService;
        this.buruService = buruService;
        this.paymentsService = paymentsService;
    }

    @PutMapping("/{id}/remove-prescriptions")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void removePrescriptions(@PathVariable String id, @RequestBody List<String> documentId) {
         orderService.removePrescriptions(id, documentId);
    }

    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Order> listOrder(@RequestParam Map<String,String> queryParamsMap) {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return orderService.handleTimorOrderSearch(queryParamsMap);
    }

    private void sortOrderSLA(List<Order> orders, String fromStatus, String toStatus) {
        orders.forEach(order -> {
            //set total SLA
            Long totalSLA = calculateSLA(order, fromStatus, toStatus);
            order.setTotalSLA(totalSLA);

            //set total time
            Long totalTime = calculateTotalTime(order);
            order.setTotalTime(totalTime);
        });

        orders.sort((order2, order1) -> order1.getTotalSLA().compareTo(order2.getTotalSLA()));
    }

    private void sortOrderSLA(List<Order> orders) {
        orders.forEach(order -> {
            //set total SLA
            Long totalSLA = calculateSLA(order, null, null);
            order.setTotalSLA(totalSLA);

            //set total time
            Long totalTime = calculateTotalTime(order);
            order.setTotalTime(totalTime);
        });

        orders.sort((order2, order1) -> order1.getTotalSLA().compareTo(order2.getTotalSLA()));
    }

    private Long calculateSLA(Order order, String fromStatus, String toStatus) {
        Long slaTime;
        LocalDateTime now = LocalDateTime.now();

        if (order.getHistories().isEmpty()) {
            // if history null, calculate => current time - order.created_at
            LocalDateTime createdDate = LocalDateTime.ofInstant(order.getCreatedAt().toInstant(), ZoneId.systemDefault());
            slaTime = ChronoUnit.MINUTES.between(createdDate, now);
        } else {
            // Sort histories, base on id (large top)
            order.getHistories().sort((history2, history1) -> history1.getId().compareTo(history2.getId()));

            // Calculate current time - top history.created_at (make sure the unit is seconds)
            if (fromStatus != null && toStatus != null) {
                OrderHistoryLog fromLog = order.getHistories().stream().filter(history -> history.getNewOrderStatus().equals(fromStatus)).findFirst().orElse(null);
                OrderHistoryLog toLog = order.getHistories().stream().filter(history -> history.getNewOrderStatus().equals(toStatus)).findFirst().orElse(null);

                if (fromLog != null && toLog != null) {
                    LocalDateTime fromDate = LocalDateTime.ofInstant(fromLog.getCreatedAt().toInstant(), ZoneId.systemDefault());
                    LocalDateTime toDate = LocalDateTime.ofInstant(toLog.getCreatedAt().toInstant(), ZoneId.systemDefault());
                    slaTime = ChronoUnit.MINUTES.between(fromDate, toDate) + 1L;
                } else {
                    slaTime = 0L;
                }
            } else {
                LocalDateTime lastDate = LocalDateTime.ofInstant(order.getHistories().get(0).getCreatedAt().toInstant(), ZoneId.systemDefault());
                slaTime = ChronoUnit.MINUTES.between(lastDate, now) + 1L;
            }
        }

        return slaTime;
    }

    private Long calculateTotalTime(Order order) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdDate = LocalDateTime.ofInstant(order.getCreatedAt().toInstant(), ZoneId.systemDefault());
        Long time = ChronoUnit.MINUTES.between(createdDate, now);
        return time;
    }

    @GetMapping("/{id}/substatus")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map ordersBuru(@PathVariable String id) {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_actions", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        PharmacyOrder pharmacyOrder = buruService.get(id);
        Map result = new HashMap();
        result.put("data", pharmacyOrder);

        return result;
    }

    @PutMapping(value = "/{id}/cancel")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void cancelOrder(@PathVariable String id, @RequestBody OrderCancel cancel) {
        orderService.cancelOrder(id, cancel);
    }

    @PutMapping(value = "/{id}/groups/{groupId}/cancel")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void cancelOrderGroup(@PathVariable String id,@PathVariable String groupId, @RequestBody OrderCancel cancel) {
        orderService.cancelShipment(id, groupId, cancel);
    }



    @PutMapping(value = "/{id}/abandon")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void abandonOrder(@PathVariable String id, @RequestBody OrderCancel cancel) {
        orderService.abandon(id, cancel);
    }

    @PostMapping(value = "/{id}/attributes")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addAttributes(@PathVariable String id, @RequestBody Map<String, Object> attributes) {
        orderService.addAttributes(id, attributes);
    }

    @PutMapping(value = "/{id}/attributes")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateAttributes(@PathVariable String id, @RequestBody Map<String, Object> attributes) {
        orderService.updateAttributes(id, attributes);
    }

    @PostMapping(value = "/{id}/groups/{groupId}/attributes")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addGroupAttributes(@PathVariable String id, @PathVariable String groupId, @RequestBody Map<String, Object> attributes) {
        orderService.addGroupAttributes(id, groupId, attributes);
    }

    @PutMapping(value = "/{id}/groups/{groupId}/attributes")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateGroupAttributes(@PathVariable String id, @PathVariable String groupId, @RequestBody Map<String, Object> attributes) {
        orderService.updateGroupAttributes(id, groupId, attributes);
    }

    @PutMapping("/{customerOrderId}/groups/{groupId}/fix-shipment-status")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void forceShipmentStatusChange(@PathVariable String customerOrderId,@PathVariable String groupId, @RequestParam(name="status", required = false) String status) throws URISyntaxException {
        orderService.forceShipmentStatusChange(customerOrderId, groupId, status);
    }


    @PostMapping("/{id}/notes")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map addCsNote(@PathVariable String id, @RequestBody OrderNote note) {
        OrderNote result = orderService.addNote(note, id);

        return response(result);
    }

    @PostMapping("/{id}/promotions")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_create_reorder', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map applyPromo(@PathVariable String id, @RequestBody OrderPromotion promo) {
        Order result = orderService.applyPromo(promo, id);
        return response(result);
    }

    @PutMapping("/{id}/promotions")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_create_reorder', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map removePromo(@PathVariable String id, @RequestBody OrderPromotion promo) {
        Order result = orderService.removePromo(promo, id);
        return response(result);
    }

    @GetMapping("/{orderId}/documents")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    @AuditedAccess (eventType = EventType.pharmacy_order_documents_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.pharmacy_order)
    public List getOrderDocuments(@PathVariable @AuditedEntityId String orderId) {
        return orderService.getOrderDocuments(orderId);
    }


    @GetMapping("/{orderId}/groups/{groupId}/documents")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List getGroupDocuments(@PathVariable String orderId, @PathVariable String groupId) {
        return orderService.getGroupDocuments(orderId, groupId);
    }

    @GetMapping("/{id}")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    public Order detailOrder(@PathVariable String id) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return orderService.getOrderDetailsById(id);
    }

    @GetMapping("/{id}/consolidated-shipment-details")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    public Order allOrderDetails(@PathVariable String id) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.pharmacy_order_accessed, Action.get, ActorType.cc_user, EntityType.pharmacy_order,
                id, ChannelType.http);
        return orderService.getConsolidatedOrderDetailsById(id);
    }

    @GetMapping("/{id}/refund-details")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getOrderRefundDetails(@PathVariable String id) {
        return orderService.getRefundDetails(id);
    }

    @PutMapping("/{id}/calculate/refund-amount")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map calculateRefundAmount(@PathVariable String id, @RequestBody OrderRefundPayload payload) {
        return orderService.calculateRefundAmount(id, payload);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("@authorizationService.isAuthorized('customer','wallet_topup', 'full')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map initiateOrderRefund(@PathVariable String id, @RequestBody Map payload) {
        return orderService.initiateOrderRefund(id, payload);
    }

    @PutMapping("/{customerOrderId}/reorder")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_create_reorder', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Order reOrder(@PathVariable String customerOrderId) {
        Order order = orderService.getOrderById(customerOrderId);
        String parentOrderId = order.getCustomerOrderId();
        order.setCartId(UUID.randomUUID().toString());
        order.setEntityType("user");
        order.getAttributes().put("revived_from", parentOrderId);
        order.getAttributes().put("source", "reorder " + parentOrderId);
        order.getAttributes().remove("DISCOUNT_NOT_HONORED");
        order.getAttributes().remove("INSURANCE_NOT_HONORED");
        order.getAttributes().remove("NOT_PICKED_FROM_APOTEK");
        order.getAttributes().remove("ORDER_RETURNED_TO_APOTEK");
        order.getAttributes().remove("cancellation_reason");
        order.getAttributes().remove("cancellation_type");
        order.getAttributes().remove("group_id");
        order.setHistories(null);
        order.setCustomerOrderId(null);
        order.setStatus("created");

        order.getDocuments().forEach(orderDocument -> {
            orderDocument.setExternalId(null);
        });

        Order newOrder = buruService.createOrder(order);

        if (StringUtils.isNotEmpty(newOrder.getCustomerOrderId())) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("revived_order_id", newOrder.getCustomerOrderId());
            orderService.addAttributes(parentOrderId, attributes);
        }

        return orderService.getOrderById(newOrder.getCustomerOrderId());
    }


    @GetMapping("/{id}/shipment/{groupId}/track")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public OrderTracking trackOrder(@PathVariable String id, @PathVariable String groupId) {
        return orderService.getOrderTracking(id, groupId);
    }

    @PatchMapping("/{customerOrderId}/documents/{documentId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void updateOrderDocument(@PathVariable String customerOrderId, @PathVariable String documentId, @RequestBody OrderDocument document) {
        orderService.updateOrderDocument(customerOrderId, documentId, document);
    }

    @PutMapping("/{customerOrderId}/documents/{documentId}/force-create")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void generateAutoRx(@PathVariable String customerOrderId, @PathVariable String documentId, @RequestBody AutoRxRequest autoRxRequest) {
        orderService.generateAutoRx(customerOrderId, documentId, autoRxRequest);
    }

    @PutMapping("/{customerOrderId}/fix-order-status")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void forceStatusChange(@PathVariable String customerOrderId, @RequestParam(name="status", required = false) String status) throws URISyntaxException {
        orderService.forceStatusChange(customerOrderId, status);
    }

    @PutMapping("/{customerOrderId}/generate-invoice/{documentType}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void regenerateInvoice(@PathVariable String customerOrderId, @PathVariable String documentType)  throws URISyntaxException {
        Map<String, Object> responseObj = new HashMap<>();
        orderService.regeneratePDInvoice(customerOrderId, documentType );
    }

    @PostMapping("/{order_id}/prescriptions")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public PrescriptionUpload uploadPrescriptionImage(@RequestParam("image") MultipartFile image,
                                                      @PathVariable String order_id,
                                                      @RequestParam(required = false, name = "X-File-Type") String xFileType,
                                                      @RequestParam(required = false, name = "X-File-Name") String xFileName) throws IOException {
        //storageService.storeMedia(image);
        return orderService.uploadPrescription(image.getInputStream(), order_id, xFileType, xFileName);

    }

    @PutMapping("/{id}/medical-instructions")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map addMedicalInstructions(@PathVariable String id, @RequestBody Map<String, Object> body) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","order_actions", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        Map result = orderService.addMedicalInstructions(id, body);

        return response(result);
    }

    @GetMapping("/{id}/medical-instructions")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getMedicalInstructions(@PathVariable String id) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","order_actions", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        Map result = orderService.getMedicalInstructions(id);

        return response(result);
    }

    @PostMapping("/{id}/generate-payment")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map generatePayment(@PathVariable String id, @RequestBody Map paymentMethod) {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","order_actions", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        Map result = orderService.generatePayment(id, paymentMethod);

        return response(result);
    }


    @PatchMapping("/{id}/shipping-update")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map shippingUpdate(@PathVariable String id, @RequestBody Map shippingMethod) {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","order_actions", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        Map result = orderService.shippingUpdate(id, shippingMethod);

        return response(result);
    }

    @GetMapping("/{id}/payment-link-status")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map paymentLinkStatus(@PathVariable String id) throws URISyntaxException {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","order_actions", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        Map result = new HashMap();
        try {
            result = paymentsService.paymentLinkStatus(id);
            result.put("payment_link_status", "active");

        } catch (HalodocWebException ex) {
            if (ex.getCode() == "8014") {
                result.put("payment_link_status", "inactive");
            } else {
                throw ex;
            }
        }

            return response(result);
    }


}
