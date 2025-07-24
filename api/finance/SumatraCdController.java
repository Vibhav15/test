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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.finance.CreateTransferRequest;
import com.halodoc.batavia.entity.finance.DoctorPayment;
import com.halodoc.batavia.entity.finance.DoctorPaymentTransfer;
import com.halodoc.batavia.entity.finance.FailureAlertSummary;
import com.halodoc.batavia.entity.finance.PaymentTotalCount;
import com.halodoc.batavia.entity.finance.ReconEntity;
import com.halodoc.batavia.entity.finance.ReconFailureMetric;
import com.halodoc.batavia.entity.finance.RetryTransferProcessRequest;
import com.halodoc.batavia.entity.finance.SnapshotCorrection;
import com.halodoc.batavia.entity.finance.SnapshotCorrectionResponse;
import com.halodoc.batavia.entity.finance.SourceDataValidation;
import com.halodoc.batavia.entity.finance.SourceDataValidationDetails;
import com.halodoc.batavia.entity.finance.StartTransferProcessRequest;
import com.halodoc.batavia.entity.finance.SumatraJobList;
import com.halodoc.batavia.entity.finance.SumatraSnapshot;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.finance.SumatraCdService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/finance/sumatra-cd")
@RestController
@Slf4j
public class SumatraCdController extends HalodocBaseApiController {
    @Autowired
    SumatraCdService sumatraCdService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_source_data_validation_list', 'view_only')")
    @GetMapping ("/source-data-validation")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<SourceDataValidation<ReconEntity>> getPaginatedSourceDataValidation(
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "order_id") String orderId, @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "entity_type") String entityType,
            @RequestParam (required = false, name = "failure_type") String failureType) throws URISyntaxException {

        return sumatraCdService.getPaginatedSourceDataValidation(perPage, pageNo, orderId, status, entityType, failureType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_source_data_validation_list', 'view_only')")
    @GetMapping ("/source-data-validation/{reconFailureId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SourceDataValidationDetails<SumatraSnapshot, ReconEntity> getSourceDataValidationDetails(@PathVariable String reconFailureId)
            throws URISyntaxException {

        return sumatraCdService.getSourceDataValidationDetails(reconFailureId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_source_data_validation_edit', 'restricted_write')")
    @PostMapping ("/source-data-validation/corrections")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.ES)
    public SnapshotCorrectionResponse snapshotCorrection(@RequestBody SnapshotCorrection<SumatraSnapshot> snapshotCorrectionRequest)
            throws URISyntaxException {

        return sumatraCdService.snapshotCorrectionRequest(snapshotCorrectionRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_failure_alert_summary_list', 'view_only')")
    @GetMapping ("/failure-alert-summary")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<FailureAlertSummary> getFailureAlertSummaryList(
            @RequestParam (required = false, name = "aggregation_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date aggregationStartDate,
            @RequestParam (required = false, name = "aggregation_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date aggregationEndDate)
            throws URISyntaxException {

        return sumatraCdService.getFailureAlertSummaryList(aggregationStartDate, aggregationEndDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_failure_alert_summary_list', 'view_only')")
    @GetMapping ("/failure-alert-summary/orders")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public List<String> getFailureAlertSummaryDetails(@RequestParam (name = "failure_code") String failureCode,
            @RequestParam (required = false, name = "aggregation_start_date") Long aggregationStartDate,
            @RequestParam (required = false, name = "aggregation_end_date") Long aggregationEndDate) throws URISyntaxException {

        return sumatraCdService.getFailureAlertSummaryDetails(failureCode, aggregationStartDate, aggregationEndDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_payment_view', 'view_only')")
    @GetMapping ("/payment")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<DoctorPayment> getPaginatedDoctorPayment(
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "doctor_id") String doctorId,
            @RequestParam (required = false, name = "cycle_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date cycleStartDate,
            @RequestParam (required = false, name = "cycle_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date cycleEndDate,
            @RequestParam (required = false, name = "bank_name") String bankName,
            @RequestParam (required = false, name = "hd_payment_id") String hdPaymentId,
            @RequestParam (required = false, name = "payment_status") String paymentStatus,
            @RequestParam (required = false, name = "balance_operator", defaultValue = "equals") String balanceOperator,
            @RequestParam (required = false, name = "net_payment_amount") Double netPaymentAmount,
            @RequestParam (required = false, name = "remarks") String remarks,
            @RequestParam (required = false, name = "doctor_type") String doctorType,
            @RequestParam (required = false, name = "doctor_speciality") String doctorSpeciality,
            @RequestParam (required = false, name = "service_type") String serviceType,
            @RequestParam (required = false, name = "payment_group_id") String paymentGroupId) throws URISyntaxException {

        return sumatraCdService.getPaginatedDoctorPayment(perPage, pageNo, doctorId, cycleStartDate, cycleEndDate, bankName, hdPaymentId,
                paymentStatus, balanceOperator, netPaymentAmount, remarks, doctorType, doctorSpeciality, serviceType, paymentGroupId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_payment_view', 'view_only')")
    @GetMapping ("/payment/search-count")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaymentTotalCount getDoctorPaymentCount(@RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "doctor_id") String doctorId,
            @RequestParam (required = false, name = "cycle_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date cycleStartDate,
            @RequestParam (required = false, name = "cycle_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Date cycleEndDate,
            @RequestParam (required = false, name = "bank_name") String bankName,
            @RequestParam (required = false, name = "hd_payment_id") String hdPaymentId,
            @RequestParam (required = false, name = "payment_status") String paymentStatus,
            @RequestParam (required = false, name = "balance_operator", defaultValue = "equals") String balanceOperator,
            @RequestParam (required = false, name = "net_payment_amount") Double netPaymentAmount,
            @RequestParam (required = false, name = "remarks") String remarks,
            @RequestParam (required = false, name = "doctor_type") String doctorType,
            @RequestParam (required = false, name = "doctor_speciality") String doctorSpeciality,
            @RequestParam (required = false, name = "service_type") String serviceType,
            @RequestParam (required = false, name = "payment_group_id") String paymentGroupId) throws URISyntaxException {

        return sumatraCdService.getDoctorPaymentCount(doctorId, cycleStartDate, cycleEndDate, bankName, hdPaymentId, paymentStatus, balanceOperator,
                netPaymentAmount, remarks, doctorType, doctorSpeciality, serviceType, paymentGroupId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_payment_send', 'restricted_write')")
    @GetMapping ("/transfer/{id}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public DoctorPaymentTransfer getTransferById(@PathVariable String id) throws URISyntaxException {

        return sumatraCdService.getTransferById(id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_payment_send', 'restricted_write')")
    @PostMapping ("/transfer/create")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.ES)
    public DoctorPaymentTransfer createTransfer(@RequestBody CreateTransferRequest createTransferRequest) throws URISyntaxException {

        return sumatraCdService.createTransfer(createTransferRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_payment_send', 'restricted_write')")
    @PostMapping ("/transfer/start")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.ES)
    public void startTransferProcess(@RequestBody StartTransferProcessRequest startTransferProcessRequest) throws URISyntaxException {
        sumatraCdService.startTransferProcess(startTransferProcessRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_payment_send', 'restricted_write')")
    @PostMapping ("/transfer/retry")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.ES)
    public void retryTransferProcess(@RequestBody RetryTransferProcessRequest retryTransferProcessRequest) throws URISyntaxException {
        sumatraCdService.retryTransferProcess(retryTransferProcessRequest);
    }

    @GetMapping ("/recon-failure-codes")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public List<String> getReconFailureCodes() throws URISyntaxException {
        if (!authorizationService.isAuthorized("finance", "sumatra_cd_failure_alert_summary_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return sumatraCdService.getReconFailureCodes();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_failure_alert_summary_list', 'view_only')")
    @GetMapping ("/failure-alert-summary/metrics")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    public PaginatedResult<ReconFailureMetric> getReconFailureMetrics(
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "failure_codes") String failureCodes,
            @RequestParam (required = false, name = "aggregation_start_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Long aggregationStartDate,
            @RequestParam (required = false, name = "aggregation_end_date") @DateTimeFormat (pattern = "yyyy-MM-dd") Long aggregationEndDate)
            throws URISyntaxException {

        return sumatraCdService.getReconFailureMetrics(perPage, pageNo, failureCodes, aggregationStartDate, aggregationEndDate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_job_execution', 'restricted_write')")
    @PostMapping ("/init/publish")
    @ApiCategory (value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    public Map executeInitJob(@RequestBody Map executeInitJobRequest) throws URISyntaxException {

        return sumatraCdService.executeInitJob(executeInitJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_aggregation_execution', 'view_only')")
    @GetMapping ("/aggregation/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getAggregationCycles() throws URISyntaxException {

        return sumatraCdService.getAggregationCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_aggregation_execution', 'restricted_write')")
    @PostMapping ("/aggregation/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public Map executeAggregationJob(@RequestBody Map executeAggregationJobRequest) throws URISyntaxException {

        return sumatraCdService.executeAggregationJob(executeAggregationJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_export_execution', 'view_only')")
    @GetMapping ("/export/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getExportCycles() throws URISyntaxException {

        return sumatraCdService.getExportCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_export_execution', 'restricted_write')")
    @PostMapping ("/export/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public Map executeExportJob(@RequestBody Map executeExportJobRequest) throws URISyntaxException {
        return sumatraCdService.executeExportJob(executeExportJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_disbursal_execution', 'view_only')")
    @GetMapping ("/disbursal/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getDisbursalCycles() throws URISyntaxException {

        return sumatraCdService.getDisbursalCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_disbursal_execution', 'restricted_write')")
    @PostMapping ("/disbursal/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public Map executeDisbursalJob(@RequestBody Map executeDisbursalJobRequest) throws URISyntaxException {

        return sumatraCdService.executeDisbursalJob(executeDisbursalJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_marking_execution', 'view_only')")
    @GetMapping ("/marker/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getMarkerCycles() throws URISyntaxException {

        return sumatraCdService.getMarkerCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_marking_execution', 'restricted_write')")
    @PostMapping ("/marker/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public Map executeMarkerJob(@RequestBody Map executeMarkerJobRequest) throws URISyntaxException {

        return sumatraCdService.executeMarkerJob(executeMarkerJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_cogs_execution', 'view_only')")
    @GetMapping ("/ap-cogs/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getApCogsCycles() throws URISyntaxException {

        return sumatraCdService.getApCogsCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_cogs_execution', 'restricted_write')")
    @PostMapping ("/ap-cogs/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public Map executeApCogsJob(@RequestBody Map executeApCogsJobRequest) throws URISyntaxException {

        return sumatraCdService.executeApCogsJob(executeApCogsJobRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_coa_report', 'view_only')")
    @GetMapping ("/coa-report/cycles")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    public SumatraJobList getCoaReportCycles() throws URISyntaxException {

        return sumatraCdService.getCoaReportCycles();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('finance','sumatra_cd_coa_report', 'restricted_write')")
    @PostMapping ("/coa-report/execute")
    @ApiCategory (value = ApiType.BATCH_PROCESSING, verticalName = Vertical.ES)
    public Map executeCoaReportJob(@RequestBody Map executeCoaReportJobRequest) throws URISyntaxException {

        return sumatraCdService.executeCoaReportJob(executeCoaReportJobRequest);
    }
}
