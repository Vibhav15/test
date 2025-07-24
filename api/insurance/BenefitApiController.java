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
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.Benefit;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}/products/{productId}/benefit-categories/{benefitCategoryId}/benefits")
@RestController
@Slf4j
public class BenefitApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Benefit> getBenefits(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @PathVariable String benefitCategoryId, @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "benefit_code") String benefitCode,
            @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "benefit_type") String benefitType,
            @RequestParam (required = false, name = "benefit_class") String benefitClass) throws URISyntaxException {

        return misoolCatalogService
                .getBenefits(insuranceProviderId, offeringId, productId, benefitCategoryId, pageNo, perPage, name, status, benefitCode, benefitType,
                        benefitClass);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_list', 'view_only')")
    @GetMapping ("/{benefitId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Benefit getBenefit(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @PathVariable String benefitCategoryId, @PathVariable String benefitId) throws URISyntaxException {

        return misoolCatalogService.getBenefit(insuranceProviderId, offeringId, productId, benefitCategoryId, benefitId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Benefit createBenefit(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @PathVariable String benefitCategoryId, @RequestBody Benefit benefit) throws URISyntaxException {
        return misoolCatalogService.createBenefit(benefit, insuranceProviderId, offeringId, productId, benefitCategoryId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_edit', 'restricted_write')")
    @PutMapping ("/{benefitId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Benefit updateBenefit(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @PathVariable String benefitCategoryId, @PathVariable String benefitId, @RequestBody Benefit benefit) throws URISyntaxException {
        return misoolCatalogService.updateBenefit(benefit, insuranceProviderId, offeringId, productId, benefitCategoryId, benefitId);
    }

}
