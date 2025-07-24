package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;

import com.halodoc.batavia.entity.misool.catalog.BenefitCodeResponse;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.BenefitCode;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}/benefitCodes")
@RestController
@Slf4j
public class BenefitCodesApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<BenefitCodeResponse> getBenefitCodes(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "benefit_code") String benefit_code) throws URISyntaxException {
        return misoolCatalogService.benefitCodes(pageNo, perPage, benefit_code, insuranceProviderId, offeringId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    BenefitCode createBenefitCode(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @RequestBody BenefitCode BenefitCode)
            throws URISyntaxException {
        return misoolCatalogService.createBenefitCode(insuranceProviderId, offeringId, BenefitCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{benefitCodeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    BenefitCodeResponse updateBenefitCode(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String benefitCodeId,
            @RequestBody BenefitCode benefitCode) throws URISyntaxException {
        return misoolCatalogService.updateBenefitCode(insuranceProviderId, offeringId, benefitCodeId, benefitCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{benefitCodeId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    BenefitCodeResponse getBenefitCode(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String benefitCodeId)
            throws URISyntaxException {
        return misoolCatalogService.getBenefitCode(insuranceProviderId, offeringId, benefitCodeId);
    }

}
