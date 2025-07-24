package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.procedure.MedicalProcedureCategory;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.bintan.ProcedureCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import java.net.URISyntaxException;

@Slf4j
@RequestMapping("api/v1/procedure-categories")
@RestController
public class ProcedureCategoryApiController extends HalodocBaseApiController {

    @Autowired
    private ProcedureCategoryService procedureCategoryService;

    @GetMapping("/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<MedicalProcedureCategory> getProcedureCategoryList(@RequestParam(defaultValue = "10", required = false) Integer per_page,
                                                                              @RequestParam(defaultValue = "1", required = false) Integer page_no,
                                                                              @RequestParam(name = "name", required = false) String name,
                                                                              @RequestParam(required = false, name = "sort_field", defaultValue = "name") String sortField,
                                                                              @RequestParam(required = false, name = "sort_order", defaultValue = "asc") String sortOrder,
                                                                              @RequestParam(name = "status", required = false) String status) throws URISyntaxException {
        return procedureCategoryService.getProcedureCategoryList(page_no, per_page, status, name, sortField, sortOrder);
    }

    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_category_view', 'view_only')")
    @GetMapping("/{categoryId}")
    public MedicalProcedureCategory getProcedureCategory(@PathVariable String categoryId) throws URISyntaxException {
        return procedureCategoryService.getProcedureCategory(categoryId);
    }

    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_category_add', 'restricted_write')")
    @PostMapping
    public MedicalProcedureCategory createProcedureCategory(@RequestBody MedicalProcedureCategory medicalProcedureCategory) {
        return procedureCategoryService.createProcedureCategory(medicalProcedureCategory);
    }

    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_category_edit', 'restricted_write')")
    @PatchMapping("/{categoryId}")
    public void updateProcedureCategory(@PathVariable String categoryId, @RequestBody MedicalProcedureCategory medicalProcedureCategory) {
        procedureCategoryService.updateProcedureCategory(categoryId, medicalProcedureCategory);
    }
}
