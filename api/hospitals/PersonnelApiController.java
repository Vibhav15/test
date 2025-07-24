package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.*;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.PersonnelService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("api/v1/personnels")
@RestController
public class PersonnelApiController extends HalodocBaseApiController {

    @Autowired
    private PersonnelService personnelService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Personnel> searchPersonnels(@RequestParam(required = false) String names,
                                                       @RequestParam(required = false, name = "meta_description") String metaDescription,
                                                       @RequestParam(required = false, defaultValue = "active,inactive") List<String> statuses,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false, defaultValue = "1") int page_no,
                                                       @RequestParam(required = false, defaultValue = "10") int per_page,
                                                       @RequestParam(required = false, name="specialities_ids") List<String> specialities,
                                                       @RequestParam(required = false) List<String> identifiers) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "personnels_list", "view_only")
                && !authorizationService.isAuthorized("contact_doctor", "doctor_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        Personnel.PersonnelSearchQueryParams personnelSearchQueryParams = new Personnel.PersonnelSearchQueryParams();

        if (type != null) {
            personnelSearchQueryParams.setType(type);
        }

        if (specialities != null) {
            personnelSearchQueryParams.setSpecialities(specialities);
        }

        if (identifiers != null) {
            personnelSearchQueryParams.setIdentifiers(identifiers);
        }

        if (metaDescription != null) {
            personnelSearchQueryParams.setMetaDescription(metaDescription);
        }

        if (names != null) {
            personnelSearchQueryParams.setNames(names);
        }

        if (Integer.valueOf(page_no) != null) {
            personnelSearchQueryParams.setPageNo(page_no);
        }

        if (Integer.valueOf(per_page) != null) {
            personnelSearchQueryParams.setPerPage(per_page);
        }

        if (statuses != null) {
            personnelSearchQueryParams.setStatuses(statuses);
        }

        return personnelService.searchPersonnels(personnelSearchQueryParams);
    }

    @GetMapping("/{personnelId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Personnel getPersonnelById(@PathVariable String personnelId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "personnel_view", "view_only")
                && !authorizationService.isAuthorized("contact_doctor", "doctor_view", "view_only") && !authorizationService.isAuthorized("insurance", "case_monitoring_ip_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return personnelService.getPersonnelById(personnelId);
    }

    @PatchMapping("/{personnelId}")     //  Backend doesn't return any value, so return type is of void type
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void updatePersonnel(@PathVariable String personnelId, @RequestBody PersonnelCrudParam personnelCrudParam) throws IOException, URISyntaxException {

        if (!authorizationService.isAuthorized("hospitals_management", "personnel_edit", "restricted_write")
                && !authorizationService.isAuthorized("contact_doctor", "doctor_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        personnelService.updatePersonnel(personnelId, personnelCrudParam, false);
    }

    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public Personnel createPersonnel(@RequestBody PersonnelCrudParam personnelCrudParam) throws IOException, URISyntaxException {

        if (!authorizationService.isAuthorized("hospitals_management", "personnel_add", "restricted_write") && !authorizationService.isAuthorized(
                "contact_doctor", "doctor_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return personnelService.createPersonnel(personnelCrudParam, false);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_view', 'view_only')")
    @GetMapping ("/provider_location_department/{personnelId}")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public List<Map> getPersonnelLocationDepartmentPackagesSchedules(@PathVariable String personnelId,
            @RequestParam (required = false, defaultValue = "active,inactive") String statuses)
            throws URISyntaxException {      //  TODO: Change return-type to 'PersonnelLocationDepartmentPackagesSchedules'

        return personnelService.getPersonnelLocationDepartmentPackagesSchedules(personnelId, statuses);
    }

    @GetMapping ("/ins")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<InsuranceSearchDoctor> searchInsuranceDoctor(@RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false, name = "code", defaultValue = DEFAULT_STRING) String code,
            @RequestParam (required = false, name = "statuses", defaultValue = DEFAULT_STRING) String statuses,
            @RequestParam (required = false, name = "speciality") String speciality,
            @RequestParam (required = false, name = "address") String address, @RequestParam (required = false, name = "provider") String provider,
            @RequestParam (required = false, name = "str_number") String strNumber,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {
        return personnelService.searchInsuranceDoctor(name, code, statuses, speciality, address, provider, strNumber, pageNo, perPage);
    }

    @PostMapping ("/ins/create")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    InsuranceDoctor createInsuranceDoctor(@RequestBody InsuranceDoctor doctor) throws URISyntaxException {
        return personnelService.createInsuranceDoctor(doctor);
    }

    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Personnel> searchPersonnel(@RequestParam (required = false, name = "names") String name,
            @RequestParam (required = false, name = "meta_description") String metaDescription,
            @RequestParam (required = false, defaultValue = "active,inactive") List<String> statuses, @RequestParam (required = false) String type,
            @RequestParam (required = false, defaultValue = "1") int page_no, @RequestParam (required = false, defaultValue = "10") int per_page,
            @RequestParam (required = false, name = "specialities_ids") List<String> specialitieIds,
            @RequestParam (required = false, name = "capabilities") List<String> capabilities,
            @RequestParam (required = false) List<String> identifiers) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "personnels_list", "view_only") && !authorizationService.isAuthorized(
                "contact_doctor", "doctor_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        PersonnelSearchQueryParamsRequestV2 personnelSearchQueryParams = new PersonnelSearchQueryParamsRequestV2();
        Optional.ofNullable(type).ifPresent(personnelSearchQueryParams::setType);
        if (specialitieIds != null) {
            personnelSearchQueryParams.setSpecialityIds(String.join(",", specialitieIds));
        }


        if (name != null) {
            personnelSearchQueryParams.setName(name);
        }


        if (capabilities != null) {
            personnelSearchQueryParams.setCapabilities(String.join(",",capabilities));
        }

        if (Integer.valueOf(page_no) != null) {
            personnelSearchQueryParams.setPageNo(page_no);
        }

        if (Integer.valueOf(per_page) != null) {
            personnelSearchQueryParams.setPerPage(per_page);
        }

        if (statuses != null) {
            personnelSearchQueryParams.setStatuses(String.join(",",statuses));
        }

        return personnelService.searchPersonnelsV2(personnelSearchQueryParams);
    }


}
