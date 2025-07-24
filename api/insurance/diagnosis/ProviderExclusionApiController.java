package com.halodoc.batavia.controller.api.insurance.diagnosis;

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
import com.halodoc.batavia.entity.diagnosis.Exclusion;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/providers/{insuranceProviderId}/exclusions")
@RestController
@Slf4j
public class ProviderExclusionApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Exclusion> getExclusions(@PathVariable String insuranceProviderId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "status", defaultValue = "") String status,
            @RequestParam (required = false, name = "name", defaultValue = "") String name,
            @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) throws URISyntaxException {

        return misoolCatalogService.getExclusions(insuranceProviderId, pageNo, perPage, name, status, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("{exclusionId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Exclusion getExclusion(@PathVariable String insuranceProviderId, @PathVariable String exclusionId,
                           @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) throws URISyntaxException {

        return misoolCatalogService.getExclusion(insuranceProviderId, exclusionId, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Exclusion saveExclusion(@PathVariable String insuranceProviderId,
                            @RequestBody Exclusion exclusion,
                            @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) throws URISyntaxException {
        return misoolCatalogService.saveExclusion(insuranceProviderId, exclusion, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("{exclusionId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Exclusion editExclusion(@PathVariable String insuranceProviderId, @PathVariable String exclusionId, @RequestBody Exclusion exclusion,
                            @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType)
            throws URISyntaxException {
        return misoolCatalogService.updateExclusion(insuranceProviderId, exclusionId, exclusion, providerType);
    }

}
