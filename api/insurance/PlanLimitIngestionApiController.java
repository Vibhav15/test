package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.PlanLimitIngestionRequest;
import com.halodoc.batavia.entity.misool.catalog.PlanLimitIngestionResponse;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}/plan-limit-ingestion")
@RestController
@Slf4j
public class PlanLimitIngestionApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<PlanLimitIngestionResponse> getPlanLimitsIngestions(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "file_name", defaultValue = "") String fileName,
            @RequestParam (required = false, name = "received_date", defaultValue = "") String receivedDate) throws URISyntaxException {

        return misoolCatalogService.getPlanLimitIngestions(pageNo, perPage, insuranceProviderId, offeringId, fileName, receivedDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    PlanLimitIngestionResponse ingestPlanLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestBody PlanLimitIngestionRequest req) throws URISyntaxException {

        return misoolCatalogService.submitPlanLimitIngestion(req, insuranceProviderId, offeringId);
    }
}
