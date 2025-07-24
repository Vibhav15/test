package com.halodoc.batavia.controller.api.insurance.claims;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.claims.PaymentReportFilterRequest;
import com.halodoc.batavia.entity.misool.claims.PaymentReportRequest;
import com.halodoc.batavia.entity.misool.claims.PaymentReportResponse;
import com.halodoc.batavia.service.misool_claims.MisoolClaimsService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/claims/payment-reports")
@RestController
@Slf4j

public class ClaimPaymentReportApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolClaimsService misoolClaimsService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_payment_report_list', 'view_only')")
    @PutMapping
    PaginatedResult<PaymentReportResponse> getClaimPaymentReports(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestBody PaymentReportFilterRequest paymentReportFilterRequest) throws URISyntaxException {
        return misoolClaimsService.getClaimPaymentReports(pageNo, perPage, paymentReportFilterRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','generate_claim_payment_report', 'restricted_write')")
    @PostMapping
    void generateClaimPaymentReport(@RequestBody PaymentReportRequest paymentReport) {
        misoolClaimsService.generateClaimPaymentReport(paymentReport);
    }
}
