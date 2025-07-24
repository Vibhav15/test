package com.halodoc.batavia.controller.api.pharmacy;

import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.ClassificationsService;
import com.halodoc.batavia.service.timor.TimorProductService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/v1/product-groups")
@RestController
public class ProductGroupApiController {

    @Autowired
    private AuthorizationService authorizationService;

    private final ClassificationsService classificationsService;
    private final TimorProductService productService;


    @Autowired
    public ProductGroupApiController(TimorProductService productService,
                                     ClassificationsService classificationsService) {
        this.classificationsService = classificationsService;
        this.productService = productService;
    }

    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_add', 'restricted_write')")
    public Map addGroup(@RequestBody Map group) {
        return productService.saveProductGroup(group);
    }


    @PutMapping("/{groupId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updateProductGroup(@PathVariable String groupId, @RequestBody Map productGroup) throws URISyntaxException {
        return productService.updateGroup(groupId, productGroup);
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getGroup(@PathVariable String groupId) {
        return productService.getGroup(groupId);
    }

    @GetMapping("/{id}/products")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getGroupProducts(@PathVariable String id) {
        return productService.getGroupProducts(id);
    }


    @GetMapping("/search")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Map> search(
            @RequestParam(required = false,  name = "search_text", defaultValue = "") String searchText,
            @RequestParam(required = false, name = "status", defaultValue = "") String status,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") String pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "10") String perPage,
            @RequestParam(required = false, name = "sort_by", defaultValue = "created_at") String sortBy,
            @RequestParam(required = false, name = "sort_order", defaultValue = "desc") String sortOrder
    ) {
        PaginatedResult<Map> paginatedResult = productService.searchGroup(searchText, pageNo, perPage, status, sortBy, sortOrder );
        return paginatedResult;
    }

    @PutMapping("/{groupId}/products/{productId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updateGroupProduct(@PathVariable String groupId, @PathVariable String productId, @RequestBody Map productGroup) throws URISyntaxException {
        return productService.updateProductinGroup(groupId, productId, productGroup);
    }

//    @PostMapping("/{groupId}/products")
//    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
//    public Map addProductinGroup(@PathVariable String groupId, @RequestBody Map productGroup) throws URISyntaxException {
//        return productService.addProductinGroup(groupId, productGroup);
//    }

    @DeleteMapping("/{groupId}/products/{productId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deleteGroupProduct (@PathVariable String groupId,
                                  @PathVariable String productId) throws URISyntaxException {
        productService.deleteGroupProduct(groupId, productId);
    }

    @PutMapping("/{groupId}/products/reorder")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map reorderGroupProduct(@PathVariable String groupId, @RequestBody List<Map> reorderReq) throws URISyntaxException {
        return productService.reorderProductinGroup(groupId, reorderReq);
    }



}
