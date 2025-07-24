package com.halodoc.batavia.controller.api.finance;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.finance.CreateTransferRequest;
import com.halodoc.batavia.entity.finance.FailureAlertSummary;
import com.halodoc.batavia.entity.finance.InitJobRequest;
import com.halodoc.batavia.entity.finance.InitJobResponse;
import com.halodoc.batavia.entity.finance.JobExecutionRequest;
import com.halodoc.batavia.entity.finance.JobExecutionResponse;
import com.halodoc.batavia.entity.finance.PaymentTotalCount;
import com.halodoc.batavia.entity.finance.PharmacyPaymentTransfer;
import com.halodoc.batavia.entity.finance.ReconEntityTPAClaims;
import com.halodoc.batavia.entity.finance.RetryTransferProcessRequest;
import com.halodoc.batavia.entity.finance.SnapshotCorrection;
import com.halodoc.batavia.entity.finance.SnapshotCorrectionResponse;
import com.halodoc.batavia.entity.finance.SourceDataValidation;
import com.halodoc.batavia.entity.finance.SourceDataValidationDetails;
import com.halodoc.batavia.entity.finance.StartTransferProcessRequest;
import com.halodoc.batavia.entity.finance.SumatraJobList;
import com.halodoc.batavia.entity.finance.SumatraSnapshotTPAClaims;
import com.halodoc.batavia.entity.finance.TPACreateTransferRequest;
import com.halodoc.batavia.entity.finance.TPAPayment;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.finance.SumatraTPAClaimsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/finance/sumatra-tpa-claims")
@RestController
@Slf4j
public class SumatraTPAClaimsController extends HalodocBaseApiController {
    @Autowired
    SumatraTPAClaimsService sumatraTPAClaimsService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_source_data_validation_list', 'view_only')")
    @GetMapping ("/source-data-validation")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<SourceDataValidation<ReconEntityTPAClaims>> getPaginatedSourceDataValidation(
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "entity_id") String claimID, @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "entity_type") String entityType,
            @RequestParam (required = false, name = "failure_type") String failureType

    ) throws URISyntaxException {
        return sumatraTPAClaimsService.getPaginatedSourceDataValidation(perPage, pageNo, claimID, status, entityType, failureType);
    }

    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_source_data_validation_list', 'view_only')")
    @GetMapping ("/source-data-validation/{reconFailureId}")
    public SourceDataValidationDetails<SumatraSnapshotTPAClaims, ReconEntityTPAClaims> getSourceDataValidationsDetails(
            @PathVariable String reconFailureId) throws URISyntaxException {
        return sumatraTPAClaimsService.getSourceDataValidationDetails(reconFailureId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_failure_alert_summary', 'view_only')")
    @GetMapping ("/source-data-validation/recon-failure-codes")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public Map<String, String> getReconFailureCodes(@RequestParam (required = false, name = "is_skippable", defaultValue = "true") String isSkippable)
            throws URISyntaxException {
        return sumatraTPAClaimsService.getReconFailureCodes(isSkippable);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_failure_alert_summary', 'view_only')")
    @GetMapping ("/failure-alert-summary/orders")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<String> getFailureAlertSummaryDetails(@RequestParam (required = false, name = "failure_code") String failureCode,
            @RequestParam (required = false, name = "aggregation_start_date") Long aggregationStartDate,
            @RequestParam (required = false, name = "aggregation_end_date") Long aggregationEndDate) throws URISyntaxException {

        return sumatraTPAClaimsService.getFailureAlertSummaryDetails(failureCode, aggregationStartDate, aggregationEndDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_source_data_validation_edit','restricted_write')")
    @PostMapping ("/source-data-validation/corrections")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.ES)
    public SnapshotCorrectionResponse snapshotCorrection(@RequestBody SnapshotCorrection<SumatraSnapshotTPAClaims> snapshotCorrectionRequest)
            throws URISyntaxException {

        return sumatraTPAClaimsService.snapshotCorrectionRequest(snapshotCorrectionRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_failure_alert_summary', 'view_only')")
    @GetMapping ("/failure-alert-summary")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<FailureAlertSummary> getFailureAlertSummaryList(
            @RequestParam (required = false, name = "aggregation_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date aggregationStartDate,
            @RequestParam (required = false, name = "aggregation_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date aggregationEndDate)
            throws URISyntaxException {

        return sumatraTPAClaimsService.getFailureAlertSummaryList(aggregationStartDate, aggregationEndDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_aggregation_execution', 'view_only')")
    @GetMapping ("/aggregation/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getAggregationCycles() throws URISyntaxException {

        return sumatraTPAClaimsService.getAggregationCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_aggregation_execution', 'restricted_write')")
    @PostMapping ("/aggregation/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeAggregationJob(@RequestBody JobExecutionRequest executeAggregationJobRequest) throws URISyntaxException {

        return sumatraTPAClaimsService.executeAggregationJob(executeAggregationJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_export_execution', 'view_only')")
    @GetMapping ("/export/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getExportCycles() throws URISyntaxException {

        return sumatraTPAClaimsService.getExportCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_export_execution', 'restricted_write')")
    @PostMapping ("/export/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeExportJob(@RequestBody JobExecutionRequest executeExportJobRequest) throws URISyntaxException {

        return sumatraTPAClaimsService.executeExportJob(executeExportJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_init_job','restricted_write')")
    @PostMapping ("/init/publish")
    @ApiCategory (value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public InitJobResponse executeInitJob(@RequestBody InitJobRequest executeInitJobRequest) throws URISyntaxException {
        return sumatraTPAClaimsService.executeInitJob(executeInitJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_disbursal_job','view_only')")
    @GetMapping ("/disbursal/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getDisbursalCycles() throws URISyntaxException {
        return sumatraTPAClaimsService.getDisbursalCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_disbursal_job','restricted_write')")
    @PostMapping ("/disbursal/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public JobExecutionResponse executeDisbursalJob(@RequestBody JobExecutionRequest disbursalJobExecutionRequest) throws URISyntaxException {
        return sumatraTPAClaimsService.executeDisbursalJob(disbursalJobExecutionRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_payment','view_only')")
    @GetMapping ("/payment")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<TPAPayment> getPaginatedTPAPayments(
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "id") String Id,
            @RequestParam (required = false, name = "execution_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date executionDate,
            @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "payment_status") String paymentStatus,
            @RequestParam (required = false, name = "payment_sub_status") String paymentSubStatus,
            @RequestParam (required = false, name = "receiver_type") String receiverType,
            @RequestParam (required = false, name = "payer_type") String payerType,
            @RequestParam (required = false, name = "tpa_transfer_type") String transferType,
            @RequestParam (required = false, name = "receiver_id") String receiverId,
            @RequestParam (required = false, name = "payer_id") String payerId) throws URISyntaxException {

        return sumatraTPAClaimsService.getPaginatedTPAPayments(perPage, pageNo, Id, executionDate, status, paymentStatus, paymentSubStatus,
                receiverType, payerType, transferType, receiverId, payerId);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_payment','view_only')")
    @GetMapping ("/payment/search-count")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaymentTotalCount getTPAPaymentsCount(@RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "id") String Id,
            @RequestParam (required = false, name = "execution_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date executionDate,
            @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "payment_status") String paymentStatus,
            @RequestParam (required = false, name = "payment_sub_status") String paymentSubStatus,
            @RequestParam (required = false, name = "receiver_type") String receiverType,
            @RequestParam (required = false, name = "payer_type") String payerType,
            @RequestParam (required = false, name = "tpa_transfer_type") String transferType,
            @RequestParam (required = false, name = "receiver_id") String receiverId,
            @RequestParam (required = false, name = "payer_id") String payerId) throws URISyntaxException {

        return sumatraTPAClaimsService.getTPAClaimsPaymentCount(Id, executionDate, status, paymentStatus, paymentSubStatus, receiverType, payerType,
                transferType, receiverId, payerId);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_payment', 'view_only')")
    @GetMapping ("/transfer/{id}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public PharmacyPaymentTransfer getTransferById(@PathVariable String id) throws URISyntaxException {

        return sumatraTPAClaimsService.getTransferById(id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_payment', 'restricted_write')")
    @PostMapping ("/transfer/create")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.ES)
    public PharmacyPaymentTransfer createTransfer(@RequestBody TPACreateTransferRequest tpaCreateTransferRequest) throws URISyntaxException {

        return sumatraTPAClaimsService.createTransfer(tpaCreateTransferRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_payment', 'restricted_write')")
    @PostMapping ("/transfer/start")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.ES)
    public void startTransferProcess(@RequestBody StartTransferProcessRequest startTransferProcessRequest) throws URISyntaxException {
        sumatraTPAClaimsService.startTransferProcess(startTransferProcessRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_tpa_claims_payment', 'restricted_write')")
    @PostMapping ("/transfer/retry")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.ES)
    public void retryTransferProcess(@RequestBody RetryTransferProcessRequest retryTransferProcessRequest) throws URISyntaxException {
        sumatraTPAClaimsService.retryTransferProcess(retryTransferProcessRequest);
    }

}
