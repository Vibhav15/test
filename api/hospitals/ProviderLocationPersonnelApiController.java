package com.halodoc.batavia.controller.api.hospitals;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.ProviderLocationPersonnelConfig;
import com.halodoc.batavia.entity.bintan.catalog.*;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.ProviderLocationPersonnelService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("api/v1/hospitals/providers")
@RestController
public class ProviderLocationPersonnelApiController extends HalodocBaseApiController {

    private final ProviderLocationPersonnelService providerLocationPersonnelService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public ProviderLocationPersonnelApiController(ProviderLocationPersonnelService providerLocationPersonnelService) {
        this.providerLocationPersonnelService = providerLocationPersonnelService;
    }

    @GetMapping("{providerId}/location/{locationId}/personnels")
    public PaginatedResult<PersonnelLocationDepartmentPackagesSchedules> getProviderLocationPersonnelList(@PathVariable String providerId, @PathVariable String locationId,
                                                                                                          @RequestParam Map<String, String> queryParams) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationPersonnelService.getProviderLocationPersonnelList(providerId, locationId, queryParams);
    }

    @GetMapping("/{providerId}/location/{locationId}/departments")
    public List<Department> getProviderLocationDepartment(@PathVariable String providerId,
                                                          @PathVariable String locationId,
                                                          @RequestParam(required = false, defaultValue = "active,inactive") String statuses) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationPersonnelService.getProviderLocationDepartment(providerId, locationId, statuses);
    }

    @PutMapping("/place_of_practice")
    public void addPersonnelsPlaceOfpractice(@RequestBody PersonnelPlaceOfPracticeCrudParam personnelPlaceOfPracticeCrudParam) {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        providerLocationPersonnelService.addPersonnelsPlaceOfpractice(personnelPlaceOfPracticeCrudParam);
    }

    @GetMapping("/location/{locationId}/personnels/{personnelId}/payment")
    public PersonnelPayment getProviderLocationPersonnelSchedules(@PathVariable String locationId,
                                                                  @PathVariable String personnelId) {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationPersonnelService.getProviderLocationPersonnelPayments(locationId, personnelId);
    }

    @GetMapping("/config")
    public ProviderLocationPersonnelConfig getProviderLocationPersonnelConfig() {
        return providerLocationPersonnelService.getProviderLocationPersonnelConfig();
    }
}
