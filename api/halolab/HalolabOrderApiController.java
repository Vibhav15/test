package com.halodoc.batavia.controller.api.halolab;

import com.halodoc.audit.sdk.Auditor;
import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.LabOrderConfig;
import com.halodoc.batavia.entity.halolab.*;
import com.halodoc.batavia.entity.oms.Order;
import com.halodoc.batavia.entity.oms.OrderPromotion;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.exception.custom.BadRequestExpectedException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.ImageUploadService;
import com.halodoc.batavia.service.halolab.HalolabHubService;
import com.halodoc.batavia.service.halolab.HalolabOrderService;
import com.halodoc.batavia.service.halolab.HalolabRmsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import jakarta.validation.constraints.NotBlank;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RestController
@RequestMapping(value = "api/v1/halolabs/orders")
@Tag(name = "labs-order", description = "Operations of order labs")
public class HalolabOrderApiController extends HalodocBaseApiController {

    private HalolabOrderService halolabOrderService;
    private HalolabHubService halolabHubService;
    private HalolabRmsService halolabRmsService;

    private ImageUploadService uploaderService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private Auditor auditor;

    @Autowired
    public HalolabOrderApiController(HalolabOrderService halolabOrderService, HalolabRmsService halolabRmsService,
            ImageUploadService uploaderService) {
        this.halolabOrderService = halolabOrderService;
        this.halolabRmsService = halolabRmsService;
        this.uploaderService = uploaderService;
    }

    @Operation(summary = "Search an order lab with an ID")
    @PreAuthorize("@authorizationService.isAuthorized('lab_service','order_view', 'view_only')")
    @GetMapping("/{customerOrderBookingId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @AuditedAccess(eventType = EventType.halolab_order_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.halolab_order)
    public Map getOrderBookingById(@PathVariable @AuditedEntityId String customerOrderBookingId) throws URISyntaxException {
        return halolabOrderService.getOrderBookingById(customerOrderBookingId);
    }

    @Operation(summary = "Search an order lab with an Order ID")
    @GetMapping("/order-id/{customerOrderId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public Map getOrderById(@PathVariable String customerOrderId) throws URISyntaxException {
        return halolabOrderService.getOrderById(customerOrderId);
    }

    @GetMapping("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<HalolabOrderList> getOrderList(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "customer_order_id") String customerOrderId,
            @RequestParam(required = false, name = "entity_id") String customerId,
            @RequestParam(required = false, name = "booking_statuses", defaultValue = "") String bookingStatuses,
            @RequestParam(required = false, name = "order_statuses", defaultValue = "") String orderStatuses,
            @RequestParam(required = false, name = "start_booking_date") @DateTimeFormat(pattern = "MM-dd-yyyy") Date startBookingDate,
            @RequestParam(required = false, name = "end_booking_date") @DateTimeFormat(pattern = "MM-dd-yyyy") Date endBookingDate,
            @RequestParam(required = false, name = "start_visit_date") @DateTimeFormat(pattern = "MM-dd-yyyy") Date startVisitDate,
            @RequestParam(required = false, name = "end_visit_date") @DateTimeFormat(pattern = "MM-dd-yyyy") Date endVisitDate,
            @RequestParam(required = false, name = "sort_field", defaultValue = "created_at") String sortField,
            @RequestParam(required = false, name = "sort_order") String sortOrder,
            @RequestParam(required = false, name = "patient_id") String patientId,
            @RequestParam(required = false, name = "demand_zone_id") String demandZoneId,
            @RequestParam(required = false, name = "demand_zone_ids") String demandZoneIds,
            @RequestParam(required = false, name = "order_source") String orderSource,
            @RequestParam(required = false, name = "customer_booking_id") String customerBookingId,
            @RequestParam(required = false, name = "inventory_types") String inventoryTypes) throws URISyntaxException {

        Long startBookDate, endBookDate, startVisit, endVisit;
        startBookDate = null;
        endBookDate = null;
        startVisit = null;
        endVisit = null;

        if (startBookingDate != null) {
            startBookDate = new DateTime(startBookingDate).withTimeAtStartOfDay().getMillis();
        }

        if (startVisitDate != null) {
            startVisit = new DateTime(startVisitDate).withTimeAtStartOfDay().getMillis();
        }

        if (endBookingDate != null) {
            endBookDate = new DateTime(endBookingDate)
                    .withHourOfDay(23)
                    .withMinuteOfHour(59)
                    .withSecondOfMinute(59)
                    .getMillis();
        }

        if (endVisitDate != null) {
            endVisit = new DateTime(endVisitDate)
                    .withHourOfDay(23)
                    .withMinuteOfHour(59)
                    .withSecondOfMinute(59)
                    .getMillis();
        }
        return halolabOrderService.search(pageNo, perPage, customerOrderId, customerId, bookingStatuses, orderStatuses,
                startBookDate, endBookDate,
                startVisit, endVisit, sortField, sortOrder, patientId, demandZoneId, demandZoneIds, customerBookingId,
                orderSource, inventoryTypes);

    }

    @PostMapping("/create-order")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    Map createOrder(@RequestParam(name = "latitude", defaultValue = "", required = true) String latitude,
            @RequestParam(name = "longitude", defaultValue = "", required = true) String longitude,
            @RequestBody Map orderRequest) throws URISyntaxException {
        return halolabOrderService.createOrder(latitude, longitude, orderRequest);
    }

    @PostMapping("/create-order/{customerOrderBookingId}/add-ons")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    Map createAddOnOrder(@PathVariable String customerOrderBookingId,
            @RequestParam(name = "latitude", defaultValue = "", required = true) String latitude,
            @RequestParam(name = "longitude", defaultValue = "", required = true) String longitude,
            @RequestBody Map orderRequest) throws URISyntaxException {
        return halolabOrderService.createAddOnOrder(customerOrderBookingId, latitude, longitude, orderRequest);
    }

    @PostMapping("/order-bookings/{customerOrderBookingId}/payments/generate-link")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    Map generatePaymentLink(@PathVariable String customerOrderBookingId,
            @RequestBody Map paymentRequest) throws URISyntaxException {
        return halolabOrderService.generatePaymentLink(customerOrderBookingId, paymentRequest);
    }

    @PostMapping("/order-bookings/{id}/promotions")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    public Map applyPromo(@RequestBody OrderPromotion promo, @PathVariable String id) {
        Order result = halolabOrderService.applyPromo(promo, id);
        return response(result);
    }

    @PutMapping("/order-bookings/{id}/promotions/remove")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public Map removePromo(@PathVariable String id, @RequestBody OrderPromotion promo) {
        Order result = halolabOrderService.removePromo(promo, id);
        try {
            return response(result);
        } catch (HalodocWebException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
                throw new BadRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/reschedule")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void rescheduleOrder(@PathVariable String customerOrderBookingId,
            @RequestBody OrderRescheduleRequest request) {
        halolabOrderService.rescheduleOrder(customerOrderBookingId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/assign-staff")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void assignStaff(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody AssignStaffRequest request) {
        halolabOrderService.assignStaff(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/upsert-staff")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void upsertStaff(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody AssignStaffRequest request) {
        halolabOrderService.upsertStaff(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/on-the-way")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void onTheWay(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody OrderRescheduleRequest request) {
        halolabOrderService.onTheWayOrder(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/arrive")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void arrive(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody OrderRescheduleRequest request) {
        halolabOrderService.arriveOrder(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/collect")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void collect(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody OrderRescheduleRequest request) {
        halolabOrderService.collectOrder(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @PostMapping("/order-bookings/{customerOrderBookingId}/visits")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    OrderVisit addVisit(@PathVariable String customerOrderBookingId,
            @RequestBody Map request) throws URISyntaxException {
        return halolabOrderService.addVisit(customerOrderBookingId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/cancel")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void cancel(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody OrderRescheduleRequest request) {
        halolabOrderService.cancelVisit(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/cancel")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void cancelOrder(@PathVariable String customerOrderBookingId,
            @RequestBody OrderCancelRequest request) {
        halolabOrderService.cancelOrder(customerOrderBookingId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/abandon")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void abandonOrder(@PathVariable String customerOrderBookingId,
            @RequestBody Map request) {
        halolabOrderService.abandonOrder(customerOrderBookingId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/payments/confirm")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void confirmOrder(@PathVariable String customerOrderBookingId,
            @RequestBody OrderConfirmRequest request) {
        halolabOrderService.confirmOrder(customerOrderBookingId, request);
    }

    @GetMapping("/{customerOrderBookingId}/documents/{documentType}/{documentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    Map getOrderBookingDocumentById(@PathVariable String customerOrderBookingId, @PathVariable String documentType,
            @PathVariable String documentId) throws URISyntaxException {
        auditor.publishEvent(EventType.halolab_order_documents_accessed, Action.get, ActorType.cc_user, EntityType.halolab_order, customerOrderBookingId,
                Map.of("documentType", documentType, "documentId", documentId), ChannelType.http);
        return halolabOrderService.getOrderBookingDocumentById(customerOrderBookingId, documentType, documentId);
    }

    @GetMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/documents/{documentType}/{documentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    Map getOrderDocumentById(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String documentType, @PathVariable String documentId) throws URISyntaxException {
        return halolabOrderService.getOrderDocumentById(customerOrderBookingId, customerOrderId, documentType,
                documentId);
    }

    @PutMapping("/{customerOrderBookingId}/regenerate-invoice/{documentId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void regenerateInvoice(@PathVariable String customerOrderBookingId, @PathVariable String documentId)
            throws URISyntaxException {
        halolabOrderService.regenerateInvoice(customerOrderBookingId, documentId);
    }

    @PostMapping("/{bookingId}/notes")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    public void createCsNotes(@PathVariable String bookingId, @RequestBody Map<String, String> csNotesData)
            throws URISyntaxException {
        halolabOrderService.createCsNotes(bookingId, csNotesData);
    }

    @GetMapping("/{bookingId}/notes")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public List<Map> getAllNotes(@PathVariable String bookingId) throws URISyntaxException {
        return halolabOrderService.getAllNotes(bookingId);

    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/submit")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void acceptSample(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody AcceptSampleRequest request) {
        halolabOrderService.acceptSample(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/visits/{customerVisitId}/reject")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void rejectSample(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String customerVisitId,
            @RequestBody RejectSampleRequest request) {
        halolabOrderService.rejectSample(customerOrderBookingId, customerOrderId, customerVisitId, request);
    }

    @GetMapping("order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/reports")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    ResultDetails getResultDetail(@PathVariable String customerOrderBookingId,
            @PathVariable String customerOrderId,
            @RequestParam(required = false, name = "comparable") String comparable) throws URISyntaxException {
        return halolabOrderService.getResultDetail(customerOrderBookingId, customerOrderId, comparable);
    }

    @GetMapping("order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/reports/{documentType}/{documentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    Map getDocumentResultById(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @PathVariable String documentType, @PathVariable String documentId) throws URISyntaxException {
        auditor.publishEvent(EventType.halolab_order_reports_accessed, Action.get, ActorType.cc_user, EntityType.halolab_order, customerOrderBookingId,
                Map.of("documentType", documentType, "documentId", documentId), ChannelType.http);
        return halolabOrderService.getDocumentResultById(customerOrderBookingId, customerOrderId, documentType,
                documentId);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/reports/refresh")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    Map retryApi(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId)
            throws URISyntaxException {
        return halolabOrderService.retryApi(customerOrderBookingId, customerOrderId);
    }

    @PostMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/reports")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    public void generateLabReport(@PathVariable @NotBlank String customerOrderBookingId,
            @PathVariable @NotBlank String customerOrderId) throws URISyntaxException {
        halolabOrderService.generateLabReport(customerOrderBookingId, customerOrderId);
    }

    @PutMapping("{customerOrderId}/generate-line-items-template")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    Map generateLineItemsTemplate(@PathVariable String customerOrderId) throws URISyntaxException {
        return halolabOrderService.generateLineItemsTemplate(customerOrderId);
    }

    @PostMapping("/report-result/support")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    public void submitLabReport(@RequestBody Map labReport) throws URISyntaxException {
        halolabRmsService.submitLabReport(labReport);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/update-attributes")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updateOrderAttributes(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @RequestBody OrderAttributeUpdateRequest updatedOrderAttributes) throws URISyntaxException {
        halolabOrderService.updateOrderAttributes(customerOrderBookingId, customerOrderId, updatedOrderAttributes);
    }

    @PutMapping("/order-bookings/{customerOrderBookingId}/orders/{customerOrderId}/switch-patient")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void switchOrderPatient(@PathVariable String customerOrderBookingId, @PathVariable String customerOrderId,
            @RequestBody SwitchPatientRequest switchPatientRequest) throws URISyntaxException {
        halolabOrderService.switchOrderPatient(customerOrderBookingId, customerOrderId, switchPatientRequest);
    }

    @GetMapping("/configs/lab-order")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public LabOrderConfig getLabOrderConfig() {
        return halolabOrderService.getLabOrderConfig();
    }

    @GetMapping("/{customerOrderBookingId}/add-ons")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public Map getAddonOrder(
            @PathVariable String customerOrderBookingId,
            @RequestParam(required = false, name = "addon_order_ids") String addonOrderIds) throws URISyntaxException {
        return halolabOrderService.getAddonOrder(customerOrderBookingId, addonOrderIds);
    }

    @GetMapping("/report/bulk-ingestion/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedBulkIngestionHistory searchBulkIngestion(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "ingestion_id") String ingestionId,
            @RequestParam(required = false, name = "start_date") String startDate,
            @RequestParam(required = false, name = "end_date") String endDate) throws URISyntaxException {

        return halolabRmsService.searchBulkIngestion(ingestionId, pageNo, perPage, startDate, endDate);
    }

    @PutMapping("/multi-get-signed-url")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CODI)
    ResponseEntity<String> getIngestionHistoryDownloadURL(@RequestBody String documentExternalId)
            throws URISyntaxException {
        return uploaderService.getSignedDocumentURL(documentExternalId);
    }

    @PostMapping("/report-result/bulk-ingestion")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    void submitTestResultBulkIngestion(@RequestBody BulkIngestionRequest bulkIngestionRequest)
            throws URISyntaxException {
        halolabRmsService.submitTestResultBulkIngestion(bulkIngestionRequest);
    }

    @GetMapping("/report/summary/{customerReportId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public ReportSummary getReportSummary(@PathVariable String customerReportId) throws URISyntaxException {
        return halolabRmsService.getReportSummary(customerReportId);
    }
}
