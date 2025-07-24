package com.halodoc.batavia.controller.api.insurance.claims.auto_adjudication;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsAutoAdjudicationService;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping("api/v1/claims/auto-adjudication")

public class ClaimAutoAdjudicationApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolClaimsAutoAdjudicationService misoolClaimsAutoAdjudicationService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("whitelisted-doctors")
    PaginatedResult<Map> getWhitelistedDoctors(@RequestParam(name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "auto_rejection_status", defaultValue = "") String autoRejectionStatus,
            @RequestParam(required = false, name = "auto_approval_status", defaultValue = "") String autoApprovalStatus)
            throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolClaimsAutoAdjudicationService.getWhitelistedDoctors(perPage, pageNo, autoRejectionStatus,
                autoApprovalStatus);
    }

    @GetMapping("approved-icd10")
    PaginatedResult<Map> getApprovedDiagnosisCodes(@RequestParam(name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "auto_rejection_status", defaultValue = "") String autoRejectionStatus,
            @RequestParam(required = false, name = "auto_approval_status", defaultValue = "") String autoApprovalStatus,
            @RequestParam(required = false, name = "primary_diagnostic_code", defaultValue = "") String primaryDiagnosticCode)
            throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
                
        return misoolClaimsAutoAdjudicationService.getApprovedDiagnosisCodes(perPage, pageNo, autoRejectionStatus,
                autoApprovalStatus,
                primaryDiagnosticCode);
    }
}
