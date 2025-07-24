
package com.halodoc.batavia.controller.api.pharmacy;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.cms.ProductManufacturer;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.timor.ManufacturerService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;

@RequestMapping("api/v1/manufacturers")
@RestController
@Slf4j
public class ManufacturerApiController extends HalodocBaseApiController {

    @Autowired
    ManufacturerService manufacturerService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    PaginatedResult<ProductManufacturer> getManufacturers(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                          @RequestParam(required = false, name = "name") String name) throws URISyntaxException {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "manufactures_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "product_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "product_add", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "product_edit", "restricted_write")) {

            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return manufacturerService.getManufacturers(name, pageNo, perPage);
    }


    @PostMapping("/")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','manufactures_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    ProductManufacturer saveManufacturer(@RequestBody ProductManufacturer manufacturer) throws URISyntaxException {
        return manufacturerService.saveManufacturer(manufacturer);
    }

    @PutMapping("/{manufacturerId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','manufactures_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    void updateManufacturer(@PathVariable String manufacturerId, @RequestBody ProductManufacturer manufacturer) throws URISyntaxException {
        manufacturerService.updateManufacturer(manufacturer);
    }
}
