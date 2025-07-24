package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.HospitalProvider;
import com.halodoc.batavia.entity.bintan.catalog.HospitalProviderLocation;
import com.halodoc.batavia.entity.bintan.catalog.ProviderLocation;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.maluku.HospitalPharmacyUser;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.aru.AruService;
import com.halodoc.batavia.service.bintan.BintanSearchService;
import com.halodoc.batavia.service.bintan.ProviderLocationService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequestMapping ("api/v1/hospitals/provider-location")
@RestController
public class ProviderLocationApiController extends HalodocBaseApiController {
    @Autowired
    private ProviderLocationService providerLocationService;

    @Autowired
    private BintanSearchService bintanSearchService;

    @Autowired
    AruService aruService;

    @Autowired
    private AuthorizationService authorizationService;

    public ProviderLocationApiController(ProviderLocationService providerLocationService) {
        this.providerLocationService = providerLocationService;
    }

    @GetMapping ("/{providerId}/locations/{locationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public HospitalProviderLocation getLocation(@PathVariable String providerId, @PathVariable String locationId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view", "view_only") &&
            !authorizationService.isAuthorized("contact_doctor", "doctor_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationService.getLocation(providerId, locationId);
    }

    @GetMapping ("/locations/{locationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public HospitalProviderLocation getProviderLocation(@PathVariable String locationId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "insurance_provider_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "whatsapp_configuration_add", "view_only") && !authorizationService.isAuthorized("insurance", "whatsapp_configuration_edit",
                "view_only") && !authorizationService.isAuthorized("insurance", "insurance_provider_view",
                "view_only") && !authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view",
                "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        
        HospitalProviderLocation hospitalProviderLocation = providerLocationService.getProviderLocation(locationId);
        List<HospitalPharmacyUser> hospitalUsers = aruService.fetchHospitalUsers(hospitalProviderLocation.getProviderId(), locationId,
                "HOSPITAL_APPOINTMENT");
        hospitalProviderLocation.setHospitalUsers(hospitalUsers);
        return hospitalProviderLocation;
    }

    @PostMapping ("/{providerId}/locations")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public HospitalProviderLocation createLocation(@PathVariable String providerId, @RequestBody HospitalProviderLocation location) {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationService.createLocation(providerId, location);
    }

    @PatchMapping ("/{providerId}/locations/{locationId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public HospitalProviderLocation updateLocation(@PathVariable String providerId, @PathVariable String locationId,
            @RequestBody HospitalProviderLocation location) {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationService.updateLocation(providerId, locationId, location);
    }

    @GetMapping ("/{providerLocationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public HospitalProvider getLocation(@PathVariable String providerLocationId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_location_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationService.getProvider(providerLocationId);
    }

    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<HospitalProviderLocation> searchProviderLocations(@RequestParam (required = false) String name,
            @RequestParam (required = false) String description, @RequestParam (required = false) String statuses,
            @RequestParam (required = false, name = "include_appointment_flag") String includeAppointmentFlag,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") int pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") int perPage) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_locations_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationService.searchProviderLocations(pageNo, perPage, name, description, statuses, includeAppointmentFlag);
    }

    @GetMapping ("/search/new")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Map> searchProviderLocationsV2(@RequestParam (required = false) String name,
            @RequestParam (required = false) String statuses, @RequestParam (required = false, name = "appointment_flag") String appointmentFlag,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") int pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") int perPage,
            @RequestParam (required = false, name = "exclude_new_appointment_flag") String excludeNewAppointmentFlag)

            throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_provider_locations_list",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_reimbursement_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return bintanSearchService.searchProviderLocations(pageNo, perPage, name, statuses, appointmentFlag, excludeNewAppointmentFlag);
    }

    @GetMapping ("/{providerId}/locations/{providerLocationId}/users")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<HospitalPharmacyUser> getHospitalAppointmentUsers(@PathVariable String providerId, @PathVariable String providerLocationId)
            throws URISyntaxException {
        return aruService.fetchHospitalUsers(providerId, providerLocationId, "HOSPITAL_APPOINTMENT");
    }

    @GetMapping ("/ins")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<HospitalProvider> getInsHospitalProvidersLocationList(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (name = "name", required = false) String name, @RequestParam (name = "statuses", required = false) String statuses,
            @RequestParam (name = "address", required = false) String address,
            @RequestParam (name = "halodoc_code", required = false) String halodocCode

    ) throws URISyntaxException {
        return providerLocationService.getInsHospitalProvidersLocationList(pageNo, perPage, statuses, name, address, halodocCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_add', 'restricted_write')")
    @PostMapping ("/ins/providers")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public HospitalProvider createInsHospitalProvider(@RequestBody Map provider) throws URISyntaxException {
        return providerLocationService.createInsHospitalProvider(provider);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_add', 'restricted_write')")
    @PostMapping ("/ins/providers/{providerId}/locations")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public HospitalProviderLocation createInsHospitalProviderLocation(@PathVariable String providerId,
            @RequestBody HospitalProviderLocation providerLocation) throws URISyntaxException {
        return providerLocationService.createInsHospitalProviderLocation(providerId, providerLocation);
    }

    @PutMapping ("/multi-get")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    public List<HospitalProviderLocation> multiGetProviderLocations(@RequestBody Set<String> providerLocationIds) {
        return providerLocationService.multiGetProviderLocations(providerLocationIds);
    }

    @GetMapping ("/ins/provider/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<HospitalProvider> getInsHospitalProvidersList(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (name = "names", required = false) String names) throws URISyntaxException {
        return providerLocationService.getInsHospitalProvider(pageNo, perPage, names);
    }
}
