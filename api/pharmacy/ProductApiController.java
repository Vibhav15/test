package com.halodoc.batavia.controller.api.pharmacy;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.cms.*;
import com.halodoc.batavia.entity.dcc.PackageBenefit;
import com.halodoc.batavia.entity.cms.product_package.*;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.entity.coupon.Coupon;
import com.halodoc.batavia.entity.dcc.PackageBenefitPayload;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.*;
import com.halodoc.batavia.service.digital_clinic.DigitalClinicCatalogService;
import com.halodoc.batavia.service.digital_clinic.DigitalClinicService;
import com.halodoc.batavia.service.subscriptions.SubscriptionsService;
import com.halodoc.batavia.service.timor.TimorProductService;
import com.halodoc.config.ConfigClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/v1/products")
@RestController
public class ProductApiController extends HalodocBaseApiController {

    @Autowired
    private AuthorizationService authorizationService;

    private Boolean isProductSearchLocationEnabled() {
        return this.configClient.getAppConfig().getTimorCmsConfiguration().getProductSearchLocationEnabled();
    }

    private ConfigClient<BataviaAppConfiguration> configClient;

    private final TimorProductService productService;

    private final StorageService storageService;
    private final CategoryService categoryService;
    private final CampaignService campaignService;
    private final DigitalClinicService digitalClinicService;
    private final SubscriptionsService subscriptionsService;
    private final DigitalClinicCatalogService digitalClinicCatalogService;

    @Autowired
    public ProductApiController(TimorProductService productService,
            StorageService storageService,
            CategoryService categoryService,
            CampaignService campaignService,
            DigitalClinicService digitalClinicService,
            SubscriptionsService subscriptionsService,
            DigitalClinicCatalogService digitalClinicCatalogService,
            ConfigClient<BataviaAppConfiguration> configClient

    ) {
        this.productService = productService;
        this.storageService = storageService;
        this.categoryService = categoryService;
        this.campaignService = campaignService;
        this.digitalClinicService = digitalClinicService;
        this.subscriptionsService = subscriptionsService;
        this.digitalClinicCatalogService = digitalClinicCatalogService;
        this.configClient = configClient;
    }

    @GetMapping("/list")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Product> NewlistProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String display,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String source,
            @RequestParam(value = "principal_id", required = false) String principalId,
            @RequestParam(required = false) int page_no,
            @RequestParam(required = false) int per_page) {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_list", "view_only")
                && !authorizationService.isAuthorized("contact_doctor", "doctor_edit", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "principal_management", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        Map<String, Object> request = new HashMap<>();

        request.put("page_no", page_no);
        request.put("per_page", per_page);

        if (StringUtils.isNotBlank(name)) {
            request.put(Product.FILTER_NAME, name);
        }
        if (StringUtils.isNotBlank(status)) {
            request.put(Product.FILTER_STATUS, status);
        }
        if (StringUtils.isNotBlank(display)) {
            request.put(Product.FILTER_DISPLAY, Boolean.valueOf(display));
        }
        if (StringUtils.isNotBlank(display)) {
            request.put(Product.FILTER_TYPE, type);
        }

        if (StringUtils.isNotBlank(source)) {
            request.put(Product.FILTER_SOURCE, source);
        }

        if (StringUtils.isNotBlank(principalId)) {
            request.put(Product.FILTER_PRINCIPAL_ID, principalId);
        }

        PaginatedResult<Product> paginatedResult = productService.pagenatedList(request);

        updateAttributes(paginatedResult.getResult());

        return paginatedResult;
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Product addProduct(@RequestBody Product product) {
        product.setPrice(product.getMaxRetailPrice());
        return productService.save(product);
    }

    @GetMapping("/dosages")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map getDosages() throws URISyntaxException {
        return response(productService.dosages());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map detailProduct(@PathVariable String id) throws URISyntaxException {
        Product product = productService.get(id);
        if (product != null) {
            product.setAttributes(productService.getAttributes(product.getId()));
            try {
               product.setCategories(productService.getCategories(product.getId()));
            } catch (HalodocWebException ex) {
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    // No categories for this product
                    product.setCategories(Collections.emptyList());
                } else {
                    throw ex;
                }
            }
        }

        return response(product);
    }

    @GetMapping("/{productExternalId}/promotion")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Map promotionDetails(@PathVariable String productExternalId) throws URISyntaxException {
        return productService.getPromotionDetails(productExternalId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @PostMapping("/{productExternalId}/create/promotion")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map createPromotion(@PathVariable String productExternalId, @RequestBody Map promotionRequest)
            throws URISyntaxException {
        return productService.createPromotion(productExternalId, promotionRequest);
    }

    @PutMapping("/{productExternalId}/update/promotion")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map updatePromotion(@PathVariable String productExternalId, @RequestBody Map promotionRequest)
            throws URISyntaxException {
        return productService.updatePromotion(productExternalId, promotionRequest);
    }

    @GetMapping("/availablePromotions")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<String> availablePromotions() throws URISyntaxException {
        return productService.getAvailablePromotions();
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Product editProduct(@PathVariable String id, @RequestBody Product product) throws URISyntaxException {
        return productService.update(product, id);
    }

    @PostMapping ("/{id}/attributes")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addProductAttributes(@PathVariable String id, @RequestBody Map productAttributes) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_edit", "restricted_write") && !authorizationService.isAuthorized(
                "pharmacy_delivery", "principal_management", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        productService.createProductAttributes(id, productAttributes);
    }

    @PutMapping ("/{id}/attributes")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateProductAttributes(@PathVariable String id, @RequestBody Map productAttributes) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_edit", "restricted_write") && !authorizationService.isAuthorized(
                "pharmacy_delivery", "principal_management", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        productService.updateProductAttributes(productAttributes, id);
    }

    @PutMapping("/{id}/ecommerce/attributes")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    public void saveProductEcomAttributes(@PathVariable String id, @RequestBody List<Map> ecomAttributes)
            throws URISyntaxException {
        productService.saveEcomAttributes(id, ecomAttributes);
    }

    @GetMapping("/autocomplete")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public List<Product> productsAutoCompleteSearch(@RequestParam(name = "search_text") String searchText,
            @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "status", required = false, defaultValue = "active") String status,
            @RequestParam(value = "latitude", required = false) BigDecimal latitude,
            @RequestParam(value = "longitude", required = false) BigDecimal longitude) {
        List<Product> products;

        if (this.isProductSearchLocationEnabled()) {
            products = productService.productsAutoCompleteSearchWithLocation(searchText, limit, latitude, longitude);
        } else {
            products = productService.productsAutoCompleteSearch(searchText, status, limit, page);
        }

        return products;
    }

    private void updateAttributes(List<Product> products) {
        List<String> productIdList = products.stream().map(Product::getExternalId).collect(Collectors.toList());

        try {
            List<MultiGetProductAttributesResponseItem> attributeMap = productService
                    .getProductAttributes(productIdList);
            products.parallelStream().forEach(p -> {
                Optional<MultiGetProductAttributesResponseItem> additionalDetailOptional = attributeMap.stream()
                        .filter(item -> item.getProductId().equalsIgnoreCase(p.getExternalId())).findFirst();

                additionalDetailOptional
                        .ifPresent(multiGetProductAttributesResponseItem -> p
                                .setAttributes(multiGetProductAttributesResponseItem
                                        .getProductDetail().getProductAttributes()));
            });

        } catch (Throwable t) {
            // Ignore
        }
    }

    @PutMapping("/multiget")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    List<Product> multiGetProducts(@RequestBody List<String> productIdList) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_view", "view_only")
                && !authorizationService
                        .isAuthorized("pharmacy_delivery", "product_add", "restricted_write")
                && !authorizationService
                        .isAuthorized("pharmacy_delivery", "product_edit", "restricted_write")
                && !authorizationService
                        .isAuthorized("pharmacy_delivery", "product_list", "restricted_write")
                && !authorizationService
                        .isAuthorized("pharmacy_delivery", "lead_view", "view_only")
                && !authorizationService
                        .isAuthorized("pharmacy_delivery", "order_create_reorder", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        if (productIdList == null || productIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Product> products = productService.getProducts(productIdList);
        updateAttributes(products);

        return products;
    }

    @GetMapping("/{productExternalId}/uom-mappings")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<UomMapping> getUomMappings(@PathVariable String productExternalId) {
        return productService.getUomMappingList(productExternalId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @PostMapping("/{productExternalId}/uom-mappings")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Map addUomMapping(@PathVariable String productExternalId, @RequestBody Map uomAddRequest)
            throws URISyntaxException {
        return productService.addUomMapping(productExternalId, uomAddRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @PutMapping("/{productExternalId}/uom-mappings/{uomMappingId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void editUomMapping(@PathVariable String productExternalId,
            @PathVariable String uomMappingId,
            @RequestBody Map uomEditRequest) throws URISyntaxException {
        productService.editUomMapping(productExternalId, uomMappingId, uomEditRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @DeleteMapping("/{productExternalId}/uom-mappings/{uomMappingId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deleteUomMapping(@PathVariable String productExternalId,
            @PathVariable String uomMappingId) throws URISyntaxException {
        productService.deleteUomMapping(productExternalId, uomMappingId);
    }

    @GetMapping("/{productExternalId}/classifications")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<ProductClassification> getProductClassificationsByProductId(@PathVariable String productExternalId) {
        return productService.getProductClassificationsByProductId(productExternalId);
    }

    @PostMapping("/{productExternalId}/classifications")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_add', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<ProductClassification> productClassificationsMultiLink(@PathVariable String productExternalId,
            @RequestBody List<ProductClassification> productClassificationList) {
        return productService.linkClassificationsToProduct(productExternalId, productClassificationList);
    }

    @PutMapping("/{productExternalId}/classifications/{productClassificationExternalId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void productClassificationUnlink(
            @PathVariable String productExternalId,
            @PathVariable String productClassificationExternalId,
            @RequestBody ProductClassification productClassification) {
        productService.unlinkClassificationFromProduct(productExternalId, productClassificationExternalId,
                productClassification);
    }

    @PutMapping("/multiget/productNames")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_list', 'view_only')")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    public Map<String, String> multiGetProductNamesForConditionList(@RequestBody List<String> productIdList) {
        if (productIdList == null || productIdList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> products = productService.getProductslist(productIdList);
        return products;
    }

    @GetMapping("/coupons")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Coupon> getGratisOngkirCoupons(@RequestParam(name = "search_text") String searchText,
            @RequestParam(name = "page_number", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "page", required = false, defaultValue = "10") int page) {
        return campaignService.getGratisOngkirCoupons(searchText, pageNumber, page);
    }

    @GetMapping("/{productId}/coupon-details")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public ProductCoupon getCouponDetails(@PathVariable String productId) {
        return productService.getCouponDetails(productId);
    }

    @PostMapping("{productId}/coupon")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public ProductCoupon createProductCouponMapping(@PathVariable String productId,
            @RequestBody Map<String, String> couponName) {
        return productService.createProductCouponMapping(productId, couponName);
    }

    @PutMapping("{productId}/coupon")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateProductCouponMapping(@PathVariable String productId,
            @RequestBody Map<String, String> couponName) {
        productService.updateProductCouponMapping(productId, couponName);
    }

    @GetMapping("{productExternalId}/product-package/details")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public ProductPackage getProductPackageDetails(@PathVariable String productExternalId) {
        return digitalClinicService.getPackageDetails(productExternalId);
    }

    @PutMapping("{productPackageId}/product-package/update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public ProductPackage updateProductPackageStatus(@PathVariable String productPackageId,
            @RequestBody ProductPackagePayload productPackage) {
        return digitalClinicService.updateProductPackageStatus(productPackageId, productPackage);
    }

    @PostMapping("/product-package/create")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public ProductPackage createProductPackage(@RequestBody ProductPackagePayload productPackage) {
        return digitalClinicService.createProductPackageMapping(productPackage);
    }

    @PutMapping("{productPackageId}/package-products/edit")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<PackageItem> editPackageProducts(@PathVariable String productPackageId,
            @RequestBody List<PackageItem> packageProducts) {
        return digitalClinicService.editPackageProducts(productPackageId, packageProducts);
    }

    @GetMapping("{productPackageId}/package-product-details")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<PackageItem> getPackageProducts(@PathVariable String productPackageId) {
        return digitalClinicService.getPackageProducts(productPackageId);
    }

    @GetMapping("/benefits/auto-complete")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public List<Map> getBenefitsList(
            @RequestParam(required = true) String name,
            @RequestParam(required = true) String type,
            @RequestParam(required = true) String include_digital_clinic_package) throws URISyntaxException {


        return subscriptionsService.getBenefitsList(name, type, include_digital_clinic_package).getResult();
    }

    @PostMapping("/package-benefits/add")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public List<PackageBenefit> addBenefitsToPackage(@RequestBody List<PackageBenefitPayload> packageBenefitsList) {
        return digitalClinicCatalogService.addBenefitsToPackage(packageBenefitsList);
    }

    @PutMapping("{productExternalId}/package-benefit/update")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<PackageBenefit> updatePackageBenefits(@PathVariable String productExternalId,
            @RequestBody List<PackageBenefitPayload> packageBenefitsList) {
        return digitalClinicCatalogService.updatePackageBenefits(productExternalId, packageBenefitsList);
    }

    @GetMapping("{productExternalId}/package-benefits")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<PackageBenefit> getPackageBenefits(@PathVariable String productExternalId) {
        return digitalClinicCatalogService.getPackageBenefitDetails(productExternalId);
    }

    @GetMapping("/digital-clinic-categories")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Object getDigitalClinicCategories() {
        return this.configClient.getAppConfig().getDigitalClinicConfiguration().getSubCategory();
    }

    @GetMapping("/{productId}/substitute-products")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public ProductSubstitute getSubstituteProducts(@PathVariable String productId) {
        return this.productService.getSubstituteProducts(productId);
    }

    @PutMapping("/{productId}/availability_zones")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public AvailabilityZone updateAvailableZone(@PathVariable String productId,@RequestBody Object availabilityZones){
        return this.productService.updateAvailabilityZone(productId,availabilityZones);
    }

    @PutMapping("/{productId}/principal/{principalId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void mapPrincipalProduct(@PathVariable String productId, @PathVariable String principalId) throws URISyntaxException {
        productService.mapPrincipalProduct(productId, principalId);
    }

    @DeleteMapping("/{productId}/principal/{principalId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deletePrincipalMapping(@PathVariable String productId, @PathVariable String principalId) throws URISyntaxException {
        productService.deletePrincipalMapping(productId, principalId);
    }

   @PutMapping("/{productId}/principal-business-units/{businessUnitId}")
   @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'restricted_write')")
   @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
   public void mapBusinessUnitProduct(@PathVariable String productId, @PathVariable String businessUnitId) throws  URISyntaxException {
        productService.mapBusinessUnitProduct(productId, businessUnitId);
   }

}
