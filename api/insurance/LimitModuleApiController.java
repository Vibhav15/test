package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.LimitModule;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{insuranceProviderId}/offerings/{offeringId}")
@RestController
@Slf4j
public class LimitModuleApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/modules")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<LimitModule> getLimitModules(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage, @PathVariable String insuranceProviderId,
            @PathVariable String offeringId, @RequestParam (name = "type") String moduleType) throws URISyntaxException {

        return misoolCatalogService.getLimitModules(insuranceProviderId, offeringId, moduleType, perPage, pageNo);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/modules/{moduleId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    LimitModule getLimitModule(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String moduleId,
            @RequestParam (name = "type") String moduleType) throws URISyntaxException {

        return misoolCatalogService.getLimitModule(insuranceProviderId, offeringId, moduleId, moduleType);
    }

}
