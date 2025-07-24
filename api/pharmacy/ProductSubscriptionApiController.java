package com.halodoc.batavia.controller.api.pharmacy;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.subscriptions.SchemeService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@Controller
@RestController
@Slf4j
@RequestMapping("api/v1/product-package")
public class ProductSubscriptionApiController extends HalodocBaseApiController {

    @Autowired
    SchemeService subscriptionService;

    @GetMapping("/package")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map getProductSubscriptionPackage (
            @RequestParam(name="condition_name", required=false, defaultValue="product_external_id") String condition_name,
            @RequestParam(name = "condition_value") String productExternalId) throws URISyntaxException {
        return response(subscriptionService.getProductSubscriptionPackage(condition_name, productExternalId));
    }

    @PostMapping("/{productId}/package")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map addProductSubscriptionPackage (
            @PathVariable String productId,
            @RequestBody Map productPackage
    ) throws  URISyntaxException{
        return subscriptionService.addProductSubscriptionPackage(productId, productPackage);
    }

    @PutMapping("/{id}/package/{packageId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updateProductSubscriptionPackage (
            @PathVariable String id,
            @PathVariable String packageId,
            @RequestBody Map updatedPackage
    ) throws URISyntaxException {
        return subscriptionService.updateProductSubscriptionPackage(id, packageId, updatedPackage);
    }

}
