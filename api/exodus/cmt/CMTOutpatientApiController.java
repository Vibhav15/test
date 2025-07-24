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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.cmt.CaseDetailResponse;
import com.halodoc.batavia.entity.exodus.cmt.CaseStatusHistory;
import com.halodoc.batavia.entity.exodus.cmt.CmtDocument;
import com.halodoc.batavia.entity.exodus.cmt.CmtSearch;
import com.halodoc.batavia.entity.exodus.cmt.CmtServiceList;
import com.halodoc.batavia.entity.exodus.cmt.CreateCMTRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.cmt.CMTService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/case-monitoring/outpatients/case")
public class CMTOutpatientApiController extends HalodocBaseApiController {
    @Autowired
    private CMTService cmtService;

    /************************************ CMT Outpatient: START ************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_list', 'view_only')")
    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<CmtSearch> searchCMTOutpatientList(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String member_external_id,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String case_number,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String provider_location_id,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String coverage_type,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String process_status,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String policy_number,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String request_statuses,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String request_assignee,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String statuses) throws URISyntaxException {
        return cmtService.searchCMTOutpatientList(pageNo, perPage, member_external_id, case_number, provider_location_id, coverage_type,
                process_status, statuses, policy_number, request_statuses, request_assignee);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_add', 'restricted_write')")
    @PostMapping ("/create")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    CaseDetailResponse createOutpatientCase(@RequestBody CreateCMTRequest request) throws URISyntaxException {
        return cmtService.createOutpatientCase(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_view', 'view_only')")
    @GetMapping ("/{caseId}")
    @AuditedAccess(eventType = EventType.cmt_outpatient_case_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_outpatient)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    CaseDetailResponse getCMTDetails(@PathVariable @AuditedEntityId String caseId) throws URISyntaxException {
        return cmtService.getCMTDetails(caseId);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/cancel")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CaseDetailResponse cancelCases(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {

        return cmtService.cancelCases(caseId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/reject")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CaseDetailResponse rejectOutpatientCase(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.rejectOutpatientCase(caseId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/update")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateCaseDetail(@PathVariable String caseId, @RequestBody Map caseRequestObj) throws URISyntaxException {
        return cmtService.updateCaseDetail(caseId, caseRequestObj);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PostMapping ("/{caseId}/documents")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    CmtDocument addNewDocument(@PathVariable String caseId, @RequestBody CmtDocument request) throws URISyntaxException {
        return cmtService.addNewDocument(caseId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @GetMapping ("/{caseId}/documents/{documentType}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<CmtDocument> getCaseDocumentsByDocumentType(@PathVariable String caseId, @PathVariable String documentType) throws URISyntaxException {
        return cmtService.getOpCaseDocumentsByDocumentType(caseId, documentType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_view', 'view_only')")
    @GetMapping ("/{caseId}/documents")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<CmtDocument> getCaseDocumentList(@PathVariable String caseId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return cmtService.getCaseDocumentList(caseId, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/documents/{documentId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CmtDocument updateCaseDocument(@PathVariable String caseId, @PathVariable String documentId, @RequestBody Map reqBody) throws URISyntaxException {

        return cmtService.updateCaseDocument(caseId, documentId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_view', 'view_only')")
    @GetMapping ("/{caseId}/service-list")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    CmtServiceList getCaseServiceList(@PathVariable String caseId) throws URISyntaxException {
        return cmtService.getCaseServiceList(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/service-details")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CmtServiceList updateServiceDetails(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {

        return cmtService.updateServiceDetails(caseId, reqBody);
    }


    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PatchMapping ("/{caseId}/service-details/{serviceDetailId}")
    Map updateServiceDetailsBysServiceDetailId(@PathVariable String caseId, @PathVariable String serviceDetailId, @RequestBody Map reqBody) throws URISyntaxException {

        return cmtService.updateServiceDetailsBysServiceDetailId(caseId, serviceDetailId, reqBody);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PutMapping ("/{caseId}/discharge")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map onDischarge(@PathVariable String caseId) throws URISyntaxException {

        return cmtService.onDischarge(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_view', 'view_only')")
    @GetMapping ("/{caseId}/case-status-history")
    @AuditedAccess(eventType = EventType.cmt_outpatient_case_status_history_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_outpatient)
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<CaseStatusHistory> getCMTStatusHistory(@PathVariable @AuditedEntityId String caseId) throws URISyntaxException {
        return cmtService.getCMTStatusHistory(caseId);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_list', 'restricted_write')")
    @PatchMapping ("/{caseId}/change-coverage")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    CaseDetailResponse outpatientChangeCoverage(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.outpatientChangeCoverage(caseId, reqBody);
    }

    @PatchMapping ("/{caseId}/special_op")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void dischargeConfirmation(@PathVariable String caseId) throws URISyntaxException {
        cmtService.dischargeConfirmation(caseId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_view', 'view_only')")
    @GetMapping ("/op-hdtpa-status-history")
    @AuditedAccess(eventType = EventType.cmt_outpatient_hdtpa_status_history_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.cmt_outpatient)
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    List<CaseStatusHistory> getOutpatientHdtpaStatusHistory(
            @RequestParam (required = false, name = "caseId", defaultValue = DEFAULT_STRING) @AuditedEntityId String caseId) throws URISyntaxException {
        return cmtService.getOutpatientHdtpaStatusHistory(caseId);

    }

    @PatchMapping ("/{caseId}/revise")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void reviseOpCase(@PathVariable String caseId) throws URISyntaxException {
        cmtService.reviseOpCase(caseId);
    }

    @PostMapping ("/validate")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map validateMemberAdmissionEligibility(@RequestBody Map request) throws URISyntaxException {
        return cmtService.validateMemberEligibility(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_op_edit', 'restricted_write')")
    @PutMapping ("/non-ppn-copay/{caseId}/save-amount")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map nonPpnCopaySaveAmount(@PathVariable String caseId, @RequestBody Map reqBody) throws URISyntaxException {
        return cmtService.nonPpnCopaySaveOutpatientCaseAmount(caseId, reqBody);
    }

    /************************************ CMT Outpatient: END ************************************/
}
