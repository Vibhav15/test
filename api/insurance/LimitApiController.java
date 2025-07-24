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
import com.halodoc.batavia.entity.misool.catalog.InstanceLimit;
import com.halodoc.batavia.entity.misool.catalog.InstanceLimitRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}/limits")
@RestController
@Slf4j
public class LimitApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<InstanceLimit> getInstanceLimits(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "linked_plan") String linkedPlan,
            @RequestParam (required = false, name = "module_type") String moduleType,
            @RequestParam (required = false, name = "module_name") String moduleName) throws URISyntaxException {

        return misoolCatalogService
                .getInstanceLimits(insuranceProviderId, offeringId, pageNo, perPage, name, status, linkedPlan, moduleType, moduleName);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{limitId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InstanceLimit getInstanceLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String limitId)
            throws URISyntaxException {

        return misoolCatalogService.getInstanceLimit(insuranceProviderId, offeringId, limitId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    InstanceLimit saveInstanceLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestBody InstanceLimitRequest limitRequest) throws URISyntaxException {
        return misoolCatalogService.saveInstanceLimit(limitRequest, insuranceProviderId, offeringId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{limitId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    InstanceLimit updateInstanceLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String limitId,
            @RequestBody InstanceLimitRequest instanceLimit) throws URISyntaxException {
        return misoolCatalogService.updateInstanceLimit(instanceLimit, insuranceProviderId, offeringId, limitId);
    }

}
