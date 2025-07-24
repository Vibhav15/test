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
import com.halodoc.batavia.service.exodus.MisoolReportingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/custom-claim-reports")
public class ExodusCustomClaimReportsApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolReportingService misoolReportingService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','tpa_claim_report_history', 'view_only')")
    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<TpaClaimReport> searchReports(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "updated_at") Date updatedAt, @RequestParam (name = "report_group") String reportGroup,
            @RequestParam (required = false, name = "report_type", defaultValue = "") String reportType) throws URISyntaxException {
        return misoolReportingService.searchClaimCustomReports(pageNo, perPage, updatedAt, reportGroup, reportType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','tpa_claim_report_add', 'restricted_write')")
    @PostMapping ("/generate")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    TpaClaimReport generateReport(@RequestBody Map payload) throws URISyntaxException {
        return misoolReportingService.generateCustomClaimReport(payload);
    }
}
