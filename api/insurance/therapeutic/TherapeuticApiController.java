package com.halodoc.batavia.controller.api.insurance.therapeutic;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.misool.claims.Therapeutic;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTPAClaimService;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.entity.common.PaginatedResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RequestMapping("api/v1/therapeutic")
@RestController
@Slf4j
public class TherapeuticApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolClaimsService misoolClaimsService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','mapping_diagnosis_therapeutic_view', 'view_only')")
    @PutMapping ("/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> searchTherapeutic(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestBody Therapeutic therapeuticData) throws URISyntaxException {
        return misoolClaimsService.searchTherapeutic(perPage, pageNo, therapeuticData);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','mapping_diagnosis_therapeutic_add', 'restricted_write')")
    @PostMapping ("/create")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createTherapeutic(@RequestBody Map therapeuticData) throws URISyntaxException {
        return misoolClaimsService.createTherapeutic(therapeuticData);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','mapping_diagnosis_therapeutic_edit', 'restricted_write')")
    @PutMapping ("/{externalId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateTherapeutic(@PathVariable String externalId, @RequestBody Map therapeuticData) throws URISyntaxException {
        return misoolClaimsService.updateTherapeutic(externalId, therapeuticData);
    }
}
