package com.halodoc.batavia.controller.api.insurance;

import java.util.Map;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/providers/{insuranceProviderId}/auto-adjudication-parameters")
@RestController
@Slf4j
public class AutoAdjudicationApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getAutoAdjudicationSetup(@PathVariable String insuranceProviderId) throws URISyntaxException {
        return misoolCatalogService.getAutoAdjudicationSetup(insuranceProviderId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @PutMapping
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateAutoAdjudicationSetup(@PathVariable String insuranceProviderId, @RequestBody Map autoAdjudicationAttribute) throws URISyntaxException {
        return misoolCatalogService.updateAutoAdjudicationSetup(insuranceProviderId, autoAdjudicationAttribute);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createAutoAdjudicationSetup(@PathVariable String insuranceProviderId, @RequestBody Map autoAdjudicationAttribute) throws URISyntaxException {
        return misoolCatalogService.createAutoAdjudicationSetup(insuranceProviderId, autoAdjudicationAttribute);
    }
}
