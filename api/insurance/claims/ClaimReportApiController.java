package com.halodoc.batavia.controller.api.insurance.claims;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import com.halodoc.batavia.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.claims.ClaimReport;
import com.halodoc.batavia.entity.misool.claims.ClaimReportRequest;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v2/claims-reports")
@RestController
@Slf4j
public class ClaimReportApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolClaimsService misoolClaimsService;
    @Autowired
    private ImageUploadService uploaderService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_report_list', 'view_only')")
    @GetMapping
    PaginatedResult<ClaimReport> getClaimReports(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "provider") String providerId,
            @RequestParam (required = false, name = "template") String template, @RequestParam (required = false, name = "start_date_time") Date date)

            throws URISyntaxException {
        return misoolClaimsService.getClaimReports(pageNo, perPage, providerId, template, date);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_report_add', 'restricted_write')")
    @PostMapping
    ClaimReportRequest generateClaimReport(@RequestBody ClaimReportRequest claimReport) {
        return misoolClaimsService.generateClaimReport(claimReport);
    }

    @PutMapping
    ResponseEntity<String> getClaimReportsDownloadURL(@RequestBody String documentExternalId) throws URISyntaxException {
        return uploaderService.getSignedDocumentURL(documentExternalId);
    }

}
