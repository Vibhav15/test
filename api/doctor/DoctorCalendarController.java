package com.halodoc.batavia.controller.api.doctor;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalendarEventRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalendarEventSearchRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalenderEventUpdateRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalenderUpdateRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.DoctorCalendarService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("api/v1/doctor-calendars")
@RestController
public class DoctorCalendarController extends HalodocBaseApiController {
    private final DoctorCalendarService doctorCalendarService;

    @Autowired
    public DoctorCalendarController(DoctorCalendarService doctorCalendarService) {
        this.doctorCalendarService = doctorCalendarService;
    }

    @PutMapping("/events")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'view_only')")
    public List<Map<String, Object>> searchEvents(@RequestBody CalendarEventSearchRequest searchRequest) throws IOException {
       return this.doctorCalendarService.searchEvents(searchRequest);
    }

    @PutMapping("/events/ongoing")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'view_only')")
    public List<Map<String, Object>> getOngoingEvents(@RequestBody CalendarEventSearchRequest searchRequest) throws IOException {
        return this.doctorCalendarService.getOngoingEvents(searchRequest);
    }

    @PostMapping("/events")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public void createEvent(@RequestBody CalendarEventRequest eventRequest) throws IOException {
        this.doctorCalendarService.createEvent(eventRequest);
    }

    @PatchMapping("/events")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public void editEvent(@RequestBody CalenderEventUpdateRequest eventRequest) throws IOException {
        this.doctorCalendarService.updateEvent(eventRequest);
    }

    @PutMapping("/next")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'view_only')")
    public List<Map<String, Object>> nextAvailableSchedule(@RequestBody Map<String, Object> nextCalendarEventSearchRequest) throws IOException {
        return this.doctorCalendarService.nextAvailableSchedule(nextCalendarEventSearchRequest);
    }

}
