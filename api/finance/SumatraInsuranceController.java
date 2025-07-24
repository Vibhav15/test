package com.halodoc.batavia.controller.api.finance;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.finance.JobExecutionResponse;
import com.halodoc.batavia.entity.finance.SumatraJobList;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.entity.finance.JobExecutionRequest;
import com.halodoc.batavia.service.finance.SumatraInsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/finance/sumatra-insurance")
@RestController
@Slf4j
public class SumatraInsuranceController extends HalodocBaseApiController {
    @Autowired
    SumatraInsService sumatraInsService;

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_insurance_aggregation_execution', 'view_only')")
    @GetMapping("/aggregation/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getAggregationCycles() throws URISyntaxException {
        return sumatraInsService.getAggregationCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_insurance_aggregation_execution', 'restricted_write')")
    @PostMapping("/aggregation/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeAggregationJob(@RequestBody JobExecutionRequest executeAggregationJobRequest)
            throws URISyntaxException {
        return sumatraInsService.executeAggregationJob(executeAggregationJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_insurance_export_execution', 'view_only')")
    @GetMapping("/export/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getExportCycles() throws URISyntaxException {
        return sumatraInsService.getExportCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_insurance_export_execution', 'restricted_write')")
    @PostMapping("/export/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeExportJob(@RequestBody JobExecutionRequest executeExportJobRequest)
            throws URISyntaxException {
        return sumatraInsService.executeExportJob(executeExportJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_insurance_marking_execution', 'view_only')")
    @GetMapping("/marker/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getMarkerCycles() throws URISyntaxException {
        return sumatraInsService.getMarkerCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_insurance_marking_execution', 'restricted_write')")
    @PostMapping("/marker/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeMarkerJob(@RequestBody JobExecutionRequest executeMarkerJobRequest)
            throws URISyntaxException {
        return sumatraInsService.executeMarkerJob(executeMarkerJobRequest);
    }
}
