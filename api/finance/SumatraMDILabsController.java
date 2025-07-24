package com.halodoc.batavia.controller.api.finance;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.exception.custom.TooManyRequestExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.finance.FailureAlertSummary;
import com.halodoc.batavia.entity.finance.InitJobRequest;
import com.halodoc.batavia.entity.finance.InitJobResponse;
import com.halodoc.batavia.entity.finance.JobExecutionRequest;
import com.halodoc.batavia.entity.finance.JobExecutionResponse;
import com.halodoc.batavia.entity.finance.ReconEntityMdiHomelabs;
import com.halodoc.batavia.entity.finance.SnapshotCorrection;
import com.halodoc.batavia.entity.finance.SnapshotCorrectionResponse;
import com.halodoc.batavia.entity.finance.SourceDataValidation;
import com.halodoc.batavia.entity.finance.SourceDataValidationDetails;
import com.halodoc.batavia.entity.finance.SumatraJobList;
import com.halodoc.batavia.entity.finance.SumatraSnapshotMdiHomelabs;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.finance.SumatraMDILabsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/finance/sumatra-mdi-homelabs")
@RestController
@Slf4j
public class SumatraMDILabsController extends HalodocBaseApiController {

    @Autowired
    SumatraMDILabsService sumatraMDILabsService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<SourceDataValidation<ReconEntityMdiHomelabs>> getPaginatedSourceDataValidation(
            @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "entity_id") String entityId,
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "entity_type") String entityType,
            @RequestParam(required = false, name = "failure_type") String failureType

    ) throws URISyntaxException {
        return sumatraMDILabsService.getPaginatedSourceDataValidation(
                perPage,
                pageNo,
                entityId,
                status,
                entityType,
                failureType);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation/{reconFailureId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SourceDataValidationDetails<SumatraSnapshotMdiHomelabs, ReconEntityMdiHomelabs> getSourceDataValidationsDetails(
            @PathVariable String reconFailureId) throws URISyntaxException {
        return sumatraMDILabsService.getSourceDataValidationDetails(reconFailureId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_source_data_validation_edit', 'restricted_write')")
    @PostMapping("/source-data-validation/corrections")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)
    public SnapshotCorrectionResponse snapshotCorrection(
            @RequestBody SnapshotCorrection<SumatraSnapshotMdiHomelabs> snapshotCorrectionRequest)
            throws URISyntaxException {

        return sumatraMDILabsService.snapshotCorrectionRequest(snapshotCorrectionRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation/recon-failure-codes")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public Map<String, String> getReconFailureCodes(
            @RequestParam(required = false, name = "is_skippable", defaultValue = "true") String isSkippable)
            throws URISyntaxException {
        return sumatraMDILabsService.getReconFailureCodes(isSkippable);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_validation_failure_alert_summary_list', 'view_only')")
    @GetMapping("/failure-alert-summary")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<FailureAlertSummary> getFailureAlertSummaryList(
            @RequestParam(required = false, name = "execution_start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date executionStartDate,
            @RequestParam(required = false, name = "execution_end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date executionEndDate)
            throws URISyntaxException {

        return sumatraMDILabsService.getFailureAlertSummaryList(
                executionStartDate,
                executionEndDate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_validation_failure_alert_summary_list', 'view_only')")
    @GetMapping("/failure-alert-summary/orders")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<String> getFailureAlertSummaryDetails(
            @RequestParam(required = false, name = "failure_code") String failureCode,
            @RequestParam(required = false, name = "execution_start_date") Long executionStartDate,
            @RequestParam(required = false, name = "execution_end_date") Long executionEndDate)
            throws URISyntaxException {

        return sumatraMDILabsService.getFailureAlertSummaryDetails(
                failureCode,
                executionStartDate,
                executionEndDate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_init_job_execution','restricted_write')")
    @PostMapping("/init/publish")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public InitJobResponse executeInitJob(@RequestBody InitJobRequest executeInitJobRequest) throws URISyntaxException {
        return sumatraMDILabsService.executeInitJob(executeInitJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_aggregation_execution', 'view_only')")
    @GetMapping("/aggregation/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getAggregationCycles() throws URISyntaxException {

        return sumatraMDILabsService.getAggregationCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_aggregation_execution', 'restricted_write')")
    @PostMapping("/aggregation/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeAggregationJob(@RequestBody JobExecutionRequest executeAggregationJobRequest)
            throws URISyntaxException {

        return sumatraMDILabsService.executeAggregationJob(executeAggregationJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_export_execution', 'view_only')")
    @GetMapping("/export/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getExportCycles() throws URISyntaxException {

        return sumatraMDILabsService.getExportCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_mdi_homelabs_export_execution', 'restricted_write')")
    @PostMapping("/export/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeExportJob(@RequestBody JobExecutionRequest executeExportJobRequest)
            throws URISyntaxException {

        try {
            return sumatraMDILabsService.executeExportJob(executeExportJobRequest);

        } catch (HalodocWebException ex) {

            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_cd_coa_report', 'view_only')")
    @GetMapping("/coa-report/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getCoaReportCycles() throws URISyntaxException {
        return sumatraMDILabsService.getCoaReportCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_cd_coa_report', 'restricted_write')")
    @PostMapping("/coa-report/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeCoaReportJob(@RequestBody JobExecutionRequest executeCoaReportJobRequest)
            throws URISyntaxException {
        return sumatraMDILabsService.executeCoaReportJob(executeCoaReportJobRequest);
    }
}
