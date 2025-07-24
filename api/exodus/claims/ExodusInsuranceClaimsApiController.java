package com.halodoc.batavia.controller.api.exodus.claims;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import com.halodoc.batavia.exception.custom.BadRequestExpectedException;
import com.halodoc.batavia.exception.custom.NotFoundExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.HospitalProvider;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.entity.configuration.TpaOcrConfiguration;
import com.halodoc.batavia.entity.exodus.claims.AssignClaimAnalystRequest;
import com.halodoc.batavia.entity.exodus.claims.BatchDocumentRequest;
import com.halodoc.batavia.entity.exodus.claims.BenifitPlan;
import com.halodoc.batavia.entity.exodus.claims.ClaimAlerts;
import com.halodoc.batavia.entity.exodus.claims.ClaimBatchDetails;
import com.halodoc.batavia.entity.exodus.claims.ClaimDocumentRequest;
import com.halodoc.batavia.entity.exodus.claims.ClaimPaymentRemark;
import com.halodoc.batavia.entity.exodus.claims.ClaimReimbursementBatchesSearch;
import com.halodoc.batavia.entity.exodus.claims.ClaimReimbursementDetails;
import com.halodoc.batavia.entity.exodus.claims.ClaimReimbursementServiceDetails;
import com.halodoc.batavia.entity.exodus.claims.ClaimReimbursementServiceList;
import com.halodoc.batavia.entity.exodus.claims.ClaimServiceDetailRequest;
import com.halodoc.batavia.entity.exodus.claims.ClaimServiceListResponse;
import com.halodoc.batavia.entity.exodus.claims.CreateBatchRequest;
import com.halodoc.batavia.entity.exodus.claims.MiscellaneousItem;
import com.halodoc.batavia.entity.exodus.claims.PublicSignedUrlResponse;
import com.halodoc.batavia.entity.exodus.claims.PublishReimbursement;
import com.halodoc.batavia.entity.exodus.claims.ReimbursementRemarks;
import com.halodoc.batavia.entity.exodus.claims.ReportHistoryReponse;
import com.halodoc.batavia.entity.exodus.claims.SaveBatchReq;
import com.halodoc.batavia.entity.exodus.claims.SaveBatchResponse;
import com.halodoc.batavia.entity.exodus.claims.SubmitBatchReq;
import com.halodoc.batavia.entity.exodus.claims.TpaAuditInfo;
import com.halodoc.batavia.entity.exodus.claims.TpaClaimReport;
import com.halodoc.batavia.entity.exodus.claims.TpaClaimStatusHistory;
import com.halodoc.batavia.entity.exodus.claims.ZeroTouchAlert;
import com.halodoc.batavia.entity.exodus.misool.catalog.BenefitResponseBody;
import com.halodoc.batavia.entity.exodus.misool.catalog.ClaimBatchUpdaterequest;
import com.halodoc.batavia.entity.exodus.misool.catalog.MemberListResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.MemberPlanResponse;
import com.halodoc.batavia.entity.misool.claims.ValidateReferralClaim;
import com.halodoc.batavia.entity.misool.claims.ValidateReferralClaimResponse;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.ImageUploadService;
import com.halodoc.batavia.service.TimorCMSService;
import com.halodoc.batavia.service.bintan.HospitalProviderLocationService;
import com.halodoc.batavia.service.bintan.ProviderLocationService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import com.halodoc.batavia.service.exodus.tpa_benefit.ExodusTPABenefitService;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTPAClaimService;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsService;
import com.halodoc.config.ConfigClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/claim-reimbursement")
public class ExodusInsuranceClaimsApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @Autowired
    private ExodusTPAClaimService exodusTPAClaimService;

    @Autowired
    private HospitalProviderLocationService hospitalProviderLocationService;

    @Autowired
    private ExodusTPABenefitService exodusTPABenefitService;

    @Autowired
    private TimorCMSService timorCMSService;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private ProviderLocationService providerLocationService;

    @Autowired
    private MisoolClaimsService misoolClaimsService;

    @Autowired
    private ConfigClient<BataviaAppConfiguration> bataviaAppConfig;

    @GetMapping ("/configs")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    public TpaOcrConfiguration getOcrConfig() {
        return bataviaAppConfig.getAppConfig().getTpaOcrConfiguration();
    }

    /**************************************** Claims: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @PutMapping ("/tpa-claim/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> searchClaimReimbursements(@RequestBody Map searchClaims) throws URISyntaxException {
        return exodusTPAClaimService.searchClaimReimbursements(searchClaims);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createClaim(@RequestParam (required = false) String claimType, @RequestBody Map reimbursement) throws URISyntaxException {
        return exodusTPAClaimService.createClaim(claimType, reimbursement);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/details")
    @AuditedAccess(eventType = EventType.exodus_claim_reimbursement_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.exodus_claim_reimbursement)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimReimbursementDetails getClaimReimbursementDetails(@PathVariable @AuditedEntityId String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getClaimReimbursementDetails(claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/publish")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimReimbursementDetails publishReimbursement(@PathVariable String claimId, @RequestParam (required = false) String claimType,
            @RequestParam (required = false) String claimAction, @RequestBody PublishReimbursement reimbursement) throws URISyntaxException {
        return exodusTPAClaimService.publishReimbursement(claimId, reimbursement, claimType, claimAction);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/save-details")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimReimbursementDetails saveReimbursementDetails(@PathVariable String claimId, @RequestParam (required = false) String claimType,
            @RequestParam (required = false) String claimAction, @RequestBody Map reimbursement) throws URISyntaxException {
        try {
            return exodusTPAClaimService.saveReimbursementDetails(claimId, claimType, claimAction, reimbursement);
        } catch (HalodocWebException ex) {
            if (ex.getStatusCode() == HttpStatus.EXPECTATION_FAILED.value()) {
                throw new BadRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/start-analyst-time")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map saveStartAnalystTime(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.saveStartAnalystTime(claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @PutMapping ("/{claimId}/reverse")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimReimbursementDetails updateClaimReverseDetails(@PathVariable String claimId, @RequestParam (required = false) String claimType,
            @RequestBody Map reqBody) throws URISyntaxException {
        return exodusTPAClaimService.updateClaimReverseDetails(claimId, claimType, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @DeleteMapping ("/{claimId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteClaimReimbursement(@PathVariable String claimId) throws URISyntaxException {
        exodusTPAClaimService.deleteClaimReimbursement(claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/coverage-list")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getClaimCoverageList() throws URISyntaxException {
        return exodusTPAClaimService.getClaimCoverageList();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/alerts")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimAlerts[] getClaimAlertMessages(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getClaimAlertMessages(claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/zero-touch/status")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ZeroTouchAlert getZeroTouchAlerts(@PathVariable String claimId) throws URISyntaxException {
        try {
            return exodusTPAClaimService.getZeroTouchAlerts(claimId);
        } catch (HalodocWebException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new NotFoundExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @PutMapping ("/sub-case-category")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List searchSubCaseCategory(@RequestBody List<String> externalId) throws URISyntaxException {
        log.info("********** Inside ExodusInsuranceClaimsApiController.searchSubCaseCategory ***************");
        log.info("*********** Request is sent with request body: " + externalId);
        return exodusTPAClaimService.searchSubCaseCategory(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/{claimId}/diagnosis-code")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map saveDiagnosisCode(@PathVariable String claimId, @RequestBody Map reqBody) throws URISyntaxException {
        return exodusTPAClaimService.saveDiagnosisCode(claimId, reqBody);
    }
    /**************************************** Claims: END ****************************************/
    /**************************************** Claim Documents: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/{claimId}/documents")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map uploadClaimDocument(@PathVariable String claimId, @RequestParam (required = true, name = "file_type") String fileType,
            @RequestParam (required = false, name = "insurance_provider_location_id") String insuranceProviderLocationId,
            @RequestBody ClaimDocumentRequest claimDocumentRequest) throws URISyntaxException {
        return exodusTPAClaimService.uploadClaimDocument(claimId, claimDocumentRequest, fileType, insuranceProviderLocationId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/documents")
    @AuditedAccess(eventType = EventType.exodus_claim_reimbursement_documents_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.exodus_claim_reimbursement)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getClaimDocuments(@PathVariable @AuditedEntityId String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getClaimDocuments(claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @DeleteMapping ("/{claimId}/documents/{documentId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteClaimDocument(@PathVariable String claimId, @PathVariable String documentId) {
        exodusTPAClaimService.deleteClaimDocument(claimId, documentId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/documents/{documentId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateDocuments(@PathVariable String claimId, @PathVariable String documentId, @RequestBody Map documents) throws URISyntaxException {
        return exodusTPAClaimService.updateDocument(documents, claimId, documentId);
    }

    /**************************************** Claim Documents: END ****************************************/
    /**************************************** Claim Remarks: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/{claimId}/remarks")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ReimbursementRemarks createRemarks(@PathVariable String claimId, @RequestBody ReimbursementRemarks remarks) throws URISyntaxException {
        return exodusTPAClaimService.createRemarks(remarks, claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/remarks")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getRemarks(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getRemarks(claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/remarks/{remarkId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ReimbursementRemarks updateRemarks(@PathVariable String claimId, @PathVariable String remarkId, @RequestBody ReimbursementRemarks remarks)
    throws URISyntaxException {
        return exodusTPAClaimService.updateRemarks(remarks, claimId, remarkId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @DeleteMapping ("/{claimId}/remarks/{remarkId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteRemarks(@PathVariable String claimId, @PathVariable String remarkId) {
        exodusTPAClaimService.deleteRemarks(claimId, remarkId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/payment-remarks")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ClaimPaymentRemark> getClaimPaymentRemarks(@PathVariable String claimId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {
        return exodusTPAClaimService.getClaimPaymentRemarks(claimId, pageNo, perPage);
    }
    /**************************************** Claim Remarks: END ****************************************/
    /**************************************** Member: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/member")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<MemberListResponse> searchMembers(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false, name = "insurance_provider_id") String insuranceProviderId,
            @RequestParam (required = false) String member_id, @RequestParam (required = false) String policy_number,
            @RequestParam (required = false) String phone_number, @RequestParam (required = false) String e_card_number,
            @RequestParam (required = false) String dob, @RequestParam (required = false) Boolean include_inactive) throws URISyntaxException {
        return exodusMisoolCatalogService.searchMembers(pageNo, perPage, insuranceProviderId, name, member_id, phone_number, policy_number,
                e_card_number, dob, include_inactive);
    }

    @GetMapping ("/member/{memberExternalId}/details")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getNewMemberDetails(@PathVariable String memberExternalId) throws URISyntaxException {
        return exodusMisoolCatalogService.getNewMemberDetails(memberExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/member/{memberId}/histories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<List> memberReimbursementHistory(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @PathVariable String memberId,
            @RequestParam (required = false, name = "member_relationship") String membershipType,
            @RequestParam (required = false, name = "claim_status") String status,
            @RequestParam (required = false, name = "primary_diagnosis_code") String primaryCode,
            @RequestParam (required = false, name = "admission_date") String admissionDate,
            @RequestParam (required = false, name = "discharge_date") String dischargeDate,
            @RequestParam (required = false, name = "coverage_types") String coverageTypes,
            @RequestParam (required = false, name = "insurance_provider_id") String insuranceProviderID) throws URISyntaxException {
        return exodusTPAClaimService.getReimbursementMemberHistory(pageNo, perPage, memberId, membershipType, status, primaryCode, admissionDate,
                dischargeDate, coverageTypes, insuranceProviderID);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/member/{memberExternalId}/plans")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<MemberPlanResponse> memberPlan(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @PathVariable String memberExternalId,
            @RequestParam (required = false, name = "member_id") String memberId,
            @RequestParam (required = false, name = "insurance_provider_id") String providerId,
            @RequestParam (required = false, name = "policy_number") String policyNumber,
            @RequestParam (required = false, name = "product_id") String productId,
            @RequestParam (required = false, name = "claim_number", defaultValue = "") String claimNumber,
            @RequestParam (required = false, name = "coverage_code") String coverageCode,
            @RequestParam (required = false, name = "fetch_benefit_plan", defaultValue = "true") boolean fetchBenefitPlan) throws URISyntaxException {
        return exodusMisoolCatalogService.getMemberPlans(pageNo, perPage, memberExternalId, memberId, providerId, policyNumber, productId,
                claimNumber, coverageCode, fetchBenefitPlan);
    }

    @GetMapping ("/{memberExternalId}/member/plans")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<MemberPlanResponse> memberCmtPlans(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @PathVariable String memberExternalId,
            @RequestParam (required = false, name = "coverage_code") String coverageCode,
            @RequestParam (required = false, name = "status", defaultValue = "active") String status,
            @RequestParam (required = false, name = "include_pre_post_details") boolean includePrePostDetails) throws URISyntaxException {
        return exodusMisoolCatalogService.memberCmtPlans(memberExternalId, pageNo, perPage, coverageCode, status, includePrePostDetails);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/member/plan/{planId}/benefits")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<BenefitResponseBody> benefitPlan(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @PathVariable String planId,
            @RequestParam (required = false, name = "member_id") String memberId,
            @RequestParam (required = false, name = "insurance_provider_id") String providerId,
            @RequestParam (required = false, name = "policy_number") String policyNumber,
            @RequestParam (required = false, name = "product_id") String productId,
            @RequestParam (required = false, name = "coverage_code") String coverageCode,
            @RequestParam (required = false, name = "admission_date") String admissionDate,
            @RequestParam (required = false, name = "disability_no") String disabilityNo,
            @RequestParam (required = false, name = "claim_number", defaultValue = "") String claimNumber,
            @RequestParam (name = "plan_codes") final Set<String> planCodes, @RequestParam (name = "member_external_id") String memberExternalId)
    throws URISyntaxException {
        return exodusMisoolCatalogService.getPlanBenefits(pageNo, perPage, planId, memberId, policyNumber, productId, planCodes, coverageCode,
                claimNumber, providerId, memberExternalId, disabilityNo, admissionDate);
    }

    /**************************************** Member: END ****************************************/
    /**************************************** Healthcare/Poly : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/departments")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<List> getPolyDepartments(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String status,
            @RequestParam (required = false) String name) throws URISyntaxException {
        return hospitalProviderLocationService.getAllDepartments(pageNo, perPage, status, name);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("{departmentId}/department-detail")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getDepartmentDetails(@PathVariable String departmentId) throws URISyntaxException {
        return hospitalProviderLocationService.getDepartmentDetails(departmentId);
    }

    /**************************************** Healthcare/Poly : END ****************************************/
    /**************************************** Benefit Simulator : START ****************************************/
    @PutMapping ("/evaluate-benefits")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map evaluateBenefits(@RequestBody Map req,
            @RequestParam (required = false, name = "transaction_type", defaultValue = DEFAULT_STRING) String transactionType)
            throws URISyntaxException {
        return exodusTPABenefitService.evaluateBenefits(req, transactionType);
    }

    @PutMapping ("/calculate-constanta")
    Map calculateConstanta(@RequestBody Map req)
            throws URISyntaxException {
        return exodusTPABenefitService.calculateConstanta(req);
    }

    /**************************************** Benefit Simulator : END ****************************************/
    /**************************************** Claim Batches : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/claim-batches")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ClaimReimbursementBatchesSearch> searchClaimReimbursementBatches(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "insurance_provider_id") String insuranceProviderExternalId,
            @RequestParam (required = false, name = "entity_external_id") String entityExternalId,
            @RequestParam (required = false, name = "batch_numbers") String batchNumbers,
            @RequestParam (required = false, name = "batch_start_date") String batchStartDate,
            @RequestParam (required = false, name = "batch_end_date") String batchEndDate,
            @RequestParam (required = false, name = "status", defaultValue = "all") String status,
            @RequestParam (required = false, name = "supervisor") String supervisor,
            @RequestParam (required = false, name = "assigned_to") String assignedTo,
            @RequestParam (required = true, name = "claim_batch_type") String claimBatchType) throws URISyntaxException {
        return exodusTPAClaimService.searchClaimReimbursementsBatches(pageNo, perPage, insuranceProviderExternalId, entityExternalId, batchNumbers,
                batchStartDate, batchEndDate, status, supervisor, assignedTo, claimBatchType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/batch-number")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map searchBatchNumber(@RequestParam (required = false, name = "batch_number") String batchNumber,
            @RequestParam (required = true, name = "claim_batch_type") String claimBatchType) throws URISyntaxException {
        return exodusTPAClaimService.searchBatchNumber(batchNumber, claimBatchType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/claim-batches/create")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    Map createClaimBatches(@RequestBody CreateBatchRequest claimIdLists) throws URISyntaxException {
        return exodusTPAClaimService.createClaimBatches(claimIdLists);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/claim-batches/{claimBatchesId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimBatchDetails updateClaimBatches(@PathVariable String claimBatchesId, @RequestBody CreateBatchRequest claimIdLists,
            @RequestParam (required = false, name = "type") String type) throws URISyntaxException {
        return exodusTPAClaimService.updateClaimBatches(claimIdLists, type, claimBatchesId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/claim-batches/{claimBatchesId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimBatchDetails getClaimBatchDetails(@PathVariable String claimBatchesId) throws URISyntaxException {
        return exodusTPAClaimService.getClaimBatchDetails(claimBatchesId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/claim-batches/{claimBatchesId}/analyst")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimReimbursementBatchesSearch assignBatchToAnalyst(@PathVariable String claimBatchesId,
            @RequestBody AssignClaimAnalystRequest assignBatchRequest) throws URISyntaxException {
        return exodusTPAClaimService.assignBatchesToAnalyst(assignBatchRequest, claimBatchesId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/claim-batches/{claimBatchesId}/claims/analyst")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimReimbursementBatchesSearch assignClaimsToAnalyst(@PathVariable String claimBatchesId,
            @RequestBody AssignClaimAnalystRequest assignClaimsRequest) throws URISyntaxException {
        return exodusTPAClaimService.assignClaimsToAnalyst(assignClaimsRequest, claimBatchesId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/claim-batches/{claimBatchesId}/claims")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ClaimReimbursementBatchesSearch> searchClaimsBatchList(@PathVariable String claimBatchesId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "claim_number") String claimNumber,
            @RequestParam (required = false, name = "regional_office") String regionalOffice,
            @RequestParam (required = false, name = "member_name_key") String memberName,
            @RequestParam (required = false, name = "member_id") String memberId,
            @RequestParam (required = false, name = "coverage_type") String coverageType,
            @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "admission_date_start") String admissionDateStart,
            @RequestParam (required = false, name = "admission_date_end") String admissionDateEnd,
            @RequestParam (required = false, name = "assigned_to") String assignedTo) throws URISyntaxException {
        return exodusTPAClaimService.searchClaimsBatchList(pageNo, perPage, claimBatchesId, claimNumber, regionalOffice, memberName, memberId,
                coverageType, status, admissionDateStart, admissionDateEnd, assignedTo);
    }

    /**************************************** Claim Batches : END ****************************************/
    /**************************************** Claim Adjudication : START ****************************************/
    @GetMapping ("/miscellaneous")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<MiscellaneousItem> getMiscellaneousItemList(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = DEFAULT_STRING) String name,
            @RequestParam (required = false, name = "status", defaultValue = "all") String status,
            @RequestParam (required = false, name = "language") String language) throws URISyntaxException {
        return exodusMisoolCatalogService.getMiscellanousItemList(pageNo, perPage, name, status, language);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/service-details/{claimExternalId}")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ClaimReimbursementServiceList[] createClaimReimbursementService(@PathVariable String claimExternalId,
            @RequestBody ClaimReimbursementServiceDetails claimReimbursementServiceRequest) throws URISyntaxException {
        return exodusTPAClaimService.createClaimReimbursementService(claimExternalId, claimReimbursementServiceRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/service-detail-list/{claimExternalId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimServiceListResponse getServiceDetailsList(@PathVariable String claimExternalId) throws URISyntaxException {
        return exodusTPAClaimService.getServiceDetailsList(claimExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/service-details/{claimServiceId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateServiceDetail(@PathVariable String claimId, @PathVariable String claimServiceId, @RequestBody Map claimServiceRequest)
            throws URISyntaxException {
        return exodusTPAClaimService.updateServiceDetail(claimId, claimServiceId, claimServiceRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/{claimId}/service-details")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateServiceDetailAfterBenefitEvaluation(@PathVariable String claimId, @RequestBody Map claimServiceRequest) {
        try {
            exodusTPAClaimService.updateServiceDetailAfterBenefitEvaluation(claimId, claimServiceRequest);
        } catch (HalodocWebException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
                throw new BadRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/{claimId}/service-details/save-service-details")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateServiceDetailAfterBenefitEvaluationV2(@PathVariable String claimId, @RequestBody ClaimServiceDetailRequest claimServiceRequest)
            throws URISyntaxException {
        return exodusTPAClaimService.updateServiceDetailAfterBenefitEvaluationV2(claimId, claimServiceRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @DeleteMapping ("/{claimId}/service-details/{claimServiceId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteClaimReimbursementService(@PathVariable String claimId, @PathVariable String claimServiceId) throws URISyntaxException {
        exodusTPAClaimService.deleteClaimReimbursementService(claimId, claimServiceId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/claim-rebatch/{claimId}/submit")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimBatchDetails submitBatch(@PathVariable String claimId, @RequestParam (name = "submission_type", required = true) String userType,
            @RequestBody SubmitBatchReq req) throws URISyntaxException {
        return exodusTPAClaimService.submitBatch(claimId, userType, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/claim-batches/upsert/{batchId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimBatchDetails reassignClaimToBatch(@PathVariable String batchId, @RequestBody ClaimBatchUpdaterequest claimBatchUpdaterequest)
    throws URISyntaxException {
        return exodusTPAClaimService.reassignClaimToBatch(claimBatchUpdaterequest, batchId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/products/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult getProductsList(@RequestParam (required = false, name = "search_text") String searchText) throws URISyntaxException {
        return timorCMSService.getProductsList(searchText);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/service-details/save-multiple")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateMultipleServiceDetail(@PathVariable String claimId, @RequestBody List<Map> claimServiceRequest) throws URISyntaxException {
        return exodusTPAClaimService.updateMultipleServiceDetail(claimServiceRequest, claimId);
    }

    /**************************************** Claim Adjudication : END ****************************************/
    /**************************************** Claim REPORT : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','tpa_claim_report_history', 'view_only')")
    @GetMapping ("/claim-report-history")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<TpaClaimReport> getTpaClaimReports(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "provider_id") String providerId,
            @RequestParam (required = false, name = "created_at") Date createdDate) throws URISyntaxException {
        return exodusTPAClaimService.getTpaClaimReports(pageNo, perPage, providerId, createdDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','tpa_claim_report_add', 'restricted_write')")
    @PostMapping ("/generate-claim-report")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    TpaClaimReport generateClaimReport(@RequestBody Map claimReport) throws URISyntaxException {
        return exodusTPAClaimService.generateClaimReport(claimReport);
    }

    /**************************************** Claim REPORT : END ****************************************/
    /**************************************** Common API's : START ****************************************/
    @GetMapping ("/{documentId}/signed-public-url")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    PublicSignedUrlResponse getSignedPublicUrl(@PathVariable String documentId) throws URISyntaxException {
        return imageUploadService.getSignedPublicUrl(documentId);
    }

    /**************************************** Common API's : END ****************************************/
    /**************************************** Audit Info : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/audit-info")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    TpaAuditInfo getTpaAuditInfo(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getTpaAuditInfo(claimId);
    }
    /**************************************** Audit Info : END ****************************************/
    /**************************************** Claim Status History : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/status-histories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<TpaClaimStatusHistory> getTpaClaimStatusHistory(@PathVariable String claimId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {
        return exodusTPAClaimService.getTpaClaimStatusHistory(pageNo, perPage, claimId);
    }
    /**************************************** Claim Status History : END ****************************************/
    /**************************************** Hospital Provider Location : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/provider/locations/{locationExternalId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    HospitalProvider getInsHospitalProviderLocations(@PathVariable String locationExternalId) throws URISyntaxException {
        return providerLocationService.getInsHospitalProviderLocations(locationExternalId);
    }

    /****************************************Hospital Provider Location : END ****************************************/
    /**************************************** Claim Referral : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @PutMapping ("/member/{memberId}/validate-referral")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ValidateReferralClaimResponse validateReferral(@PathVariable String memberId,
            @RequestParam (required = false, name = "insurance_provider_id") String insProviderID, @RequestBody ValidateReferralClaim referralClaim)
            throws URISyntaxException {
        return misoolClaimsService.validateReferralClaim(memberId, insProviderID, referralClaim);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/member/{memberId}/plan-details")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getMemberPlanDetails(@PathVariable String memberId, @RequestParam (required = false, name = "coverage_code") String coverageCode,
            @RequestParam (required = false, name = "insurance_provider_id") String insProviderID) throws URISyntaxException {
        return exodusMisoolCatalogService.getMemberPlanDetails(memberId, coverageCode, insProviderID);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/member/{memberId}/benefits")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getMemberBenefits(@PathVariable String memberId, @RequestParam (required = false, name = "coverage_code") String coverageCode,
            @RequestParam (required = false, name = "search_text") String searchText,
            @RequestParam (required = false, name = "transaction_type") String transactionType) throws URISyntaxException {
        return exodusMisoolCatalogService.getMemberBenefits(memberId, coverageCode, searchText, transactionType);
    }

    /**************************************** Claim Referral : END ****************************************/
    /**********************************Cashless Claim : START *********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_cashless_add', 'restricted_write')")
    @PostMapping ("/batch/{batchId}/documents")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map uploadBatchDocument(@PathVariable String batchId, @RequestBody BatchDocumentRequest batchDocumentRequest) throws URISyntaxException {
        return exodusTPAClaimService.uploadBatchDocument(batchId, batchDocumentRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_cashless_add', 'view_only')")
    @GetMapping ("/batch/{batchId}/documents")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<List> getBatchDocuments(@PathVariable String batchId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {
        return exodusTPAClaimService.getBatchDocuments(batchId, perPage, pageNo);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_cashless_add', 'restricted_write')")
    @PutMapping ("batch/{batchId}/save")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    SaveBatchResponse saveBatch(@PathVariable String batchId, @RequestBody SaveBatchReq saveBatchReq) throws URISyntaxException {
        return exodusTPAClaimService.saveBatch(batchId, saveBatchReq);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_cashless_list', 'view_only')")
    @PutMapping ("/members/benefit-limits")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<Map> getMemberBenefitLimit(@RequestBody BenifitPlan req) throws URISyntaxException {
        return exodusTPABenefitService.getBenefitLimitV2(req);
    }

    @PatchMapping ("/disabilities/{disabilityNumber}/claims/{claimNumber}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateDisability(@PathVariable String disabilityNumber, @PathVariable String claimNumber) throws URISyntaxException {
        exodusTPAClaimService.updateDisability(disabilityNumber, claimNumber);
    }

    @GetMapping ("/claims/{claimId}/service-details/summary")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getServiceDetailSummary(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getServiceDetailsSummary(claimId);
    }

    @GetMapping ("/claims/{claimId}/room-details/summary")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getRoomDetailSummary(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getRoomDetailsSummary(claimId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_cashless_list', 'restricted_write')")
    @PatchMapping ("/{claimId}/change-coverage")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map changeCoverage(@PathVariable String claimId, @RequestBody Map reqBody) throws URISyntaxException {
        return exodusTPAClaimService.changeCoverage(claimId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/claims/{claimId}/itemized-service-details")
    @AuditedAccess(eventType = EventType.exodus_claim_reimbursement_service_details_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.exodus_claim_reimbursement)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getItemizedServiceDetails(@PathVariable @AuditedEntityId String claimId, @RequestParam (required = false) String category,
            @RequestParam (required = false) String mapped) throws URISyntaxException {
        return exodusTPAClaimService.getItemizedServiceDetails(claimId, category, mapped);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/claims/{claimId}/itemized-service-details")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    List updateItemizedServiceDetails(@PathVariable String claimId, @RequestBody Map itemizedItem) throws URISyntaxException {
        return exodusTPAClaimService.updateItemizedServiceDetails(claimId, itemizedItem);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PutMapping ("/claims/{claimId}/itemized-service-details/save")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    List saveItemizedServiceDetails(@PathVariable String claimId, @RequestBody Map itemizedItem) throws URISyntaxException {
        return exodusTPAClaimService.saveItemizedServiceDetails(claimId, itemizedItem);
    }

    /**************************************** GENERALI REPORTING : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/claims/{claimId}/resend-report")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ReportHistoryReponse resendGeneraliReport(@PathVariable (required = true) String claimId, @RequestBody Map reqBody) throws URISyntaxException {
        return exodusTPAClaimService.resendGeneraliReport(claimId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/claims/{claimId}/report-history")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> getGeneraliReportHistory(@PathVariable String claimId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {
        return exodusTPAClaimService.getGeneraliReportHistory(claimId, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/claims/{claimId}/manual-report")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map setManuallyGeneraliReport(@PathVariable (required = true) String claimId, @RequestBody Map reqBod) throws URISyntaxException {
        return exodusTPAClaimService.setManuallyGeneraliReport(claimId, reqBod);
    }

    /**************************************** GENERALI REPORTING : END ****************************************/
    @PutMapping ("/disabilities/auto-detect-disability")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List autoDetectDisability(@RequestBody Map reqBody) throws URISyntaxException {
        return exodusTPAClaimService.autoDetectDisability(reqBody);
    }

    @PutMapping ("/disabilities/get-post-discharge-disability")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getDisabilityInfo(@RequestBody Map reqBody, @RequestParam String disabilityNumber) throws URISyntaxException {
        return exodusTPAClaimService.getDisabilityInfo(reqBody, disabilityNumber);
    }

    @PutMapping ("/change-member/reimbursement")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map changeMemberForReimbursementClaim(@RequestBody Map reqBody) throws URISyntaxException {
        return exodusTPAClaimService.changeMemberForReimbursementClaim(reqBody);
    }

    @PutMapping ("/non-ppn-copay/{claimExternalId}/save-amount")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map nonPpnCopaySaveAmount(@PathVariable String claimExternalId, @RequestBody Map reqBody) throws URISyntaxException {
        return exodusTPAClaimService.nonPpnCopaySaveAmount(claimExternalId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @PutMapping ("/{memberExternalId}/non-ppn-info")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getNonPpnLimitInfo(@PathVariable String memberExternalId, @RequestBody Map reqBody,
            @RequestParam (required = false, name = "is_history_required", defaultValue = "false") Boolean isHistoryRequired,
            @RequestParam (required = false, name = "exclude_log", defaultValue = DEFAULT_STRING) String excludeLog)
            throws URISyntaxException {
        try {
            return exodusMisoolCatalogService.getNonPpnLimitInfo(memberExternalId, reqBody, isHistoryRequired, excludeLog);
        } catch (HalodocWebException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
                throw new BadRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{memberExternalId}/policy-period-list")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getPolicyPeriodList(@PathVariable String memberExternalId,
            @RequestParam (required = false, name = "policy_external_id", defaultValue = DEFAULT_STRING) String policyExternalId,
            @RequestParam (required = false, name = "no_of_policy_periods") Integer noOfPolicyPeriod) throws URISyntaxException {
        return exodusMisoolCatalogService.getPolicyPeriodList(memberExternalId, policyExternalId, noOfPolicyPeriod);

            }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_cashless_list', 'view_only')")
    @PutMapping ("/expire")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void expireClaim(@RequestBody Map reqObj) throws URISyntaxException {
        exodusTPAClaimService.expireClaim(reqObj);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_cashless_list', 'view_only')")
    @PutMapping ("/{claimId}/revise")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void reverseClaimStatus(@PathVariable (required = true) String claimId, @RequestBody Map reqObj) throws URISyntaxException {
        exodusTPAClaimService.reverseClaimStatus(claimId, reqObj);
    }

}
