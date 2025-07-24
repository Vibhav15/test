package com.halodoc.batavia.controller.api.exodus.claims;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.entity.exodus.claims.*;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTPAClaimService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/claim-reimbursement/disabilities")
public class ExodusClaimsDisabilityManagementApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusTPAClaimService exodusTPAClaimService;

    /**************************************** Disability Management : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/{disabilityNo}")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> getAllClaimsMappedToDisability(@PathVariable String disabilityNo,
    @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
    @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {
        return exodusTPAClaimService.getClaimDisabilitiesOfPrimaryAndDependents(disabilityNo, perPage, pageNo);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/{disabilityNo}/dependents")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List getDependentClaimsOfPrimaryClaimDisability(@PathVariable String disabilityNo) throws URISyntaxException {
        return exodusTPAClaimService.getDependentClaimsOfPrimaryClaimDisability(disabilityNo);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @GetMapping ("/claim/{claimExternalId}/disability/{disabilityNo}/reinstatement")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    DisabilityReinstantmentResponse getDisabilityReinstantmentDate(@PathVariable String claimExternalId , @PathVariable String disabilityNo) throws URISyntaxException {
        return exodusTPAClaimService.getDisabilityReinstantmentDate(claimExternalId ,disabilityNo);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'restricted_write')")
    @PostMapping("/claim/{claimExternalId}/disability")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public void createDisability(@PathVariable String claimExternalId,  @RequestBody DisabilityManagementRequest req) throws URISyntaxException {
        exodusTPAClaimService.createDisability(claimExternalId, req);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_list', 'view_only')")
    @PutMapping("/claim/member/{memberExternalId}/disability")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> getAllClaimMappedtoDisability(@PathVariable String memberExternalId,@RequestBody Map searchClaims) throws URISyntaxException{
        return exodusTPAClaimService.getAllClaimMappedtoDisability(memberExternalId,searchClaims);
    }

}
