package com.halodoc.batavia.controller.api.finance;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.finance.*;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.exception.custom.TooManyRequestExpectedException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.finance.SumatraMedisendService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/v1/finance/sumatra-medisend")
@RestController
@Slf4j
public class SumatraMedisendController extends HalodocBaseApiController {

    @Autowired
    SumatraMedisendService sumatraMedisendService;

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<SourceDataValidation<ReconEntityMedisend>> getPaginatedSourceDataValidation(
            @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "entity_id") String entityId,
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "entity_type") String entityType,
            @RequestParam(required = false, name = "failure_type") String failureType) throws URISyntaxException {

        return sumatraMedisendService.getPaginatedSourceDataValidation(
                perPage,
                pageNo,
                entityId,
                status,
                entityType,
                failureType);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation/{reconFailureId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SourceDataValidationDetails<SumatraSnapshotMedisend, ReconEntityMedisend> getSourceDataValidationDetails(
            @PathVariable String reconFailureId) throws URISyntaxException {

        return sumatraMedisendService.getSourceDataValidationDetails(reconFailureId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_source_data_validation_edit', 'restricted_write')")
    @PostMapping("/source-data-validation/corrections")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)
    public SnapshotCorrectionResponse snapshotCorrection(
            @RequestBody SnapshotCorrection<SumatraSnapshotMedisend> snapshotCorrectionRequest)
            throws URISyntaxException {

        return sumatraMedisendService.snapshotCorrectionRequest(snapshotCorrectionRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation/recon-failure-codes")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public Map<String, String> getReconFailureCodes(
            @RequestParam(required = false, name = "is_skippable", defaultValue = "true") String isSkippable)
            throws URISyntaxException {
        return sumatraMedisendService.getReconFailureCodes(isSkippable);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_failure_alert_summary_list', 'view_only')")
    @GetMapping("/failure-alert-summary")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<FailureAlertSummary> getFailureAlertSummaryList() throws URISyntaxException {

        return sumatraMedisendService.getFailureAlertSummaryList();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_failure_alert_summary_list', 'view_only')")
    @GetMapping("/failure-alert-summary/orders")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<String> getFailureAlertSummaryDetails(
            @RequestParam(name = "failure_code") String failureCode) throws URISyntaxException {
        return sumatraMedisendService.getFailureAlertSummaryDetails(
                failureCode);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_job_execution', 'restricted_write')")
    @PostMapping("/init/publish")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeInitJob(@RequestBody Map executeInitJobRequest) throws URISyntaxException {

        return sumatraMedisendService.executeInitJob(executeInitJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_aggregation_execution', 'view_only')")
    @GetMapping("/aggregation/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getAggregationCycles() throws URISyntaxException {

        return sumatraMedisendService.getAggregationCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_aggregation_execution', 'restricted_write')")
    @PostMapping("/aggregation/execute")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeAggregationJob(@RequestBody Map executeAggregationJobRequest) throws URISyntaxException {

        return sumatraMedisendService.executeAggregationJob(executeAggregationJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_export_execution', 'view_only')")
    @GetMapping("/export/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getExportCycles() throws URISyntaxException {

        return sumatraMedisendService.getExportCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_export_execution', 'restricted_write')")
    @PostMapping("/export/execute")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeExportJob(@RequestBody Map executeExportJobRequest) throws URISyntaxException {

        try {
            return sumatraMedisendService.executeExportJob(executeExportJobRequest);
        } catch (HalodocWebException ex) {

            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }

    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_marking_execution', 'view_only')")
    @GetMapping("/marker/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getMarkerCycles() throws URISyntaxException {

        return sumatraMedisendService.getMarkerCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_medisend_marking_execution', 'restricted_write')")
    @PostMapping("/marker/execute")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeMarkerJob(@RequestBody Map executeMarkerJobRequest) throws URISyntaxException {

        return sumatraMedisendService.executeMarkerJob(executeMarkerJobRequest);
    }
}
