package com.halodoc.batavia.controller.api.finance;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.finance.DocumentsReference;
import com.halodoc.batavia.entity.finance.JobExecutionRequest;
import com.halodoc.batavia.entity.finance.JobExecutionResponse;
import com.halodoc.batavia.entity.finance.SumatraJobList;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.finance.SumatraTaxSubmissionService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/finance/sumatra-tax-submission")
@RestController
@Slf4j
public class SumatraTaxSubmissionController extends HalodocBaseApiController {

    @Autowired
    SumatraTaxSubmissionService sumatraTaxSubmissionService;

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_tax_submissions', 'view_only')")
    @GetMapping("/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getTaxSubmissionCycles() throws URISyntaxException {
        return sumatraTaxSubmissionService.getTaxSubmissionCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_tax_submissions', 'restricted_write')")
    @PostMapping("/execute")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public JobExecutionResponse executeTaxSubmissionCycle(
            @RequestBody JobExecutionRequest executionTaxSubmissionRequest) throws URISyntaxException {
        return sumatraTaxSubmissionService.executeTaxSubmissionCycle(executionTaxSubmissionRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_tax_submissions', 'view_only')")
    @PostMapping("/document")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public DocumentsReference getTaxSubmissionDocument(@RequestBody JobExecutionRequest executionTaxSubmissionRequest)
            throws URISyntaxException {
        return sumatraTaxSubmissionService.getTaxSubmissionDocument(executionTaxSubmissionRequest);
    }

}
