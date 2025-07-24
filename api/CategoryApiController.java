package com.halodoc.batavia.controller.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.cms.Category;
import com.halodoc.batavia.entity.cms.CategoryAttributes;
import com.halodoc.batavia.entity.cms.CategoryPromotion;
import com.halodoc.batavia.entity.cms.Product;
import com.halodoc.batavia.entity.cms.SearchCategoryProductsResult;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.CategoryService;
import com.halodoc.batavia.service.StorageService;

@Controller
@RequestMapping ("api/v1/categories")
@RestController
public class CategoryApiController extends HalodocBaseApiController {
    private final CategoryService categoryService;

    private final StorageService storageService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public CategoryApiController(CategoryService categoryService, StorageService storageService) {
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    private Category getByParentId(Long parentId, Set<Category> categories) {
        return categories.stream().filter(category -> category.getId().equals(parentId)).findFirst().orElse(null);
    }

    @GetMapping
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Category> NewlistCategory(@RequestParam (required = false) String searchCode,
            @RequestParam (required = false) String searchName, @RequestParam (required = false) String status,
            @RequestParam (required = false) String display, @RequestParam (required = false) int page_no,
            @RequestParam (required = false) int per_page, @RequestParam (required = false) String sort_by,
            @RequestParam (required = false) String sort_order, @RequestParam (required = false) String channel,
            @RequestParam (required = false) String categoryLevel) {

        Map<String, Object> request = new HashMap<>();

        request.put("page_no", page_no);
        request.put("per_page", per_page);
        if (StringUtils.isNotBlank(searchCode)) {
            request.put("code", searchCode);
        }
        if (StringUtils.isNotBlank(searchName)) {
            request.put("name", searchName);
        }
        if (StringUtils.isNotBlank(status)) {
            request.put("status", status);
        }
        if (StringUtils.isNotBlank(display)) {
            request.put("display", Boolean.valueOf(display));
        }
        if (StringUtils.isNotBlank(sort_by)) {
            request.put("sort_by", sort_by);
        }
        if (StringUtils.isNotBlank(sort_order)) {
            request.put("sort_order", sort_order);
        }
        if (StringUtils.isNotBlank(channel)) {
            request.put("channel", channel);
        }
        if (StringUtils.isNotBlank(categoryLevel)) {
            request.put("category_level", categoryLevel);
        }
        PaginatedResult<Category> paginatedResult = categoryService.pagenatedList(request);

        return paginatedResult;
    }

    @GetMapping ("/search")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_list', 'none')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public List<Category> searchCategory(@RequestParam (required = false) String searchName) {

        List<Category> list = categoryService.listCategory(searchName);

        return list;
    }

    @GetMapping ("/all")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_list', 'none')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Category> NewlistAllCategory(@RequestParam (required = false) int page_no, @RequestParam (required = false) int per_page) {
        Map<String, Object> request = new HashMap<>();
        request.put("page_no", page_no);
        request.put("per_page", per_page);
        PaginatedResult<Category> paginatedResult = categoryService.pagenatedList(request);
        return paginatedResult;
    }

    @GetMapping ("/search_by_name")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Category> searchByName(@RequestParam (required = false) int page_no, @RequestParam (required = false) int per_page,
            @RequestParam (required = false) String channel, @RequestParam (required = false) String status,
            @RequestParam (required = false) String name) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "category_list", "none") && !authorizationService.isAuthorized("marketing",
                "banner_add", "restricted_write") && !authorizationService.isAuthorized("marketing", "banner_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        Map<String, Object> request = new HashMap<>();
        request.put("page_no", page_no);
        request.put("per_page", per_page);
        request.put("name", name);
        request.put("channel", channel);
        request.put("status", status);
        PaginatedResult<Category> paginatedResult = categoryService.searchByName(request);
        return paginatedResult;
    }

    @GetMapping ("/{id}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_view', 'view_only')")
    public Category detailCategory(@PathVariable String id) {
        return categoryService.get(id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_view', 'view_only')")
    @PutMapping ("/multiget")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    public List<Category> fetchCategoryList(@RequestBody (required = false) List<String> categoryIds) throws URISyntaxException {
        return categoryService.getCategories(categoryIds);
    }

    @PostMapping
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Category addCategory(@RequestBody Category category) {
        return categoryService.add(category);
    }

    @PutMapping ("/{id}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void editCategory(@RequestBody Category category, @PathVariable String id) {
        List<CategoryAttributes> attrs = category.getAttributes();

        List<CategoryAttributes> newAttrs = attrs.stream().filter(p -> p.getCreatedAt() == 0).collect(Collectors.toList());

        if (newAttrs != null && !newAttrs.isEmpty()) {
            categoryService.addAttrs(category, newAttrs);
        }

        categoryService.update(category, id);
    }

    @GetMapping ("/{categoryId}/promotion")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public CategoryPromotion getCategoryPromotion(@PathVariable Long categoryId) {
        CategoryPromotion promotionDetails = categoryService.getCategoryPromotion(categoryId);

        if (promotionDetails != null && promotionDetails.getSubcategoryId() != null && !promotionDetails.getSubcategoryId().isEmpty()) {
            try {
                List<String> ids = new ArrayList<>();
                ids.add(promotionDetails.getSubcategoryId());
                Category subCategoryDetails = categoryService.getCategories(ids).get(0);
                promotionDetails.setSubCategoryName(subCategoryDetails.getName());
                promotionDetails.setSubCategoryCode(subCategoryDetails.getCode());
            } catch (Throwable t) {
                promotionDetails.setSubCategoryName("");
                promotionDetails.setSubCategoryCode("");
            }
        }
        return promotionDetails;
    }

    @PutMapping ("/{categoryId}/promotion")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateCategoryPromotion(@PathVariable Long categoryId, @RequestBody CategoryPromotion promotion) {
        categoryService.updateCategoryPromotion(categoryId, promotion);
    }

    @PostMapping ("/{categoryId}/promotion")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public CategoryPromotion createCategoryPromotion(@PathVariable Long categoryId, @RequestBody CategoryPromotion promotion) {
        return categoryService.createCategoryPromotion(categoryId, promotion);
    }

    @GetMapping ("/{categoryExternalId}/products")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','category_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public List<Product> getCategoryProducts(@PathVariable String categoryExternalId,
            @RequestParam (required = true, name = "search_text") String searchText) {
        Map<String, Object> request = new HashMap<>();

        if (StringUtils.isNotBlank(searchText)) {
            request.put("search_text", searchText);
        }

        request.put("listing_strategy", "general");

        List<SearchCategoryProductsResult> responseEntity = categoryService.searchCategoryProducts(categoryExternalId, request);

        List<Product> productsList = new ArrayList<>();
        responseEntity.forEach(resp -> {
            productsList.add(resp.getProduct());
        });
        return productsList;
    }
}
