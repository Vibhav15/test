package com.halodoc.batavia.controller.api.medisend;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.medisend.ManualPayment;
import com.halodoc.batavia.entity.medisend.Settlement;
import com.halodoc.batavia.entity.medisend.SettlementAllocation;
import com.halodoc.batavia.entity.medisend.SettlementReallocation;
import com.halodoc.batavia.entity.medisend.SettlementRefund;
import com.halodoc.batavia.entity.medisend.SettlementShipment;
import com.halodoc.batavia.entity.medisend.VirtualAccount;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.medisend.CreditAccountsService;
import com.halodoc.batavia.service.medisend.MedisendOrderService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping ("api/v1/medisend/settlements")
@RestController
public class MedisendSettlementApiController {
    @Autowired
    private CreditAccountsService creditAccountsService;

    @Autowired
    private MedisendOrderService medisendOrderService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping ("/virtual-accounts/{entityId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','merchant_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<VirtualAccount> getCreditAccounts(@PathVariable String entityId) throws URISyntaxException {
        return creditAccountsService.getVirtualAccounts(entityId);
    }

    @PostMapping ("/manual-payments")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_manual_payment', 'view_only')")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public ManualPayment submitManualPayment(@RequestHeader ("X-Document-Type") String xDocumentType, @RequestHeader ("X-File-Type") String xFileType,
            @RequestParam ("file") MultipartFile file, @RequestParam ("data") String data) throws IOException, UnirestException {
        return medisendOrderService.submitManualPayment(xDocumentType, xFileType, file, data);
    }

    @GetMapping ("/orders/{orderId}/shipments/{shipmentId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<SettlementShipment> getSettlementTrackingPerShipment(@PathVariable String orderId, @PathVariable String shipmentId)
            throws URISyntaxException {
        return medisendOrderService.getSettlementTrackingPerShipment(orderId, shipmentId);
    }

    @GetMapping ("/search")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','settlement_history_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Settlement> getSettlementHistory(
            @RequestParam (required = false, name = "settlement_id", defaultValue = "") String settlementId,
            @RequestParam (required = false, name = "shipment_id", defaultValue = "") String shipmentId,
            @RequestParam (required = false, name = "invoice_id", defaultValue = "") String invoiceId,
            @RequestParam (required = false, name = "settlement_method", defaultValue = "") String settlementMethod,
            @RequestParam (required = false, name = "is_over_limit", defaultValue = "") String isOverLimit,
            @RequestParam (required = false, name = "deleted", defaultValue = "") String deleted,
            @RequestParam (required = false, name = "merchant_location_id", defaultValue = "") String merchantLocationId,
            @RequestParam (required = false, name = "is_search_page", defaultValue = "") String isSearchPage,
            @RequestParam (required = false, name = "payment_method", defaultValue = "") String paymentMethod,
            @RequestParam (required = false, name = "start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam (required = false, name = "end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "sort_by", defaultValue = "updated_at") String sortBy,
            @RequestParam (required = false, name = "sort_order", defaultValue = "desc") String sortOrder) throws URISyntaxException {
        return medisendOrderService.getSettlementHistory(settlementId, shipmentId, invoiceId, settlementMethod, isOverLimit, deleted,
                merchantLocationId, isSearchPage, paymentMethod, startDate, endDate, pageNo, perPage, sortBy, sortOrder);
    }

    @PutMapping ("/refund")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','settlement_history_refund', 'view_only')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map submitManualRefund(@RequestBody SettlementRefund data) throws URISyntaxException {
        return medisendOrderService.submitManualRefund(data);
    }

    @GetMapping ("/overlimit-amount/merchant-location/{merchantLocationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public BigDecimal getOverlimitAmountByMerchant(@PathVariable String merchantLocationId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "medisend_order_view", "view_only") && !authorizationService.isAuthorized(
                "pharmacy_delivery", "merchant_location_add", "restricted_write") && !authorizationService.isAuthorized("pharmacy_delivery",
                "merchant_location_view", "view_only") && !authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_edit",
                "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return medisendOrderService.getOverlimitAmountByMerchant(merchantLocationId);
    }

    @GetMapping("/{shipmentId}/allocations")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','settlement_history_refund', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<SettlementAllocation> getSettlementAllocation(@PathVariable String shipmentId) throws URISyntaxException {
        return medisendOrderService.getSettlementAllocation(shipmentId);
    }

    @DeleteMapping("/reverse/settlement")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','settlement_history_refund', 'view_only')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void reverseSettlement(@RequestParam(required = false, name = "settlement_id", defaultValue = "") String settlementId,
                                @RequestParam(required = false, name = "merchant_location_id", defaultValue = "") String merchantLocationId) throws URISyntaxException {
        medisendOrderService.reverseSettlement(settlementId, merchantLocationId);
    }

    @GetMapping("/merchant_location/reallocation")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','settlement_history_refund', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<SettlementReallocation> getSettlementReallocation(@RequestParam(required = false, name = "settlement_id", defaultValue = "") String settlementId,
                                               @RequestParam(required = false, name = "merchant_location_id", defaultValue = "") String merchantLocationId) throws URISyntaxException {
        return medisendOrderService.getSettlementReallocation(settlementId, merchantLocationId);
    }

    @PatchMapping("/merchant_location/reallocation")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','settlement_history_refund', 'view_only')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateSettlementReallocation(@RequestParam(required = false, name = "settlement_id", defaultValue = "") String settlementId,
                                            @RequestParam(required = false, name = "merchant_location_id", defaultValue = "") String merchantLocationId) throws URISyntaxException {
        medisendOrderService.updateSettlementReallocation(settlementId, merchantLocationId);
    }

    @PatchMapping("/merchant_location/reallocate-overlimit-payment")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','settlement_history_refund', 'view_only')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateSettlementReallocationForOverLimitPayment(@RequestParam(required = false, name = "settlement_id", defaultValue = "") String settlementId,
                                             @RequestParam(required = false, name = "merchant_location_id", defaultValue = "") String merchantLocationId) throws URISyntaxException {
        medisendOrderService.updateSettlementReallocationForOverLimitPayment(settlementId, merchantLocationId);
    }
}
