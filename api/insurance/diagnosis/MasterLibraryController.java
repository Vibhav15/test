package com.halodoc.batavia.controller.api.insurance.diagnosis;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.entity.diagnosis.*;
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
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.DiagnosisService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/diagnosis/master-libraries")
@RestController
@Slf4j
public class MasterLibraryController extends HalodocBaseApiController {
    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<MasterLibrary> getMasterLibraries(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage) throws URISyntaxException {

        return diagnosisService.getMasterLibraries(pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_view', 'view_only')")
    @GetMapping ("{libraryId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    MasterLibrary getMasterLibrary(@PathVariable String libraryId) throws URISyntaxException {

        return diagnosisService.getMasterLibrary(libraryId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    MasterLibrary saveMasterLibrary(@RequestBody MasterLibrary masterLibrary) throws URISyntaxException {
        return diagnosisService.saveMasterLibrary(masterLibrary);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_edit', 'restricted_write')")
    @PutMapping ("{libraryId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    MasterLibrary updateMasterLibrary(@PathVariable String libraryId, @RequestBody MasterLibrary masterLibrary) throws URISyntaxException {
        return diagnosisService.updateMasterLibrary(libraryId, masterLibrary);
    }

    // Diagnosis Codes
    @PutMapping ("diagnosis-codes")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    List<DiagnosisCode> multiGetDiagnosisCodes(@RequestParam (required = false, name = "use_code", defaultValue = "false") Boolean useCode, @RequestBody MultiGetDiagnosisCodeRequest codeIdList) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "master_library_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "analyst_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "claim_reimbursement_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "package_condition", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return diagnosisService.multiGetDiagnosisCodes(codeIdList, useCode);
    }

    @GetMapping ("{libraryId}/diagnosis-codes")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<DiagnosisCode> getDiagnosisCodes(@PathVariable String libraryId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "search_query", defaultValue = "") String searchQuery,
            @RequestParam (required = false, name = "status", defaultValue = "") String status,
                                                         @RequestParam (required = false, name = "attributes_query", defaultValue = "") String attributesQuery,
                                                         @RequestParam (required = false, name = "is_multiple_column_query", defaultValue = "false") Boolean isMultipleColumn
                                                     ) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "master_library_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "analyst_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return diagnosisService.getDiagnosisCodes(libraryId, pageNo, perPage, searchQuery, attributesQuery, isMultipleColumn, status);
    }

    @PutMapping ("{libraryId}/diagnosis-codes")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<DiagnosisCode> getValidatedDiagnosisCodes(@PathVariable String libraryId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "search_query", defaultValue = "") String searchQuery,
            @RequestParam (required = false, name = "status", defaultValue = "") String status,
            @RequestBody DiagnosisCodeValidationRequest searchRequest,
                                                              @RequestParam (required = false, name = "attributes_query", defaultValue = "") String attributesQuery,
                                                              @RequestParam (required = false, name = "is_multiple_column_query", defaultValue = "false") Boolean isMultipleColumn) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "master_library_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "analyst_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return diagnosisService.getValidatedDiagnosisCodes(libraryId, pageNo, perPage, searchQuery, status, searchRequest, attributesQuery, isMultipleColumn);
    }

    @GetMapping ("{libraryId}/diagnosis-codes/{codeId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    DiagnosisCode getDiagnosisCode(@PathVariable String libraryId, @PathVariable String codeId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "master_library_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "analyst_claims_view", "view_only") && !authorizationService.isAuthorized("insurance", "supervisor_claims_view",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return diagnosisService.getDiagnosisCode(libraryId, codeId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_add', 'restricted_write')")
    @PostMapping ("{libraryId}/diagnosis-codes")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    DiagnosisCode saveDiagnosisCode(@PathVariable String libraryId, @RequestBody DiagnosisCode diagnosisCode) throws URISyntaxException {
        return diagnosisService.saveDiagnosisCode(libraryId, diagnosisCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_edit', 'restricted_write')")
    @PutMapping ("{libraryId}/diagnosis-codes/{codeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    DiagnosisCode updateDiagnosisCode(@PathVariable String libraryId, @PathVariable String codeId, @RequestBody DiagnosisCode diagnosisCode)
            throws URISyntaxException {
        return diagnosisService.updateDiagnosisCode(libraryId, codeId, diagnosisCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_add', 'restricted_write')")
    @PostMapping ("{libraryId}/codes")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    AtcCode saveAtcCode(@PathVariable String libraryId, @RequestBody AtcCode atcCode, @RequestParam String library_type) throws URISyntaxException {
        return diagnosisService.saveAtcCode(libraryId, atcCode, library_type);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_edit', 'restricted_write')")
    @PutMapping ("{libraryId}/codes/{codeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    AtcCode updateAtcCode(@PathVariable String libraryId, @PathVariable String codeId, @RequestBody AtcCode atcCode)
            throws URISyntaxException {
        return diagnosisService.updateAtcCode(libraryId, codeId, atcCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','master_library_list', 'view_only')")
    @GetMapping ("{libraryId}/generate-xlsx")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Icd9CodeDownloadResponse getXlsxDocuments(@PathVariable String libraryId) throws URISyntaxException {
        return diagnosisService.generateXlsx(libraryId);
    }

}
