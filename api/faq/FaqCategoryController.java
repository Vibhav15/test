package com.halodoc.batavia.controller.api.faq;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.faq.FaqCategory;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.faq.FaqCategoryService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

@RequestMapping("api/v1/faq/categories")
@RestController
public class FaqCategoryController extends HalodocBaseApiController{

    private FaqCategoryService faqCategoryService;

    @Autowired
    public FaqCategoryController(FaqCategoryService faqCategoryService) {
        this.faqCategoryService = faqCategoryService;
    }


    @GetMapping()
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_category_list', 'view_only')")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    PaginatedResult<FaqCategory> getFaqCategories(
         @RequestParam(value="search_text", defaultValue="") String searchTerm,
         @RequestParam(value="product_type", required=false) String productCode,
         @RequestParam(value="statuses", required=false) String statuses,
         @RequestParam(value="page_no", defaultValue="1") int pageNo,
         @RequestParam(value="per_page", defaultValue="10") int perPage,
         @RequestParam(value="sort_by", defaultValue="updated_at") String displayOrder,
         @RequestParam(value="sort_order", defaultValue="desc") String sortOrder) throws URISyntaxException {
        return faqCategoryService.search(searchTerm, productCode, statuses, pageNo, perPage, displayOrder, sortOrder);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_category_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public FaqCategory find(@PathVariable(name = "id") String id) throws URISyntaxException {
        return faqCategoryService.getFaqCategory(id);
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_category_add', 'restricted_write')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    FaqCategory saveFaq(@RequestBody FaqCategory faqCategory) throws URISyntaxException {
        return faqCategoryService.saveFaqCategory(faqCategory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_category_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    FaqCategory updateFaqCategory(@PathVariable String id, @RequestBody FaqCategory faqCategory) throws URISyntaxException {
        return faqCategoryService.updateFaqCategory(id, faqCategory);
    }

}
