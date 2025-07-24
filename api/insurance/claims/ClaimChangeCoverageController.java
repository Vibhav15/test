package com.halodoc.batavia.controller.api.insurance.claims;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.claims.ChangeCoverageClaimsReport;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsChangeCoverageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/change-coverage-claims-report")

public class ClaimChangeCoverageController extends HalodocBaseApiController {

    @Autowired
    private MisoolClaimsChangeCoverageService misoolClaimsChangeCoverageService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','change_coverage_list', 'restricted_write')")
    @PostMapping()
    ChangeCoverageClaimsReport[] createInsuranceProviderEntity(@RequestBody String template) throws URISyntaxException {
        return misoolClaimsChangeCoverageService.createChangeCoverageClaimsReport(template);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','change_coverage_list', 'view_only')")
    @GetMapping("/report-history")
    PaginatedResult<ChangeCoverageClaimsReport> searchReportHistory(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {

        return misoolClaimsChangeCoverageService.searchReportHistory(pageNo, perPage);
    }
}
