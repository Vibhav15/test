package com.halodoc.batavia.controller.api.exodus.insurance;

import com.halodoc.batavia.entity.exodus.product.ProductEntity;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;

import java.net.URISyntaxException;
import java.util.List;

@RequestMapping("api/v1/exodus/insurance/product")
@RestController
@Slf4j
public class ExodusProductApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    List<ProductEntity> getActiveProducts(@RequestParam(required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogService.getActiveProducts(name);
    }
}
