package com.halodoc.batavia.controller.api.insurance.claims;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.audit.sdk.Auditor;
import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import com.halodoc.batavia.entity.misool.claims.*;
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
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsAutoAdjudicationService;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/claims")
@RestController
@Slf4j

public class ClaimAdjudicationController extends HalodocBaseApiController {
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MisoolClaimsService misoolClaimsService;

    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @Autowired
    private MisoolClaimsAutoAdjudicationService misoolClaimsAutoAdjudicationService;

    @Autowired
    private Auditor auditor;

    @PutMapping
    PaginatedResult<Claim> searchClaims(@RequestBody ClaimSearchRequest claimSearchRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_list", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolClaimsService.searchClaims(claimSearchRequest);
    }

    @GetMapping ("{claimExternalId}")
    Claim getClaim(@PathVariable String claimExternalId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.insurance_claim_accessed, Action.get, ActorType.cc_user, EntityType.insurance_claim,
                claimExternalId, ChannelType.http);
        return misoolClaimsService.getClaim(claimExternalId);
    }

    @PutMapping ("{claimExternalId}")
    Claim updateClaim(@PathVariable String claimExternalId, @RequestParam (name = "userType", required = true) String userType,
            @RequestBody ClaimUpdateRequest req) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_edit", "restricted_write") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolClaimsService.updateClaim(claimExternalId, userType, req);
    }

    @PutMapping ("{claimExternalId}/void-claim")
    void voidClaimBenefit(@PathVariable String claimExternalId,
            @RequestBody VoidClaimRequest req) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_edit", "restricted_write") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        misoolClaimsService.voidClaim(claimExternalId, req);
    }

    @GetMapping ("{claimExternalId}/claim-remarks")
    ClaimRemark[] getClaimRemarks(@PathVariable String claimExternalId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolClaimsService.getClaimRemarks(claimExternalId);
    }

    @PostMapping ("{claimExternalId}/claim-remarks")
    void addClaimRemark(@PathVariable String claimExternalId, @RequestBody ClaimRemark claimRemark) throws URISyntaxException {
        log.info("create claim remark request:{} and claim remark type :{}  ", claimRemark.toString(), claimRemark.getRemarkType());
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_add", "restricted_write") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        misoolClaimsService.addClaimRemark(claimExternalId, claimRemark);
    }

    @GetMapping ("{claimExternalId}/claim-attributes")
    List getClaimAttributes(@PathVariable String claimExternalId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolClaimsService.getClaimAttributes(claimExternalId);
    }

    @PutMapping ("{claimExternalId}/claim-attributes")
    void updateClaimAttributes(@PathVariable String claimExternalId, @RequestBody List<Map> claimAttributes) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_add", "restricted_write") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        misoolClaimsService.updateClaimAttributes(claimExternalId, claimAttributes);
    }

    @GetMapping ("{claimExternalId}/claim-details")
    ClaimDetail[] getClaimDetails(@PathVariable String claimExternalId) {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolClaimsService.getClaimDetails(claimExternalId);
    }

    @GetMapping ("{claimExternalId}/claim-details/{claimDetailsExternalId}")
    ClaimDetail getClaimDetail(@PathVariable String claimExternalId, @PathVariable String claimDetailsExternalId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolClaimsService.getClaimDetail(claimExternalId, claimDetailsExternalId);
    }

    @GetMapping ("{claimExternalId}/claim-details/{claimDetailsExternalId}/initial")
    Object getInitialClaimDetail(@PathVariable String claimExternalId, @PathVariable String claimDetailsExternalId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolClaimsService.getInitialClaimDetails(claimExternalId, claimDetailsExternalId);
    }

    @PutMapping ("{claimExternalId}/claim-details/{claimDetailsExternalId}")
    void updateClaimDetails(@PathVariable String claimExternalId, @PathVariable String claimDetailsExternalId, @RequestBody ClaimDetail claimDetail)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_edit", "restricted_write") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        misoolClaimsService.updateClaimDetails(claimExternalId, claimDetailsExternalId, claimDetail);
    }

    @PutMapping ("exclusions/primary")
    PrimaryExclusionResponse getPrimaryExclusion(@RequestBody PrimaryExclusionRequest req) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolCatalogService.getPrimaryExclusion(req);
    }

    @PutMapping ("{claimExternalId}/diagnosis-code")
    void updateDiagnosisCodes(@PathVariable String claimExternalId, @RequestBody ClaimDiagnosisCodes claimDiagnosisCodes) {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_edit", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_edit", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        misoolClaimsService.updateDiagnosisCodes(claimExternalId, claimDiagnosisCodes);
    }

    @GetMapping ("{exclusionId}/diagnosis-codes/{codeId}")
    Object validateExclusionCode(@RequestParam (name = "member_id") String memberId, @PathVariable String exclusionId, @PathVariable String codeId)
            throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolCatalogService.validateExclusionCode(exclusionId, codeId, memberId);
    }

    @PutMapping("{claimExternalId}/referral_data/{entityId}")
    ClaimReferralCSVRecord updateClaimReferralRecord(@PathVariable String claimExternalId, @PathVariable String entityId) throws URISyntaxException {
        return misoolClaimsService.updateClaimReferralDetails(claimExternalId, entityId);
    }

    @GetMapping ("{claimExternalId}/adjudication/parameters")
    Map getClaimAutoAdjudicationRulesParameters(@PathVariable String claimExternalId)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService
                .isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolClaimsAutoAdjudicationService.getClaimAutoAdjudicationRulesParameters(claimExternalId);
    }

    /**************************************** Change Coverage: START ****************************************/

    @PreAuthorize("@authorizationService.isAuthorized('insurance','change_coverage_list', 'view_only')")
    @GetMapping ("{claimExternalId}/ip-benefits")
    IPBenefitList[] getIPBenefit(@PathVariable String claimExternalId) {

        return misoolClaimsService.getIPBenefit(claimExternalId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','change_coverage_list', 'view_only')")
    @PutMapping ("change-coverage-search")
    PaginatedResult<Claim> searchChangeCoverage(@RequestBody ClaimChangeCoverageRequest claimChangeCoverageRequest) throws URISyntaxException {

        return misoolClaimsService.searchChangeCoverage(claimChangeCoverageRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','change_coverage_list', 'restricted_write')")
    @PutMapping("{claimExternalId}/change-coverage")
    ChangeCoverageBenefitRequest updateChangeCoverage(@PathVariable String claimExternalId,
                                                      @RequestBody ChangeCoverageBenefitRequest changeCoverageBenefitRequest) throws URISyntaxException {
        return misoolClaimsService.updateChangeCoverage(claimExternalId, changeCoverageBenefitRequest);
    }


    /**************************************** Change Coverage: END ****************************************/
}
