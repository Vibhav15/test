package com.halodoc.batavia.controller.api.doctor;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.identity.DoctorCalendarEventRequest;
import com.halodoc.batavia.entity.bintan.identity.PaginatedEventDetailResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.IdentityService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequestMapping("api/v1/doctor-reschedule")
@RestController
public class DoctorRescheduleController extends HalodocBaseApiController {
    private final IdentityService bintanIdentityService;

    @Autowired
    public DoctorRescheduleController(IdentityService bintanIdentityService) {
        this.bintanIdentityService = bintanIdentityService;
    }

    @PutMapping("/requests/{requestId}/approve")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public void approveRequest(@PathVariable String requestId) throws IOException {
        this.bintanIdentityService.approveRescheduleRequest(requestId);
    }

    @PutMapping("/requests/{requestId}/reject")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public void rejectRequest(@PathVariable String requestId) throws IOException {
        this.bintanIdentityService.rejectRescheduleRequest(requestId);
    }

    @PutMapping("/requests")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'view_only')")
    public PaginatedEventDetailResult getRescheduleRequests(@RequestBody DoctorCalendarEventRequest eventRequest) throws IOException {
        return this.bintanIdentityService.getDoctorRescheduleRequests(eventRequest);
    }

}
