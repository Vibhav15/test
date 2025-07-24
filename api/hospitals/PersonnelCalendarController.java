package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalendarEventRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalenderEventUpdateRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.bintan.PersonnelCalendarService;
import com.halodoc.batavia.service.kuta.KutaService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequestMapping("api/v1/personnel-calendars")
@RestController
public class PersonnelCalendarController extends HalodocBaseApiController {
    private final PersonnelCalendarService personnelCalendarService;

    @Autowired
    private KutaService kutaService;

    @Autowired
    public PersonnelCalendarController(PersonnelCalendarService personnelCalendarService) {
        this.personnelCalendarService = personnelCalendarService;
    }

    @PostMapping("/events")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','personnel_add', 'restricted_write')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    public void createEvent(@RequestBody CalendarEventRequest eventRequest) throws IOException {
        this.personnelCalendarService.createEvent(eventRequest);
    }

    @PatchMapping("/events")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','personnel_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void editEvent(@RequestBody CalenderEventUpdateRequest eventRequest) throws IOException {
        this.personnelCalendarService.updateEvent(eventRequest);
    }

}
