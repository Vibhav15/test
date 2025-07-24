package com.halodoc.batavia.controller.api.exodus.insurance;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.claims.PublicSignedUrlResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.AdminDepositDocumentResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.AdminDepositHistoryReq;
import com.halodoc.batavia.entity.exodus.misool.catalog.AdminDepositHistoryResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.AdminDepositListResponse;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusAdminDepositRequest;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusAdminDepositHistory;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusDeductibleConfig;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusInsBank;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusInsurancePlan;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusInsurancePolicy;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusInusranceBenefit;
import com.halodoc.batavia.entity.exodus.product.ProductEntity;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.ImageUploadService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogDepositManagementService;
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
@RequestMapping ("api/v1/exodus/admin-deposit")
public class AdminDepositApiController extends HalodocBaseApiController {

    @Autowired
    private ExodusMisoolCatalogDepositManagementService exodusMisoolCatalogDepositManagementService;

    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @Autowired
    private ImageUploadService imageUploadService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','admin_deposit_list', 'view_only')")
    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<AdminDepositListResponse> searchAdminDeposit(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "insurance_provider_id", defaultValue = DEFAULT_STRING)@NotBlank String insuranceProviderId,
            @RequestParam (required = false, name = "deposit_code", defaultValue = DEFAULT_STRING) String depositCode,
            @RequestParam (required = false, name = "remark", defaultValue = DEFAULT_STRING) String remark) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.searchAdminDeposit(pageNo, perPage,insuranceProviderId,depositCode, remark);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','admin_deposit_edit', 'restricted_write')")
    @PutMapping ("/{insuranceProviderId}/deposit/{adminDepositExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateAdminDeposit(@PathVariable (required = true) @NotBlank String adminDepositExternalId,@PathVariable (required = true) @NotBlank String insuranceProviderId, @RequestBody ExodusAdminDepositRequest request)
            throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.updateAdminDeposit(insuranceProviderId,adminDepositExternalId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','admin_deposit_add', 'restricted_write')")
    @PostMapping("/{insuranceProviderId}/deposit")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ExodusAdminDepositRequest createAdminDeposit(@PathVariable (required = true) @NotBlank String insuranceProviderId,@RequestBody ExodusAdminDepositRequest request) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.createAdminDeposit(insuranceProviderId,request);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("{providerExternalId}/products")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<ProductEntity> getProductsByProviderId(@PathVariable (required = true, name = "providerExternalId") @NotBlank String providerExternalId,
            @RequestParam (required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogService.getProductsByProviderId(providerExternalId, name);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("{providerExternalId}/plans")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<ExodusInsurancePlan> getPlanByProviderId(@PathVariable (required = true, name = "providerExternalId") @NotBlank String providerExternalId,
            @RequestParam (required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.getPlanByProviderId(providerExternalId, name);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("/{providerExternalId}/benefits")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<ExodusInusranceBenefit> getBenefitByProviderId(@PathVariable (required = true, name = "providerExternalId") @NotBlank String providerExternalId,
            @RequestParam (required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.getBenefitByProviderId(providerExternalId, name);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("{providerExternalId}/policies")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<ExodusInsurancePolicy> getPolicyByProviderId(@PathVariable (required = true, name = "providerExternalId") @NotBlank String providerExternalId,
            @RequestParam (required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.getPolicyByProviderId(providerExternalId, name);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','admin_deposit_view', 'view_only')")
    @GetMapping ("/banks")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusInsBank> getBanksList(@RequestParam (required = false) String bank_name, @RequestParam (required = false) String bank_code)
            throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.getBankList(bank_name, bank_code);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','admin_deposit_view', 'view_only')")
    @GetMapping ("/deductible-configuration")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusDeductibleConfig> getDeductibleConfigurationList(@RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false) String claim_type, @RequestParam (required = false) String coverage_type,
            @RequestParam (required = false) String claim_result) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.getDeductibleConfigurationList(name, claim_type, coverage_type, claim_result);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','admin_deposit_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/deposit/{adminDepositExternalId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ExodusAdminDepositRequest getAdminDepositById(@PathVariable (required = true, name = "insuranceProviderId") String insuranceProviderId,
            @PathVariable @NotBlank String adminDepositExternalId) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.getAdminDepositById(insuranceProviderId, adminDepositExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{insuranceProviderId}/deposit/{adminDepositExternalId}/history")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusAdminDepositHistory> adminDepositHistory(
            @PathVariable (required = true, name = "insuranceProviderId") @NotBlank String insuranceProviderId,
            @PathVariable (required = true, name = "adminDepositExternalId") @NotBlank String adminDepositExternalId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false) String adjustment_type, @RequestParam (required = false) String start_date,
            @RequestParam (required = false) String end_date) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService
                .adminDepositHistory(pageNo,perPage,insuranceProviderId,adminDepositExternalId,adjustment_type,start_date,end_date);
    }
    @PreAuthorize("@authorizationService.isAuthorized('insurance','admin_deposit_view', 'view_only')")
    @PostMapping("/{insuranceProviderId}/deposit/{adminDepositId}/history")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    AdminDepositHistoryResponse createAdminDepositHistory(@PathVariable (required = true) @NotBlank String insuranceProviderId ,@PathVariable (required = true) @NotBlank String adminDepositId,@RequestBody AdminDepositHistoryReq req) throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.createAdminDepositHistory(insuranceProviderId,adminDepositId,req);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','admin_deposit_view', 'restricted_write')")
    @GetMapping ("/{insuranceProviderId}/deposit/{adminId}/history/download")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    AdminDepositDocumentResponse getAdminDepositDocumentById(@PathVariable @NotBlank String insuranceProviderId, @PathVariable  @NotBlank String adminId)
            throws URISyntaxException {
        return exodusMisoolCatalogDepositManagementService.getAdminDepositDocumentById(insuranceProviderId, adminId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{documentId}/signed-public-url")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    PublicSignedUrlResponse getSignedPublicUrl(@PathVariable String documentId) throws URISyntaxException {
        return imageUploadService.getSignedPublicUrl(documentId);
    }
}
