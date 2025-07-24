package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.Plan;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}/plans")
@RestController
@Slf4j
public class PlanApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<Plan> getPlans(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "code") String code,
            @RequestParam (required = false, name = "status") String status) throws URISyntaxException {
        return misoolCatalogService.plan(pageNo, perPage, name, code, status, insuranceProviderId, offeringId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Plan createPlan(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @RequestBody Plan plan) throws URISyntaxException {
        return misoolCatalogService.createPlan(insuranceProviderId, offeringId, plan);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{planId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Plan updatePlan(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String planId, @RequestBody Plan plan)
            throws URISyntaxException {
        return misoolCatalogService.updatePlan(insuranceProviderId, offeringId, planId, plan);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{planId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Plan getPlan(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String planId) throws URISyntaxException {
        return misoolCatalogService.getPlan(insuranceProviderId, offeringId, planId);
    }

}
