package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import java.util.Map;
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
import com.halodoc.batavia.entity.misool.catalog.DynamicModuleLimitRequest;
import com.halodoc.batavia.entity.misool.catalog.ModuleLimitsRequest;
import com.halodoc.batavia.entity.misool.catalog.ModuleLimitsResponse;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}/module-limits")
@RestController
@Slf4j
public class ModuleLimitsApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<ModuleLimitsResponse> getModuleLimits(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "balance_source") String balanceSource,
            @RequestParam (required = false, name = "plan_code") String planCode) throws URISyntaxException {

        return misoolCatalogService.getModuleLimits(insuranceProviderId, offeringId, pageNo, perPage, name, status, balanceSource, planCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{moduleLimitId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ModuleLimitsResponse getModuleLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String moduleLimitId)
            throws URISyntaxException {

        return misoolCatalogService.getModuleLimit(insuranceProviderId, offeringId, moduleLimitId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("member_dynamic")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    ModuleLimitsResponse createDynamicModuleLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestBody DynamicModuleLimitRequest req) throws URISyntaxException {
        return misoolCatalogService.createDynamicModuleLimit(req, insuranceProviderId, offeringId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    ModuleLimitsResponse saveModuleLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestBody ModuleLimitsRequest req) throws URISyntaxException {

        return misoolCatalogService.saveModuleLimit(req, insuranceProviderId, offeringId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{moduleLimitId}/static")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    ModuleLimitsResponse updateStaticModuleLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @PathVariable String moduleLimitId, @RequestBody ModuleLimitsRequest req) throws URISyntaxException {
        return misoolCatalogService.updateStaticModuleLimit(req, insuranceProviderId, offeringId, moduleLimitId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{moduleLimitId}/member_dynamic")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    ModuleLimitsResponse updateDynamicModuleLimit(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
                                           @PathVariable String moduleLimitId, @RequestBody DynamicModuleLimitRequest req) throws URISyntaxException {
        return misoolCatalogService.updateDynamicModuleLimit(req, insuranceProviderId, offeringId, moduleLimitId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/validate/{type}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map validateProductCodeOrBenefitIndicator(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String type,
            @RequestBody Map req) throws URISyntaxException {
        return misoolCatalogService.validateProductOrBenefitCode(insuranceProviderId, offeringId, type, req);
    }
}
