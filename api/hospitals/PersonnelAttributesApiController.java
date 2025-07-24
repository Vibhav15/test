package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.Speciality;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.PersonnelService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.net.URISyntaxException;
import java.util.List;

@RequestMapping("api/v2/personnels")
@RestController
public class PersonnelAttributesApiController extends HalodocBaseApiController {

    @Autowired
    private PersonnelService personnelService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/{personnelId}/specialities")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<Speciality> getPersonnelSpecialities(@PathVariable @NotBlank String personnelId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "personnel_view", "view_only")
                && !authorizationService.isAuthorized("contact_doctor", "doctor_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return personnelService.getPersonnelSpecialities(personnelId);
    }
}
