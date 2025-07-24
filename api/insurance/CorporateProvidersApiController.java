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
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.CorporateProvider;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/corporates")
@RestController
@Slf4j
public class CorporateProvidersApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','corporate_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<CorporateProvider> getCorporateProvidersList(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false, name = "status", defaultValue = "all") String status) throws URISyntaxException {

        return misoolCatalogService.getCorporateProvidersList(name, status, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','corporate_provider_list', 'view_only')")
    @GetMapping ("/{corporateProviderId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    CorporateProvider getCorporateProvider(@PathVariable String corporateProviderId) throws URISyntaxException {
        return misoolCatalogService.getCorporateProvider(corporateProviderId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','corporate_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    CorporateProvider createCorporateProvider(@RequestBody CorporateProvider req) {
        return misoolCatalogService.createCorporateProvider(req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','corporate_provider_edit', 'restricted_write')")
    @PutMapping ("/{corporateProviderId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateCorporateProvider(@PathVariable String corporateProviderId, @RequestBody CorporateProvider req) throws URISyntaxException {
        misoolCatalogService.updateCorporateProvider(corporateProviderId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','corporate_provider_list', 'view_only')")
    @GetMapping("/policies")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> getAllInsuranceProvidersPoliciesList(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "policy_number", defaultValue = "") String policyNumber,
            @RequestParam (required = false, name = "status", defaultValue = "all") String status) throws URISyntaxException {

        return misoolCatalogService.getAllInsuranceProvidersPoliciesList(policyNumber, status, pageNo, perPage);
    }
}
