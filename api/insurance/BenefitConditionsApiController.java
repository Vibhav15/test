package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.misool.catalog.BenefitConditions;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}/products/{productId}/benefit-categories/{benefitCategoryId}/benefits/{benefitId}/benefit-condition")
@RestController
@Slf4j
public class BenefitConditionsApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map<String, Object> getBenefitConditions(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @PathVariable String productId, @PathVariable String benefitCategoryId, @PathVariable String benefitId) throws URISyntaxException {

        return misoolCatalogService.getBenefitConditions(insuranceProviderId, offeringId, productId, benefitCategoryId, benefitId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Map<String, Object> createBenefitConditions(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @PathVariable String productId, @PathVariable String benefitCategoryId, @PathVariable String benefitId,
            @RequestBody BenefitConditions benefitConditions) throws URISyntaxException {
        return misoolCatalogService
                .createBenefitConditions(benefitConditions, insuranceProviderId, offeringId, productId, benefitCategoryId, benefitId);
    }

}
