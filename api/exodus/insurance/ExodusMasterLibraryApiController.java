package com.halodoc.batavia.controller.api.exodus.insurance;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.claims.ExodusCaseCategory;
import com.halodoc.batavia.entity.exodus.claims.ExodusReasonCodeRequest;
import com.halodoc.batavia.entity.exodus.claims.ExodusReasonCodeResponse;
import com.halodoc.batavia.entity.exodus.claims.ExodusReasonCodeSearch;
import com.halodoc.batavia.entity.exodus.claims.ExodusSubCaseCategorySearch;
import com.halodoc.batavia.entity.exodus.claims.MiscellaneousItem;
import com.halodoc.batavia.entity.exodus.communication.ParameterList;
import com.halodoc.batavia.entity.exodus.communication.WhatsappConfiguration;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusSubCaseCategory;
import com.halodoc.batavia.entity.exodus.misool.catalog.LetterSetup;
import com.halodoc.batavia.entity.exodus.misool.catalog.MemberFlag;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.exodus.communication.ExodusCommunicationService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTPAClaimService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus-master-library")
public class ExodusMasterLibraryApiController extends HalodocBaseApiController {
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @Autowired
    private ExodusTPAClaimService exodusTPAClaimService;

    @Autowired
    private ExodusCommunicationService exodusCommunicationService;

    /**************************************** Member Flag: START ****************************************/
    @GetMapping ("/member-flags")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<MemberFlag> searchMemberFlags(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "member_flag_code", defaultValue = DEFAULT_STRING) String memberFlagCode,
            @RequestParam (required = false, name = "status", defaultValue = "all") String status,
            @RequestParam (required = false, name = "member_flag_name", defaultValue = DEFAULT_STRING) String memberFlagName)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "member_flag_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return exodusMisoolCatalogService.searchMemberFlags(pageNo, perPage, memberFlagCode, status, memberFlagName);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_flag_add', 'restricted_write')")
    @PostMapping ("/member-flags")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    MemberFlag createMemberFlags(@RequestBody MemberFlag memberFlag) throws URISyntaxException {
        return exodusMisoolCatalogService.createMemberFlag(memberFlag);
    }

    /**************************************** Member Flag: END ****************************************/
    /**************************************** Miscellaneous Item: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','miscellaneous_catalog_add', 'restricted_write')")
    @PostMapping ("/miscellaneous-items")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    MiscellaneousItem createMiscellaneousItem(@RequestBody MiscellaneousItem miscellaneousItem) throws URISyntaxException {
        return exodusMisoolCatalogService.createMiscellaneousItem(miscellaneousItem);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','miscellaneous_catalog_edit', 'restricted_write')")
    @PatchMapping ("/miscellaneous-items/{miscellaneousItemId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    MiscellaneousItem updateMiscellaneousItem(@PathVariable String miscellaneousItemId, @RequestBody Map miscellaneousItem)
            throws URISyntaxException {
        return exodusMisoolCatalogService.updateMiscellaneousItem(miscellaneousItemId, miscellaneousItem);
    }

    /**************************************** Miscellaneous Item: END ****************************************/
    /**************************************** Case Categories: START ****************************************/
    @GetMapping ("/case-categories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusCaseCategory> searchCaseCategories(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "case_category_code", defaultValue = DEFAULT_STRING) String caseCategoryCode,
            @RequestParam (required = false, name = "code_search") String codeSearch,
            @RequestParam (required = false, name = "case_category_name", defaultValue = DEFAULT_STRING) String caseCategoryName,
            @RequestParam (required = false, name = "service_type", defaultValue = "all") String serviceType,
            @RequestParam (required = false, name = "coverage_type", defaultValue = "all") String coverageType,
            @RequestParam (required = false, name = "status", defaultValue = "all") String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "case_category_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return exodusTPAClaimService.searchCaseCategories(pageNo, perPage, caseCategoryCode, codeSearch, caseCategoryName, serviceType, coverageType,
                status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_category_add', 'restricted_write')")
    @PostMapping ("/case-categories")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ExodusCaseCategory createCaseCategories(@RequestBody ExodusCaseCategory exodusCaseCatgegory) throws URISyntaxException {
        return exodusTPAClaimService.createCaseCategories(exodusCaseCatgegory);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_category_edit', 'restricted_write')")
    @PatchMapping ("/case-categories/{caseCategoryId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ExodusCaseCategory updateCaseCategories(@PathVariable String caseCategoryId, @RequestBody Map caseCategories)
            throws URISyntaxException {
        return exodusTPAClaimService.updateCaseCategories(caseCategoryId, caseCategories);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_category_edit', 'restricted_write')")
    @PatchMapping ("/case-categories")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    List updateBulkCaseCategoriesStatus(@RequestParam (required = false, name = "status") String status,
            @RequestBody List<String> caseCategoriesExternalIdList) throws URISyntaxException {
        return exodusTPAClaimService.updateBulkCaseCategoriesStatus(status, caseCategoriesExternalIdList);
    }

    @PutMapping ("/case-category-list")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getCaseCategoryListByIds(@RequestBody List<String> request) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "case_monitoring_op_view", "view_only") && !authorizationService.isAuthorized("insurance",
        "claim_cashless_view", "view_only")) {
    throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
}
        log.info("********** Inside ExodusMasterLibraryApiController.getCaseCategoryListByIds ***************");
        log.info("*********** Request is sent with request body: " + request);
        return exodusTPAClaimService.getCaseCategoryListByIds(request);
    }

    /**************************************** Case Categories: END ****************************************/
    /**************************************** Sub Case Category: START ****************************************/
    @GetMapping ("/sub-case-categories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusSubCaseCategorySearch> searchSubCaseCategories(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "case_category_code", defaultValue = DEFAULT_STRING) String caseCategoryCode,
            @RequestParam (required = false, name = "sub_case_category_code", defaultValue = DEFAULT_STRING) String subCaseCategoryCode,
            @RequestParam (required = false, name = "sub_case_category_name", defaultValue = DEFAULT_STRING) String subCaseCategoryName,
            @RequestParam (required = false, name = "status", defaultValue = DEFAULT_STRING) String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "sub_case_category_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return exodusTPAClaimService.searchSubCaseCategories(pageNo, perPage, caseCategoryCode, subCaseCategoryCode, subCaseCategoryName, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_case_category_add', 'restricted_write')")
    @PostMapping ("/case-categories/{caseCategoryId}/sub-case-categories")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ExodusSubCaseCategory createSubCaseCategories(@PathVariable String caseCategoryId,
            @RequestBody ExodusSubCaseCategory subCaseCategories) throws URISyntaxException {
        return exodusTPAClaimService.createSubCaseCategories(caseCategoryId, subCaseCategories);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_case_category_edit', 'restricted_write')")
    @PatchMapping ("/case-categories/{caseCategoryId}/sub-case-categories/{subCaseCategoryId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ExodusSubCaseCategory updateSubCaseCategories(@PathVariable String caseCategoryId,
            @PathVariable String subCaseCategoryId, @RequestBody Map subCaseCategories) throws URISyntaxException {
        return exodusTPAClaimService.updateSubCaseCategories(caseCategoryId, subCaseCategoryId, subCaseCategories);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','sub_case_category_edit', 'restricted_write')")
    @PatchMapping ("/sub-case-categories")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    List updateBulkSubCaseCategoriesStatus(@RequestParam (required = false, name = "status") String status,
            @RequestBody List<String> subCaseCategoriesExternalIdList) throws URISyntaxException {
        return exodusTPAClaimService.updateBulkSubCaseCategoriesStatus(status, subCaseCategoriesExternalIdList);
    }

    /**************************************** Sub Case Category: END ****************************************/
    /**************************************** Reason Code: START ****************************************/
    @GetMapping ("/reason-codes")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusReasonCodeSearch> searchReasonCode(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = DEFAULT_STRING) String name,
            @RequestParam (required = false, name = "status", defaultValue = DEFAULT_STRING) String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "reason_code_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return exodusTPAClaimService.searchReasonCode(pageNo, perPage, name, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','reason_code_add', 'restricted_write')")
    @PostMapping ("/reason-codes")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ExodusReasonCodeResponse createReasonCode(@RequestBody ExodusReasonCodeRequest request) throws URISyntaxException {
        return exodusTPAClaimService.createReasonCode(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','reason_code_edit', 'restricted_write')")
    @PatchMapping ("/reason-codes/{reasonCodesId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ExodusReasonCodeResponse updateSubCaseCategories(@PathVariable (required = true) String reasonCodesId,
            @RequestBody ExodusReasonCodeRequest request) throws URISyntaxException {
        return exodusTPAClaimService.updateReasonCode(reasonCodesId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','reason_code_edit', 'restricted_write')")
    @PatchMapping ("/reason-codes")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    List updateBulkReasonCodeStatus(@RequestParam (required = false, name = "status") String status,
            @RequestBody List<String> reasonCodeExternalIdList) throws URISyntaxException {
        return exodusTPAClaimService.updateBulkReasonCodeStatus(status, reasonCodeExternalIdList);
    }
    /**************************************** Reason Code: END ****************************************/
    /**************************************** Whatsapp Configuration: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','whatsapp_configuration_list', 'view_only')")
    @GetMapping ("/whatsapp-configuration")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<WhatsappConfiguration> searchWhatsappConfiguration(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "template_name", defaultValue = DEFAULT_STRING) String templateName,
            @RequestParam (required = false, name = "insurance_provider_id", defaultValue = DEFAULT_STRING) String insuranceProviderId,
            @RequestParam (required = false, name = "coverage_type", defaultValue = DEFAULT_STRING) String coverageType,
            @RequestParam (required = false, name = "transaction_type", defaultValue = DEFAULT_STRING) String transactionType,
            @RequestParam (required = false, name = "claim_status", defaultValue = DEFAULT_STRING) String claimStatus) throws URISyntaxException {
        return exodusCommunicationService
                .searchWhatsappConfiguration(pageNo, perPage, templateName, insuranceProviderId, coverageType, transactionType, claimStatus);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','whatsapp_configuration_view', 'restricted_write')")
    @GetMapping ("/whatsapp-configuration/{templateExternalId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    WhatsappConfiguration getWhatsappConfigurationDetail(@PathVariable (required = true) String templateExternalId) throws URISyntaxException {
        return exodusCommunicationService.getWhatsappConfigurationDetail(templateExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','whatsapp_configuration_add', 'restricted_write')")
    @PostMapping ("/whatsapp-configuration")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    WhatsappConfiguration createWhatsappConfiguration(@RequestBody WhatsappConfiguration request) throws URISyntaxException {
        return exodusCommunicationService.createWhatsappConfiguration(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','whatsapp_configuration_edit', 'restricted_write')")
    @PutMapping ("/whatsapp-configuration/{templateExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateWhatsappConfiguration(@PathVariable (required = true) String templateExternalId, @RequestBody WhatsappConfiguration request)
            throws URISyntaxException {
        return exodusCommunicationService.updateWhatsappConfiguration(templateExternalId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','whatsapp_configuration_edit', 'restricted_write')")
    @DeleteMapping ("/whatsapp-configuration/{templateExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteWhatsappConfiguration(@PathVariable (required = true) String templateExternalId) throws URISyntaxException {
        exodusCommunicationService.deleteWhatsappConfiguration(templateExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','whatsapp_configuration_view', 'restricted_write')")
    @GetMapping ("/whatsapp-configuration/parameter-list")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ParameterList> getParameterList(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "parameter_name", defaultValue = DEFAULT_STRING) String parameterName,
            @RequestParam (required = false, name = "description", defaultValue = DEFAULT_STRING) String description) throws URISyntaxException {
        return exodusCommunicationService.getParameterList(pageNo, perPage, parameterName, description);
    }
    /**************************************** Whatsapp Configuration: END ****************************************/
    /**************************************** Letter Setup: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_list', 'view_only')")
    @PutMapping ("/letter-setup/search")
    @ApiCategory(value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<LetterSetup> searchLetterSetup(@RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name,
            @RequestParam (required = false, name = "letter_type", defaultValue = DEFAULT_STRING) String letterType,
            @RequestParam (required = false, name = "content_type", defaultValue = DEFAULT_STRING) String contentType) throws URISyntaxException {
        return exodusMisoolCatalogService.searchLetterSetup(perPage, pageNo, name, letterType, contentType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_add', 'restricted_write')")
    @PostMapping ("/letter-setup/create")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    LetterSetup createLetterSetup(@RequestBody LetterSetup request) throws URISyntaxException {
        return exodusMisoolCatalogService.createLetterSetup(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_view', 'restricted_write')")
    @GetMapping ("/letter-setup/{letterSetupId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    LetterSetup getLetterSetupById(@PathVariable (required = true) String letterSetupId) throws URISyntaxException {
        return exodusMisoolCatalogService.getLetterSetupById(letterSetupId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_edit', 'restricted_write')")
    @PutMapping ("/letter-setup/{letterId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    LetterSetup updateLetterSetup(@PathVariable (required = true) String letterId, @RequestBody LetterSetup request) throws URISyntaxException {
        return exodusMisoolCatalogService.updateLetterSetup(letterId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','letter_setup_edit', 'restricted_write')")
    @DeleteMapping ("/letter-setup/{letterId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteLetter(@PathVariable (required = true) String letterId) throws URISyntaxException {
        exodusMisoolCatalogService.deleteLetter(letterId);
    }

    /**************************************** Letter Setup: END ****************************************/
}
