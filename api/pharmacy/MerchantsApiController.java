package com.halodoc.batavia.controller.api.pharmacy;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bali.logan.Customer;
import com.halodoc.batavia.entity.bali.logan.Identity;
import com.halodoc.batavia.entity.cms.PharmacyUser;
import com.halodoc.batavia.entity.cms.PriceConversionAttribute;
import com.halodoc.batavia.entity.cms.Role;
import com.halodoc.batavia.entity.cms.merchants.*;
import com.halodoc.batavia.entity.cms.merchants.product.MerchantProduct;
import com.halodoc.batavia.entity.cms.merchants.product.SlashPrice;
import com.halodoc.batavia.entity.cms.merchants.product.SlashPriceResponse;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.medisend.EntityService;
import com.halodoc.batavia.entity.medisend.EntityType;
import com.halodoc.batavia.entity.medisend.UserPoints;
import com.halodoc.batavia.entity.finance.PaymentWithholdRequest;
import com.halodoc.batavia.entity.finance.PharmacyReconCatalog;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.GeoService;
import com.halodoc.batavia.service.PharmacyService;
import com.halodoc.batavia.service.PharmacyUserService;
import com.halodoc.batavia.service.bali.logan.BaliLoganService;
import com.halodoc.batavia.service.finance.ReconCatalogService;
import com.halodoc.batavia.service.medisend.DistributorService;
import com.halodoc.batavia.service.medisend.LoyaltyPointManagementService;
import com.halodoc.batavia.service.timor.MerchantProductsService;
import com.halodoc.batavia.service.timor.MerchantsService;
import com.halodoc.batavia.service.timor.PriceConversionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("api/v1/pharmacy/merchants")
@RestController
@Slf4j
public class MerchantsApiController extends HalodocBaseApiController {

    @Autowired
    MerchantsService pharmacyMerchantsService;
    @Autowired
    private PharmacyUserService pharmacyUserService;
    @Autowired
    private GeoService geoService;
    @Autowired
    private BaliLoganService baliLoganService;
    @Autowired
    private PriceConversionService priceConversionService;
    @Autowired
    private MerchantProductsService merchantProductsService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private PharmacyService pharmacyService;
    @Autowired
    private DistributorService distributorService;
    @Autowired
    private ReconCatalogService reconCatalogService;

    @Autowired
    private LoyaltyPointManagementService loyaltyPointManagementService;


    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    PaginatedResult<Merchant> getMerchants(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                           @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                           @RequestParam(required = false, name = "name") String name,
                                           @RequestParam(required = false, name = "status") String status,
                                           @RequestParam(required = false, name = "phone") String phone,
                                           @RequestParam(required = false, name = "email") String email) throws URISyntaxException {

        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_list", "view_only")
                && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return pharmacyMerchantsService.getMerchants(name, pageNo, perPage, phone, email, status);
    }

    @GetMapping("/locations/meta_data/categorization")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    List<String> getMerchantLocationCategories() throws URISyntaxException {
        return pharmacyMerchantsService.getMerchantLocationCategories();
    }

    @GetMapping("/locations/meta_data/product_segmentation")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    List<String> getProductSegmentation() throws URISyntaxException {
        return pharmacyMerchantsService.getProductSegmentationList();
    }

    @GetMapping("/locations/meta_data/categorization/{merchantCategory}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    List<MerchantLocationCategoryMetaData> getMetaDataByMerchantLocationCategory(@PathVariable String merchantCategory) throws URISyntaxException, UnsupportedEncodingException {
        return pharmacyMerchantsService.getMetaDataByMerchantLocationCategory(merchantCategory);
    }

    @GetMapping("/{merchantId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    Merchant getMerchant(@PathVariable String merchantId) throws URISyntaxException {
        return pharmacyMerchantsService.getMerchant(merchantId);
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    Merchant saveMerchant(@RequestBody Merchant merchant) throws URISyntaxException {
        return pharmacyMerchantsService.saveMerchant(merchant);
    }

    @PutMapping("/{merchantId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    Merchant updateMerchant(@PathVariable String merchantId, @RequestBody Merchant merchant) throws URISyntaxException {
        return pharmacyMerchantsService.updateMerchant(merchant);
    }

    @GetMapping("/{merchantId}/locations")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    PaginatedResult<MerchantLocation> getMerchantLocations(@PathVariable String merchantId,
                                                           @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                           @RequestParam(required = false, name = "name") String name,
                                                           @RequestParam(required = false, name = "zone_id") String zoneId,
                                                           @RequestParam(required = false, name = "status") String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_list", "view_only")
                && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return pharmacyMerchantsService.getMerchantLocations(merchantId, name, zoneId, pageNo, perPage, status);
    }

    @GetMapping("/{merchantId}/locations/multi-get")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    PaginatedResult<MerchantLocation> getMerchantLocationsByZoneIds(@PathVariable String merchantId,
                                                                    @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                                    @RequestParam(required = false, name = "name") String name,
                                                                    @RequestParam(required = false, name = "zone_ids") String zoneIds,
                                                                    @RequestParam(required = false, name = "status") String status) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_list", "view_only")
                && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_list", "view_only")) {

            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();

        }

        return pharmacyMerchantsService.getMerchantLocationsByZoneIds(merchantId, name, zoneIds, pageNo, perPage, status);
    }

    @GetMapping("/locations/multiget")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.PD)
    List<MerchantLocation> getMerchantLocationsList(@RequestParam(required = true, name = "merchant_location_ids") List<String> merchantLocationIds) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "point_exchange_view", "view_only")
                ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        List<MerchantLocation> merchantList = pharmacyService.getMerchantLocations(merchantLocationIds);
        return merchantList;
    }

    @PostMapping("/{merchantId}/locations")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    MerchantLocation saveMerchantLocations(@PathVariable String merchantId, @RequestBody MerchantLocation merchantLocation) throws URISyntaxException {
        return pharmacyMerchantsService.addMerchantLocation(merchantId, merchantLocation);
    }

    @PutMapping("/{merchantId}/locations/{merchantLocationId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public MerchantLocation updateMerchantLocations(@PathVariable String merchantId, @PathVariable String merchantLocationId,
                                                    @RequestBody MerchantLocation merchantLocation) throws URISyntaxException {
        MerchantLocation updatedMerchantLocation;
        updatedMerchantLocation = pharmacyMerchantsService.updateMerchantLocation(merchantId, merchantLocationId, merchantLocation);

        List<PharmacyUser> pharmacyUsers = fetchMerchantLocUsers(merchantId, merchantLocationId);
        updatedMerchantLocation.setPharmacyUsers(pharmacyUsers);

        return updatedMerchantLocation;

    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/point-details")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public UserPoints getMLPointDetails(@PathVariable String merchantId, @PathVariable String merchantLocationId) throws URISyntaxException {
        return this.loyaltyPointManagementService.getUserPoints(merchantLocationId, EntityType.MERCHANT_LOCATION, EntityService.DERAWAN);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/documents/{documentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    public String getKycDocUrl(@PathVariable String merchantId, @PathVariable String merchantLocationId,
                                        @PathVariable String documentId) {
        return pharmacyMerchantsService.getKycDocumentUrl(merchantId, merchantLocationId, documentId);
    }

    @PostMapping("/{merchantId}/locations/{merchantLocationId}/kyc-upload")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public Map uploadKycDocument(@PathVariable String merchantId, @PathVariable String merchantLocationId,
                                       @RequestHeader("X-Document-Type") String kycDocType,
                                        @RequestHeader("X-File-Type") String xFileType,
                                       @RequestHeader("Content-Length") final Long contentLength,
                                       InputStream fileStream) {
        return pharmacyMerchantsService.uploadKycDocument(merchantId, merchantLocationId, kycDocType, xFileType, contentLength, fileStream);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    MerchantLocation getMerchantLocations(@PathVariable String merchantId,
                                          @PathVariable String merchantLocationId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_list", "view_only")
                && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        MerchantLocation merchantLocation;
        merchantLocation = pharmacyMerchantsService.getMerchantLocation(merchantId, merchantLocationId);

        List<PharmacyUser> pharmacyUsers = fetchMerchantLocUsers(merchantId, merchantLocationId);
        merchantLocation.setPharmacyUsers(pharmacyUsers);

        return merchantLocation;
    }

    @GetMapping("/locations/{merchantLocationId}/search-branches")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map searchDistributorBranchesForMapping(@PathVariable String merchantLocationId,
                                                   @RequestParam(required = false, name = "search_text", defaultValue = "") String branchName
    ) throws URISyntaxException {
        return pharmacyMerchantsService.searchDistributorBranchesForMapping(branchName, merchantLocationId);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/mapped-branches")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map getBranchesMappedToPharmacy(@PathVariable String merchantId,
                                           @PathVariable String merchantLocationId,
                                           @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                           @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return pharmacyMerchantsService.getBranchesMappedToPharmacy(merchantId, merchantLocationId, pageNo, perPage);
    }

    @PostMapping("/{merchantId}/locations/{merchantLocationId}/branches")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    // triggered by add from search dropdown if no attribute
    public List addDistributorBranchToPharmacy(@PathVariable String merchantId,
                                               @PathVariable String merchantLocationId,
                                               @RequestBody List<Map> addBranchRequest) throws URISyntaxException {
        return pharmacyMerchantsService.mapDistributorBranchToPharmacy(merchantId, merchantLocationId, addBranchRequest);
    }

    @PutMapping("/{merchantId}/locations/{merchantLocationId}/branches/{mappingId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    // triggered by add from search dropdown if there is an attribute
    public Map updateDistributorBranchPharmacyMapping(@PathVariable String merchantId,
                                                      @PathVariable String merchantLocationId,
                                                      @PathVariable String mappingId,
                                                      @RequestBody Map updateBranchPharmacyReqeust) throws URISyntaxException {
        return pharmacyMerchantsService.updateDistributorBranchPharmacyMapping(merchantId,
                merchantLocationId,
                mappingId, updateBranchPharmacyReqeust);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/products/ecom")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    PaginatedResult<MerchantProduct> getMerchantLocationEcomProducts(@PathVariable String merchantId,
                                                                     @PathVariable String merchantLocationId,
                                                                     @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                     @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                                     @RequestParam(required = false, name = "name", defaultValue = "") String name,
                                                                     @RequestParam(required = false, name = "status", defaultValue = "active,inactive") String status,
                                                                     @RequestParam(required = false, name = "ecom_provider", defaultValue = "") String ecomProvider,
                                                                     @RequestParam(required = false, name = "type", defaultValue = "") String type) throws URISyntaxException {

        if (StringUtils.isBlank(name)) {
            return merchantProductsService.getMerchantEcomProducts(merchantId, merchantLocationId, ecomProvider, pageNo, perPage);
        } else {
            return merchantProductsService.searchMerchantProducts(merchantId, merchantLocationId, pageNo, perPage, name, type, status);
        }

    }

    @PostMapping("/{merchantId}/locations/{merchantLocationId}/products")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    MerchantProduct saveMerchantLocationProduct(@PathVariable String merchantId,
                                                @PathVariable String merchantLocationId,
                                                @RequestBody MerchantProduct merchantProduct) throws URISyntaxException {
        return merchantProductsService.saveMerchantProduct(merchantId, merchantLocationId, merchantProduct);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/products/{merchantProductId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    MerchantProduct getMerchantLocationProduct(@PathVariable String merchantId,
                                               @PathVariable String merchantLocationId,
                                               @PathVariable String merchantProductId) throws URISyntaxException {
        return merchantProductsService.getMerchantProduct(merchantId, merchantLocationId, merchantProductId);

    }

    @PutMapping("/{merchantId}/locations/{merchantLocationId}/products/{merchantProductId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public MerchantProduct updateMerchantLocationProduct(@PathVariable String merchantId,
                                                         @PathVariable String merchantLocationId,
                                                         @RequestBody MerchantProduct merchantProduct) throws URISyntaxException {
        return merchantProductsService.updateMerchantProduct(merchantId, merchantLocationId, merchantProduct);
    }

    @PutMapping("/merchant_products/{merchantProductId}/ecom_attribute")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<MerchantProductEcomAttribute> updateMerchantProductEcomAttribute(@PathVariable String merchantProductId,
                                                                                 @RequestBody MerchantProductEcomAttribute[] merchantProductEcomAttribute) throws URISyntaxException {
        return merchantProductsService.updateMerchantProductEcomAttribute(merchantProductId, merchantProductEcomAttribute);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/skus/{skuId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public MerchantProduct searchSKUId(@PathVariable Long merchantId,
                                       @PathVariable Long merchantLocationId,
                                       @PathVariable String skuId) throws URISyntaxException {
        return merchantProductsService.checkSKUState(merchantId, merchantLocationId, skuId);

    }

    @PutMapping("/{merchantId}/locations/{merchantLocationId}/user/{gpId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void addUserToPharmacy(@PathVariable String merchantId, @PathVariable String merchantLocationId,
                                  @PathVariable String gpId, @RequestBody PharmacyUser pharmacyUser) throws URISyntaxException {
        pharmacyUserService.updatePharmacyUser(gpId, merchantId, merchantLocationId, pharmacyUser);
    }

    @GetMapping("/geozones")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map listGeozoneByLatLng(@RequestParam Double latitude, @RequestParam Double longitude) {
        Map result = new HashMap();
        result.put("data", geoService.listByLatLng(latitude, longitude));

        return result;
    }


    @PutMapping("/{id}/locations/{storeId}/business_hour")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<BusinessHoursItem> updateStoreBusinessHour(@PathVariable Long id, @PathVariable Long storeId, @RequestBody BusinessHoursItem businessHour) throws URISyntaxException {
        pharmacyMerchantsService.updateSingleBusinessHour(id, storeId, businessHour);

        MerchantLocation merchantStore;
        merchantStore = pharmacyMerchantsService.getMerchantLocationByInternalId(id, storeId);

        return merchantStore.getBusinessHours();
    }

    @PostMapping("/{id}/locations/{storeId}/business_hour")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public List<BusinessHoursItem> saveStoreBusinessHours(@PathVariable Long id, @PathVariable Long storeId, @RequestBody List<BusinessHoursItem> pharmacyBusinessHours) throws URISyntaxException {
        pharmacyMerchantsService.addBusinessHour(id, storeId, pharmacyBusinessHours);

        MerchantLocation merchantStore;
        merchantStore = pharmacyMerchantsService.getMerchantLocationByInternalId(id, storeId);

        return merchantStore.getBusinessHours();
    }

    @PostMapping("/{itemId}/price_conversion/{conversionLevel}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<PriceConversionAttribute> updatePriceConversion(@PathVariable String itemId, @PathVariable String conversionLevel, @RequestBody List<PriceConversionAttribute> priceConversionAttributes) throws URISyntaxException {
        priceConversionAttributes.forEach(priceConversionAttribute -> {
            priceConversionService.savePriceConversionAttribute(priceConversionAttribute);
        });
        return priceConversionService.getPriceConversionAttribute(conversionLevel, itemId);
    }

    @PutMapping("/{id}/locations/{storeId}/user")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<PharmacyUser> updateStoreUser(@PathVariable Long id, @PathVariable Long storeId, @RequestBody PharmacyUser pharmacyUser) {
        pharmacyUser.setStatus(pharmacyUser.BURU_STATUS_PENDING);
        pharmacyUserService.updatePharmacyUser(pharmacyUser.getGpid(), "0", "0", pharmacyUser);
        List<PharmacyUser> pharmacyUsers = fetchMerchantLocUsers(pharmacyUser.getMerchantId(), pharmacyUser.getMerchantLocationId());
        return pharmacyUsers;
    }

    @GetMapping("/users")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<PharmacyUser> listPharmacyUser(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                          @RequestParam(required = false, defaultValue = "") String status,
                                                          @RequestParam(required = false, name = "phone_number", defaultValue = "") String phoneNumber,
                                                          @RequestParam(required = false) Boolean mapped,
                                                          @RequestParam(required = false, name = "access_role", defaultValue = "") String accessRole,
                                                          @RequestParam(required = false, defaultValue = "false") Boolean deleted,
                                                          @RequestParam(required = false, defaultValue = "") String gpid) throws URISyntaxException {
        List<PharmacyUser> pharmacyUsers;
        if (StringUtils.isNotBlank(gpid)) {
            PharmacyUser pharmacyUser = pharmacyUserService.get(gpid);
            pharmacyUsers = Collections.singletonList(pharmacyUser);
            perPage = 0; // HACK to mark next page false
        } else {
            pharmacyUsers = pharmacyUserService.list(status, pageNo, perPage, gpid, deleted, mapped, accessRole, phoneNumber);
        }

        PaginatedResult<PharmacyUser> output = new PaginatedResult<>();
        output.setResult(pharmacyUsers);
        output.setNextPage(pharmacyUsers.size() == perPage);
        return output;
    }

    @GetMapping("/users/{gpId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_members', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public PharmacyUser getUser(@PathVariable String gpId) throws URISyntaxException {
        return pharmacyUserService.getPharmacyUserV1(gpId);
    }

    @PutMapping("/{gpId}/activate")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','medisend_members', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void addPharmacyUserForActivation(@PathVariable String gpId, @RequestBody Map activationRequestBody) throws URISyntaxException {
        pharmacyUserService.activatePharmacyUser(gpId, activationRequestBody);
    }

    private void updateUserDetail(PharmacyUser pharmacyUser) throws URISyntaxException {
        Customer user = baliLoganService.getCustomer(pharmacyUser.getGpid());
        Identity phoneIdentity = baliLoganService.getIdentity(user.getIdentities(), "phone_number");
        Identity emailIdentity = baliLoganService.getIdentity(user.getIdentities(), "email");
        if (phoneIdentity != null) {
            pharmacyUser.setPhoneNumber(phoneIdentity.getValue());
        }
        if (emailIdentity != null) {
            pharmacyUser.setEmail(emailIdentity.getValue());
        }
        // pharmacyUser.setName(user.getFirstName().concat(" ").concat(user.getLastName()));
    }

    private List<PharmacyUser> fetchMerchantLocUsers(String merchantId, String merchantLocId) {
        List<PharmacyUser> pharmacyUsers = pharmacyUserService.listUserMerchantLoc(merchantId, merchantLocId);
        pharmacyUsers = pharmacyUsers.parallelStream().peek(pharmacyUser -> {
            if (StringUtils.isNotBlank(pharmacyUser.getGpid())) {
                try {
                    updateUserDetail(pharmacyUser);

                } catch (Exception ex) {
                    log.error("Could not find pharmacy user info for '" + pharmacyUser.getGpid() + "': " + ex.getMessage());
                }
            }
        }).collect(Collectors.toList());
        return pharmacyUsers;
    }


    @GetMapping("/locations/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    PaginatedResult<MerchantLocation> getMerchantLocationsByName(@RequestParam(required = false, name = "name", defaultValue = "") String name,
                                                                 @RequestParam(required = false, name = "status", defaultValue = "") String status,
                                                                 @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                                 @RequestParam(required = false, name = "merchant_location_ids") List<String> merchantLocationIds,
                                                                 @RequestParam(required = false, name = "merchant_ids") String merchantIds,
                                                                 @RequestParam(required = false, name = "verification_status") String verificationStatus,
                                                                 @RequestParam(required = false, name = "zone_id") String zoneId,
                                                                 @RequestParam(required = false, name = "is_docs_expired") String isDocsExpired,
                                                                 @RequestParam(required = false, name = "is_b2b") String isB2B,
                                                                 @RequestParam(required = false, name = "is_b2c") String isB2C,
                                                                 @RequestParam(required = false, name = "merchant_categorization") String merchantCategorization
    ) throws URISyntaxException {


        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","sellout_record_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "product_edit", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_create_reorder", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "product_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","settlement_history_list", "view_only")
                && !authorizationService.isAuthorized("medex_delivery","medex_order_listing", "view_only")) {

            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();

        }
        return pharmacyMerchantsService.getMerchantLocationsByName(name, status, pageNo, perPage, merchantLocationIds, merchantIds, verificationStatus, zoneId, isDocsExpired, isB2B, isB2C, merchantCategorization);
    }

    @GetMapping("/locations/autocomplete")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    PaginatedResult<MerchantLocationAutoComplete> getMerchantLocationsAutocomplete(@RequestParam(required = false, name = "name", defaultValue = "") String name,
                                                                                   @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                                   @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage
    ) throws URISyntaxException {


        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","merchant_location_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","medisend_order_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","point_exchange_view", "view_only")) {

            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();

        }
        return pharmacyMerchantsService.getMerchantLocationsAutocomplete(name, pageNo, perPage);
    }

    @PutMapping("/users/{gpid}/activate")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void activateUser(@PathVariable String gpid) throws URISyntaxException {
        pharmacyUserService.activateUser(gpid);
    }

    @PutMapping("/users/{gpid}/recover")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void recoverUser(@PathVariable String gpid) throws URISyntaxException {
        pharmacyUserService.recoverUser(gpid);
    }

    @PutMapping("/users/{gpid}/delete")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deleteUser(@PathVariable String gpid, @RequestBody Map payload) throws URISyntaxException {
        pharmacyUserService.deleteUser(gpid, payload);
    }

    @GetMapping("/users/roles")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    List<Role> getPharmacyRoles() throws URISyntaxException {
        return pharmacyUserService.getRoles();
    }

    @PutMapping("/{id}/locations/{storeId}/user/assign-role")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<PharmacyUser> assignRoleToPharmacyUser(@PathVariable Long id, @PathVariable Long storeId, @RequestBody PharmacyUser pharmacyUser) {
        pharmacyUserService.updatePharmacyUser(pharmacyUser.getGpid(), pharmacyUser.getMerchantId(), pharmacyUser.getMerchantLocationId(), pharmacyUser);
        List<PharmacyUser> pharmacyUsers = fetchMerchantLocUsers(pharmacyUser.getMerchantId(), pharmacyUser.getMerchantLocationId());
        return pharmacyUsers;
    }


    @GetMapping("/{merchantId}/locations/{merchantLocationId}/location-mappings")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    List getMappedDistributorsToPharmacy(@PathVariable String merchantId,
                                         @PathVariable String merchantLocationId
    ) throws URISyntaxException {
        return pharmacyMerchantsService.getMappedDistributorsListToPharmacy(merchantId, merchantLocationId);
    }

    @PostMapping("/{merchantId}/locations/{merchantLocationId}/delivery-options")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void createDeliveryOptions(@RequestBody List<DeliveryOption> deliveryOptionsData,
            @PathVariable String merchantId,
            @PathVariable String merchantLocationId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_add", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        pharmacyMerchantsService.createDeliveryOptions(deliveryOptionsData, merchantId, merchantLocationId);
    }

    @PutMapping("/{merchantId}/locations/{merchantLocationId}/delivery-options")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateDeliveryOptions(@RequestBody List<DeliveryOption> deliveryOptionsData,
            @PathVariable String merchantId,
            @PathVariable String merchantLocationId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_add", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        pharmacyMerchantsService.updateDeliveryOptions(deliveryOptionsData, merchantId, merchantLocationId);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/delivery-options")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    List<DeliveryOption> fetchDeliveryOptions(@PathVariable String merchantId,
            @PathVariable String merchantLocationId) throws URISyntaxException {
        return pharmacyMerchantsService.getDeliveryOptions(merchantId, merchantLocationId);
    }

    @PostMapping("/products/{merchantProductId}/slash_price")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public SlashPriceResponse createSlashPrice(@PathVariable String merchantProductId,
                                 @RequestBody SlashPrice slashPrice) {
        return merchantProductsService.createSlashPrice(merchantProductId, slashPrice);
    }

    @GetMapping("/products/{merchantProductId}/slash_price")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_view', 'view_only')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public List<SlashPriceResponse> createSlashPrice(@PathVariable String merchantProductId,
                                               @RequestParam(required = false, name = "ecom_provider", defaultValue = "") String ecomProvider) throws URISyntaxException {
        return merchantProductsService.getSlashPrice(merchantProductId, ecomProvider);
    }

    @PutMapping("/products/{merchantProductId}/slash_price/deactivate")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','inventories_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deactiveSlashPrice(@PathVariable String merchantProductId,
                                   @RequestParam(required = false, name = "ecom_provider", defaultValue = "") String ecomProvider) throws URISyntaxException {
        merchantProductsService.deactiveSlashPrice(merchantProductId, ecomProvider);
    }


    @PostMapping("/{merchantId}/locations/{merchantLocationId}/verification-status-audit-logs")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<VerificationStatusAuditLog> fetchVerificationStatusAuditLog(@PathVariable String merchantId,
                                                                    @PathVariable String merchantLocationId,
                                                                    @RequestBody List<String> requiredLogsBody) throws URISyntaxException {
        return merchantProductsService.fetchVerificationStatusAuditLog(merchantId,merchantLocationId,requiredLogsBody);
    }

    @GetMapping("/recon-information/{id}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public PharmacyReconCatalog getPharmacyReconCatalog(@PathVariable String id) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "pharmacy_recon_information_view",
                "view_only") && !authorizationService.isAuthorized("pharmacy_delivery", "merchant_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return reconCatalogService.getPharmacyReconCatalog(id);
    }

    @PatchMapping("/recon-information/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','pharmacy_recon_information_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updatePharmacyPaymentWithhold(@PathVariable String id, @RequestBody PaymentWithholdRequest pharmacyPaymentWithholdReq) throws URISyntaxException {
        reconCatalogService.updatePharmacyPaymentWithhold(id, pharmacyPaymentWithholdReq);
    }

    @PutMapping ("/{merchantId}/locations/{merchantLocationId}/attributes")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateMerchantLocationAttributes(@PathVariable String merchantId, @PathVariable String merchantLocationId,
            @RequestBody List<Map> attributes) throws URISyntaxException {
        pharmacyMerchantsService.updateMerchantLocationAttributesExternal(merchantId, merchantLocationId, attributes);
    }
}
