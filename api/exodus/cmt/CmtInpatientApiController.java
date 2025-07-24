package com.halodoc.batavia.controller.api.exodus.cmt;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.exodus.cmt.CaseConsolidatedHistoryResponse;
import com.halodoc.batavia.entity.exodus.cmt.SearchCaseConsolidatedHistoryRequest;
import com.halodoc.batavia.entity.exodus.cmt.ServiceDetailsResponse;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.cmt.BulkCreateOrUpdateIpCaseDocumentsRequest;
import com.halodoc.batavia.entity.exodus.cmt.CaseDetailResponse;
import com.halodoc.batavia.entity.exodus.cmt.CaseStatusHistory;
import com.halodoc.batavia.entity.exodus.cmt.CmtDocumentIP;
import com.halodoc.batavia.entity.exodus.cmt.CmtSearch;
import com.halodoc.batavia.entity.exodus.cmt.CreateIpService;
import com.halodoc.batavia.entity.exodus.cmt.CreateIpServiceRequest;
import com.halodoc.batavia.entity.exodus.cmt.DocumentChecklist;
import com.halodoc.batavia.entity.exodus.cmt.GetServiceDetailsList;
import com.halodoc.batavia.entity.exodus.cmt.IglApprovedAlerts;
import com.halodoc.batavia.entity.exodus.cmt.Investigation;
import com.halodoc.batavia.entity.exodus.cmt.InvestigationCategories;
import com.halodoc.batavia.entity.exodus.cmt.IpDocumentChecklistRequest;
import com.halodoc.batavia.entity.exodus.cmt.ManageSuperseding;
import com.halodoc.batavia.entity.exodus.cmt.SuperSedingRequest;
import com.halodoc.batavia.entity.exodus.cmt.SupersedingResponse;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.cmt.CMTService;
import com.halodoc.batavia.service.exodus.cmt.CreateIpRoomBoardList;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/case-monitoring/inpatient/case")
public class CmtInpatientApiController extends HalodocBaseApiController {
    @Autowired
    private CMTService cmtService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<CmtSearch> searchCMTInpatientList(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "member_external_id", defaultValue = DEFAULT_STRING) String memberExternalId,
            @RequestParam (required = false, name = "case_number", defaultValue = DEFAULT_STRING) String caseNumber,
            @RequestParam (required = false, name = "provider_location_id", defaultValue = DEFAULT_STRING) String providerLocationId,
            @RequestParam (required = false, name = "process_status", defaultValue = DEFAULT_STRING) String processStatus,
            @RequestParam (required = false, name = "admission_start_date", defaultValue = DEFAULT_STRING) String startDate,
            @RequestParam (required = false, name = "admission_end_date", defaultValue = DEFAULT_STRING) String endDate,
            @RequestParam (required = false, name = "investigation_status", defaultValue = DEFAULT_STRING) String investigationStatus,
            @RequestParam (required = false, name = "member_name", defaultValue = DEFAULT_STRING) String memberName,
            @RequestParam (required = false, name = "member_additional_info", defaultValue = DEFAULT_STRING) String memberAdditionalInfo,
            @RequestParam (required = false, name = "superseeding_status", defaultValue = DEFAULT_STRING) String superseedingStatus,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String statuses,
            @RequestParam (required = false, name = "sorting_column", defaultValue = "") String sortingColumns,
            @RequestParam (required = false, name = "sorting_order", defaultValue = "DESC") String sortingOrders) throws URISyntaxException {
        return cmtService.searchCMTInpatientList(pageNo, perPage, memberExternalId, caseNumber, providerLocationId, processStatus, startDate, endDate,
                investigationStatus, superseedingStatus, statuses, sortingColumns, sortingOrders, memberName, memberAdditionalInfo);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}")
    @AuditedAccess(eventType = EventType.cmt_inpatient_case_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    CaseDetailResponse getCMTInpatientDetails(@PathVariable @AuditedEntityId String caseId) throws URISyntaxException {
        return cmtService.getCMTInpatientDetails(caseId);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/update")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateIpCaseDetail(@PathVariable String caseId, @RequestBody Map caseRequestObj) throws URISyntaxException {
        return cmtService.updateIpCaseDetail(caseId, caseRequestObj);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}/case-status-history")
    @AuditedAccess(eventType = EventType.cmt_inpatient_case_status_history_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<CaseStatusHistory> getCMTIPStatusHistory(@PathVariable @AuditedEntityId String caseId,
            @RequestParam (required = false, name = "action", defaultValue = "PROCESS_STATUS_UPDATED") String action) throws URISyntaxException {
        return cmtService.getCMTIPStatusHistory(caseId, action);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @GetMapping ("{caseId}/daily-monitoring")
    @AuditedAccess(eventType = EventType.cmt_inpatient_daily_monitoring_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<List> getDailyMonitoring(@PathVariable @AuditedEntityId String caseId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "5") Integer perPage) throws URISyntaxException {
        return cmtService.getDailyMonitoring(caseId, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @GetMapping ("{caseId}/daily-monitoring-documents")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<CmtDocumentIP> getDailyMonitoringDocuments(@PathVariable String caseId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage) throws URISyntaxException {
        return cmtService.getDailyMonitoringDocuments(caseId, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @PostMapping ("{caseId}/daily-monitoring-documents")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    CmtDocumentIP createDailyMonitoringDocument(@PathVariable String caseId, @RequestBody Map req) throws URISyntaxException {
        return cmtService.createDailyMonitoringDocument(caseId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @PostMapping ("{caseId}/daily-monitoring-documents/multiple")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    Map bulkCreateDailyMonitoringDocument(@PathVariable String caseId, @RequestBody BulkCreateOrUpdateIpCaseDocumentsRequest req) throws URISyntaxException {
        return cmtService.bulkCreateCaseDailyMonitoringDocuments(caseId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @PutMapping ("{caseId}/daily-monitoring-documents")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    Map bulkUpdateCaseDailyMonitoringDocuments(@PathVariable String caseId, @RequestBody BulkCreateOrUpdateIpCaseDocumentsRequest req)
            throws URISyntaxException {
        return cmtService.bulkUpdateCaseDailyMonitoringDocuments(caseId, req);
    }

    @PutMapping ("{caseId}/daily-monitoring-documents/{documentId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public CmtDocumentIP updateDailyMonitoringDocument(@PathVariable String caseId, @PathVariable String documentId, @RequestBody CmtDocumentIP req)
            throws URISyntaxException {
        return cmtService.updateDailyMonitoringDocument(caseId, documentId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @PatchMapping ("{caseId}/daily-monitoring/save")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    List<Map> saveDailyMonitoring(@PathVariable String caseId, @RequestBody List<Map> requestBody) throws URISyntaxException {
        return cmtService.saveDailyMonitoring(caseId, requestBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PostMapping ("/{caseId}/investigations")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Investigation createInvestigation(@PathVariable String caseId, @RequestBody Investigation request) throws URISyntaxException {
        return cmtService.createInvestigation(request, caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}/investigations")
    @AuditedAccess(eventType = EventType.cmt_inpatient_investigation_history_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Investigation> getInvestigationHistoryList(@PathVariable @AuditedEntityId String caseId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return cmtService.getInvestigationHistoryList(caseId, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}/investigations/{investigationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Investigation getInvestigationDetails(@PathVariable String caseId, @PathVariable String investigationId) throws URISyntaxException {
        return cmtService.getInvestigationDetails(caseId, investigationId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}/investigations/{investigationId}/documents")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<Investigation> getInvestigationFormHistory(@PathVariable String caseId, @PathVariable String investigationId) throws URISyntaxException {
        return cmtService.getInvestigationFormHistory(caseId, investigationId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/cancel")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CaseDetailResponse cancelInpatientCases(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.cancelInpatientCases(caseId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @GetMapping ("/investigation-categories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InvestigationCategories> getInvestigationCategory(
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name) throws URISyntaxException {
        return cmtService.getInvestigationCategory(name);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("/investigation-sub-categories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InvestigationCategories> getInvestigationSubCategory(
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name, @RequestBody List<String> externalId)
            throws URISyntaxException {
        return cmtService.getInvestigationSubCategory(name, externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/investigations/{investigationId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateInvestigationDetail(@PathVariable String caseId, @PathVariable String investigationId, @RequestBody Map caseRequestObj)
            throws URISyntaxException {
        return cmtService.updateInvestigationDetail(caseId, investigationId, caseRequestObj);
    }

    @GetMapping ("/{caseId}/documents")
    @AuditedAccess(eventType = EventType.cmt_inpatient_case_documents_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<CmtDocumentIP> getCmtIPDocuments(@PathVariable @AuditedEntityId String caseId,
            @RequestParam (required = false, name = "document_status") String documentStatuses,
            @RequestParam (required = false, name = "document_type") String documentType,
            @RequestParam (required = false, name = "document_category") String documentCategory,
            @RequestParam (required = false, name = "document_processing_statuses") String documentProcessStatus,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo) throws URISyntaxException {
        return cmtService.getCmtIPDocuments(caseId, documentType, documentCategory, documentStatuses, perPage, pageNo, documentProcessStatus);
    }

    @PostMapping ("{caseId}/documents")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public CmtDocumentIP createIPDocuments(@PathVariable String caseId, @RequestBody CmtDocumentIP req) throws URISyntaxException {
        return cmtService.createIPDocuments(caseId, req);
    }

    @PostMapping ("{caseId}/documents/multiple")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    public Map bulkCreateCaseDocuments(@PathVariable String caseId, @RequestBody BulkCreateOrUpdateIpCaseDocumentsRequest req)
            throws URISyntaxException {
        return cmtService.bulkCreateCaseDocuments(caseId, req);
    }

    @PutMapping ("{caseId}/documents")
    @ApiCategory(value = ApiType.BATCH_PROCESSING, verticalName = Vertical.INS)
    public Map bulkUpdateCaseDocuments(@PathVariable String caseId, @RequestBody BulkCreateOrUpdateIpCaseDocumentsRequest req)
            throws URISyntaxException {
        return cmtService.bulkUpdateCaseDocuments(caseId, req);
    }

    @PutMapping ("{caseId}/documents/{documentId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public CmtDocumentIP updateIPDocuments(@PathVariable String caseId, @PathVariable String documentId, @RequestBody CmtDocumentIP req)
            throws URISyntaxException {
        return cmtService.updateIPDocuments(caseId, documentId, req);
    }

    @PostMapping ("{caseId}/documents/checklists")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public Map createIPDocumentChecklists(@PathVariable String caseId, @RequestBody IpDocumentChecklistRequest req) throws URISyntaxException {
        return cmtService.createIPDocumentChecklists(caseId, req);
    }

    @GetMapping ("{caseId}/documents/checklists")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<DocumentChecklist> getIPDocumentChecklists(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.getIPDocumentChecklists(caseId);
    }

    @PostMapping ("{caseId}/superseding")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public SupersedingResponse createSuperSeeding(@PathVariable String caseId, @RequestBody SuperSedingRequest request) throws URISyntaxException {
        return cmtService.createSuperSeeding(caseId, request);
    }

    @PostMapping ("{caseId}/superseding/{seedingId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public SupersedingResponse getSuperSedingById(@PathVariable String caseId, @PathVariable String seedingId) throws URISyntaxException {
        return cmtService.getSuperSedingById(caseId, seedingId);
    }

    @PatchMapping ("{caseId}/superseding/{superSedingId}/manage_superseding")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public SupersedingResponse manageSuperseding(@PathVariable String caseId, @PathVariable String superSedingId,
            @RequestBody ManageSuperseding request) throws URISyntaxException {
        return cmtService.manageSuperseding(caseId, superSedingId, request);
    }

    @GetMapping ("{caseId}/superseding/history")
    @AuditedAccess(eventType = EventType.cmt_inpatient_superseeding_history_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<SupersedingResponse> getSupersedingHistory(@PathVariable @AuditedEntityId String caseId,
            @RequestParam (name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (name = "status", required = false) String status) throws URISyntaxException {
        return cmtService.getSupersedingHistory(caseId, perPage, pageNo, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/generate-igl")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map generateIGL(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.generateIGL(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @GetMapping ("/investigation-question-categories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InvestigationCategories> getInvestigationQuestionCategory(
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name) throws URISyntaxException {
        return cmtService.getInvestigationQuestionCategory(name);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @GetMapping ("/investigation-response-categories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InvestigationCategories> getInvestigationResponseCategory(
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name) throws URISyntaxException {
        return cmtService.getInvestigationResponseCategory(name);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/investigations/{investigationId}/details")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map saveContinueInvestigationDetail(@PathVariable String caseId, @PathVariable String investigationId, @RequestBody List<Map> caseRequestObj)
            throws URISyntaxException {
        return cmtService.saveContinueInvestigationDetail(caseId, investigationId, caseRequestObj);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @GetMapping ("/investigation-templates")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InvestigationCategories> getInvestigationTemplate(
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name) throws URISyntaxException {
        return cmtService.getInvestigationTemplate(name);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PostMapping ("/{caseId}/investigations/{investigationId}/report")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Investigation createInvestigationReport(@PathVariable String caseId, @PathVariable String investigationId) throws URISyntaxException {
        return cmtService.createInvestigationReport(caseId, investigationId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/reject")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CaseDetailResponse rejectInpatientCases(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.rejectInpatientCases(caseId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/admission")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map preAdmissionToAdmissionCase(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.preAdmissionToAdmissionCase(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}/investigations/{investigationId}/form")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Investigation getInvestigationForm(@PathVariable String caseId, @PathVariable String investigationId,
            @RequestParam (required = false, name = "date_time", defaultValue = DEFAULT_STRING) String dateTime) throws URISyntaxException {
        return cmtService.getInvestigationForm(caseId, investigationId, dateTime);
    }

    @PostMapping ("/{caseId}/service-details")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    List<CreateIpService> createServices(@PathVariable String caseId, @RequestBody CreateIpServiceRequest req) throws URISyntaxException {
        return cmtService.createServices(caseId, req);
    }

    @GetMapping ("/{caseId}/service-details")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ServiceDetailsResponse getServiceList(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.getServiceList(caseId);
    }

    @PatchMapping ("/{caseId}/room-and-service-details")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateIPServiceDetails(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        cmtService.updateIPServiceDetails(caseId, reqBody);
    }

    @DeleteMapping ("/{caseId}/service-details/{serviceExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteIPServiceDetails(@PathVariable String caseId, @PathVariable String serviceExternalId) throws URISyntaxException {
        cmtService.deleteServiceDetails(caseId, serviceExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/close-billing")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map onDischarge(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.onInpatientDischarge(caseId, reqBody);
    }

    @PatchMapping ("/{caseId}/service-details/{serviceId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map onUpdateIndividualService(@PathVariable String caseId, @PathVariable String serviceId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.updateIndividualService(caseId, serviceId, reqBody);
    }

    @GetMapping ("/{caseId}/room-details")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getRoomBoardList(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.getRoomBoardList(caseId);
    }

    @PostMapping ("/{caseId}/room-details")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    CreateIpRoomBoardList createRoomBoardList(@PathVariable String caseId, @RequestBody CreateIpRoomBoardList req) throws URISyntaxException {
        return cmtService.createRoomBoardList(caseId, req);
    }

    @PatchMapping ("/{caseId}/room-details/{roomId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CreateIpRoomBoardList updateRoomBoard(@PathVariable (required = true) String caseId, @PathVariable String roomId,
            @RequestBody Map req) {
        return cmtService.updateRoomBoard(caseId, roomId, req);
    }

    @DeleteMapping ("/{caseId}/room-details/{roomExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteIPRoomDetails(@PathVariable String caseId, @PathVariable String roomExternalId) throws URISyntaxException {
        cmtService.deleteIPRoomDetails(caseId, roomExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/change-coverage")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CaseDetailResponse changeCoverage(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.changeCoverage(caseId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'restricted_write')")
    @PatchMapping ("/{caseId}/inpatient-template-letter")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map lmaInpatientTemplateLetter(@PathVariable String caseId,
            @RequestParam (required = false, name = "language", defaultValue = DEFAULT_STRING) String language) throws URISyntaxException {
        return cmtService.lmaInpatientTemplateLetter(caseId, language);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'restricted_write')")
    @PatchMapping ("/{caseId}/maternity-template-letter")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map lmaMaternityTemplateLetter(@PathVariable String caseId,
            @RequestParam (required = false, name = "language", defaultValue = DEFAULT_STRING) String language) throws URISyntaxException {
        return cmtService.lmaMaternityTemplateLetter(caseId, language);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/temporary-billing")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map generateTemporaryBilling(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.generateTemporaryBilling(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/ip-hdtpa-status-history")
    @AuditedAccess(eventType = EventType.cmt_inpatient_hdtpa_status_history_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<CaseStatusHistory> getInpatientHdtpaStatusHistory(
            @RequestParam (required = false, name = "caseId", defaultValue = DEFAULT_STRING) @AuditedEntityId String caseId) throws URISyntaxException {
        return cmtService.getInpatientHdtpaStatusHistory(caseId);

    }

    @PatchMapping ("/{caseId}/revise")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void reviseIpCase(@PathVariable String caseId) throws URISyntaxException {
        cmtService.reviseIpCase(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}/itemized-room-and-service-details")
    @AuditedAccess(eventType = EventType.cmt_inpatient_room_service_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getItemizedRoomAndServiceDetails(@PathVariable @NotNull @AuditedEntityId String caseId) throws URISyntaxException {
        return cmtService.getItemizedRoomAndServiceDetails(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/itemized-room-and-service-details/save")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateItemizedRoomAndServiceDetails(@PathVariable @NotNull String caseId, @RequestBody Map itemizedItem) throws URISyntaxException {
        return cmtService.updateItemizedRoomAndServiceDetails(caseId, itemizedItem);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("/document/{documentId}/abort-ocr")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void abortOcr(@PathVariable @NotNull String documentId) throws URISyntaxException {
        cmtService.abortOcr(documentId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("{caseId}/document/{documentId}/abort-ocr")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void abortOcrV2(@PathVariable @NotNull String caseId, @PathVariable @NotNull String documentId) throws URISyntaxException {
        cmtService.abortOcrV2(caseId, documentId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PutMapping ("/non-ppn-copay/{caseId}/save-amount")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map nonPpnCopaySaveAmount(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.nonPpnCopaySaveAmount(caseId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @PutMapping ("/{caseId}/search-consolidated-history")
    @AuditedAccess(eventType = EventType.cmt_inpatient_case_chronology_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_inpatient)
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<CaseConsolidatedHistoryResponse> searchCaseConsolidatedHistory(@PathVariable @AuditedEntityId String caseId,
            @RequestBody SearchCaseConsolidatedHistoryRequest req) throws URISyntaxException {
        return cmtService.searchCaseConsolidatedHistory(caseId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/estimation")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateEstimationLos(@PathVariable String caseId, @RequestBody Map req) throws URISyntaxException {
        return cmtService.updateEstimationLos(caseId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}/process/evaluation-result")
    IglApprovedAlerts getIglApprovedStatus(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.getIglApprovedStatus(caseId);
    }

    /******************************************** CMT Inpatient: END ********************************************/
}
