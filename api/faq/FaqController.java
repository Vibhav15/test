package com.halodoc.batavia.controller.api.faq;

import java.net.URISyntaxException;
import java.util.List;
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
import com.halodoc.batavia.entity.faq.Faq;
import com.halodoc.batavia.entity.faq.FaqCategoriesAttributes;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.faq.FaqService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

@RequestMapping("api/v1/faqs")
@RestController
public class FaqController extends HalodocBaseApiController{
    private FaqService faqService;

    @Autowired
    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping()
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_list', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
     PaginatedResult<Faq> getFaqs(@RequestParam(value="search_text", defaultValue="") String searchTerm,
                @RequestParam(value="product_type", required=false) String productCode,
                @RequestParam(value="category_ids", required=false) String[] categoryIds,
                @RequestParam(value="statuses", required=false) String status,
                @RequestParam(value="page_no", defaultValue="1") int pageNo, @RequestParam(value="per_page", defaultValue=Integer.MAX_VALUE+"") int perPage,
                @RequestParam(value="sort_by", defaultValue="updated_at") String displayOrder,
                @RequestParam(value="sort_order", defaultValue="desc") String sortOrder) throws URISyntaxException {
        return faqService.search(searchTerm, productCode, categoryIds, status, pageNo, perPage, displayOrder, sortOrder);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Faq find(@PathVariable(name = "id") String id) throws URISyntaxException {
        return faqService.getFaq(id);
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_add', 'restricted_write')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    Faq saveFaq(@RequestBody Faq faq) throws URISyntaxException {
        return faqService.saveFaq(faq);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    Faq updateFaqCategory(@PathVariable String id, @RequestBody Faq faq) throws URISyntaxException {
        return faqService.updateFaq(id, faq);
    }

    @PutMapping("/v1/faq_categories")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    void addFaqCategories(@RequestBody List<FaqCategoriesAttributes> faqCategories) throws URISyntaxException {
        faqService.addFaqCategories(faqCategories);
    }

    @PutMapping("/{id}/active")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    Faq toggleActive(@PathVariable String id, @RequestBody Faq faq) throws URISyntaxException {
        return faqService.toggleActive(id, faq);
    }

    @PutMapping("/trending/swap/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    Faq swapTrending(@PathVariable String id, @RequestBody Faq faq) throws URISyntaxException {
        return faqService.swapTrending(id, faq);
    }

    @PutMapping("/{id}/trending")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    Faq updateTrending(@PathVariable String id, @RequestBody Faq faq) throws URISyntaxException {
        return faqService.updateTrending(id, faq);
    }

    @GetMapping("/trending")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    PaginatedResult<Faq> findTrendings(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "product_type") String productCode,
            @RequestParam (required = false, name = "type") String type,
            @RequestParam (required = false, name = "sort_by", defaultValue =  "display_order") String sortBy,
            @RequestParam (required = false, name = "sort_order", defaultValue = "asc") String sortOrder) throws URISyntaxException {
        return faqService.getTrendings(pageNo, perPage, productCode,type,sortBy,sortOrder);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("@authorizationService.isAuthorized('cms','faq_question_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Faq publish(@RequestBody Faq model, @PathVariable(name = "id") String id) throws URISyntaxException {
        return faqService.publish(id, model);
    }

}
