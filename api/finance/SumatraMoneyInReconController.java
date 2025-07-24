package com.halodoc.batavia.controller.api.finance;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.finance.FailureAlertSummary;
import com.halodoc.batavia.entity.finance.InitJobRequest;
import com.halodoc.batavia.entity.finance.InitJobResponse;
import com.halodoc.batavia.entity.finance.JobCycles;
import com.halodoc.batavia.entity.finance.JobExecutionRequest;
import com.halodoc.batavia.entity.finance.JobExecutionResponse;
import com.halodoc.batavia.entity.finance.ReconEntityMoneyInRecon;
import com.halodoc.batavia.entity.finance.SnapshotCorrection;
import com.halodoc.batavia.entity.finance.SnapshotCorrectionResponse;
import com.halodoc.batavia.entity.finance.SourceDataValidation;
import com.halodoc.batavia.entity.finance.SourceDataValidationDetails;
import com.halodoc.batavia.entity.finance.SumatraJobList;
import com.halodoc.batavia.entity.finance.SumatraSnapshotMoneyInRecon;
import com.halodoc.batavia.entity.finance.ScroogePGResult;
import com.halodoc.batavia.entity.finance.UpdateApprovalStatus;
import com.halodoc.batavia.entity.finance.UpdatePgBankApprovalStatus;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.entity.finance.BankPgResult;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.finance.SumatraMoneyInReconService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/finance/sumatra-money-in-recon")
@RestController
@Slf4j
public class SumatraMoneyInReconController extends HalodocBaseApiController {

    @Autowired
    SumatraMoneyInReconService sumatraMoneyInReconService;

    @Autowired
    AuthorizationService authorizationService;

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<SourceDataValidation<ReconEntityMoneyInRecon>> getPaginatedSourceDataValidation(
            @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "entity_id") String entityId,
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "entity_type") String entityType,
            @RequestParam(required = false, name = "failure_type") String failureType) throws URISyntaxException {
        return sumatraMoneyInReconService.getPaginatedSourceDataValidation(
                perPage,
                pageNo,
                entityId,
                status,
                entityType,
                failureType);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation/{reconFailureId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SourceDataValidationDetails<SumatraSnapshotMoneyInRecon, ReconEntityMoneyInRecon> getSourceDataValidationDetails(
            @PathVariable String reconFailureId) throws URISyntaxException {

        return sumatraMoneyInReconService.getSourceDataValidationDetails(reconFailureId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_source_data_validation_edit', 'restricted_write')")
    @PostMapping("/source-data-validation/corrections")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)
    public SnapshotCorrectionResponse snapshotCorrection(
            @RequestBody SnapshotCorrection<SumatraSnapshotMoneyInRecon> snapshotCorrectionRequest)
            throws URISyntaxException {

        return sumatraMoneyInReconService.snapshotCorrectionRequest(snapshotCorrectionRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_source_data_validation_list', 'view_only')")
    @GetMapping("/source-data-validation/recon-failure-codes")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public Map<String, String> getReconFailureCodes(
            @RequestParam(required = false, name = "is_skippable", defaultValue = "true") final String isSkippable)
            throws URISyntaxException {
        return sumatraMoneyInReconService.getReconFailureCodes(isSkippable);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_failure_alert_summary_list', 'view_only')")
    @GetMapping("/failure-alert-summary")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<FailureAlertSummary> getFailureAlertSummaryList(
            @RequestParam(required = false, name = "execution_start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date executionStartDate,
            @RequestParam(required = false, name = "execution_end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date executionEndDate)
            throws URISyntaxException {

        return sumatraMoneyInReconService.getFailureAlertSummaryList(
                executionStartDate,
                executionEndDate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_failure_alert_summary_list', 'view_only')")
    @GetMapping("/failure-alert-summary/orders")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<String> getFailureAlertSummaryDetails(
            @RequestParam(required = false, name = "failure_code") String failureCode,
            @RequestParam(required = false, name = "execution_start_date") Long executionStartDate,
            @RequestParam(required = false, name = "execution_end_date") Long executionEndDate)
            throws URISyntaxException {

        return sumatraMoneyInReconService.getFailureAlertSummaryDetails(
                failureCode,
                executionStartDate,
                executionEndDate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_scrooge_vs_payment', 'view_only')")
    @GetMapping("/scrooge-pg")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<ScroogePGResult> getScroogePGList(
            @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "service_types") String serviceTypes,
            @RequestParam(required = false, name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false, name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false, name = "payment_providers") String paymentProviders,
            @RequestParam(required = false, name = "payment_methods") String paymentMethods,
            @RequestParam(required = false, name = "statuses") String statuses,
            @RequestParam(required = false, name = "provider_approval_code") String providerApprovalCode,
            @RequestParam(required = false, name = "order_id") String orderId,
            @RequestParam(required = false, name = "cycle_start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date cycleStartDate,
            @RequestParam(required = false, name = "cycle_end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date cycleEndDate

    ) throws URISyntaxException {
        return sumatraMoneyInReconService.getScroogePgComparison(
                perPage,
                pageNo,
                serviceTypes,
                startDate,
                endDate,
                paymentProviders,
                paymentMethods,
                statuses,
                providerApprovalCode,
                orderId,
                cycleStartDate,
                cycleEndDate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_scrooge_vs_payment', 'restricted_write')")
    @PutMapping("/update-status")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)
    public UpdateApprovalStatus executeUpdateStatus(@RequestBody UpdateApprovalStatus updateApprovalStatus)
            throws URISyntaxException {

        return sumatraMoneyInReconService.executeUpdateStatus(updateApprovalStatus);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_scrooge_vs_payment', 'view_only')")
    @GetMapping("/scrooge-pg/get-cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public List<JobCycles> getScroogePgCycles() throws URISyntaxException {
        return sumatraMoneyInReconService.getScroogePgCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_payment_vs_bank', 'view_only')")
    @GetMapping("/pg-bank")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<BankPgResult> getBankPgList(
            @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false, name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false, name = "settlement_banks") String settlementBanks,
            @RequestParam(required = false, name = "payment_providers") String paymentProviders,
            @RequestParam(required = false, name = "statuses") String statuses,
            @RequestParam(required = false, name = "cycle_start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date cycleStartDate,
            @RequestParam(required = false, name = "cycle_end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date cycleEndDate)
            throws URISyntaxException {
        return sumatraMoneyInReconService.getBankPgComparison(
                perPage,
                pageNo,
                startDate,
                endDate,
                settlementBanks,
                paymentProviders,
                statuses,
                cycleStartDate,
                cycleEndDate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_payment_vs_bank', 'restricted_write')")
    @PutMapping("/pg-bank-update-status")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)
    public UpdatePgBankApprovalStatus executePgBankUpdateStatus(
            @RequestBody UpdatePgBankApprovalStatus updatePgBankApprovalStatus) throws URISyntaxException {

        return sumatraMoneyInReconService.executePgBankUpdateStatus(updatePgBankApprovalStatus);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_job_execution', 'restricted_write')")
    @PostMapping("/init/publish")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public InitJobResponse executeInitJob(@RequestBody InitJobRequest executeInitJobRequest) throws URISyntaxException {

        return sumatraMoneyInReconService.executeInitJob(executeInitJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_aggregation_execution', 'view_only')")
    @GetMapping("/aggregation/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getAggregationCycles() throws URISyntaxException {

        return sumatraMoneyInReconService.getAggregationCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_aggregation_execution', 'restricted_write')")
    @PostMapping("/aggregation/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeAggregationJob(@RequestBody JobExecutionRequest executeAggregationJobRequest)
            throws URISyntaxException {

        return sumatraMoneyInReconService.executeAggregationJob(executeAggregationJobRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_export_execution', 'view_only')")
    @GetMapping("/export/cycles")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getExportCycles() throws URISyntaxException {

        return sumatraMoneyInReconService.getExportCycles();
    }

    @PreAuthorize("@authorizationService.isAuthorized('finance','sumatra_money_in_recon_export_execution', 'restricted_write')")
    @PostMapping("/export/execute")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeExportJob(@RequestBody JobExecutionRequest executeExportJobRequest)
            throws URISyntaxException {

        return sumatraMoneyInReconService.executeExportJob(executeExportJobRequest);
    }

}
