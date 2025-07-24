package com.halodoc.batavia.controller.api.exodus.insurance;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.halodoc.audit.sdk.Auditor;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.NotBlank;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.ImageUpload;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusInsurancePolicyDocumentResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusInsurancePolicyTncResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.PolicyTncRequest;
import com.halodoc.batavia.entity.exodus.misool.catalog.TncDocumentResponse;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/insurance/providers/exodus-policy-tnc")
public class ExodusPolicyTncApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private Auditor auditor;

    /******************************************** Policy-Document: START ********************************************/
    @GetMapping ("/insurance-documents/{moduleType}/{moduleExternalId}")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusInsurancePolicyDocumentResponse> getInsuranceModuleDocuments(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @PathVariable (name = "moduleType") @NotBlank String moduleType,
            @PathVariable (name = "moduleExternalId") @NotBlank String moduleExternalId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.exodus_claim_reimbursement_policy_tnc_documents_accessed, Action.get, ActorType.cc_user, EntityType.exodus_claim_reimbursement, moduleExternalId,
                Map.of("moduleType", moduleType), ChannelType.http);
        return exodusMisoolCatalogService.getInsuranceModuleDocuments(pageNo, perPage, moduleType, moduleExternalId);
    }

    /******************************************** Policy-Document: END ********************************************/


    /******************************************** TnC-Document: START ********************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PostMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/validate-policy-tnc")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map validatePolicyTnC(@PathVariable @NotBlank String insuranceProviderId, @PathVariable @NotBlank String entityId,
            @PathVariable @NotBlank String productId, @RequestParam String documentId) {
        return exodusMisoolCatalogService.validatePolicyTnC(insuranceProviderId, entityId, productId, documentId);
    }

    @PutMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/exoduspolicytnc/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusInsurancePolicyTncResponse> getPolicyTncList(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @PathVariable @NotBlank String insuranceProviderId, @PathVariable @NotBlank String entityId, @PathVariable @NotBlank String productId,
            @RequestBody PolicyTncRequest request) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        request.setProductId(productId);
        request.setProviderId(insuranceProviderId);
        request.setEntityId(entityId);
        return exodusMisoolCatalogService.getPolicyTncList(perPage, pageNo, request);
    }

    @PutMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/member-tnc/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusInsurancePolicyTncResponse> getMemberTncList(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "100") Integer perPage,
            @PathVariable @NotBlank String insuranceProviderId, @PathVariable @NotBlank String entityId, @PathVariable @NotBlank String productId,
            @RequestBody PolicyTncRequest request) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_view", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        request.setProductId(productId);
        request.setProviderId(insuranceProviderId);
        request.setEntityId(entityId);
        return exodusMisoolCatalogService.getMemberTncList(perPage, pageNo, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/status/{tncExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void upsertTncStatus(@PathVariable @NotBlank String insuranceProviderId, @PathVariable @NotBlank String entityId,
            @PathVariable @NotBlank String productId, @PathVariable @NotBlank String tncExternalId, @RequestParam String status)
            throws URISyntaxException {
        exodusMisoolCatalogService.upsertPolicyTncStatsus(insuranceProviderId, entityId, productId, tncExternalId, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/download")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    List<ImageUpload> downloadDocuments(@RequestBody Set<String> documentIds) {
        return exodusMisoolCatalogService.multiGetDocument(documentIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/last-uploaded-document")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    TncDocumentResponse getTnCDocumentById(@PathVariable  @NotBlank String insuranceProviderId, @PathVariable  @NotBlank String productId, @PathVariable  @NotBlank String entityId)
            throws URISyntaxException {
        return exodusMisoolCatalogService.getTnCDocumentById(insuranceProviderId, entityId, productId);
    }

    @GetMapping ("/{insuranceProviderId}/entities/{entityId}/products/{productId}/tnc/list")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> getTnCList(   @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,@PathVariable  @NotBlank String insuranceProviderId, @PathVariable  @NotBlank String productId, @PathVariable  @NotBlank String entityId) throws URISyntaxException {
        return exodusMisoolCatalogService.getTnCList(pageNo, perPage,insuranceProviderId, entityId, productId);
    }
    /******************************************** TnC-Document: END ********************************************/
}
