package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalendarEventRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalendarEventSearchRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalenderEventUpdateRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.bintan.PersonnelCalendarService;
import com.halodoc.batavia.service.bintan.ProcedureCalendarService;
import com.halodoc.batavia.service.kuta.KutaService;
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
@RequestMapping("api/v1/medical-procedure-calendars")
@RestController
public class ProcedureCalendarController extends HalodocBaseApiController {
    private final ProcedureCalendarService procedureCalendarService;

    @Autowired
    private KutaService kutaService;

    @Autowired
    public ProcedureCalendarController(ProcedureCalendarService procedureCalendarService) {
        this.procedureCalendarService = procedureCalendarService;
    }

    @PutMapping("/events")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<Map<String, Object>> searchEvents(@RequestBody CalendarEventSearchRequest searchRequest) throws IOException {
       return this.procedureCalendarService.searchEvents(searchRequest);
    }

    @PutMapping("/search/events")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<Map<String, Object>> searchCalenderEvents(@RequestBody CalendarEventSearchRequest searchRequest) throws IOException {
        return this.kutaService.searchEvents(searchRequest);
    }

    @PostMapping("/events")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_add', 'restricted_write')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    public void createEvent(@RequestBody CalendarEventRequest eventRequest) throws IOException {
        this.procedureCalendarService.createEvent(eventRequest);
    }

    @PatchMapping("/events")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void editEvent(@RequestBody CalenderEventUpdateRequest eventRequest) throws IOException {
        this.procedureCalendarService.updateEvent(eventRequest);
    }

}
