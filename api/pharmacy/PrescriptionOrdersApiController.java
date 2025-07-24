package com.halodoc.batavia.controller.api.pharmacy;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.delivery_system.OrderStatusRequest;
import com.halodoc.batavia.entity.oms.Order;

import com.halodoc.batavia.entity.oms.cart.Cart;
import com.halodoc.batavia.entity.oms.cart.CartConfirmRequest;
import com.halodoc.batavia.entity.oms.cart.OrderItemQuantityUpdateRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.delivery_system.DeliverySystemService;
import com.halodoc.batavia.service.timor.TimorOmsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/v1/leads/orders")
@RestController
@Slf4j
public class PrescriptionOrdersApiController extends HalodocBaseApiController {


    TimorOmsService timorOmsService;
    DeliverySystemService deliverySystemService;

    @Autowired
    public PrescriptionOrdersApiController(TimorOmsService timorOmsService,
            DeliverySystemService deliverySystemService) {
        this.timorOmsService = timorOmsService;
        this.deliverySystemService = deliverySystemService;
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_create_reorder', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    Order checkPrice(@RequestBody Cart cart) {
        return timorOmsService.createOrder(cart);
    }

    @GetMapping("/{customerOrderId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    Order getOrderStatus(@PathVariable String customerOrderId) {
        return timorOmsService.getOrderStatusById(customerOrderId);
    }

    @PutMapping("/{customerOrderId}/items/update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_create_reorder', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    Order updateItems(@PathVariable String customerOrderId,
                      @RequestBody OrderItemQuantityUpdateRequest orderItemQuantityUpdateRequest) {
        return timorOmsService.updateItems(customerOrderId, orderItemQuantityUpdateRequest);
    }

    @PutMapping("/{customerOrderId}/confirm")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void confirmOrder(@PathVariable String customerOrderId, @RequestBody CartConfirmRequest cartConfirmRequest) {
        timorOmsService.confirmOrder(customerOrderId, cartConfirmRequest);
//        Order order = timorOmsService.getOrderById(customerOrderId);
//        if(!StringUtils.isEmpty(order.getAttributes().get("source").toString()) &&
//                order.getAttributes().get("source")order.getAttributes().get("source").toString().equalsIgnoreCase("jne")) {
//            timorOmsService.shipOrder(customerOrderId, order.getShipments().get(0).getId());
//            timorOmsService.deliverOrder(customerOrderId, order.getShipments().get(0).getId());
//            OrderShipment shipment = new OrderShipment();
//            shipment.setTrackingId(order.getAttributes().get("order_source_id").toString());
//            timorOmsService.updateJNEOrderShipment(customerOrderId, order.getShipments().get(0).getId(), shipment);
//        }
    }

    @PutMapping("/{customerOrderId}/shipments/{shipmentId}/ship")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void shipOrder(@PathVariable String customerOrderId, @PathVariable Long shipmentId) {
        timorOmsService.shipOrder(customerOrderId, shipmentId);
    }

    @PutMapping("/{customerOrderId}/shipments/{shipmentId}/deliver")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void deliverOrder(@PathVariable String customerOrderId, @PathVariable Long shipmentId) {
        timorOmsService.deliverOrder(customerOrderId, shipmentId);
    }

    @PutMapping("/status-update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void updateOrderStatus(@RequestBody OrderStatusRequest orderStatusRequest) {
        deliverySystemService.updateStatusInDeliverySystem(orderStatusRequest);
    }

    @PutMapping("/{customerOrderId}/groups/{groupId}/dispatch")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void deliverPickupOrder(@PathVariable String customerOrderId, @PathVariable String groupId) {
        timorOmsService.deliverPickupOrder(customerOrderId, groupId);
    }

    @PutMapping("/{customerOrderId}/shipments/{shipmentId}/rebook")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void rebookOrder(@PathVariable String customerOrderId, @PathVariable Long shipmentId) {
        timorOmsService.rebookOrder(customerOrderId, shipmentId);
    }

}
