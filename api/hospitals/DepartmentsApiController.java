package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.Department;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.DepartmentService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("api/v1/departments")
@RestController
public class DepartmentsApiController extends HalodocBaseApiController {

    private final DepartmentService departmentService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public DepartmentsApiController(DepartmentService providerService) {
        this.departmentService = providerService;
    }

    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Department> getDepartmentList(@RequestParam Integer per_page, @RequestParam Integer page_no,
                                                              @RequestParam(name="name", required = false) String name,
                                                              @RequestParam(name="status", required = false) String status

    ) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "departments_list", "view_only")
                && !authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return departmentService.getDepartmentList(page_no, per_page, status,name);
    }


    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','department_view', 'view_only')")
    @GetMapping("/{departmentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Department getDepartment(@PathVariable String departmentId) throws URISyntaxException {
        return departmentService.getDepartment(departmentId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','department_add', 'restricted_write')")
    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.createDepartment(department);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','department_edit', 'restricted_write')")
    @PatchMapping("/{departmentId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public Object updateDepartment(@PathVariable String departmentId, @RequestBody Department department) {
        return departmentService.updateDepartment(departmentId, department);
    }


    @GetMapping("/{departmentId}/procedures")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<Department> getDepartmentProcedures(@PathVariable String departmentId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "department_view", "view_only")
                && !authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return departmentService.getDepartmentProcedures(departmentId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','department_edit', 'restricted_write')")
    @PutMapping("/{departmentId}/procedures/multi-link")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public Map linkProceduresToDepartment(@PathVariable String departmentId, @RequestBody List<String> procedureIds) {
        return departmentService.linkProceduresToDepartment(departmentId, procedureIds);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','department_edit', 'restricted_write')")
    @PutMapping("/{departmentId}/procedures/multi-unlink")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public Map unlinkProceduresToDepartment(@PathVariable String departmentId, @RequestBody List<String> procedureIds) {
        return departmentService.unlinkProceduresToDepartment(departmentId, procedureIds);
    }


}
