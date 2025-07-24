package com.halodoc.batavia.controller.api;


import com.halodoc.batavia.entity.cms.B2bProductCategory;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.B2bCategoriesService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Controller
@RequestMapping("api/v1/b2b-categories")
@RestController
public class B2bCategoriesApiController {

    private final B2bCategoriesService b2bCategoriesService;

    @Autowired
    public B2bCategoriesApiController(B2bCategoriesService b2bCategoryService) {
        this.b2bCategoriesService = b2bCategoryService;
    }

    @GetMapping("/search")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','b2b_category_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<B2bProductCategory> searchCategories(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                                @RequestParam(required = false) String categoryName,
                                                                @RequestParam(required = false) String status,
                                                                @RequestParam(required = false) Long startDate,
                                                                @RequestParam(required = false) Long endDate) throws URISyntaxException {
        PaginatedResult<B2bProductCategory> paginatedResult = b2bCategoriesService.search(pageNo, perPage, categoryName, status, startDate, endDate);
        return paginatedResult;
    }
    @PostMapping("/create")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','b2b_category_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public B2bProductCategory createCategory(@RequestParam(required = true) MultipartFile file,
                                             @RequestParam(required = true) String data) throws IOException, URISyntaxException {
        return b2bCategoriesService.createCategory(file, data);
    }

    @PostMapping("/{categoryId}/update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','b2b_category_update', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public B2bProductCategory updateCategoryAndCsv(@PathVariable String categoryId, @RequestParam(required = true) MultipartFile file,
                                                    @RequestParam(required = true) String data,
                                                   @RequestParam(required = true, name="replace_csv") String replaceCsv)
                                                    throws IOException, URISyntaxException {
        return b2bCategoriesService.updateCategoryAndCSV(categoryId,file,data,Boolean.valueOf(replaceCsv));
    }

    @PatchMapping("/{categoryId}/update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','b2b_category_update', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updateCategoryWithoutCsv(@PathVariable String categoryId,
                                        @RequestBody B2bProductCategory productCategory)
            throws URISyntaxException {
        return b2bCategoriesService.updateCategoryWithoutCsv(categoryId, productCategory);
    }

    @GetMapping ("/{categoryId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','b2b_category_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public B2bProductCategory getCategoryDetails(@PathVariable String categoryId)
            throws URISyntaxException {
        return b2bCategoriesService.getCategoryDetails(categoryId);
    }

}
