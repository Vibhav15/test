package com.halodoc.batavia.controller.api.medisend;

import com.halodoc.batavia.entity.common.PaginatedData;
import com.halodoc.batavia.entity.medisend.PointExchangeRequest;
import com.halodoc.batavia.entity.medisend.PointExchangeUpdateRequest;
import com.halodoc.batavia.entity.medisend.RewardCatalog;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.medisend.LoyaltyPointManagementService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/v1/medisend/point-exchange")
@RestController
public class PointExchangeController {
    @Autowired
    LoyaltyPointManagementService loyaltyPointManagementService;

    @GetMapping("/requests")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_exchange_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedData<PointExchangeRequest> getPaginatedRewardRedemptions(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "referenceId") String referenceId,
            @RequestParam (required = false, name = "phoneNumber") String phoneNumber,
            @RequestParam (required = false, name = "entityId") String entityId,
            @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "startDate") String startDate,
            @RequestParam (required = false, name = "endDate") String endDate
            ) throws URISyntaxException {
        return loyaltyPointManagementService.getPaginatedPointExchangeRequests(pageNo, perPage, referenceId,
                                                                        phoneNumber, entityId, status, startDate, endDate);
    }

    @GetMapping("/requests/{exchangeRequestId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_exchange_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public PointExchangeRequest getPointExchangeRequestById(@PathVariable String exchangeRequestId) throws URISyntaxException {
        return loyaltyPointManagementService.getPointExchangeRequestById(exchangeRequestId);
    }

    @GetMapping("/rewards/catalog/{catalogId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_exchange_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public RewardCatalog getRewardCatalog(@PathVariable String catalogId) throws URISyntaxException {
        return loyaltyPointManagementService.getRewardCatalogById(catalogId);
    }

    @PutMapping("/requests/{exchangeRequestId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_exchange_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updatePointExchangeRequestStatus(@PathVariable String exchangeRequestId,
                                                @RequestBody PointExchangeUpdateRequest updateExchangeRequest) throws URISyntaxException {
        return loyaltyPointManagementService.updatePointExchangeRequestStatus(exchangeRequestId, updateExchangeRequest);
    }

    @PutMapping("/requests/batch-update")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_exchange_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map batchUpdateExchangeStatus(@RequestBody List<PointExchangeUpdateRequest> updateExchangeRequests) throws URISyntaxException {
        return loyaltyPointManagementService.batchUpdateExchangeRequestStatus(updateExchangeRequests);
    }

}
