package com.halodoc.batavia.controller.api.hospitals;

import java.net.URISyntaxException;
import java.util.List;
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
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.Speciality;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.SpecialityService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping ("api/v2/specialities")
@RestController
public class SpecialityApiController extends HalodocBaseApiController {
    @Autowired
    private SpecialityService specialityService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Speciality> getSpecialityList(@RequestParam (defaultValue = "20", required = false) Integer per_page,
            @RequestParam (defaultValue = "1", required = false) Integer page_no, @RequestParam (name = "name", required = false) String name,
            @RequestParam (name = "language", required = false) String language, @RequestParam (name = "status", required = false) String status)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "specialities_list", "view_only") &&
                !authorizationService.isAuthorized("hospitals_management", "personnels_list", "view_only") &&
                !authorizationService.isAuthorized("hospitals_management", "personnel_view", "view_only") &&
                !authorizationService.isAuthorized("hospitals_management", "personnel_add", "restricted_write") &&
                !authorizationService.isAuthorized("hospitals_management", "personnel_edit", "restricted_write") &&
                !authorizationService.isAuthorized("pharmacy_delivery", "product_edit", "view_only") &&
                !authorizationService.isAuthorized("marketing", "campaign_edit", "restricted_write") &&
                !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write") &&
                !authorizationService.isAuthorized("insurance", "case_monitoring_ip_view", "view_only") &&
                !authorizationService.isAuthorized("insurance", "claim_cashless_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return specialityService.getSpecialityList(page_no, per_page, status, name, language);
    }

    @GetMapping ("/{specialityId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Speciality getSpeciality(@PathVariable String specialityId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "speciality_view", "view_only") && !authorizationService.isAuthorized(
                "pharmacy_delivery", "product_edit", "view_only") && !authorizationService.isAuthorized(
                "insurance", "case_monitoring_ip_view", "view_only") && !authorizationService.isAuthorized(
                "insurance", "claim_cashless_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return specialityService.getSpeciality(specialityId);
    }

    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','speciality_add', 'restricted_write')")
    @PostMapping
    public Speciality createProvider(@RequestBody Speciality speciality) {
        return specialityService.createSpeciality(speciality);
    }

    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','speciality_edit', 'restricted_write')")
    @PatchMapping ("/{specialityId}")
    public Object updateProvider(@PathVariable String specialityId, @RequestBody Speciality speciality) {
        return specialityService.updateSpeciality(specialityId, speciality);
    }

    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','speciality_view', 'view_only')")
    @PutMapping ("/multi_get")
    public List<Speciality> getSpecialitiesById(@RequestBody List<String> specialityIds) throws URISyntaxException {
        return specialityService.getSpecialitiesById(specialityIds);
    }
}
