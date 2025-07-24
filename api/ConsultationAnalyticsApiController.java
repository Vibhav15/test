package com.halodoc.batavia.controller.api;

import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import com.halodoc.batavia.entity.bintan.adam.*;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.adam.ConsultationAnalyticsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/v1/consultation-analytics")
@RestController
public class ConsultationAnalyticsApiController {

    private final ConsultationAnalyticsService consultationAnalyticsService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public ConsultationAnalyticsApiController(ConsultationAnalyticsService consultationAnalyticsService) {
        this.consultationAnalyticsService = consultationAnalyticsService;
    }

    @GetMapping("/config")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','data_collection_add', 'view_only')")
    public ConsultationConfig saveGeneralAnalysis() {
        return consultationAnalyticsService.getConfig();
    }

    @GetMapping("/{consultationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @AuditedAccess(eventType = EventType.consultation_analytics_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.consultation)
    public ChatDetails getChatDetails(@PathVariable @NotBlank @AuditedEntityId String consultationId)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("contact_doctor", "data_collection_view", "view_only")
                && !authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only")
                && !authorizationService.isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return consultationAnalyticsService.getChatData((consultationId));
    }

    @GetMapping("/{consultationId}/analysis")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','data_collection_view', 'view_only')")
    public List<ChatScore> getConsultationAnalysis(@PathVariable @NotBlank String consultationId)
            throws URISyntaxException {
        return consultationAnalyticsService.getChatAnalysis((consultationId));
    }

    @PostMapping("/{consultationId}/analysis")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','data_collection_add', 'restricted_write')")
    public List<ChatScore> postConsultationAnalysis(@RequestBody List<ChatScore> chatScore,
            @PathVariable String consultationId) throws URISyntaxException {
        return consultationAnalyticsService.postChatAnalysis(consultationId, chatScore);
    }

    @GetMapping("/{consultationId}/general-analysis")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','data_collection_view', 'view_only')")
    public ConsultationAnalysis getGeneralAnalysis(@PathVariable String consultationId) throws URISyntaxException {
        return consultationAnalyticsService.getConsultationAnalysis((consultationId));
    }

    @PostMapping("/{consultationId}/general-analysis")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','data_collection_add', 'restricted_write')")
    public ConsultationAnalysis saveGeneralAnalysis(@RequestBody ConsultationAnalysis generalAnalysis,
            @PathVariable String consultationId) throws URISyntaxException {
        return consultationAnalyticsService.postConsultationAnalysis(consultationId, generalAnalysis);
    }

    @PutMapping("/files/signed-urls")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','data_collection_view', 'view_only')")
    public List<SignedUrl> multiGetSignedUrls(@NotNull @Valid @RequestBody final Map<String, List<String>> data) {
        return consultationAnalyticsService.multiGetSignedUrls(data.get("urls"));
    }
}
