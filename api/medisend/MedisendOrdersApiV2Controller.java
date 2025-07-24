package com.halodoc.batavia.controller.api.medisend;


import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.medisend.MedisendShipment;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.medisend.MedisendOrderService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/v2/medisend/orders")
@RestController
public class MedisendOrdersApiV2Controller {
    @Autowired
    private MedisendOrderService medisendOrderService;

    @GetMapping("/search")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<MedisendShipment> getMedisendOrder(
            @RequestParam(required = false, name = "orderId", defaultValue = "") String searchText,
            @RequestParam(required = false, name = "shipmentId", defaultValue = "") String shipmentId,
            @RequestParam(required = false, name = "invoiceId", defaultValue = "") String invoiceId,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "distributor") String distributor,
            @RequestParam(required = false, name = "distributor_branches") String distributorBranches,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "shipment_statuses") String shipmentStatuses,
            @RequestParam(required = false, name = "action_required") String actionRequired,
            @RequestParam(required = false, name = "sort_field") String sortField,
            @RequestParam(required = false, name = "sort_order") String sortOrder,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false, name = "customer_entity_id", defaultValue = "") String entityId,
            @RequestParam(required = false, name = "entity_type") String entityType,
            @RequestParam(required = false, name = "view_ineligible_invoice") String viewIneligibleInvoice,
            @RequestParam(required = false, name = "merchant_location_ids") String merchantLocationIds,
            @RequestParam(required = false, name = "payment_method") String paymentMethod,
            @RequestParam(required = false, name = "payment_status") String paymentStatus
    ) throws URISyntaxException {
        return medisendOrderService.searchV2(searchText, shipmentId, invoiceId, distributor, pageNo, perPage, shipmentStatuses, actionRequired,
                sortField, sortOrder, startDate, endDate, entityId, entityType, distributorBranches, viewIneligibleInvoice, merchantLocationIds,
                paymentMethod, paymentStatus);
    }

    @GetMapping("/invoice/documents")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_order_actions', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map generateBulkInvoice(
            @RequestParam(required = false, name = "orderId", defaultValue = "") String searchText,
            @RequestParam(required = false, name = "shipmentId", defaultValue = "") String shipmentId,
            @RequestParam(required = false, name = "invoiceId", defaultValue = "") String invoiceId,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "distributor") String distributor,
            @RequestParam(required = false, name = "distributor_branches") String distributorBranches,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "shipment_statuses") String shipmentStatuses,
            @RequestParam(required = false, name = "action_required") String actionRequired,
            @RequestParam(required = false, name = "sort_field") String sortField,
            @RequestParam(required = false, name = "sort_order") String sortOrder,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false, name = "customer_entity_id", defaultValue = "") String entityId,
            @RequestParam(required = false, name = "entity_type", defaultValue = "pharmacy_user") String entityType,
            @RequestParam(required = false, name = "view_ineligible_invoice") String viewIneligibleInvoice,
            @RequestParam(required = false, name = "merchant_location_ids") String merchantLocationIds
    ) throws URISyntaxException {
        return medisendOrderService.generateBulkInvoiceV2(searchText, shipmentId, invoiceId, distributor, pageNo, perPage, shipmentStatuses, actionRequired,
                sortField, sortOrder, startDate, endDate, entityId, entityType, distributorBranches, viewIneligibleInvoice, merchantLocationIds);
    }
}
