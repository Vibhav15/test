package com.halodoc.batavia.controller.api.insurance.diagnosis;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.halodoc.batavia.entity.diagnosis.DiagnosisCode;
import com.halodoc.batavia.entity.diagnosis.SubLibrary;
import com.halodoc.batavia.entity.misool.catalog.ProviderLibraryLink;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/diagnosis/sub-libraries")
@RestController
@Slf4j
public class SubLibraryController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<SubLibrary> getSubLibraries(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "status", defaultValue = "") String status,
            @RequestParam (required = false, name = "name", defaultValue = "") String name) throws URISyntaxException {

        return misoolCatalogService.getSubLibraries(pageNo, perPage, name, status);
    }

    @GetMapping ("/{libraryId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    SubLibrary getSubLibrary(@PathVariable String libraryId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "sub_library_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolCatalogService.getSubLibrary(libraryId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    SubLibrary saveSubLibrary(@RequestBody SubLibrary subLibrary) throws URISyntaxException {
        return misoolCatalogService.saveSubLibrary(subLibrary);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_edit', 'restricted_write')")
    @PutMapping ("/{libraryId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    SubLibrary editSubLibrary(@PathVariable String libraryId, @RequestBody SubLibrary subLibrary) throws URISyntaxException {
        return misoolCatalogService.updateSubLibrary(libraryId, subLibrary);
    }

    // Diagnosis Codes
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_list', 'view_only')")
    @GetMapping ("{libraryId}/diagnosis-codes")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<DiagnosisCode> getDiagnosisCodes(@PathVariable String libraryId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage) throws URISyntaxException {

        return misoolCatalogService.getDiagnosisCodes(libraryId, pageNo, perPage);
    }

    @GetMapping ("{libraryId}/diagnosis-codes/{codeId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    DiagnosisCode getDiagnosisCode(@PathVariable String libraryId, @PathVariable String codeId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "sub_library_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolCatalogService.getDiagnosisCode(libraryId, codeId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_add', 'restricted_write')")
    @PostMapping ("{libraryId}/diagnosis-codes")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    DiagnosisCode saveDiagnosisCode(@PathVariable String libraryId, @RequestBody DiagnosisCode diagnosisCode) throws URISyntaxException {
        return misoolCatalogService.addDiagnosisCode(libraryId, diagnosisCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_edit', 'restricted_write')")
    @DeleteMapping ("{libraryId}/diagnosis-codes/{codeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void removeDiagnosisCode(@PathVariable String libraryId, @PathVariable String codeId) throws URISyntaxException {
        misoolCatalogService.removeDiagnosisCode(libraryId, codeId);
    }

    @GetMapping ("provider-sublibraries/{providerId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ProviderLibraryLink getProviderLibraryLink(@PathVariable String providerId,
                                               @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "sub_library_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "analyst_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "insurance_provider_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolCatalogService.getProviderLibraryLink(providerId, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_add', 'restricted_write')")
    @PostMapping ("provider-sublibraries")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    ProviderLibraryLink saveProviderLibraryLink(@RequestBody ProviderLibraryLink providerLibraryLink,
                                                @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) throws URISyntaxException {

        return misoolCatalogService.addProviderLibraryLink(providerLibraryLink, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_library_edit', 'restricted_write')")
    @PutMapping ("provider-sublibraries/{providerId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    ProviderLibraryLink editProviderLibraryLink(@PathVariable String providerId, @RequestBody ProviderLibraryLink providerLibraryLink,
                                                @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType)
            throws URISyntaxException {
        return misoolCatalogService.updateProviderLibraryLink(providerId, providerLibraryLink, providerType);
    }

}
