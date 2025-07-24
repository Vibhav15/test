package com.halodoc.batavia.controller.api.insurance.diagnosis;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.diagnosis.DiagnosisCode;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.DiagnosisService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RequestMapping("api/v1/diagnosis")
@RestController
@Slf4j
public class DiagnosisApiController extends HalodocBaseApiController {
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private DiagnosisService diagnosisService;

    @GetMapping("{icd10Code}/description")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public String getDiagnosisCodeDescription(@PathVariable String icd10Code) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "membership_query", "view_only") && !authorizationService
                .isAuthorized("insurance", "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return diagnosisService.getDiagnosisCodeDescription(icd10Code);
    }

    @GetMapping("/recommended/diagnosis-codes")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<DiagnosisCode> getRecommendedDiagnosisCodes(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "search_query", defaultValue = "") String searchQuery) throws URISyntaxException {
        return diagnosisService.getRecommendedDiagnosisCodes(pageNo, perPage, searchQuery);
    }
}
