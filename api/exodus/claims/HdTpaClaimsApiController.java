package com.halodoc.batavia.controller.api.exodus.claims;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.claims.TpaClaimReport;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusHdTpaClaimService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/hdtpa")
public class HdTpaClaimsApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusHdTpaClaimService exodusHdTPAClaimService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','tpa_claim_report_history', 'view_only')")
    @GetMapping ("/sync")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<TpaClaimReport> getHdTpaDataSyncReports(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "provider_id") String providerId,
            @RequestParam (required = false, name = "template", defaultValue = "") String template,
            @RequestParam (required = false, name = "created_at") Date createdDate) throws URISyntaxException {
        return exodusHdTPAClaimService.getHdTpaDataSyncReports(pageNo, perPage, providerId, createdDate, template);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','tpa_claim_report_add', 'restricted_write')")
    @PostMapping ("/sync")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    TpaClaimReport generateHdTpaDataSyncReport(@RequestBody Map payload) throws URISyntaxException {
        return exodusHdTPAClaimService.generateHdTpaDataSyncReport(payload);
    }
}
