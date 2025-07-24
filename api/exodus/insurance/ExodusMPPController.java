package com.halodoc.batavia.controller.api.exodus.insurance;

import java.net.URISyntaxException;
import java.util.Map;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.NotBlank;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.exodus.misool.catalog.MPPRequest;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogMPPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/ins/internal/mpp/providers")
public class ExodusMPPController extends HalodocBaseApiController {

    @Autowired
    private ExodusMisoolCatalogMPPService exodusMisoolCatalogMPPService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @PutMapping ("/{insuranceProviderId}/products/{productExternalId}/members/{memberExternalId}/plans")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getMPPData(@PathVariable (required = true) @NotBlank String productExternalId,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name,
            @RequestParam (required = false, name = "memberId", defaultValue = DEFAULT_STRING) String memberId,
            @RequestParam (required = false, name = "policyNumber", defaultValue = DEFAULT_STRING) String policyNumber,
            @RequestParam (required = false, name = "claimNumber", defaultValue = DEFAULT_STRING) String claimNumber,
            @RequestParam (required = false, name = "fetchBenefitPlan", defaultValue = "false") boolean fetchBenefitPlan,
            @PathVariable (required = true) @NotBlank String insuranceProviderId, @PathVariable (required = true) @NotBlank String memberExternalId,
            @RequestBody Map mppRequest) throws URISyntaxException {

        return exodusMisoolCatalogMPPService.getMPPData(insuranceProviderId, productExternalId, memberExternalId, name, mppRequest, fetchBenefitPlan,
                memberId, policyNumber, claimNumber);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @PutMapping ("/{insuranceProviderId}/products/{productExternalId}/members/{memberExternalId}/member-product-plans")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getMPPDataV2(@PathVariable (required = true) @NotBlank String productExternalId,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name,
            @RequestParam (required = false, name = "memberId", defaultValue = DEFAULT_STRING) String memberId,
            @RequestParam (required = false, name = "policyNumber", defaultValue = DEFAULT_STRING) String policyNumber,
            @RequestParam (required = false, name = "claimNumber", defaultValue = DEFAULT_STRING) String claimNumber,
            @RequestParam (required = false, name = "fetchBenefitPlan", defaultValue = "false") boolean fetchBenefitPlan,
            @PathVariable (required = true) @NotBlank String insuranceProviderId, @PathVariable (required = true) @NotBlank String memberExternalId,
            @RequestBody Map mppRequest) throws URISyntaxException {

        return exodusMisoolCatalogMPPService.getMppDataV2(insuranceProviderId, productExternalId, memberExternalId, name, mppRequest,
                fetchBenefitPlan, memberId, policyNumber, claimNumber);
    }
}
