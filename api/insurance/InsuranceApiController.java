package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bali.logan.Patient;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.customer.LinkMemberAdmin;
import com.halodoc.batavia.entity.customer.LinkPatient;
import com.halodoc.batavia.entity.customer.LinkPatientBody;
import com.halodoc.batavia.entity.customer.MembershipDetail;
import com.halodoc.batavia.entity.customer.MembershipDetailResponse;
import com.halodoc.batavia.entity.customer.PatientInfo;
import com.halodoc.batavia.entity.ingestion_remainingbalance.RemainingBalanceIngestionRequest;
import com.halodoc.batavia.entity.ingestion_remainingbalance.RemainingBalanceResponse;
import com.halodoc.batavia.entity.komodo.InsuranceProvider;
import com.halodoc.batavia.entity.komodo.InsuranceProviderWithValidations;
import com.halodoc.batavia.entity.komodo.ProviderCategory;
import com.halodoc.batavia.entity.komodo.ValidationCheck;
import com.halodoc.batavia.entity.misool.MemberGlobalLimitsRequest;
import com.halodoc.batavia.entity.misool.MemberGlobalLimitsResponse;
import com.halodoc.batavia.entity.misool.catalog.BenefitCategory;
import com.halodoc.batavia.entity.misool.catalog.ClaimReversal;
import com.halodoc.batavia.entity.misool.catalog.DiagnosisCodeObject;
import com.halodoc.batavia.entity.misool.catalog.DiagnosisCodeResponse;
import com.halodoc.batavia.entity.misool.catalog.DiagnosisExclusionIdList;
import com.halodoc.batavia.entity.misool.catalog.DiagnosisExclusionSearch;
import com.halodoc.batavia.entity.misool.catalog.InstanceLimitResponse;
import com.halodoc.batavia.entity.misool.catalog.InsuranceCampaign;
import com.halodoc.batavia.entity.misool.catalog.InsuranceDocument;
import com.halodoc.batavia.entity.misool.catalog.Member;
import com.halodoc.batavia.entity.misool.catalog.MemberInfo;
import com.halodoc.batavia.entity.misool.catalog.MemberSearch;
import com.halodoc.batavia.entity.misool.catalog.MembershipDashboardResponse;
import com.halodoc.batavia.entity.misool.catalog.MembershipDataRenewal;
import com.halodoc.batavia.entity.misool.catalog.MembershipIngestion;
import com.halodoc.batavia.entity.misool.catalog.MembershipIngestionStats;
import com.halodoc.batavia.entity.misool.catalog.MembershipRelinkingIngestFile;
import com.halodoc.batavia.entity.misool.catalog.MembershipRelinkingResponse;
import com.halodoc.batavia.entity.misool.catalog.MembershipResponse;
import com.halodoc.batavia.entity.misool.catalog.ModuleLimit;
import com.halodoc.batavia.entity.misool.catalog.Plan;
import com.halodoc.batavia.entity.misool.catalog.Policy;
import com.halodoc.batavia.entity.misool.catalog.PolicyOffering;
import com.halodoc.batavia.entity.misool.catalog.PolicyResponse;
import com.halodoc.batavia.entity.misool.catalog.PolicyTermsAndCondition;
import com.halodoc.batavia.entity.misool.catalog.PolicyUpdateRequest;
import com.halodoc.batavia.entity.misool.catalog.PreExistingCondition;
import com.halodoc.batavia.entity.misool.catalog.PreExistingConditionRequest;
import com.halodoc.batavia.entity.misool.catalog.Product;
import com.halodoc.batavia.entity.misool.catalog.SuspendMemberRequest;
import com.halodoc.batavia.entity.misool.catalog.SuspensionHistory;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bali.logan.BaliLoganService;
import com.halodoc.batavia.service.ingestion_remainingbalance.RemainingBalanceIngestionService;
import com.halodoc.batavia.service.membership.MembershipService;
import com.halodoc.batavia.service.misool.MisoolBenefitsV2Service;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance")
@RestController
@Slf4j
public class InsuranceApiController extends HalodocBaseApiController {
    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MisoolBenefitsV2Service misoolBenefitsV2Service;

    @Autowired
    private MisoolClaimsService misoolClaimsService;

    @Autowired
    private BaliLoganService baliLoganService;

    @Autowired
    private RemainingBalanceIngestionService remainingBalanceIngestionService;

    // InsuranceProvider Start
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<InsuranceProvider> getInsuranceProviders(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "entity_type", defaultValue = "membership") String entityType,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "category_id") String category_id) throws URISyntaxException {
        if (!authorizationService.isAuthorized("marketing", "campaign_edit", "restricted_write") && !authorizationService.isAuthorized("insurance",
                "insurance_provider_list", "view_only") && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolCatalogService.insuranceProviders(name, pageNo, perPage, status, category_id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    InsuranceProviderWithValidations saveInsuranceProvider(@RequestBody InsuranceProviderWithValidations insuranceProviderWithValidations)
            throws URISyntaxException {

        insuranceProviderWithValidations.setProvider(misoolCatalogService.saveInsuranceProvider(insuranceProviderWithValidations.getProvider()));

        String providerId = insuranceProviderWithValidations.getProvider().getExternalId();
        //        Setting provider external id after creating a new provider
        for (ValidationCheck validationCheck : insuranceProviderWithValidations.getValidationChecks()) {
            validationCheck.setProviderExternalId(providerId);
        }

        insuranceProviderWithValidations.setValidationChecks(
                membershipService.saveMultipleValidationsCheck(insuranceProviderWithValidations.getValidationChecks(), providerId));

        return insuranceProviderWithValidations;
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{id}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    InsuranceProviderWithValidations updateInsuranceProvider(@PathVariable String id,
            @RequestBody InsuranceProviderWithValidations insuranceProviderWithValidations) throws URISyntaxException {

        insuranceProviderWithValidations.setProvider(
                misoolCatalogService.updateInsuranceProvider(id, insuranceProviderWithValidations.getProvider()));

        return insuranceProviderWithValidations;
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{id}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    InsuranceProviderWithValidations getInsuranceProvider(@PathVariable String id) throws URISyntaxException {
        InsuranceProviderWithValidations insuranceProviderWithValidations = new InsuranceProviderWithValidations();

        insuranceProviderWithValidations.setProvider(misoolCatalogService.getInsuranceProvider(id));
        insuranceProviderWithValidations.setValidationChecks(membershipService.getValidationsCheck(id));

        return insuranceProviderWithValidations;
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/provider-element")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    ValidationCheck saveValidationsCheck(@RequestBody ValidationCheck validationCheck) throws URISyntaxException {

        return membershipService.saveValidationsCheck(validationCheck);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/provider-element/{id}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    ValidationCheck updateValidationsCheck(@RequestBody ValidationCheck validationCheck) throws URISyntaxException {

        return membershipService.updateValidationsCheck(validationCheck);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/insurance-documents/{module_type}/{module_external_id}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    List<InsuranceDocument> getInsuranceModuleDocuments(@PathVariable (name = "module_type") String moduleType,
            @PathVariable (name = "module_external_id") String moduleExternalId) {
        return misoolCatalogService.getInsuranceModuleDocuments(moduleType, moduleExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @PutMapping ("/insurance-documents")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    List<InsuranceDocument> getInsuranceDocuments(@RequestBody Map moduleIds) {
        return misoolCatalogService.getInsuranceDocuments(moduleIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @PostMapping ("/insurance-documents/{module_type}/{module_external_id}")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    List<InsuranceDocument> addInsuranceDocuments(@PathVariable (name = "module_type") String moduleType,
            @PathVariable (name = "module_external_id") String moduleExternalId, @RequestBody Map insuranceDocumentRequest) {
        return misoolCatalogService.addInsuranceDocuments(moduleType, moduleExternalId, insuranceDocumentRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @DeleteMapping ("/insurance-documents/{externalId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateInsuranceDocuments(@PathVariable () String externalId) {
        misoolCatalogService.deleteInsuranceDocument(externalId);
    }

    // POLICY OFFERING START

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<PolicyOffering> getPolicyOfferings(@PathVariable String insuranceProviderId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status)
            throws URISyntaxException {
        return misoolCatalogService.policyOfferings(pageNo, perPage, name, status, insuranceProviderId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/offerings/create")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    PolicyOffering createPolicyOffering(@PathVariable String insuranceProviderId, @RequestBody PolicyOffering offering) throws URISyntaxException {
        return misoolCatalogService.createPolicyOffering(insuranceProviderId, offering);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    PolicyOffering updatePolicyOffering(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @RequestBody PolicyOffering offering) throws URISyntaxException {
        return misoolCatalogService.updatePolicyOffering(insuranceProviderId, policyOfferingId, offering);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    PolicyOffering getPolicyOffering(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId) throws URISyntaxException {
        return misoolCatalogService.getPolicyOffering(insuranceProviderId, policyOfferingId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/providers/{insuranceProviderId}/offerings/{policyOfferingId}/campaign")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<InsuranceCampaign> searchInsuranceCampaign(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "campaign_name") String campaignName) throws URISyntaxException {
        return misoolCatalogService.searchInsuranceCampaign(pageNo, perPage, campaignName, insuranceProviderId, policyOfferingId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/providers/{insuranceProviderId}/offerings/{policyOfferingId}/campaign/{campaignId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InsuranceCampaign getInsuranceCampaign(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @PathVariable String campaignId) throws URISyntaxException {
        return misoolCatalogService.getInsuranceCampaign(insuranceProviderId, policyOfferingId, campaignId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/providers/{insuranceProviderId}/offerings/{policyOfferingId}/campaign")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    InsuranceCampaign createInsuranceCampaign(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @RequestBody InsuranceCampaign insuranceCampaign) throws URISyntaxException {
        return misoolCatalogService.createInsuranceCampaign(insuranceProviderId, policyOfferingId, insuranceCampaign);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/providers/{insuranceProviderId}/offerings/{policyOfferingId}/campaign/{campaignId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    InsuranceCampaign updateInsuranceCampaign(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @PathVariable String campaignId, @RequestBody InsuranceCampaign campaign) throws URISyntaxException {
        return misoolCatalogService.updateInsuranceCampaign(insuranceProviderId, policyOfferingId, campaignId, campaign);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/offerings/{policyOfferingId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    PolicyOffering getPolicyOfferingByOfferingIdOnly(@PathVariable String policyOfferingId) throws URISyntaxException {
        return misoolCatalogService.getPolicyOfferingByOfferingIdOnly(policyOfferingId);
    }

    // POLICY OFFERING END

    // PRODUCT START

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{offeringId}/products")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<Product> getProducts(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status)
            throws URISyntaxException {
        return misoolCatalogService.product(pageNo, perPage, name, status, insuranceProviderId, offeringId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/offerings/{offeringId}/products")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Product createProduct(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @RequestBody Product product)
            throws URISyntaxException {
        return misoolCatalogService.createProduct(insuranceProviderId, offeringId, product);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/offerings/{offeringId}/products/{productId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Product updateProduct(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @RequestBody Product product) throws URISyntaxException {
        return misoolCatalogService.updateProduct(insuranceProviderId, offeringId, productId, product);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{offeringId}/products/{productId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Product getProduct(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId)
            throws URISyntaxException {
        return misoolCatalogService.getProduct(insuranceProviderId, offeringId, productId);
    }

    // PRODUCT END

    //BENEFIT CATEGORY

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{offeringId}/products/{productId}/benefit-categories")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<BenefitCategory> getBenefitCategories(@PathVariable String insuranceProviderId, @PathVariable String offeringId,
            @PathVariable String productId, @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status)
            throws URISyntaxException {
        return misoolCatalogService.getBenefitCategories(pageNo, perPage, name, status, insuranceProviderId, offeringId, productId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/offerings/{offeringId}/products/{productId}/benefit-categories")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    BenefitCategory createBenefitCategory(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @RequestBody BenefitCategory benefitCategory) throws URISyntaxException {
        return misoolCatalogService.createBenefitCategory(insuranceProviderId, offeringId, productId, benefitCategory);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/offerings/{offeringId}/products/{productId}/benefit-categories/{categoryId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    BenefitCategory updateBenefitCategory(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @PathVariable String categoryId, @RequestBody BenefitCategory benefitCategory) throws URISyntaxException {
        return misoolCatalogService.updateBenefitCategory(insuranceProviderId, offeringId, productId, categoryId, benefitCategory);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{offeringId}/products/{productId}/benefit-categories/{categoryId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    BenefitCategory getBenefitCategory(@PathVariable String insuranceProviderId, @PathVariable String offeringId, @PathVariable String productId,
            @PathVariable String categoryId) throws URISyntaxException {
        return misoolCatalogService.getBenefitCategory(insuranceProviderId, offeringId, productId, categoryId);
    }

    // POLICIES START

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/policies")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<Policy> getPolicies(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String policyName, @RequestParam (required = false, name = "policy_no") String policyNo,
            @RequestParam (required = false, name = "status") String status, @RequestParam (required = false, name = "start_date") Date startDate,
            @RequestParam (required = false, name = "end_date") Date endDate, @PathVariable String insuranceProviderId) throws URISyntaxException {
        return misoolCatalogService.policies(pageNo, perPage, policyName, policyNo, status, startDate, endDate, insuranceProviderId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/policies")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    PolicyResponse createPolicy(@PathVariable String insuranceProviderId, @RequestBody Policy policy) throws URISyntaxException {
        return misoolCatalogService.createPolicy(insuranceProviderId, policy);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/policies/{policyId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updatePolicy(@PathVariable String insuranceProviderId, @PathVariable String policyId, @RequestBody PolicyUpdateRequest policy)
            throws URISyntaxException {
        misoolCatalogService.updatePolicy(insuranceProviderId, policyId, policy);
    }

    @GetMapping ("/{insuranceProviderId}/policies/{policyId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    PolicyResponse getPolicy(@PathVariable String insuranceProviderId, @PathVariable String policyId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_view", "view_only") && !authorizationService.isAuthorized("insurance",
        "insurance_provider_list", "view_only") && !authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService.isAuthorized("insurance",
        "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolCatalogService.getPolicy(insuranceProviderId, policyId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("policies/internal/{policyId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getPolicyByPolicyExternalId(@PathVariable String policyId) throws URISyntaxException {
        return misoolCatalogService.getPolicyByExternalId(policyId);
    }

    // POLICIES END

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("policies/internal/{policyId}/plans")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<Plan> getPlansByPolicy(@PathVariable String policyId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "code") String code) throws URISyntaxException {
        return misoolCatalogService.getPlansByPolicy(policyId, perPage, pageNo, code);
    }

    //membership

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/ingestions")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<MembershipResponse> getIngestedMemberships(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "entity_type", defaultValue = "membership") String entityType,
            @PathVariable String insuranceProviderId) throws URISyntaxException {
        return misoolCatalogService.ingestedMemberships(pageNo, perPage, entityType, insuranceProviderId);
    }

    // Claim Reversal
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reversal_list', 'view_only')")
    @GetMapping ("/reversal")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<ClaimReversal> getClaimReversal(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "insurance_provider_id", defaultValue = "all") String providerId) throws URISyntaxException {
        return misoolCatalogService.getClaimReversalList(pageNo, perPage, providerId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/ingestions")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    MembershipResponse IngestMembership(@PathVariable String insuranceProviderId, @RequestBody MembershipIngestion fileBody)
            throws URISyntaxException {
        return misoolCatalogService.ingestMembership(insuranceProviderId, fileBody);
    }

    // Unlinking tool
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/policies/unlink")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void unlinkMembership(@PathVariable String insuranceProviderId, @RequestBody Map unlinkingTool) throws URISyntaxException {
        membershipService.unlinkPolicy(insuranceProviderId, unlinkingTool);
    }

    // InsuranceProvider Category
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/categories")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<ProviderCategory> providerCategories(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "50") Integer perPage,
            @RequestParam (required = false, name = "name") String name) throws URISyntaxException {

        return misoolCatalogService.providersCategories(name, pageNo, perPage);
    }

    // Member Management APIs
    @GetMapping ("/{insuranceProviderId}/members/{memberId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Member getMember(@PathVariable String insuranceProviderId, @PathVariable String memberId,
            @RequestParam (name = "policy_number", required = false) String policyNumber,
            @RequestParam (name = "useMemberId", required = false) Boolean useMemberId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolCatalogService.getMemberByProviderExternalIdandMemberExternalId(insuranceProviderId, memberId, policyNumber, useMemberId);
    }

    @PutMapping ("/{insuranceProviderId}/members")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Member updateMember(@PathVariable String insuranceProviderId, @RequestBody Member member) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_edit", "restricted_write") && !authorizationService.isAuthorized(
                "insurance", "supervisor_claims_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolCatalogService.updateMember(insuranceProviderId, member);
    }

    @GetMapping ("/{insuranceProviderId}/members/{memberId}/family")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Member[] getFamilyMembersOfMember(@PathVariable String insuranceProviderId, @PathVariable String memberId,
            @RequestParam (name = "policyId", required = true) String policyId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolCatalogService.getFamilyMembersOfMember(insuranceProviderId, memberId, policyId);
    }

    @GetMapping ("benefit-usages/{benefitUsageId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Object getBenefitUsages(@PathVariable String benefitUsageId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolBenefitsV2Service.getBenefitUsages(benefitUsageId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @PutMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/plan-codes/search")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Plan SearchPlanByPlanCode(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId, @RequestBody Map req)
            throws URISyntaxException {
        return misoolCatalogService.searchPlanByPlanCode(insuranceProviderId, policyOfferingId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_provider_list', 'view_only')")
    @GetMapping ("/subscriptions")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getSubscriptions(@RequestParam (required = false, name = "entity_id") String entityId) throws URISyntaxException {
        return membershipService.getSubscriptions(entityId);
    }

    //remaining Balance

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/remaining-balance")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<RemainingBalanceResponse> getIngestedRemainingBalance(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "insurance_provider_id") String insuranceProviderId) throws URISyntaxException {
        return remainingBalanceIngestionService.getIngestedRemainingBalance(pageNo, perPage, insuranceProviderId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/remaining-balance")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    RemainingBalanceResponse ingestRemainingBalance(@PathVariable String insuranceProviderId, @RequestBody RemainingBalanceIngestionRequest fileBody)
            throws URISyntaxException {
        return remainingBalanceIngestionService.ingestRemainingBalance(insuranceProviderId, fileBody);
    }

    //membership Relinking
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/relinking")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<MembershipRelinkingResponse> getRelinkingMemberships(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "entity_type", defaultValue = "membership") String entityType,
            @PathVariable String insuranceProviderId) throws URISyntaxException {
        return misoolCatalogService.ingestedRelinkingMemberships(pageNo, perPage, entityType, insuranceProviderId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/relinking")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    MembershipRelinkingResponse RelinkMembership(@PathVariable String insuranceProviderId, @RequestBody MembershipRelinkingIngestFile fileBody)
            throws URISyntaxException {
        return misoolCatalogService.ingestRelinkingMembership(insuranceProviderId, fileBody);
    }

    // MEMBERSHIP DATA RENEWAL
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/member-renewal")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateMemberRenewal(@PathVariable String insuranceProviderId, @RequestBody MembershipDataRenewal membershipDataRenewal)
            throws URISyntaxException {
        misoolCatalogService.updateMemberRenewal(insuranceProviderId, membershipDataRenewal);
    }

    /******************************** Policy T&C: START ********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/policyTermsConditions")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<PolicyTermsAndCondition> getPolicyTnC(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @RequestParam (required = true, name = "page_no") String pageNo, @RequestParam (required = true, name = "per_page") String perPage) {
        return misoolCatalogService.getPolicyTnC(insuranceProviderId, policyOfferingId, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @PutMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/policyTermsConditions/search")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<PolicyTermsAndCondition> searchPolicyTnC(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @RequestBody Map reqBody, @RequestParam (required = false, name = "page_no", defaultValue = "1") String pageNo,
            @RequestParam (required = true, name = "per_page") String perPage) {
        return misoolCatalogService.searchPolicyTnC(insuranceProviderId, policyOfferingId, pageNo, perPage, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/policyTermsConditions/{policyTermsConditionsExternalId}/active")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    PolicyTermsAndCondition activatePolicyTnCStatus(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @PathVariable String policyTermsConditionsExternalId) throws URISyntaxException {
        return misoolCatalogService.activatePolicyTnCStatus(insuranceProviderId, policyOfferingId, policyTermsConditionsExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/policyTermsConditions/{policyTermsConditionsExternalId}/inactive")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    PolicyTermsAndCondition inactivatePolicyTnCStatus(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @PathVariable String policyTermsConditionsExternalId) throws URISyntaxException {
        return misoolCatalogService.inactivatePolicyTnCStatus(insuranceProviderId, policyOfferingId, policyTermsConditionsExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/policyTermsConditions/history")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<PolicyTermsAndCondition> getPolicyTnCHistory(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @RequestParam (required = false, name = "uploadedDocument") String uploadedDocument,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") String pageNo,
            @RequestParam (required = true, name = "per_page") String perPage) {
        return misoolCatalogService.getPolicyTnCHistory(insuranceProviderId, policyOfferingId, uploadedDocument, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/validatePolicyTnC")
    Map validatePolicyTnC(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId, @RequestParam String documentId) {
        return misoolCatalogService.validatePolicyTnC(insuranceProviderId, policyOfferingId, documentId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/offerings/{policyOfferingId}/getPolicyTnCFileUploadStatus")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getPolicyTnCFileUploadStatus(@PathVariable String insuranceProviderId, @PathVariable String policyOfferingId,
            @RequestParam String documentId) {
        return misoolCatalogService.getPolicyTnCFileUploadStatus(insuranceProviderId, policyOfferingId, documentId);
    }
    /******************************** Policy T&C: END ********************************/
    /******************************** MembershipQueryDashboard: START ********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @PutMapping ("/members/search")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<MembershipDashboardResponse> getMemberSearch(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage, @RequestBody MemberSearch memberSearch)
            throws URISyntaxException {
        return misoolCatalogService.memberDashboardResponseList(pageNo, perPage, memberSearch);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @GetMapping ("/{providerId}/offerings/{offeringId}/module-limits/members/{memberId}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<ModuleLimit> getModuleLimits(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "plan_code") String planCode,
            @RequestParam (required = false, name = "moduleLimitName") String moduleLimitName, @PathVariable String memberId,
            @PathVariable String providerId, @PathVariable String offeringId) throws URISyntaxException {
        PaginatedResult<ModuleLimit> moduleLimits = misoolCatalogService.getModuleLimits(offeringId, providerId, memberId, pageNo, perPage, planCode,
                moduleLimitName);
        return moduleLimits;
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @GetMapping ("/{providerId}/offerings/{offeringId}/instance-limits/benefits")
    PaginatedResult<InstanceLimitResponse> getInstanceLimitResponse(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "linked_plan") String linkedPlan,
            @RequestParam (required = false, name = "benefitDescription") String benefitDescription,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @PathVariable String providerId,
            @PathVariable String offeringId) throws URISyntaxException {
        PaginatedResult<InstanceLimitResponse> instanceLimitResponse = misoolCatalogService.getInstanceLimitResponse(providerId, offeringId, perPage,
                pageNo, linkedPlan, benefitDescription);
        return instanceLimitResponse;

    }

    @GetMapping ("/{insuranceProviderId}/memberinfo/{memberId}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    MemberInfo getMemberInfo(@PathVariable String insuranceProviderId, @PathVariable String memberId,
            @RequestParam (name = "policy_external_id", required = true) String policyExternalId,
            @RequestParam (name = "useMemberId", required = false) Boolean useMemberId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "supervisor_claims_view", "view_only") && !authorizationService.isAuthorized("insurance", "membership_query", "view_only")
                && !authorizationService.isAuthorized("insurance", "case_monitoring_ip_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return misoolCatalogService.getMemberInfo(insuranceProviderId, memberId, policyExternalId, useMemberId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @PostMapping ("/{providerId}/offerings/{offeringId}/policies/{policyId}/evaluate-benefits")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<Map> evaluateMemberBenefits(@RequestBody List<Map> req) {
        return misoolBenefitsV2Service.evaluateMemberBenefits(req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @PutMapping ("/{providerId}/offerings/{offeringId}/members/{memberId}/remaining-global-limits")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    MemberGlobalLimitsResponse evaluateMemberRemainingGlobalLimits(@RequestBody MemberGlobalLimitsRequest req) throws URISyntaxException {
        return misoolBenefitsV2Service.evaluateMemberRemainingGlobalLimits(req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @PostMapping ("/recalculate-balance")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Map recalculateBalance(@RequestBody Map req) {
        return misoolClaimsService.recalculateBalance(req);
    }
    /******************************** MembershipQueryDashboard: END ********************************/
    /******************************** Exclusions - Diagnosis: START ********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/exclusions/{id}/diagnosis-codes/add-diagnosis-code")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    DiagnosisCodeResponse addDiagnosisCode(@PathVariable String id, @RequestBody DiagnosisCodeObject diagnosisCodeObject,
            @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) {

        return misoolCatalogService.addDiagnosisCode(diagnosisCodeObject, id, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/exclusions/{id}/diagnosis-codes")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<DiagnosisCodeResponse> getDiagnosisExclusionCodes(@PathVariable String id,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) throws URISyntaxException {

        return misoolCatalogService.getDiagnosisExclusionCodes(id, perPage, pageNo, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/exclusions/{id}/diagnosis-codes/{codeId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<DiagnosisCodeResponse> getDiagnosisCodeByCodeId(@PathVariable String id, @PathVariable String codeId,
            @RequestParam (required = false, name = "member_id") String memberId) {

        return misoolCatalogService.getDiagnosisCodeByCodeId(id, codeId, memberId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @PutMapping ("/exclusions/{id}/diagnosis-codes/search")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<DiagnosisCodeResponse> searchDiagnosisCodes(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestBody DiagnosisExclusionSearch diagnosisExclusionSearch, @PathVariable String id) throws URISyntaxException {
        return misoolCatalogService.searchDiagnosisCodes(pageNo, perPage, id, diagnosisExclusionSearch);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/exclusions/{id}/diagnosis-codes/{exclusionDiagnosisCodeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    DiagnosisCodeResponse updateDiagnosisCode(@PathVariable String id, @PathVariable String exclusionDiagnosisCodeId,
            @RequestBody DiagnosisCodeObject diagnosisCodeObject,
            @RequestParam (required = false, name = "provider_type", defaultValue = "") String providerType) {

        return misoolCatalogService.updateDiagnosisCode(id, exclusionDiagnosisCodeId, diagnosisCodeObject, providerType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/exclusions/{id}/diagnosis-codes/active")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    DiagnosisCodeResponse activateDiagnosisCode(@RequestBody DiagnosisExclusionIdList diagnosisCodeExternalIdList, @PathVariable String id) {

        return misoolCatalogService.activateDiagnosisCode(diagnosisCodeExternalIdList, id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @PutMapping ("/members/user-detail")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    MembershipDetailResponse getMemberDetails(@RequestBody MembershipDetail membershipDetail) throws URISyntaxException {
        return membershipService.getMemberDetails(membershipDetail);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @GetMapping ("/members/{memberId}/linking-history")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<PatientInfo> getMemberLinkingHistory(@PathVariable String memberId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return membershipService.getMemberLinkingHistory(pageNo, perPage, memberId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'restricted_write')")
    @PutMapping ("/members/{providerId}/link-patient")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    LinkPatient linkPatient(@PathVariable String providerId, @RequestBody LinkPatientBody linkPatient) throws URISyntaxException {
        return membershipService.linkPatient(providerId, linkPatient);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'restricted_write')")
    @PutMapping ("/members/{providerId}/link/admin")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    LinkPatient linkMemberAdmin(@PathVariable String providerId, @RequestBody LinkMemberAdmin linkMemberAdmin) throws URISyntaxException {
        return membershipService.linkMemberAdmin(providerId, linkMemberAdmin);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'restricted_write')")
    @PutMapping ("/members/{providerId}/unlink-patient/{memberId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    LinkPatient unlinkPatient(@PathVariable String providerId, @PathVariable String memberId,
            @RequestParam (required = false, name = "reason", defaultValue = "") String reason) throws URISyntaxException {
        return membershipService.unlinkPatient(providerId, memberId, reason);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'restricted_write')")
    @PostMapping ("/members/create-user")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Patient createPatient(@RequestBody Patient patient, @RequestParam (required = false, name = "phone_number", defaultValue = "") String phoneNumber)
            throws URISyntaxException {
        return membershipService.createPatient(patient, phoneNumber);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @DeleteMapping ("/exclusions/{id}/diagnosis-codes")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteDiagnosisCode(@RequestBody DiagnosisExclusionIdList diagnosisCodeExternalIdList, @PathVariable String id) {

        misoolCatalogService.deleteDiagnosisCode(diagnosisCodeExternalIdList, id);
    }

    /******************************** Exclusions - Diagnosis: END ********************************/
    /******************************** Pre-existing condition: START ********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @PutMapping ("/{memberId}/pre-existing-conditions")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<PreExistingCondition> getPreExistingConditions(@PathVariable String memberId,
            @RequestBody () PreExistingConditionRequest preExistingConditionRequest) throws URISyntaxException {

        return misoolCatalogService.getPreExistingConditions(memberId, preExistingConditionRequest);
    }
    /******************************** Pre-existing condition: END ********************************/
    /******************************* Ingestion Logging Start******************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @PutMapping ("/{insuranceProviderId}/ingestions/get-ingestion-count")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    MembershipIngestionStats getMemberIngestionCount(@RequestBody () String[] memberIngestionCountRequest) throws URISyntaxException {
        return misoolCatalogService.getMemberIngestionCount(memberIngestionCountRequest);
    }

    /******************************* Ingestion Logging End******************************************/
    /******************************** DCO Member Suspension: START ********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'view_only')")
    @GetMapping ("/dco-suspension/{memberExternalId}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<SuspensionHistory> getDcoSuspensionHistory(@PathVariable String memberExternalId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {

        return misoolCatalogService.getDcoSuspensionHistory(memberExternalId, perPage, pageNo);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','membership_query', 'restricted_write')")
    @PostMapping ("/dco_suspension/{memberExternalId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map suspendDcoMember(@PathVariable String memberExternalId, @RequestBody SuspendMemberRequest reqBody) throws URISyntaxException {
        return misoolCatalogService.suspendDcoMember(memberExternalId, reqBody);
    }

    /******************************** DCO Member Suspension: START ********************************/

    /***************************** Conversion Rate Start *****************************************/
    @GetMapping ("/conversion-rate-list")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<Map> getAllConversionRateList(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "date", defaultValue = "") String date,
            @RequestParam (required = false, name = "banks", defaultValue = "") String banks
    ) throws URISyntaxException {
        return misoolCatalogService.getAllConversionRateList(pageNo, perPage, date,banks);
    }


    @PostMapping ("/conversion-rate/create")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createConversionRate(@RequestBody Map reqBody) throws URISyntaxException {

        return misoolCatalogService.createConversionRate(reqBody);

    }

    @DeleteMapping ("/conversion-rate-delete/{conversionRateExternalId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteConversionRate(@PathVariable () String conversionRateExternalId) {
        misoolCatalogService.deleteConversionRate(conversionRateExternalId);
    }

    @PutMapping  ("/conversion-rate/{conversionRateExternalId}/edit")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map editConversionRate(@PathVariable () String conversionRateExternalId,@RequestBody Map reqBody) throws URISyntaxException {

        return misoolCatalogService.editConversionRate(conversionRateExternalId,reqBody);

    }
    /***************************** Conversion Rate End *****************************************/
}
