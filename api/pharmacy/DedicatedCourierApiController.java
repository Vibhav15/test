package com.halodoc.batavia.controller.api.pharmacy;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.medex.OrderShipmentCard;
import com.halodoc.batavia.entity.medex.Vendor;
import com.halodoc.batavia.entity.medisend.CourierUser;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.courier.CourierService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

@Controller
@RequestMapping("api/v1/courier")
@RestController
public class DedicatedCourierApiController extends HalodocBaseApiController {

    private final CourierService courierService;
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    DedicatedCourierApiController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping("/partners/search")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<CourierUser> searchCourierServicePartners(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                     @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                                     @RequestParam(required = false, name = "sort_order", defaultValue = "desc") String sortOrder,
                                                                     @RequestParam(required = false, name = "sort_field", defaultValue = "created_at") String sortField,
                                                                     @RequestParam(required = false, name = "phone_number") Number PhoneNumber,
                                                                     @RequestParam(required = false, defaultValue = "") String status) throws URISyntaxException {
        return courierService.searchCourierServicePartners(pageNo, perPage, status,sortOrder,sortField,PhoneNumber);
    }

    @PutMapping("/partners/{gpid}/activate")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void activateCourierUser(@PathVariable String gpid, @RequestBody Map requestBody) throws  URISyntaxException {
        courierService.activateCourierUser(gpid, requestBody);
    }

    @PutMapping("/partners/{gpid}/deactivate")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deactivateCourierUser(@PathVariable String gpid) throws  URISyntaxException {
        courierService.deactivateCourierUser(gpid);
    }

    @PutMapping("/b2c-partners/{gpid}/activate")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void activateB2CCourierUser(@PathVariable String gpid, @RequestBody Map requestBody) throws  URISyntaxException {
        courierService.activateB2CCourierUser(gpid, requestBody);
    }

    @GetMapping("/shipments")
    @PreAuthorize("@authorizationService.isAuthorized('medex_delivery','medex_order_listing', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<OrderShipmentCard> lisOrderShipments(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(required = false, name = "per_page", defaultValue = "20") int perPage,
                                                                      @RequestParam(required = false, name = "sort_order", defaultValue = "asc") String sortOrder,
                                                                      @RequestParam(required = false, name = "start_order_date", defaultValue = "") Date startOrderDate,
                                                                      @RequestParam(required = false, name = "end_order_date", defaultValue = "") Date endOrderDate,
                                                                      @RequestParam(required = false, name = "shipmentId") String shipmentId,
                                                                      @RequestParam(required = false, name = "orderId") String orderId,
                                                                      @RequestParam(required = false, name = "partner_id") String partnerId,
                                                                      @RequestParam(required = false, name = "merchant_location") String merchantLocation,
                                                                      @RequestParam(required = false) String status) throws URISyntaxException {
        return courierService.getOrderShipments(pageNo, perPage, sortOrder, startOrderDate, endOrderDate, status, shipmentId, orderId, partnerId, merchantLocation);
    }

    @GetMapping("/partners")
    @PreAuthorize("@authorizationService.isAuthorized('medex_delivery','medex_order_listing', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List<Map> getDriversList(
            @RequestParam(required = false, name = "partner_name", defaultValue = "") String partnerName,
            @RequestParam(required = false, name = "phone_number", defaultValue = "") String phoneNumber
    ) throws URISyntaxException {
        return courierService.getDriversList(partnerName, phoneNumber);
    }

    @GetMapping("/download")
    @PreAuthorize("@authorizationService.isAuthorized('medex_delivery','medex_order_listing', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public ResponseEntity<byte[]> downloadShipments(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") int perPage,
            @RequestParam(required = false, name = "sort_order", defaultValue = "asc") String sortOrder,
            @RequestParam(required = false, name = "start_order_date", defaultValue = "") Date startOrderDate,
            @RequestParam(required = false, name = "end_order_date", defaultValue = "") Date endOrderDate,
            @RequestParam(required = false, name = "shipmentId") String shipmentId,
            @RequestParam(required = false, name = "orderId") String orderId,
            @RequestParam(required = false, name = "partner_id") String partnerId,
            @RequestParam(required = false, name = "merchant_location") String merchantLocation,
            @RequestParam(required = false) String status) throws URISyntaxException {
        return courierService.downloadShipmentDocument(pageNo, perPage, sortOrder, startOrderDate, endOrderDate, status, shipmentId, orderId, partnerId, merchantLocation);
    }

    @GetMapping("/vendor/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Vendor> searchVendor(
            @RequestParam(required = false, name = "vendor_name", defaultValue = "") String vendorName
            ) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "merchant_location_edit", "restricted_write") && !authorizationService.isAuthorized(
                "medex_delivery", "medex_vendor_listing", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return courierService.searchVendor(vendorName);
    }

    @PostMapping("/vendor/add")
    @PreAuthorize("@authorizationService.isAuthorized('medex_delivery','medex_vendor_listing', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addVendor(@RequestBody Vendor requestBody) throws URISyntaxException {
         courierService.addVendor(requestBody);
    }

    @PutMapping("/vendor/update")
    @PreAuthorize("@authorizationService.isAuthorized('medex_delivery','medex_vendor_listing', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateVendor(@RequestBody Vendor vendorDetails) throws URISyntaxException {
       courierService.updateVendor(vendorDetails);
    }
}
