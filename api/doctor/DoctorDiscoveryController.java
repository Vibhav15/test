package com.halodoc.batavia.controller.api.doctor;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.bintan.DoctorDiscoveryService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@RequestMapping("api/v2/doctors")
@RestController
public class DoctorDiscoveryController extends HalodocBaseApiController {

    @Autowired private DoctorDiscoveryService doctorDiscoveryService;

    @Autowired
    public DoctorDiscoveryController(DoctorDiscoveryService doctorDiscoveryService) {
        this.doctorDiscoveryService = doctorDiscoveryService;
    }

    @GetMapping("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'view_only')")
    public Map search(@RequestParam(required = true) String search_text) throws URISyntaxException {
        return doctorDiscoveryService.searchDoctor(search_text);
    }

}
