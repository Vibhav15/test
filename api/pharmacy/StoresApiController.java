package com.halodoc.batavia.controller.api.pharmacy;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.cms.stores.Store;
import com.halodoc.batavia.entity.cms.stores.StoreLocation;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.timor.PopupStoresService;
import com.halodoc.batavia.service.timor.TimorEcomGatewayService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("api/v1/pharmacy/stores")
@RestController
@Slf4j
public class StoresApiController extends HalodocBaseApiController {

    private final PopupStoresService popupStoresService;
    private final TimorEcomGatewayService timorEcomGatewayService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public StoresApiController(PopupStoresService popupStoresService, TimorEcomGatewayService timorEcomGatewayService) {
        this.popupStoresService = popupStoresService;
        this.timorEcomGatewayService = timorEcomGatewayService;
    }


    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Store> getStores(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                            @RequestParam(required = false, name = "name") String name,
                                            @RequestParam(required = false, name = "language", defaultValue = "english") String language,
                                            @RequestParam(required = false, name = "statuses", defaultValue = "") String statuses,
                                            @RequestParam(required = false, name = "sort_by", defaultValue = "display_order") String sortBy,
                                            @RequestParam(required = false, name = "sort_order", defaultValue = "asc") String sortOrder) throws URISyntaxException {

        if (!authorizationService.isAuthorized("pharmacy_delivery","official_stores_list", "view_only")
                && !authorizationService.isAuthorized("marketing","condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing","condition_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return popupStoresService.getStores(pageNo, perPage, name,language, statuses,sortBy, sortOrder);

    }

    @GetMapping("/{storeId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','official_stores_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Store getStore(@PathVariable String storeId) {
        return popupStoresService.getStore(storeId);
    }


    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','official_stores_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Store createStore(@RequestBody Store store) {
        return popupStoresService.createStore(store);
    }

    @PatchMapping("/{storeId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','official_stores_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Store updateStore(@PathVariable String storeId, @RequestBody Store store) {
        return popupStoresService.updateStore(store);
    }

    @PatchMapping("/{storeId}/locations")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','official_stores_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Store updateStoreMerchants(@PathVariable String storeId, @RequestBody List<StoreLocation> storeLocations) {
        List<StoreLocation> newStoreLocations = storeLocations.stream()
                .filter(storeLocation -> StringUtils.isBlank(storeLocation.getExternalId()))
                .collect(Collectors.toList());
        List<StoreLocation> updatedStoreLocations = storeLocations.stream()
                .filter(storeLocation -> !StringUtils.isBlank(storeLocation.getExternalId()))
                .collect(Collectors.toList());
        if (newStoreLocations != null && !newStoreLocations.isEmpty()) {
            popupStoresService.createStoreLocations(storeId, newStoreLocations);
        }

        if (updatedStoreLocations != null && !updatedStoreLocations.isEmpty()) {
            popupStoresService.updateStoreLocations(storeId, updatedStoreLocations);
        }
        return popupStoresService.getStore(storeId);
    }

    @PatchMapping("/{storeId}/locations/{storeLocationId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','official_stores_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public StoreLocation updateStoreMerchant(@PathVariable String storeId, @PathVariable String storeLocationId,
                                             @RequestBody StoreLocation storeLocation) {
        if (storeLocation != null) {
            popupStoresService.updateStoreLocation(storeId, storeLocationId, storeLocation);
        }
        return popupStoresService.getStoreLocation(storeId, storeLocationId);
    }

    @GetMapping("/withdrawal-history/{shopId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','withdrawal_history', 'restricted_write')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public ResponseEntity getWithdrawal(@PathVariable String shopId,
                             @RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
                             @RequestParam(required = false, name = "per_page", defaultValue = "100") Integer perPage,
                             @RequestParam(required = false, name = "export", defaultValue = "1") Integer export,
                             @RequestParam(required = false, name = "from_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                             @RequestParam(required = false, name = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws URISyntaxException {
        ResponseEntity<byte[]> result = timorEcomGatewayService.exportWithdrawal(shopId, page, perPage, export,startDate, endDate);

        byte[] body = result.getBody();
        MediaType contentType = result.getHeaders().getContentType();
        ContentDisposition disposition = result.getHeaders().getContentDisposition();


        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + disposition.getFilename() + "\"")
                .contentType(contentType)
                .body(body);
    }

}
