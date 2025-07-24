package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.ProviderLocationDepartment;
import com.halodoc.batavia.entity.bintan.catalog.DoctorSchedule;
import com.halodoc.batavia.entity.bintan.catalog.HospitalProvider;
import com.halodoc.batavia.entity.bintan.catalog.HospitalProviderLocation;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.claims.ClaimRoomBoardRequest;
import com.halodoc.batavia.entity.exodus.claims.ClaimRoomBoardResponse;
import com.halodoc.batavia.entity.exodus.claims.RoomClassMasterData;
import com.halodoc.batavia.entity.exodus.claims.SearchRoomClassId;
import com.halodoc.batavia.entity.maluku.HospitalPharmacyUser;
import com.halodoc.batavia.service.aru.AruService;
import com.halodoc.batavia.service.bintan.HospitalProviderLocationService;
import com.halodoc.batavia.service.bintan.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("api/v1/hospitals/providers")
@RestController
public class ProvidersApiController extends HalodocBaseApiController {

    private final ProviderService providerService;
    private final HospitalProviderLocationService hospitalProviderLocationService;

    @Autowired
    AruService aruService;

    @Autowired
    public ProvidersApiController(ProviderService providerService,
                                  HospitalProviderLocationService hospitalProviderLocationService) {
        this.providerService = providerService;
        this.hospitalProviderLocationService = hospitalProviderLocationService;
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_providers_list', 'view_only')")
    @GetMapping
    public PaginatedResult<HospitalProvider> getProvidersList(@RequestParam Integer per_page, @RequestParam Integer page_no,
                                                              @RequestParam(name = "names", required = false) String names,
                                                              @RequestParam(name = "statuses", required = false) String statuses,
                                                              @RequestParam(name = "providerLocationName", required = false) String providerLocationName

    ) throws URISyntaxException {
        return providerService.getProvidersList(page_no, per_page, statuses, names, providerLocationName);
    }

    @GetMapping("/{providerId}/search")
    PaginatedResult<HospitalProviderLocation> getProviderLocation(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                                  @RequestParam(required = false) String capabilities,
                                                                  @PathVariable String  providerId,
                                                                  @RequestParam(required = false) String name
    ) throws URISyntaxException {
        return hospitalProviderLocationService.getProviderLocation(providerId,pageNo, perPage,capabilities,name);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_provider_view', 'view_only')")
    @GetMapping("/{providerId}")
    public HospitalProvider getProvider(@PathVariable String providerId) throws URISyntaxException {
        return providerService.getProvider(providerId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_add', 'restricted_write')")
    @PostMapping
    public HospitalProvider createProvider(@RequestBody Object provider) {
        return providerService.createProvider(provider);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_edit', 'restricted_write')")
    @PatchMapping ("/{providerId}")
    public Object updateProvider(@PathVariable String providerId, @RequestBody Object provider) {
        return providerService.updateProvider(providerId, provider);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_view', 'view_only')")
    @GetMapping ("/{providerId}/locations/{locationId}")
    public HospitalProviderLocation getLocation(@PathVariable String providerId, @PathVariable String locationId) throws URISyntaxException {
        HospitalProviderLocation hospitalProviderLocation = hospitalProviderLocationService.getLocation(providerId, locationId);
        List<HospitalPharmacyUser> hospitalUsers = aruService.fetchHospitalUsers(providerId, locationId, "HOSPITAL_APPOINTMENT");
        hospitalProviderLocation.setHospitalUsers(hospitalUsers);
        return hospitalProviderLocation;
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_add', 'restricted_write')")
    @PostMapping ("/{providerId}/locations")
    public HospitalProviderLocation createLocation(@PathVariable String providerId, @RequestBody HospitalProviderLocation location) {
        return hospitalProviderLocationService.createLocation(providerId, location);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PatchMapping ("/{providerId}/locations/{locationId}")
    public HospitalProviderLocation updateLocation(@PathVariable String providerId, @PathVariable String locationId,
            @RequestBody HospitalProviderLocation location) {
        return hospitalProviderLocationService.updateLocation(providerId, locationId, location);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_view', 'view_only')")
    @GetMapping ("/{providerId}/locations/{locationId}/department-inventories")
    public List<ProviderLocationDepartment> getProviderLocationDepartments(@PathVariable String providerId, @PathVariable String locationId)
            throws URISyntaxException {
        return hospitalProviderLocationService.getProviderLocationDepartments(providerId, locationId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/locations/{locationId}/departments/multi-link")
    public Map linkDepartmentsToLocation(@PathVariable String providerId, @PathVariable String locationId, @RequestBody List<String> departmentIds)
            throws URISyntaxException {
        return hospitalProviderLocationService.linkDepartmentsTolocation(providerId, locationId, departmentIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/locations/{locationId}/departments/multi-unlink")
    public Map unlinkDepartmentsFromLocation(@PathVariable String providerId, @PathVariable String locationId,
            @RequestBody List<String> departmentIds) throws URISyntaxException {
        return hospitalProviderLocationService.unlinkDepartmentsFromlocation(providerId, locationId, departmentIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/locations/{locationId}/departments/{departmentId}/multi-link")
    public Map linkProceduresToDepartment(@PathVariable String providerId, @PathVariable String locationId, @PathVariable String departmentId,
            @RequestBody List<String> procedureIds) throws URISyntaxException {
        return hospitalProviderLocationService.linkProceduresToDepartment(providerId, locationId, departmentId, procedureIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/locations/{locationId}/departments/{departmentId}/multi-unlink")
    public Map unlinkProceduresFromDepartment(@PathVariable String providerId, @PathVariable String locationId, @PathVariable String departmentId,
            @RequestBody List<String> procedureIds) throws URISyntaxException {
        return hospitalProviderLocationService.unlinkProceduresFromDepartment(providerId, locationId, departmentId, procedureIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/locations/{locationId}/departments/{departmentId}/schedules")
    public List<DoctorSchedule> addSchedulesToDepartment(@PathVariable String providerId, @PathVariable String locationId,
            @PathVariable String departmentId, @RequestBody List<DoctorSchedule> schedules) throws URISyntaxException {
        return hospitalProviderLocationService.addDepartmentSchedules(providerId, locationId, departmentId, schedules);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/locations/{locationId}/departments/{departmentId}/schedules/{scheduleId}")
    public void updateSchedule(@PathVariable String providerId, @PathVariable String locationId, @PathVariable String departmentId,
            @PathVariable String scheduleId, @RequestBody DoctorSchedule schedule) throws URISyntaxException {
        hospitalProviderLocationService.updateSchedule(providerId, locationId, departmentId, scheduleId, schedule);
    }

}
