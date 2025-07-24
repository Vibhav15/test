package com.halodoc.batavia.controller.api.insurance.claims;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.claims.ClaimIngestion;
import com.halodoc.batavia.entity.misool.claims.ClaimIngestionRequest;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/claims/ingestions")
@RestController
@Slf4j

public class ClaimIngestionsController extends HalodocBaseApiController {
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MisoolClaimsService misoolClaimsService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_ingestions_list', 'view_only')")
    @GetMapping
    PaginatedResult<ClaimIngestion> getClaimIngestions(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "entity_type", defaultValue = "membership") String entityType,
            @RequestParam (required = false, name = "insurance_provider_id") String insuranceProviderId) throws URISyntaxException {
        return misoolClaimsService.getClaimIngestions(pageNo, perPage, insuranceProviderId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_ingestions_upload', 'restricted_write')")
    @PostMapping
    ClaimIngestion submitClaimIngestion(@RequestBody ClaimIngestionRequest claimIngestionRequest) throws URISyntaxException {
        return misoolClaimsService.submitClaimIngestion(claimIngestionRequest);
    }
}
