package com.halodoc.batavia.controller.api.medisend;


import com.halodoc.batavia.entity.cms.Pharmacy;
import com.halodoc.batavia.entity.medisend.*;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.medisend.IntegratedDistributorFactory;
import com.halodoc.batavia.service.medisend.MedisendOrderService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/v1/medisend/orders")
@RestController
public class MedisendOrdersApiController {

    @Autowired
    private MedisendOrderService medisendOrderService;

    @Autowired
    private IntegratedDistributorFactory integratedDistributorFactory;

    @GetMapping("/{merchantId}/pharmacy/{merchantLocationId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Pharmacy getPharmacy(@PathVariable String merchantId,
                                @PathVariable String merchantLocationId
    ) throws URISyntaxException {
        return medisendOrderService.getPharmacy(merchantId, merchantLocationId);
    }

    @GetMapping("/{orderId}/distributor-branch-names")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_list', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getDistributorBranches(@PathVariable String orderId) throws URISyntaxException {
        return medisendOrderService.getDistributorBranchNames(orderId);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_view', 'view_only')")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    public MedisendOrder getOrderDetails(@PathVariable String orderId) throws URISyntaxException {
        return medisendOrderService.getOrderDetailWithReturnDocument(orderId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/deliver")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deliverOrder(@PathVariable String orderId, @PathVariable String shipmentId) throws URISyntaxException {
        medisendOrderService.deliverOrder(orderId, shipmentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/cancel")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void cancelOrder(@PathVariable String orderId, @PathVariable String shipmentId,
                            @RequestBody Map cancelOrderRequest) throws URISyntaxException {
        medisendOrderService.cancelOrder(orderId, shipmentId, cancelOrderRequest);
    }

    @GetMapping("{orderId}/shipments/{shipmentId}/documents")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getShipmentDocuments(@PathVariable String orderId, @PathVariable String shipmentId)
            throws URISyntaxException {
        return medisendOrderService.getShipmentDocuments(orderId, shipmentId);
    }

    @PostMapping("/{orderId}/notes")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map addOrderNote(@PathVariable String orderId,
                            @RequestBody Map orderNote) throws URISyntaxException {
        return medisendOrderService.addOrderNote(orderId, orderNote);
    }

    @PostMapping("/{orderId}/shipments/partial")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map createPartialShipment(@PathVariable String orderId,
                                     @RequestBody CreatePartialShipmentRequest partialRequest) throws URISyntaxException {
        return medisendOrderService.createPartialShipment(orderId, partialRequest);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/confirm")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void confirmMedisendOrder(@PathVariable String orderId, @PathVariable String shipmentId) throws URISyntaxException {
        medisendOrderService.confirmOrder(orderId, shipmentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/transit")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void transitOrder(@PathVariable String orderId, @PathVariable String shipmentId) throws URISyntaxException {
        medisendOrderService.transitOrder(orderId, shipmentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/items/update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateShipmentStock(@PathVariable String orderId, @PathVariable String shipmentId,
                                    @RequestBody UpdateShipmentStockRequest updateShipmentStockRequest) throws URISyntaxException {
        medisendOrderService.updateShipmentStock(orderId, shipmentId, updateShipmentStockRequest);
    }

    @PostMapping("/{orderId}/shipments/{shipmentId}/retry")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void retryShipment(@PathVariable String orderId, @PathVariable String shipmentId
    ) throws URISyntaxException {
        medisendOrderService.retryShipment(orderId, shipmentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/items/{itemId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void setInputBatchNumber(@PathVariable String orderId, @PathVariable String shipmentId, @PathVariable String itemId,
                                    @RequestBody InputBatchNumberAttribute inputBatchNumberAttribute) throws URISyntaxException {
        medisendOrderService.setInputBatchNumber(orderId, shipmentId, itemId, inputBatchNumberAttribute);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/documents/upload")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public void uploadShipmentDocument(@PathVariable String orderId, @PathVariable String shipmentId,
                                       @RequestHeader("X-Document-Type") String xDocumentType,
                                       @RequestHeader("Content-Length") final Long contentLength,
                                       InputStream fileStream) {
        medisendOrderService.uploadShipmentDocument(orderId, shipmentId, xDocumentType, contentLength, fileStream);
    }

    @DeleteMapping("/{orderId}/shipments/{shipmentId}/documents/{documentId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deleteShipmentDocument(@PathVariable String orderId,
                                       @PathVariable String shipmentId, @PathVariable String documentId) throws URISyntaxException {
        medisendOrderService.deleteShipmentDocument(orderId, shipmentId, documentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/reject-payment")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void rejectShipmentPayment(@PathVariable String orderId, @PathVariable String shipmentId,
                                      @RequestBody Map rejectRequest) throws URISyntaxException {
        medisendOrderService.rejectShipmentPayment(orderId, shipmentId, rejectRequest);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/paid-payment")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void acceptshipmentPayment(@PathVariable String orderId, @PathVariable String shipmentId) throws URISyntaxException {
        medisendOrderService.acceptShipmentPayment(orderId, shipmentId);

    }

    @GetMapping("{orderId}/shipments/{shipmentId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public MedisendOrder getShipmentDetails(@PathVariable String orderId, @PathVariable String shipmentId)
            throws URISyntaxException {
        return medisendOrderService.getModifiedShipment(orderId, shipmentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/update-invoice-data")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateDistributorInvoiceData(@PathVariable String orderId, @PathVariable String shipmentId,
                                             @RequestBody Map invoiceUpdateRequestBody) throws URISyntaxException {
        medisendOrderService.updateDistributorInvoiceData(orderId, shipmentId, invoiceUpdateRequestBody);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/generate-invoice")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map generateInvoice(@PathVariable String orderId, @PathVariable String shipmentId) throws URISyntaxException {
        return medisendOrderService.generateInvoice(orderId, shipmentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void onShipmentMethodChnage(@PathVariable String orderId, @PathVariable String shipmentId,
                                       @RequestBody Map updateBody) throws URISyntaxException {
        medisendOrderService.onShipmentMethodCHange(orderId, shipmentId, updateBody);
    }

    @PostMapping("/{orderId}/shipments/{shipmentId}/items/{itemId}/add-batch-number")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map addBatchNumber(@PathVariable String orderId, @PathVariable String shipmentId, @PathVariable String itemId,
                              @RequestBody Map<String, Object> inputBatchNumberAttribute) throws URISyntaxException {
        return medisendOrderService.addBatchNumber(orderId, shipmentId, itemId, inputBatchNumberAttribute);
    }

    @DeleteMapping("/{orderId}/shipments/{shipmentId}/items/{itemId}/delete-batch-no")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map deleteBatchNumber(@PathVariable String orderId, @PathVariable String shipmentId, @PathVariable String itemId,
                                 @RequestBody Map<String, Object> inputBatchNumberAttribute) throws URISyntaxException {
        return medisendOrderService.deleteBatchNumber(orderId, shipmentId, itemId, inputBatchNumberAttribute);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/return")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void onreturnShipmentRequested(@PathVariable String orderId, @PathVariable String shipmentId,
                                          @RequestBody Map returnRequestBody) throws URISyntaxException {


        medisendOrderService.onreturnShipmentRequested(orderId, shipmentId, returnRequestBody);
    }

    @PatchMapping("/{orderId}/fix-order-status")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void onFixOrderSTatusRequested(@PathVariable String orderId) throws URISyntaxException {

        medisendOrderService.onFixOrderStatusRequested(orderId);
    }

    @GetMapping("/invoice/merge/documents/{txnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map generateBulkInvoicePdfUrl(@PathVariable String txnId) throws URISyntaxException {
        return medisendOrderService.generateBulkInvoicePdfUrl(txnId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/modify-status")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','modify_status_forcefully', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void onModifyStatus(@PathVariable String orderId, @PathVariable String shipmentId,
                               @RequestBody Map returnRequestBody) throws URISyntaxException {
        medisendOrderService.modifyShipmentStatus(orderId, shipmentId, returnRequestBody);
    }

    @PostMapping("/{orderId}/shipments/{shipmentId}/multireturn")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void returnShipmentRequestMulti(@PathVariable String orderId, @PathVariable String shipmentId,
                                           @RequestBody Map returnRequestBody) throws URISyntaxException {


        medisendOrderService.onReturnShipmentRequestMulti(orderId, shipmentId, returnRequestBody);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/multireturn/{returnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateReturnShipmentRequestMulti(@PathVariable String orderId, @PathVariable String shipmentId, @PathVariable String returnId,
                                                 @RequestBody Map returnRequestBody) throws URISyntaxException {


        medisendOrderService.updateReturnShipmentRequestMulti(orderId, shipmentId, returnId, returnRequestBody);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/markasdelivered")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void markAsDeliveredOrder(@PathVariable String orderId, @PathVariable String shipmentId) throws URISyntaxException {
        medisendOrderService.deliverOrder(orderId, shipmentId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/return/{returnId}/cancel")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void cancelReturnShipmentOrder(@PathVariable String orderId, @PathVariable String shipmentId, @PathVariable String returnId) throws URISyntaxException {
        medisendOrderService.cancelReturnShipmentOrder(orderId, shipmentId, returnId);
    }

    @GetMapping("/{orderId}/shipments/{shipmentId}/multireturn/{returnOrderId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getReturnOrderDocuments(@PathVariable String orderId, @PathVariable String shipmentId, @PathVariable String returnOrderId) throws URISyntaxException {
        return medisendOrderService.getReturnOrderDocuments(orderId, shipmentId, returnOrderId);
    }

    @PutMapping("/{orderId}/shipments/{shipmentId}/documents/uploadReturnDoc")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public ReturnDocumentUploadResponse uploadReturnDoc(@PathVariable String orderId, @PathVariable String shipmentId,
                                                        @RequestHeader("X-Document-Type") String xDocumentType,
                                                        @RequestHeader("Content-Length") final Long contentLength,
                                                        InputStream fileStream) {
        ReturnDocumentUploadResponse dataRes = medisendOrderService.uploadReturnDocuments(orderId, shipmentId, xDocumentType, contentLength, fileStream);
        return dataRes;
    }

    @PostMapping("/trigger")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map invoiceSync(@RequestBody DistributorShipment distributorShipment) throws URISyntaxException {
        IntegratedDistributorActions integratedDistributorActions = integratedDistributorFactory.getIntegratedDistributorAction(distributorShipment.getDistributorCode());
        return integratedDistributorActions.invoiceSync(distributorShipment);
    }

    @GetMapping("/shipments/{shipmentId}/invoice-status")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getInvoiceStatus( @PathVariable String shipmentId) throws URISyntaxException {
        return medisendOrderService.getInvoiceStatus(shipmentId);
    }

    @PutMapping ("/{orderId}/shipment/{shipmentId}/reject/{returnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void rejectReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId, @RequestBody Map rejectBody) throws URISyntaxException {
        medisendOrderService.rejectReturn(shipmentId, orderId, returnId, rejectBody);
    }


    @PutMapping ("/{orderId}/shipment/{shipmentId}/confirm/{returnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void confirmReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId) throws URISyntaxException {
        medisendOrderService.confirmReturn(shipmentId, orderId, returnId);
    }

    @PutMapping ("/{orderId}/shipment/{shipmentId}/transit/{returnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void transitReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId) throws URISyntaxException {
        medisendOrderService.transitReturn(shipmentId, orderId, returnId);
    }

    @PutMapping ("/{orderId}/shipment/{shipmentId}/deliver/{returnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deliverReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId) throws URISyntaxException {
        medisendOrderService.deliverReturn(shipmentId, orderId, returnId);
    }

    @PutMapping ("/{orderId}/shipment/{shipmentId}/complete/{returnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void completeReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId, @RequestBody Map costTagging) throws URISyntaxException {
        medisendOrderService.completeReturn(shipmentId, orderId, returnId, costTagging);
    }

    @PostMapping ("/{orderId}/shipments/{shipmentId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void requestReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @RequestBody OrderReturn orderReturn) throws URISyntaxException {
        medisendOrderService.requestReturn(orderId, shipmentId, orderReturn);
    }

    @PutMapping ("/{orderId}/shipments/{shipmentId}/returns/{returnId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId, @RequestBody OrderReturn orderReturn) throws URISyntaxException {
        medisendOrderService.updateReturn(orderId, shipmentId, returnId, orderReturn);
    }

    @PutMapping ("/{orderId}/shipments/{shipmentId}/returns/{returnId}/cancel")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_return_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void cancelReturn(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId) throws URISyntaxException {
        medisendOrderService.cancelReturn(orderId, shipmentId, returnId);
    }

    @GetMapping ("/{orderId}/shipments/{shipmentId}/returns/{returnId}/return-note")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getReturnNote(@PathVariable ("orderId") String orderId, @PathVariable ("shipmentId") String shipmentId,
            @PathVariable ("returnId") String returnId) throws URISyntaxException {
        return medisendOrderService.getReturnNote(orderId, shipmentId, returnId);
    }

    @GetMapping ("/shipments/{shipmentId}/attributes")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getShipmentAttributes(@PathVariable String shipmentId, @RequestParam (name = "keys", defaultValue = "") String keys)
            throws URISyntaxException {
        return medisendOrderService.getShipmentAttributes(shipmentId, keys);
    }
}
