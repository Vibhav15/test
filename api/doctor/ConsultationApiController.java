package com.halodoc.batavia.controller.api.doctor;

import com.halodoc.audit.sdk.Auditor;
import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.*;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.ConsultationService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/v1/consultations")
@RestController
public class ConsultationApiController extends HalodocBaseApiController {
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private Auditor auditor;

    @GetMapping("/{consumerConsultationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @AuditedAccess(eventType = EventType.consultation_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.consultation)
    public Consultation detail(@PathVariable @AuditedEntityId String consumerConsultationId) {
        if (!authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")
                && !authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only")
                && !authorizationService.isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return consultationService.getFull(consumerConsultationId);
    }

    @GetMapping("/{consumerConsultationId}/documents")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @AuditedAccess(eventType = EventType.consultation_documents_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.consultation)
    public List getConsultationDocuments(@PathVariable @AuditedEntityId String consumerConsultationId) {
        if (!authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")
                && !authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only")
                && !authorizationService.isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return consultationService.getConsultationDocuments(consumerConsultationId);
    }

    @GetMapping
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','consultations', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<Consultation> search(Model model, @RequestParam(required = false) String consultationNo,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false, name = "start_consultation_date") String startConsultationDate,
            @RequestParam(required = false, name = "end_consultation_date") String endConsultationDate,
            @RequestParam(required = false, defaultValue = "requested,confirmed,started,approved,completed,closed,abandoned,cancelled") String status,
            @RequestParam(required = false, defaultValue = "online,offline,private_practice,digital_clinic,digital_clinic_extended") String type,
            @RequestParam(required = false, defaultValue = "scheduled,walk_in", name = "forms") String consultationForm,
            @RequestParam(required = false, name = "dc_category") String dcConsultationParentCategory,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") String pageNumber,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") String perPage)
            throws URISyntaxException {
        return consultationService.search(consultationNo, patientId, doctorId, customerId, status,
                pageNumber, perPage, startConsultationDate, endConsultationDate, type, consultationForm);

    }

    @GetMapping("/{consultationId}/erx")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @AuditedAccess(eventType = EventType.consultation_erx_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.consultation)
    public EPrescription getEPrescription(@PathVariable @AuditedEntityId String consultationId) {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")
                && !authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "lead_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.consultation_erx_accessed, Action.get, ActorType.cc_user,
                EntityType.consultation,
                consultationId, ChannelType.http);

        EPrescription ePrescription = new EPrescription();
        if (StringUtils.isNotEmpty(consultationId)) {
            ePrescription = consultationService.getEPrescription(consultationId);
        }
        return ePrescription;
    }

    @GetMapping("/{consultationId}/lab-referrals")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @AuditedAccess(eventType = EventType.consultation_lab_referrals_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.consultation)
    public EPrescription getLabReferralDocument(@PathVariable @AuditedEntityId String consultationId) {

        if (!authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.consultation_lab_referrals_accessed, Action.get, ActorType.cc_user,
                EntityType.consultation,
                consultationId, ChannelType.http);

        EPrescription labReferralDocument = new EPrescription();
        if (StringUtils.isNotEmpty(consultationId)) {
            labReferralDocument = consultationService.getLabReferralDocument(consultationId);
        }
        return labReferralDocument;
    }

    @PutMapping("/{consultationId}/generate-receipts/{documentType}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','consultations', 'restricted_write')")
    public void regenerateInvoice(@PathVariable String consultationId, @PathVariable String documentType)
            throws URISyntaxException {
        Map<String, Object> responseObj = new HashMap<>();

        consultationService.regenerateInvoice(consultationId, documentType);
    }

    @GetMapping("/conversations/payments")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public Object getDoctorPayments(
            @RequestParam(required = true, name = "doctor_id") String doctorId,
            @RequestParam(required = false, name = "per_page") String perPage,
            @RequestParam(required = false, name = "page_no") String pageNumber

    ) {

        Map<String, Object> request = new HashMap<>();

        if (StringUtils.isNotBlank(doctorId)) {
            request.put("doctor_id", doctorId);
        }
        request.put("per_page", perPage);
        request.put("page_no", pageNumber);

        return consultationService.getDoctorPayments(request);
    }

    @GetMapping("/notes/note-detail-requests")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor', 'summary_revise_request', 'view_only')")
    public PaginatedResult<ReviseRequest> searchReviseRequests(
            @RequestParam(name = "statuses", defaultValue = "pending,system_approved,approved,applied,rejected,expired", required = false) String statuses,
            @RequestParam(name = "page_no", defaultValue = "1", required = false) Integer pageNo,
            @RequestParam(name = "per_page", defaultValue = "10", required = false) Integer perPage,
            @RequestParam(name = "consultation_id", defaultValue = "", required = false) String consultationId)
            throws URISyntaxException {
        return consultationService.searchReviseRequests(statuses, pageNo, perPage, consultationId);
    }

    @PutMapping("/{consultationId}/note/{noteType}/reject")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor', 'summary_revise_request', 'restricted_write')")
    public void rejectReviseRequest(
            @PathVariable String consultationId,
            @PathVariable String noteType,
            @RequestBody(required = true) ReviseRequestReason reason) {

        consultationService.rejectReviseRequest(consultationId, noteType, reason);
    }

    @PutMapping("/{consultationId}/note/{noteType}/approve")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor', 'summary_revise_request', 'restricted_write')")
    public void approveReviseRequest(
            @PathVariable String consultationId,
            @PathVariable String noteType,
            @RequestBody(required = false) ReviseRequestReason reason) {

        consultationService.approveReviseRequest(consultationId, noteType, reason);
    }

    @PutMapping("/{consultationId}/note/{noteType}/regenerate")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','consultations', 'restricted_write')")
    public void regenerateRestRecommendation(
            @PathVariable String consultationId,
            @PathVariable String noteType) {

        consultationService.regenerateRestRecommendation(consultationId, noteType);
    }
}
