package com.halodoc.batavia.controller.api.medisend;


import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.medisend.*;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.medisend.DistributorBranchService;
import com.halodoc.batavia.service.medisend.DistributorService;
import com.halodoc.batavia.service.medisend.WarehouseService;
import com.halodoc.batavia.service.timor.PriceConversionService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping ("api/v1/medisend/distributors")
@RestController
public class DistributorApiController {
    @Autowired
    private DistributorService distributorService;

    @Autowired
    private DistributorBranchService distributorBranchService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private PriceConversionService priceConversionService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Distributor> searchDistributor(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = "") String searchText,
            @RequestParam (required = false, name = "status") String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "distributor_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "point_program_actions", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "point_program_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return distributorService.searchDistributor(searchText, pageNo, perPage, status);
    }

    @GetMapping ("/search-branch")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Distributor> searchDistributorBranch(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                          @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                          @RequestParam (required = false, name = "search_text", defaultValue = "") String searchText,
                                                          @RequestParam (required = false, name = "type", defaultValue = "branch") String type,
                                                          @RequestParam (required = false, name = "status") String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "distributor_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "point_program_actions", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "point_program_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return distributorService.searchDistributorBranch(searchText, pageNo, perPage, status, type);
    }

    @GetMapping ("/search-branches")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Distributor> searchDistributorBranches(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = "") String searchText,
            @RequestParam (required = false, name = "status") String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "distributor_view", "view_only")
                && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return distributorService.searchDistributorBranches(searchText, pageNo, perPage, status);
    }

    @PostMapping
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Distributor addDistributor(@RequestBody Distributor addDistributorRequest) throws URISyntaxException {
        return distributorService.addDistributor(addDistributorRequest);
    }

    @PutMapping ("/{distributorId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateDistributor(@PathVariable String distributorId, @RequestBody Distributor updateDistributorRequest) throws URISyntaxException {
        distributorService.updateDistributor(distributorId, updateDistributorRequest);
    }

    @GetMapping ("/{distributorId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Distributor getDistributor(@PathVariable String distributorId) throws URISyntaxException {
        return distributorService.getDistributor(distributorId);
    }

    @GetMapping ("/{distributorId}/branches/search")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<DistributorBranch> searchDistributorBranch(@PathVariable String distributorId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = "") String searchText,
            @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "credit_status") String creditStatus

    ) throws URISyntaxException {
        return distributorBranchService.searchDistributorBranch(distributorId, searchText, pageNo, perPage, status, creditStatus);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/delivery-instructions")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public DeliveryInstructions getDOInstructions(@PathVariable String distributorId, @PathVariable String distributorBranchId)
            throws URISyntaxException {
        return distributorBranchService.getDeliveryInstructions(distributorId, distributorBranchId);
    }

    @PostMapping ("/{distributorId}/branches/{distributorBranchId}/delivery-instructions")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void createNewDeliveryInstructions(@PathVariable String distributorId,
                                              @PathVariable String distributorBranchId,
                                              @RequestBody DeliveryInstructions deliveryInstructions)
            throws URISyntaxException {
        distributorBranchService.createNewDeliveryInstructions(distributorId, distributorBranchId, deliveryInstructions);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}/delivery-instructions/upsert")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateDeliveryInstructions(@PathVariable String distributorId,
                                              @PathVariable String distributorBranchId,
                                              @RequestBody DeliveryInstructions deliveryInstructions)
            throws URISyntaxException {
        distributorBranchService.updateDeliveryInstructions(distributorId, distributorBranchId, deliveryInstructions);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/delivery-instructions/documents")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getDOInstructionsDocuments(@PathVariable String distributorId, @PathVariable String distributorBranchId)
            throws URISyntaxException {
        return distributorBranchService.getDeliveryInstructionsDocuments(distributorId, distributorBranchId);
    }

    @PostMapping("/{distributorId}/branches/{distributorBranchId}/delivery-instructions/upload")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public void uploadDoInstructionDocument(@PathVariable String distributorId, @PathVariable String distributorBranchId,
                                 @RequestHeader("X-Instruction-Type") String instructionType,
                                 @RequestHeader("X-File-Type") String xFileType,
                                 @RequestHeader("Content-Length") final Long contentLength,
                                 InputStream fileStream) throws URISyntaxException {
        distributorBranchService.uploadDoInstructionDocument(distributorId, distributorBranchId,
                instructionType, xFileType, contentLength, fileStream);
    }




    @GetMapping ("/{distributorId}/branches/{distributorBranchId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public DistributorBranch getDistributorBranch(@PathVariable String distributorId, @PathVariable String distributorBranchId)
            throws URISyntaxException {
        return distributorBranchService.getDistributorBranch(distributorId, distributorBranchId);
    }

    @PostMapping ("/{distributorId}/branches")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public DistributorBranch addDistributorBranch(@PathVariable String distributorId, @RequestBody DistributorBranch addBranchRequest)
            throws URISyntaxException {
        return distributorBranchService.addDistributorBranch(distributorId, addBranchRequest);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateDistributorBranch(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @RequestBody DistributorBranch updateBranchRequest) throws URISyntaxException {
        distributorBranchService.updateDistributorBranch(distributorId, distributorBranchId, updateBranchRequest);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/distributor-products/search")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map searchDistributorProducts(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @RequestParam (required = false, name = "name") String name) throws URISyntaxException {
        return distributorBranchService.searchDistributorProducts(distributorId, distributorBranchId, name);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/products/mapping/count")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getMappedProductsCount(@PathVariable String distributorId, @PathVariable String distributorBranchId) throws URISyntaxException {
        return distributorBranchService.getMappedProductsCount(distributorId, distributorBranchId);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/products/{distributorBranchProductId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    public EntityProduct getDistributorBranchProduct(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @PathVariable String distributorBranchProductId) throws URISyntaxException {
        return distributorBranchService.getProduct(distributorId, distributorBranchId, distributorBranchProductId);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/products/{distributorBranchProductId}/pharmacy-discounts")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map searchMerchantDiscounts(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "5") Integer perPage,
            @RequestParam (required = false, name = "merchant_location_name", defaultValue = "") String searchText,
            @RequestParam (required = false, name = "status", defaultValue = "") String status, @PathVariable String distributorId,
            @PathVariable String distributorBranchId, @PathVariable String distributorBranchProductId) throws URISyntaxException {
        return distributorBranchService.searchMerchantDiscounts(distributorId, distributorBranchId, distributorBranchProductId, searchText, status,
                pageNo, perPage);
    }

    @DeleteMapping ("/entities/entity_merchant_product/{entityId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_inventory_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map deletePharmacyEntity(@PathVariable String entityId) throws URISyntaxException {
        return distributorBranchService.deletePharmacyEntity(entityId);
    }

    @PostMapping ("/{distributorId}/branches/{distributorBranchId}/products/{productId}/merchant-locations/{merchantLocationId}/rate-cards")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map createPharmacyRateCard(@PathVariable String distributorId, @PathVariable String distributorBranchId, @PathVariable String productId,
            @PathVariable String merchantLocationId, @RequestBody Map rateCard) throws URISyntaxException {
        return distributorBranchService.createPharmacyRateCard(distributorId, distributorBranchId, productId, merchantLocationId, rateCard);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}/products/{productId}/merchant-locations/{merchantLocationId}/rate-cards/{rateCardId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updatePharmacyRateCard(@PathVariable String distributorId, @PathVariable String distributorBranchId, @PathVariable String productId,
            @PathVariable String merchantLocationId, @PathVariable String rateCardId, @RequestBody Map rateCard) throws URISyntaxException {
        return distributorBranchService.updatePharmacyRateCard(distributorId, distributorBranchId, productId, merchantLocationId, rateCardId,
                rateCard);
    }

    @PutMapping ("/branches/{distributorBranchId}/product/{productId}/merchant/{merchantLocationId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updateDiscountBonusStatus(@PathVariable String distributorBranchId, @PathVariable String productId,
            @PathVariable String merchantLocationId, @RequestBody Map discountBonusBody) throws URISyntaxException {
        return distributorBranchService.updateDiscountBonusStatus(distributorBranchId, productId, merchantLocationId, discountBonusBody);
    }

    @PostMapping ("/{distributorId}/branches/{distributorBranchId}/products/{productId}/merchant-locations/{merchantLocationId}/bonuses")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map createPharmacyBonusItem(@PathVariable String distributorId, @PathVariable String distributorBranchId, @PathVariable String productId,
            @PathVariable String merchantLocationId, @RequestBody Map bonusItem) throws URISyntaxException {
        return distributorBranchService.createPharmacyBonusItem(distributorId, distributorBranchId, productId, merchantLocationId, bonusItem);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}/products/{productId}/merchant-locations/{merchantLocationId}/bonuses/{bonusItemId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updatePharmacyBonusItem(@PathVariable String distributorId, @PathVariable String distributorBranchId, @PathVariable String productId,
            @PathVariable String merchantLocationId, @PathVariable String bonusItemId, @RequestBody Map bonusItem) throws URISyntaxException {
        return distributorBranchService.updatePharmacyBonusItem(distributorId, distributorBranchId, productId, merchantLocationId, bonusItemId,
                bonusItem);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}/products/{distributorBranchProductId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateDistributorBranchProduct(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @PathVariable String distributorBranchProductId, @RequestBody EntityProduct entityProduct) throws URISyntaxException {
        distributorBranchService.updateProduct(distributorId, distributorBranchId, distributorBranchProductId, entityProduct);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/products/search")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<EntityProduct> searchProductsInDistributorBranch(@PathVariable String distributorId,
            @PathVariable String distributorBranchId, @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = "") String searchText,
            @RequestParam (required = false, name = "mapped") String mapped, @RequestParam (required = false, name = "status") String status)
            throws URISyntaxException {

        return distributorBranchService.searchProducts(distributorId, distributorBranchId, searchText, pageNo, perPage, status, mapped);
    }

    @PostMapping ("/{distributorId}/branches/{distributorBranchId}/warehouses")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addWarehouse(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @RequestBody DistributorWarehouse warehouse) throws URISyntaxException {
        warehouseService.addWarehouse(distributorId, distributorBranchId, warehouse);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}/warehouses/{warehouseId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateWarehouse(@PathVariable String distributorId, @PathVariable String distributorBranchId, @PathVariable String warehouseId,
            @RequestBody DistributorWarehouse warehouse) throws URISyntaxException {
        warehouseService.updateWarehouse(distributorId, distributorBranchId, warehouseId, warehouse);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/warehouses")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','inventories_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getDistributorWarehouses(@PathVariable String distributorId, @PathVariable String distributorBranchId)
            throws URISyntaxException {
        return warehouseService.getWarehousesList(distributorId, distributorBranchId);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/warehouses/{warehouseId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','inventories_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public DistributorWarehouse getWarehouseById(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @PathVariable String warehouseId) throws URISyntaxException {
        return warehouseService.getWarehouseById(distributorId, distributorBranchId, warehouseId);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/search-pharmacies")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map searchPharmaciesForMapping(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @RequestParam (required = false, name = "name", defaultValue = "") String pharmacyName) throws URISyntaxException {
        return distributorBranchService.searchPharmaciesForMapping(pharmacyName, distributorBranchId);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/mapped-locations")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<PharmacyDistributorBranchMapping> getPharmaciesMappedToDistributorBranch(@PathVariable String distributorId,
            @PathVariable String distributorBranchId, @RequestParam (required = false, name = "mapping", defaultValue = "mapped") String mapping,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage) throws URISyntaxException {
        return distributorBranchService.getPharmaciesMappedToDistributorBranch(distributorId, distributorBranchId, pageNo, perPage, mapping);
    }

    @PostMapping ("/{distributorId}/branches/{distributorBranchId}/locations")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map addPharmacyToDistributorBranch(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @RequestBody Map addPharmacyRequest) throws URISyntaxException {
        return distributorBranchService.mapPharmacyToDistributorBranch(distributorId, distributorBranchId, addPharmacyRequest);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}/locations/{mappingId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updatePharmacyDistributorBranch(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @PathVariable String mappingId, @RequestBody Map updatePharmacyReqeust) throws URISyntaxException {
        return distributorBranchService.updatePharmacyDistributorBranchMapping(distributorId, distributorBranchId, mappingId, updatePharmacyReqeust);
    }

    @GetMapping ("/entities/{entityType}/{entityId}/rate-cards")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_inventories', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<EntityRateCard> getRateCard(@PathVariable String entityType, @PathVariable String entityId) throws URISyntaxException {
        return distributorBranchService.getRateCard(entityType, entityId);
    }

    @PostMapping ("/entities/{entityType}/{entityId}/rate-cards")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_inventories', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map saveRateCard(@PathVariable String entityType, @PathVariable String entityId, @RequestBody Map rateCard) throws URISyntaxException {
        return distributorBranchService.saveRatecard(entityType, entityId, rateCard);
    }

    @PutMapping ("/entities/{entityType}/{entityId}/rate-cards/{externalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_inventory_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updateRateCard(@PathVariable String entityType, @PathVariable String entityId, @PathVariable String externalId,
            @RequestBody Map rateCard) throws URISyntaxException {
        return distributorBranchService.updateRateCard(entityType, entityId, externalId, rateCard);
    }

    @GetMapping ("{distributorId}/branches/{distributorBranchId}/distributor-pharmacy-search/{pharmacyLocationId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_inventories', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<DistributorEntityPharmacy> searchDistributorPharmacy(@PathVariable String pharmacyLocationId,
            @PathVariable String distributorId, @PathVariable String distributorBranchId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = "") String searchText,
            @RequestParam (required = false, name = "status") String status) throws URISyntaxException {
        return distributorBranchService.searchDistributorPharmacy(pharmacyLocationId, searchText, pageNo, perPage, status, distributorId,
                distributorBranchId);
    }

    @GetMapping ("{distributorId}/branches/{distributorBranchId}/mapped-count")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getMappedCount(@PathVariable String distributorId, @PathVariable String distributorBranchId) throws URISyntaxException {
        return distributorBranchService.getMappedCount(distributorId, distributorBranchId);
    }

    /**
     * Code to add API's for medisend delivery 2.0
     */
    @PostMapping ("/{distributorId}/branches/{distributorBranchId}/warehouses/{warehouseId}/add-warehouse-pickupschedule")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addWarehousePickupTimeSchedules(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @PathVariable String warehouseId, @RequestBody Map warehouse) throws URISyntaxException {
        warehouseService.addWareHousePickupSchedule(distributorId, distributorBranchId, warehouseId, warehouse);
    }

    @PatchMapping ("/{distributorId}/branches/{distributorBranchId}/warehouses/{warehouseId}/update-warehouse-pickupschedule/{scheduleId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateWarehousePickupTimeSchedules(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @PathVariable String warehouseId, @PathVariable String scheduleId, @RequestBody Map warehouse) throws URISyntaxException {
        warehouseService.updateWareHousePickupSchedule(distributorId, distributorBranchId, warehouseId, scheduleId, warehouse);
    }

    @DeleteMapping ("/{distributorId}/branches/{distributorBranchId}/warehouses/{warehouseId}/delete-warehouse-pickupschedule/{scheduleId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deleteWarehouseSchedule(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @PathVariable String warehouseId, @PathVariable String scheduleId) throws URISyntaxException {
        warehouseService.deleteWarehouseSchedule(distributorId, distributorBranchId, warehouseId, scheduleId);
    }

    @GetMapping ("/entities/{entityType}/{entityId}/bonuses")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<EntityBonusItem> getBonusItemList(@PathVariable String entityType, @PathVariable String entityId) throws URISyntaxException {
        return distributorBranchService.getBonusItemList(entityType, entityId);
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/search-sku")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_search_sku', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map searchSkus(@PathVariable String distributorId, @PathVariable String distributorBranchId, @PathVariable String mapped,
            @PathVariable String skuId) throws URISyntaxException {
        return distributorBranchService.searchSkus(distributorId, distributorBranchId, mapped, skuId);
    }

    @PostMapping ("/entities/{entityType}/{entityId}/bonuses")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_inventories', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public EntityBonusItem addBonusItem(@PathVariable String entityType, @PathVariable String entityId, @RequestBody Map bonusItem)
            throws URISyntaxException {
        return distributorBranchService.addBonusItem(entityType, entityId, bonusItem);
    }

    @PutMapping ("/entities/{entityType}/{entityId}/bonuses/{externalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_inventories', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updateBonusItem(@PathVariable String entityType, @PathVariable String entityId, @PathVariable String externalId,
            @RequestBody Map rateCard) throws URISyntaxException {
        return distributorBranchService.updateBonusItem(entityType, entityId, externalId, rateCard);
    }

    @GetMapping ("/distributorautocomplete")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map getDistributors(@RequestParam ("search_text") String searchText, @RequestParam ("limit") Integer limit) throws URISyntaxException {
        return distributorService.getAutoCompleteDistributors(searchText, limit);
    }

    @GetMapping ("/branchautocomplete")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map getDistributorBranches(@RequestParam ("search_text") String searchText, @RequestParam ("limit") Integer limit)
            throws URISyntaxException {
        return distributorService.getAutoCompleteDistributorBranches(searchText, limit);
    }

    @GetMapping ("/logistics")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getDistributorLogistics() throws URISyntaxException {
        return distributorService.getLogistics();
    }

    @GetMapping ("/{distributorId}/branches/{distributorBranchId}/logistics")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getDistributorBranchLogistics(@PathVariable String distributorId, @PathVariable String distributorBranchId)
            throws URISyntaxException {
        return distributorService.getDistributorBranchLogistics(distributorId, distributorBranchId);
    }

    @PutMapping ("/{distributorId}/branches/{distributorBranchId}/logistics")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','distributor_branch_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void saveDistributorBranchLogistics(@PathVariable String distributorId, @PathVariable String distributorBranchId,
            @RequestBody Map logisticRequest) throws URISyntaxException {
        distributorService.saveDistributorBranchLogistics(distributorId, distributorBranchId, logisticRequest);
    }

    @GetMapping("/{distributorId}/branches/{branchId}/products/{productId}/batch-info")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getBatchInfo(@PathVariable String distributorId,@PathVariable String branchId,@PathVariable String productId) throws URISyntaxException{
            return distributorService.getBatchInfo(distributorId,branchId,productId);
    }
}
