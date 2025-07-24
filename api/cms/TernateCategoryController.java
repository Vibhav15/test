package com.halodoc.batavia.controller.api.cms;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.constant.CommonConstants;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.cms.TernateCategoryService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/cms/categories")
@RestController
@Slf4j
public class TernateCategoryController extends HalodocBaseApiController {
    @Autowired
    TernateCategoryService ternateCategoryService;

    @Autowired
    AuthorizationService authorizationService;

    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map<String, Object>> searchCategories(
            @RequestParam (required = false, name = "statuses", defaultValue = CommonConstants.CATEGORY_STATUS_DEFAULT) String statuses,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "" + CommonConstants.ARTICLE_DEFAULT_LIMIT_PERPAGE) Integer perPage,
            @RequestParam (required = false, name = "type") String type, @RequestParam (required = false, name = "search_text") String search_text,
            @RequestParam (required = false, name = "sort_by") String sortBy,
            @RequestParam (required = false, name = "sort_order", defaultValue = "asc") String sortOrder) throws URISyntaxException {
        if (!authorizationService.isAuthorized("article_management", "category_list", "view_only") && !authorizationService.isAuthorized("marketing",
                "banner_add", "restricted_write") && !authorizationService.isAuthorized("marketing", "banner_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return ternateCategoryService.searchCategories(statuses, pageNo, perPage, type, search_text, sortBy, sortOrder);
    }

    @GetMapping ("/{id}")
    @PreAuthorize ("@authorizationService.isAuthorized('article_management','category_list', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Map getCategoryById(@PathVariable (value = "id") String id) throws URISyntaxException {
        return ternateCategoryService.getCategoryById(id);
    }

    @PutMapping ("/{id}")
    @PreAuthorize ("@authorizationService.isAuthorized('article_management','category_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateCategory(@RequestBody Map articleRequest, @PathVariable String id) throws URISyntaxException {
        return ternateCategoryService.updateCategory(articleRequest, id);
    }

    @PutMapping ("/sequence")
    @PreAuthorize ("@authorizationService.isAuthorized('article_management','category_list', 'view_only')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateCategorySequence(@RequestBody List<Map> articleRequest) throws URISyntaxException {
        ternateCategoryService.updateCategorySequence(articleRequest);
    }
}
