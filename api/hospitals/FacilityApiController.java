package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.Facility;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.bintan.FacilityService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@Slf4j
@RequestMapping("api/v1/facilities")
@RestController
public class FacilityApiController extends HalodocBaseApiController {

    private final FacilityService facilityService;

    @Autowired
    public FacilityApiController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_view', 'view_only')")
    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Facility> getFacilitiesList(@RequestParam(required = false, defaultValue = "100") String per_page,
                                                      @RequestParam(required = false, defaultValue = "1") String page_no,
                                                      @RequestParam(name = "name", required = false) String name,
                                                      @RequestParam(name = "status", required = false) String status


    ) throws URISyntaxException {
        Facility.FacilitySearchQueryParams facilitySearchQueryParams = new Facility.FacilitySearchQueryParams();

        if (name != null) {
            facilitySearchQueryParams.setName(name);
        }

        if (Integer.valueOf(page_no) != null) {
            facilitySearchQueryParams.setPageNo(page_no);
        }

        if (Integer.valueOf(per_page) != null) {
            facilitySearchQueryParams.setPerPage(per_page);
        }

        if (status != null) {
            facilitySearchQueryParams.setStatus(status);
        }

        return facilityService.searchFacilities(facilitySearchQueryParams);
    }
}
