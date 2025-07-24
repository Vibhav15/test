package com.halodoc.batavia.controller.api.doctor;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.Doctor;
import com.halodoc.batavia.entity.bintan.catalog.DoctorAttributes;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.DoctorCategoriesService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("api/v1/doctor-categories")
@RestController
public class DoctorCategoriesApiController extends HalodocBaseApiController {
    private final DoctorCategoriesService doctorCategoriesService;

    @Autowired
    public DoctorCategoriesApiController(DoctorCategoriesService doctorCategoriesService) {
        this.doctorCategoriesService = doctorCategoriesService;
    }

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<Map> listCategories(@RequestParam(name = "page_no", defaultValue = "1", required = false) Integer pageNo,
                                            @RequestParam(name = "per_page", defaultValue = "20", required = false) Integer perPage,
                                            @RequestParam(name = "status", required = false) String status,
                                            @RequestParam(name = "display", required = false) String display,
                                            @RequestParam(name = "code", required = false) String code,
                                            @RequestParam(name = "name", required = false) String name,
                                            @RequestParam(name = "parent_id", required = false) String parentId,
                                            @RequestParam(name = "include_sub_category", required = false) String includeSubCategory)
            throws URISyntaxException {

        if (!authorizationService.isAuthorized("contact_doctor","doctor_categories_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","product_edit", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        PaginatedResult<Map> paginatedResult = doctorCategoriesService.categoriesList(pageNo, perPage, status, display, code, name, parentId, includeSubCategory);

        return paginatedResult;

    }

    @GetMapping("/{id}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public Map detailCategory(@PathVariable String id) {
        if (!authorizationService.isAuthorized("contact_doctor","doctor_categories_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","product_edit", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return doctorCategoriesService.get(id);
    }


    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_categories_add', 'restricted_write')")
    public Object createCategory(@RequestBody Object category) {

        Object newCategory = new Object();

        if(category != null) {
            newCategory = doctorCategoriesService.create(category);
        }

        return newCategory;
    }

    @PutMapping("/{id}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_categories_edit', 'restricted_write')")
    public Object updateCategory(@PathVariable String id, @RequestBody Object category) {

        Object newCategory = new Object();

        if(category != null) {
            newCategory = doctorCategoriesService.update(id, category);
        }

        return newCategory;
    }

    @PostMapping("/{id}/attributes")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_categories_edit', 'restricted_write')")
    public Object addAttributes(@PathVariable String id, @RequestBody List<DoctorAttributes> attributes) {

        Object newAttributes = new Object();

        if(attributes != null) {
            newAttributes = doctorCategoriesService.addAttribute(id, attributes);
        }

        return newAttributes;
    }

    @GetMapping("/{id}/doctors")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_list', 'view_only')")
    public PaginatedResult<Map> listDoctors(@PathVariable String id,
                @RequestParam(name = "page_no", defaultValue = "1", required = false) Integer pageNo,
                @RequestParam(name = "per_page", defaultValue = "50", required = false) Integer perPage,
                @RequestParam(name = "doctor_category_statuses", defaultValue = "active", required = false)
                                                        String doctor_category_statuses
     )
            throws URISyntaxException {


        PaginatedResult<Map> paginatedResult = doctorCategoriesService.doctorsInCategory(id, pageNo, perPage, doctor_category_statuses);

        return paginatedResult;

    }

}
