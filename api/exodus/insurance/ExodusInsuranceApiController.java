package com.halodoc.batavia.controller.api.exodus.insurance;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.misool.catalog.AreaList;
import com.halodoc.batavia.entity.exodus.misool.catalog.BenefitRequestBody;
import com.halodoc.batavia.entity.exodus.misool.catalog.BenefitResponseBody;
import com.halodoc.batavia.entity.exodus.misool.catalog.CalimAdjudicationInsuranceProvider;
import com.halodoc.batavia.entity.exodus.misool.catalog.ClaimAdjudicationMember;
import com.halodoc.batavia.entity.exodus.misool.catalog.ClaimInsuranceEntities;
import com.halodoc.batavia.entity.exodus.misool.catalog.ClaimMember;
import com.halodoc.batavia.entity.exodus.misool.catalog.FamilyMember;
import com.halodoc.batavia.entity.exodus.misool.catalog.InsuranceProvider;
import com.halodoc.batavia.entity.exodus.misool.catalog.InsuranceProviderEntity;
import com.halodoc.batavia.entity.exodus.misool.catalog.InsuranceProviderEntityProduct;
import com.halodoc.batavia.entity.exodus.misool.catalog.InsuranceProviderPlan;
import com.halodoc.batavia.entity.exodus.misool.catalog.InsuranceProviderPlanPackage;
import com.halodoc.batavia.entity.exodus.misool.catalog.InsuranceProviderPlanPackageUpdate;
import com.halodoc.batavia.entity.exodus.misool.catalog.InsuranceProviderPlanUpdate;
import com.halodoc.batavia.entity.exodus.misool.catalog.PreExistingCondition;
import com.halodoc.batavia.entity.exodus.misool.catalog.PreExistingConditionResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.ProductPlanLetterSetup;
import com.halodoc.batavia.entity.exodus.product.DeleteSharedPlanRequest;
import com.halodoc.batavia.entity.exodus.product.ExodusSharedPlans;
import com.halodoc.batavia.entity.exodus.product.SharedPlanRequest;
import com.halodoc.batavia.entity.exodus.product.SharedPlanResponse;
import com.halodoc.batavia.entity.exodus.product.SharedPlans;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/insurance/providers")
public class ExodusInsuranceApiController extends HalodocBaseApiController {
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    /**************************************** Insurance Provider: START ****************************************/
    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InsuranceProvider> searchInsuranceProviders(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String status, @RequestParam (required = false, name = "payor_id") String payorId,
            @RequestParam (required = false, name = "insurance_type") String insuranceType,
            @RequestParam (required = false, name = "market_name") String marketName) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "whatsapp_configuration_add", "view_only") && !authorizationService.isAuthorized("insurance", "whatsapp_configuration_edit",
                "view_only") && !authorizationService.isAuthorized("insurance", "insurance_provider_view",
                "view_only") && !authorizationService.isAuthorized("insurance", "tpa_claim_report_history",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_ingestions_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_reversal_add",
                "restricted_write") && !authorizationService.isAuthorized("insurance", "claim_reversal_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_report_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_payment_report_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "supervisor_claims_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "analyst_claims_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_cashless_add",
                "restricted_write") && !authorizationService.isAuthorized("insurance", "claim_cashless_view",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_cashless_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "client_portal_member_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_reimbursement_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_reimbursement_add",
                "restricted_write") && !authorizationService.isAuthorized("insurance", "claim_reimbursement_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return exodusMisoolCatalogService.searchInsuranceProviders(pageNo, perPage, name, status, insuranceType, marketName, payorId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    InsuranceProvider createInsuranceProvider(@RequestBody InsuranceProvider insuranceProvider) throws URISyntaxException {
        return exodusMisoolCatalogService.createInsuranceProvider(insuranceProvider);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    InsuranceProvider updateInsuranceProvider(@PathVariable String insuranceProviderId, @RequestBody InsuranceProvider insuranceProvider)
            throws URISyntaxException {
        return exodusMisoolCatalogService.updateInsuranceProvider(insuranceProviderId, insuranceProvider);
    }

    @GetMapping ("/{insuranceProviderId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InsuranceProvider getInsuranceProviderById(@PathVariable String insuranceProviderId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "whatsapp_configuration_add", "view_only") && !authorizationService.isAuthorized("insurance", "whatsapp_configuration_edit",
                "view_only") && !authorizationService.isAuthorized("insurance", "insurance_provider_view",
                "view_only") && !authorizationService.isAuthorized("insurance", "tpa_claim_report_history",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_ingestions_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_reversal_add",
                "restricted_write") && !authorizationService.isAuthorized("insurance", "claim_reversal_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_report_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_payment_report_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "supervisor_claims_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "analyst_claims_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_cashless_add",
                "restricted_write") && !authorizationService.isAuthorized("insurance", "claim_cashless_view",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_cashless_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "client_portal_member_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return exodusMisoolCatalogService.getInsuranceProviderById(insuranceProviderId);
    }
    /**************************************** Insurance Provider: END ****************************************/
    /************************************ Insurance Provider Entity: START ************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InsuranceProviderEntity> searchInsuranceProviderEntities(@PathVariable @NotBlank String insuranceProviderId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String status, @RequestParam (required = false, name = "market_name") String marketName)
            throws URISyntaxException {
        
        return exodusMisoolCatalogService.searchInsuranceProviderEntities(insuranceProviderId, pageNo, perPage, name, status, marketName);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/entities")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    InsuranceProviderEntity createInsuranceProviderEntity(@PathVariable String insuranceProviderId,
            @RequestBody InsuranceProviderEntity insuranceProviderEntity) throws URISyntaxException {
        return exodusMisoolCatalogService.createInsuranceProviderEntity(insuranceProviderId, insuranceProviderEntity);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/entities/{entityId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    InsuranceProviderEntity updateInsuranceProviderEntity(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @RequestBody InsuranceProviderEntity insuranceProviderEntity) throws URISyntaxException {
        return exodusMisoolCatalogService.updateInsuranceProviderEntity(insuranceProviderId, entityId, insuranceProviderEntity);
    }

    @GetMapping ("/{insuranceProviderId}/entities/{entityId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InsuranceProviderEntity getInsuranceProviderEntityById(@PathVariable String insuranceProviderId, @PathVariable String entityId)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "case_monitoring_ip_view", "view_only") && !authorizationService.isAuthorized("insurance", "claim_cashless_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return exodusMisoolCatalogService.getInsuranceProviderEntityById(insuranceProviderId, entityId);
    }
    /************************************ Insurance Provider Entity: END ************************************/
    /******************************************** Product: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InsuranceProviderEntityProduct> searchInsuranceProviderEntityProducts(@PathVariable String insuranceProviderId,
            @PathVariable String entityId, @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String status) throws URISyntaxException {
        return exodusMisoolCatalogService.searchInsuranceProviderEntityProducts(insuranceProviderId, entityId, pageNo, perPage, name, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/entities/{entityId}/products")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    InsuranceProviderEntityProduct createInsuranceProviderEntityProduct(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @RequestBody InsuranceProviderEntityProduct entityProduct) throws URISyntaxException {
        return exodusMisoolCatalogService.createInsuranceProviderEntityProduct(insuranceProviderId, entityId, entityProduct);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    InsuranceProviderEntityProduct updateInsuranceProviderEntityProduct(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @RequestBody InsuranceProviderEntityProduct entityProduct) throws URISyntaxException {
        return exodusMisoolCatalogService.updateInsuranceProviderEntityProduct(insuranceProviderId, entityId, productId, entityProduct);
    }

    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InsuranceProviderEntityProduct getInsuranceProviderEntityProductById(@PathVariable String insuranceProviderId, @PathVariable String productId,
            @PathVariable String entityId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("insurance", "membership_query", "view_only") && !authorizationService.isAuthorized("insurance",
                "case_monitoring_ip_view", "view_only") && !authorizationService.isAuthorized("insurance", "insurance_provider_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return exodusMisoolCatalogService.getInsuranceProviderEntityProductById(insuranceProviderId, entityId, productId);
    }
    /******************************************** Product: END ********************************************/
    /******************************************** Benefit: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans/{planId}/benefits")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<BenefitResponseBody> searchInsuranceProviderBenefits(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @PathVariable String planId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String status, @RequestParam (required = false) String shared_benefit_flag) throws URISyntaxException {
        return exodusMisoolCatalogService.searchInsuranceProviderBenefits(insuranceProviderId, entityId, productId, planId, pageNo, perPage, name,
                status, shared_benefit_flag);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans/{planId}/benefits")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    BenefitResponseBody createInsuranceProviderBenefit(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @PathVariable String planId, @RequestBody BenefitRequestBody benefitRequestBody)
            throws URISyntaxException {
        return exodusMisoolCatalogService.createInsuranceProviderBenefit(insuranceProviderId, entityId, productId, planId, benefitRequestBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans/{planId}/benefits/{benefitId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    BenefitResponseBody updateInsuranceProviderBenefit(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @PathVariable String planId, @PathVariable String benefitId,
            @RequestBody BenefitRequestBody benefitRequestBody) throws URISyntaxException {
        return exodusMisoolCatalogService.updateInsuranceProviderBenefit(insuranceProviderId, entityId, productId, planId, benefitId,
                benefitRequestBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans/{planId}/benefits/{benefitId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    BenefitResponseBody getInsuranceProviderBenefitById(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @PathVariable String planId, @PathVariable String benefitId) throws URISyntaxException {
        return exodusMisoolCatalogService.getInsuranceProviderBenefitById(insuranceProviderId, entityId, productId, planId, benefitId);
    }

    /******************************************** Benefit: END ********************************************/
    /******************************************** Plan: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InsuranceProviderPlan> searchInsuranceProviderEntityPlan(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String status) throws URISyntaxException {
        return exodusMisoolCatalogService.searchInsuranceProviderEntityPlan(insuranceProviderId, entityId, productId, pageNo, perPage, name, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InsuranceProviderPlan> searchInsuranceProviderEntityPlanV2(@PathVariable String insuranceProviderId,
            @PathVariable String entityId, @PathVariable String productId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String status) throws URISyntaxException {
        return exodusMisoolCatalogService.searchInsuranceProviderEntityPlanV2(insuranceProviderId, entityId, productId, pageNo, perPage, name,
                status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    InsuranceProviderPlan createInsuranceProviderEntityPlan(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @RequestBody InsuranceProviderPlan entityPlan) throws URISyntaxException {
        return exodusMisoolCatalogService.createInsuranceProviderEntityPlan(insuranceProviderId, entityId, productId, entityPlan);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans/{planId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    InsuranceProviderPlanUpdate updateInsuranceProviderEntityPlan(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @PathVariable String planId, @RequestBody InsuranceProviderPlanUpdate entityPlan)
            throws URISyntaxException {
        return exodusMisoolCatalogService.updateInsuranceProviderEntityPlan(insuranceProviderId, entityId, productId, planId, entityPlan);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plans/{planId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InsuranceProviderPlan getInsuranceProviderEntityPlantById(@PathVariable String insuranceProviderId, @PathVariable String productId,
            @PathVariable String planId, @PathVariable String entityId) throws URISyntaxException {
        return exodusMisoolCatalogService.getInsuranceProviderEntityPlanById(insuranceProviderId, entityId, productId, planId);
    }
    /******************************************** Plan: END ********************************************/
    /******************************************** COVERAGES: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/coverages")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getCoverageList() throws URISyntaxException {
        return exodusMisoolCatalogService.getCoverageList();
    }

    /******************************************** COVERAGES: END ********************************************/
    /******************************************** Plan-Package: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plan-packages")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InsuranceProviderPlanPackage> searchInsuranceProviderEntityPlan(@PathVariable String insuranceProviderId,
            @PathVariable String entityId, @PathVariable String productId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name)
            throws URISyntaxException {
        return exodusMisoolCatalogService.searchInsuranceProviderEntityPlanPackage(insuranceProviderId, entityId, productId, pageNo, perPage, name);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plan-packages/{planPackageId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    InsuranceProviderPlanPackageUpdate updateInsuranceProviderEntityPlanPackage(@PathVariable String insuranceProviderId,
            @PathVariable String entityId, @PathVariable String productId, @PathVariable String planPackageId,
            @RequestBody InsuranceProviderPlanPackageUpdate entityPlanPackage) throws URISyntaxException {
        return exodusMisoolCatalogService.updateInsuranceProviderEntityPlanPackage(insuranceProviderId, entityId, productId, planPackageId,
                entityPlanPackage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @DeleteMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plan-packages/{planPackageId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteInsuranceProviderEntityPlanPackage(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @PathVariable String planPackageId) throws URISyntaxException {
        exodusMisoolCatalogService.deleteInsuranceProviderEntityPlanPackage(insuranceProviderId, entityId, productId, planPackageId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/plan-packages/active-plans")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<InsuranceProviderPlan> getActivePlans(@PathVariable String insuranceProviderId, @PathVariable String entityId,
            @PathVariable String productId, @RequestParam (required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogService.getActivePlans(insuranceProviderId, entityId, productId, name);
    }

    /******************************************** Plan-Package: END ********************************************/

    //    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/members/list")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ClaimMember> searchMember(@RequestParam (required = false, name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "perPage", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name)
            throws URISyntaxException {
        return exodusMisoolCatalogService.searchMember(pageNo, perPage, name);
    }

    @PutMapping ("/multi-get")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    List<CalimAdjudicationInsuranceProvider> ProviderList(@RequestBody List<String> providerIds) throws URISyntaxException {
        return exodusMisoolCatalogService.getProvider(providerIds);
    }

    @PutMapping ("/members/multi-get")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    List<ClaimAdjudicationMember> getMembersList(@RequestBody List<String> memberIds) throws URISyntaxException {
        return exodusMisoolCatalogService.getMemberList(memberIds);
    }

    @PutMapping ("/entities/multi-get")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    List<ClaimInsuranceEntities> multiGetEntitiesName(@RequestBody Set<String> entityIds) throws URISyntaxException {
        return exodusMisoolCatalogService.multiGetEntitiesName(entityIds);
    }

    @PutMapping ("/members/{memberId}/existing-conditions")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    PreExistingCondition updatePreExisting(@PathVariable String memberId, @RequestBody PreExistingCondition preExistingConditionRequest)
            throws URISyntaxException {
        return exodusMisoolCatalogService.updatePreExistingConsition(memberId, preExistingConditionRequest);
    }

    @GetMapping ("/members/{memberId}/existing-conditions")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    PreExistingConditionResponse getPreExistingCondition(@PathVariable String memberId) throws URISyntaxException {
        return exodusMisoolCatalogService.getPreExistingCondition(memberId);
    }

    @GetMapping ("/{providerId}/members/{memberId}/family")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    FamilyMember[] getFamilyMembers(@PathVariable String providerId, @PathVariable String memberId,
            @RequestParam (name = "policyId", required = true) String policyId) throws URISyntaxException {
        return exodusMisoolCatalogService.getFamilyMembers(providerId, memberId, policyId);
    }

    /******************************************** Shared-Plan: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{productExternalId}/shared-plans")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<SharedPlanResponse> getSharedPlanList(@PathVariable (required = true) String productExternalId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String planCode
    ) throws URISyntaxException {
        SharedPlanRequest request = new SharedPlanRequest();
        request.setProductExternalId(productExternalId);
        request.setPageNo(pageNo);
        request.setPerPage(perPage);
        request.setPlanCode(planCode);
        return exodusMisoolCatalogService.getSharedPlansList(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{productExternalId}/shared-plans")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ExodusSharedPlans createSharedPlan(@PathVariable (required = true) String productExternalId, @RequestBody ExodusSharedPlans request)
            throws URISyntaxException {

        return exodusMisoolCatalogService.createSharedPlan(productExternalId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{productExternalId}/shared-plans/search")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<SharedPlans> searchSharedPlans(@PathVariable String productExternalId,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String planCode) throws URISyntaxException {
        SharedPlanRequest request = new SharedPlanRequest();
        request.setProductExternalId(productExternalId);
        request.setPlanCode(planCode);

        return exodusMisoolCatalogService.searchSharedPlans(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/product/{productExternalId}/shared-plans/{sharedPlanLimitId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ExodusSharedPlans editSharedPlan(@PathVariable String productExternalId, @PathVariable String sharedPlanLimitId,
            @RequestBody ExodusSharedPlans request) throws URISyntaxException {
        return exodusMisoolCatalogService.editSharedPlan(productExternalId, sharedPlanLimitId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @DeleteMapping ("/product/{productExternalId}/shared-plans/{sharedPlanLimitId}/delete")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void deleteSharedPlan(@PathVariable String productExternalId, @PathVariable String sharedPlanLimitId,
            @RequestBody DeleteSharedPlanRequest request) throws URISyntaxException {
        exodusMisoolCatalogService.deleteSharedPlan(productExternalId, sharedPlanLimitId, request);
    }
    /******************************************** Shared-Plan: END ********************************************/
    /******************************************** Area: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/area-codes")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<AreaList> searchAreaCode(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "area_code", defaultValue = DEFAULT_STRING) String areaCode,
            @RequestParam (required = false, name = "code", defaultValue = DEFAULT_STRING) String countryCode
    ) throws URISyntaxException {
        return exodusMisoolCatalogService.searchAreaCode(pageNo, perPage, areaCode, countryCode);

    }

    @GetMapping ("/area-codes/{code}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    AreaList getAreaByCode(@PathVariable String code) throws URISyntaxException {
        return exodusMisoolCatalogService.getAreaByCode(code);
    }

    @PutMapping ("/doctor-blacklist-status")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getDoctorBlacklistStatus(@RequestBody Map doctorMap) throws URISyntaxException {
        return exodusMisoolCatalogService.getDoctorBlacklistStatus(doctorMap);
    }

    @PutMapping ("/hospital-blacklist-status")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getHospitalBlacklistStatus(@RequestBody Map hospitalMap) throws URISyntaxException {
        return exodusMisoolCatalogService.getHospitalBlacklistStatus(hospitalMap);
    }

    /******************************************** Area: END ********************************************/
    @PostMapping ("/products/{productExternalID}/benefit-terms-search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> searchBenefitTermsAndConditions(@NotNull @PathVariable String productExternalID,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage, @RequestBody Map searchReq)
            throws URISyntaxException {
        return exodusMisoolCatalogService.searchBenefitTermsAndConditions(pageNo, perPage, productExternalID, searchReq);

    }

    @PutMapping ("preffered-provider")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map prefferedProvider(@RequestBody Map req) throws URISyntaxException {
        return exodusMisoolCatalogService.prefferedProvider(req);
    }

    /******************************************** Product LetterSetup: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_list', 'view_only')")
    @PutMapping ("/letter-setup/product/{productId}/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ProductPlanLetterSetup> searchProductLetterSetup(@PathVariable (required = true) String productId,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name,
            @RequestParam (required = false, name = "letter_type", defaultValue = DEFAULT_STRING) String letterType,
            @RequestParam (required = false, name = "plan_codes", defaultValue = DEFAULT_STRING) String planCodes) throws URISyntaxException {
        return exodusMisoolCatalogService.searchProductLetterSetup(productId, perPage, pageNo, name, letterType, planCodes);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_add', 'restricted_write')")
    @PostMapping ("product/{productId}/letter-setup/create")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ProductPlanLetterSetup createProductLetterSetup(@PathVariable (required = true) String productId, @RequestBody ProductPlanLetterSetup request)
            throws URISyntaxException {
        return exodusMisoolCatalogService.createProductLetterSetup(request, productId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_add', 'view_only')")
    @PutMapping ("/product/{productId}/letter-setup/validate")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map validateProductLetterCreation(@PathVariable (required = true) String productId, @RequestBody ProductPlanLetterSetup request,
            @RequestParam (required = false, name = "validation_type", defaultValue = DEFAULT_STRING) String validationType)
            throws URISyntaxException {
        return exodusMisoolCatalogService.validateProductLetterCreation(productId, request, validationType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_edit', 'view_only')")
    @PutMapping ("/product/{productId}/letter-setup/{letterId}/update")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ProductPlanLetterSetup updateProductLetterSetup(@PathVariable (required = true) String productId, @PathVariable (required = true) String letterId,
            @RequestBody ProductPlanLetterSetup request) throws URISyntaxException {
        return exodusMisoolCatalogService.updateProductLetterSetup(productId, letterId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_edit', 'restricted_write')")
    @DeleteMapping ("/product/{productId}/letter-setup/{letterId}/delete")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteProductLetter(@PathVariable (required = true) String letterId, @PathVariable (required = true) String productId)
            throws URISyntaxException {
        exodusMisoolCatalogService.deleteProductLetter(productId, letterId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_list', 'view_only')")
    @PutMapping ("/letter-setup/plan/{planId}/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ProductPlanLetterSetup> searchPlanLetterSetup(@PathVariable (required = true) String planId,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name) throws URISyntaxException {
        return exodusMisoolCatalogService.searchPlanLetterSetup(planId, perPage, pageNo, name);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_add', 'restricted_write')")
    @PostMapping ("plan/{planId}/letter-setup/create")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ProductPlanLetterSetup createPlanLetterSetup(@PathVariable (required = true) String planId, @RequestBody ProductPlanLetterSetup request)
            throws URISyntaxException {
        return exodusMisoolCatalogService.createPlanLetterSetup(request, planId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_edit', 'view_only')")
    @PutMapping ("/plan/{planId}/letter-setup/{letterId}/update")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ProductPlanLetterSetup updatePlanLetterSetup(@PathVariable (required = true) String planId, @PathVariable (required = true) String letterId,
            @RequestBody ProductPlanLetterSetup request) throws URISyntaxException {
        return exodusMisoolCatalogService.updatePlanLetterSetup(planId, letterId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_edit', 'restricted_write')")
    @DeleteMapping ("/plan/{planId}/letter-setup/{letterId}/delete")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deletePlanLetter(@PathVariable (required = true) String planId, @PathVariable (required = true) String letterId) throws URISyntaxException {
        exodusMisoolCatalogService.deletePlanLetter(planId, letterId);
    }

    /******************************************** Product LetterSetup: END ********************************************/
}
