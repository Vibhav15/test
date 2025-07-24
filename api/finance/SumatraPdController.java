package com.halodoc.batavia.controller.api.finance;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.finance.*;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.exception.custom.TooManyRequestExpectedException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.finance.SumatraPdService;
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
@RequestMapping ("api/v1/finance/sumatra-pd")
@RestController
@Slf4j
public class SumatraPdController extends HalodocBaseApiController {
    @Autowired
    SumatraPdService sumatraPdService;

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_source_data_validation_list', 'view_only')")
    @GetMapping ("/source-data-validation")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<SourceDataValidation<ReconEntityPd>> getPaginatedSourceDataValidation(
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "order_id") String orderId, @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "entity_type") String entityType,
            @RequestParam (required = false, name = "failure_type") String failureType) throws URISyntaxException {

        return sumatraPdService.getPaginatedSourceDataValidation(perPage, pageNo, orderId, status, entityType, failureType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_source_data_validation_list', 'view_only')")
    @GetMapping ("/source-data-validation/{reconFailureId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SourceDataValidationDetails<SumatraSnapshotPd, ReconEntityPd> getSourceDataValidationDetails(@PathVariable String reconFailureId)
            throws URISyntaxException {

        return sumatraPdService.getSourceDataValidationDetails(reconFailureId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_source_data_validation_edit', 'restricted_write')")
    @PostMapping ("/source-data-validation/corrections")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.ES)
    public SnapshotCorrectionResponse snapshotCorrection(@RequestBody SnapshotCorrection<SumatraSnapshotPd> snapshotCorrectionRequest)
            throws URISyntaxException {

        return sumatraPdService.snapshotCorrection(snapshotCorrectionRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_failure_alert_summary_list', 'view_only')")
    @GetMapping ("/failure-alert-summary")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<FailureAlertSummary> getFailureAlertSummaryList(
            @RequestParam (name = "execution_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date executionStartDate,
            @RequestParam (required = false, name = "execution_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date executionEndDate,
            @RequestParam (required = false, name = "date_filter_type") String dateFilterType) throws URISyntaxException {

        return sumatraPdService.getFailureAlertSummaryList(executionStartDate, executionEndDate, dateFilterType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_failure_alert_summary_list', 'view_only')")
    @GetMapping ("/failure-alert-summary/orders")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<String> getFailureAlertSummaryDetails(@RequestParam (name = "failure_code") String failureCode,
            @RequestParam (required = false, name = "date_filter_type") String dateFilterType,
            @RequestParam (name = "execution_start_date") Long executionStartDate, @RequestParam (name = "execution_end_date") Long executionEndDate)
            throws URISyntaxException {

        return sumatraPdService.getFailureAlertSummaryDetails(failureCode, dateFilterType, executionStartDate, executionEndDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_source_data_validation_list', 'view_only')")
    @GetMapping ("/source-data-validation/recon-failure-codes")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public Map<String, String> getReconFailureCodes() throws URISyntaxException {
        return sumatraPdService.getReconFailureCodes();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_payment_view', 'view_only')")
    @GetMapping ("/payment")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<PharmacyPayment> getPaginatedPharmacyPayment(
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "pharmacy_id") String pharmacyId,
            @RequestParam (required = false, name = "period_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date periodStartDate,
            @RequestParam (required = false, name = "period_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date periodEndDate,
            @RequestParam (required = false, name = "bank_name") String bankName,
            @RequestParam (required = false, name = "hd_payment_id") String hdPaymentId,
            @RequestParam (required = false, name = "payment_status") String paymentStatus,
            @RequestParam (required = false, name = "balance_operator", defaultValue = "equals") String balanceOperator,
            @RequestParam (required = false, name = "net_payment_amount") Double netPaymentAmount,
            @RequestParam (required = false, name = "remarks") String remarks,
            @RequestParam (required = false, name = "payment_group_id") String paymentGroupId,
            @RequestParam (required = false, name = "disbursal_cycle_type") String disbursalCycleType) throws URISyntaxException {

        return sumatraPdService.getPaginatedPharmacyPayment(perPage, pageNo, pharmacyId, periodStartDate, periodEndDate, bankName, hdPaymentId,
                paymentStatus, balanceOperator, netPaymentAmount, remarks, paymentGroupId, disbursalCycleType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_payment_view', 'view_only')")
    @GetMapping ("/payment/search-count")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaymentTotalCount getPharmacyPaymentCount(@RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "pharmacy_id") String pharmacyId,
            @RequestParam (required = false, name = "period_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date periodStartDate,
            @RequestParam (required = false, name = "period_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date periodEndDate,
            @RequestParam (required = false, name = "bank_name") String bankName,
            @RequestParam (required = false, name = "hd_payment_id") String hdPaymentId,
            @RequestParam (required = false, name = "payment_status") String paymentStatus,
            @RequestParam (required = false, name = "balance_operator", defaultValue = "equals") String balanceOperator,
            @RequestParam (required = false, name = "net_payment_amount") Double netPaymentAmount,
            @RequestParam (required = false, name = "remarks") String remarks,
            @RequestParam (required = false, name = "payment_group_id") String paymentGroupId,
            @RequestParam (required = false, name = "disbursal_cycle_type") String disbursalCycleType) throws URISyntaxException {

        return sumatraPdService.getPharmacyPaymentCount(pharmacyId, periodStartDate, periodEndDate, bankName, hdPaymentId, paymentStatus,
                balanceOperator, netPaymentAmount, remarks, paymentGroupId, disbursalCycleType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_payment_send', 'restricted_write')")
    @GetMapping ("/transfer/{id}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public PharmacyPaymentTransfer getTransferById(@PathVariable String id) throws URISyntaxException {

        return sumatraPdService.getTransferById(id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_payment_send', 'restricted_write')")
    @PostMapping ("/transfer/create")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.ES)
    public PharmacyPaymentTransfer createTransfer(@RequestBody CreateTransferRequest createTransferRequest) throws URISyntaxException {

        return sumatraPdService.createTransfer(createTransferRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_payment_send', 'restricted_write')")
    @PostMapping ("/transfer/start")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.ES)
    public void startTransferProcess(@RequestBody StartTransferProcessRequest startTransferProcessRequest) throws URISyntaxException {
        sumatraPdService.startTransferProcess(startTransferProcessRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_payment_send', 'restricted_write')")
    @PostMapping ("/transfer/retry")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.ES)
    public void retryTransferProcess(@RequestBody RetryTransferProcessRequest retryTransferProcessRequest) throws URISyntaxException {
        sumatraPdService.retryTransferProcess(retryTransferProcessRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_job_execution', 'restricted_write')")
    @PostMapping ("/init/publish")
    @ApiCategory (value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeInitJob(@RequestBody Map executeInitJobRequest) throws URISyntaxException {
        return sumatraPdService.executeInitJob(executeInitJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_aggregation_execution', 'view_only')")
    @GetMapping ("/aggregation/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getAggregationCycles() throws URISyntaxException {
        return sumatraPdService.getAggregationCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_aggregation_execution', 'restricted_write')")
    @PostMapping ("/aggregation/execute")
    @ApiCategory (value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeAggregationJob(@RequestBody Map executeAggregationJobRequest) throws URISyntaxException {
        try {
            return sumatraPdService.executeAggregationJob(executeAggregationJobRequest);
        } catch (HalodocWebException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(), ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_export_execution', 'view_only')")
    @GetMapping ("/export/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getExportCycles() throws URISyntaxException {
        return sumatraPdService.getExportCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_export_execution', 'restricted_write')")
    @PostMapping ("/export/execute")
    @ApiCategory (value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeExportJob(@RequestBody Map executeExportJobRequest) throws URISyntaxException {
        return sumatraPdService.executeExportJob(executeExportJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_disbursal_execution', 'view_only')")
    @GetMapping ("/disbursal/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getDisbursalCycles() throws URISyntaxException {
        return sumatraPdService.getDisbursalCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_disbursal_execution', 'restricted_write')")
    @PostMapping ("/disbursal/execute")
    @ApiCategory (value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeDisbursalJob(@RequestBody Map executeDisbursalJobRequest) throws URISyntaxException {

        return sumatraPdService.executeDisbursalJob(executeDisbursalJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_marking_execution', 'view_only')")
    @GetMapping ("/marker/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getMarkerCycles() throws URISyntaxException {
        return sumatraPdService.getMarkerCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_marking_execution', 'restricted_write')")
    @PostMapping ("/marker/execute")
    @ApiCategory (value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeMarkerJob(@RequestBody Map executeMarkerJobRequest) throws URISyntaxException {
        return sumatraPdService.executeMarkerJob(executeMarkerJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_flagged_orders', 'view_only')")
    @GetMapping ("/flagged-orders/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getFlaggedOrders() throws URISyntaxException {

        return sumatraPdService.getFlaggedOrders();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_pd_flagged_orders', 'restricted_write')")
    @PostMapping ("/flagged-orders/delete")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.ES)
    public Map executeFlaggedOrderJob(@RequestBody Map executeFlaggedOrderJobRequest) throws URISyntaxException {
        return sumatraPdService.executeFlaggedOrderJob(executeFlaggedOrderJobRequest);
    }
}
