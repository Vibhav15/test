package com.halodoc.batavia.controller.api.halolab;

import com.halodoc.batavia.entity.bintan.doctor_calendar.CalendarEventRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalendarEventSearchRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalenderEventUpdateRequest;
import com.halodoc.batavia.entity.bintan.doctor_calendar.CalenderTimeZoneUpdateRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.halolab.*;
import com.halodoc.batavia.entity.halolab.Package;
import com.halodoc.batavia.entity.kakaban.PaginatedLabTestSearchResult;
import com.halodoc.batavia.entity.kakaban.LabTest;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.halolab.HalolabHubService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("api/v1/halolabs/halolab-hubs")
@RestController
public class HalolabHubApiController extends HalodocBaseApiController {

    private HalolabHubService halolabHubService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public HalolabHubApiController(HalolabHubService halolabHubService) {
        this.halolabHubService = halolabHubService;
    }


    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    PaginatedHalolabHubSearchResult searchLabProvider(
            @RequestParam(value = "search_text", required = false) String searchText,
            @RequestParam(value = "names", defaultValue = "") String names,
            @RequestParam(value = "statuses", defaultValue = "") String statuses,
            @RequestParam(value = "per_page", defaultValue = "20") final Integer perPage,
            @RequestParam(name = "page_number", defaultValue = "1") final Integer pageNumber,
            @RequestParam(value = "is_corporate", required = false) Boolean isCorporate,
            @RequestParam(value = "description", defaultValue = "") final String description) {

        if (!authorizationService.isAuthorized("lab_service","provider_list", "view_only")
                && !authorizationService.isAuthorized("lab_service","order_list", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return halolabHubService.getHalolabHubs(searchText, names, statuses, pageNumber, perPage, isCorporate, description);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','provider_view', 'view_only')")
    @GetMapping("/{externalHubId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    HalolabHub getHalolabHubById(@PathVariable String externalHubId) {
        return halolabHubService.getHalolabHubById(externalHubId);
    }


    @PreAuthorize("@authorizationService.isAuthorized('lab_service','provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    HalolabHub addNewHalolabHub(@RequestBody HalolabHub halolabHub) {
        return halolabHubService.addNewHalolabHub(halolabHub);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','provider_edit', 'restricted_write')")
    @PutMapping("/{externalHalolabId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updateHalolabHub(@PathVariable String externalHalolabId, @RequestBody HalolabHub halolabHub) {
        halolabHubService.updateHalolabHub(externalHalolabId, halolabHub);
    }

    @GetMapping("/demand-zones/schedules/{demandZoneName}/availability")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    List getAvailableDates(@PathVariable String demandZoneName) {
        return halolabHubService.getAvailableDates(demandZoneName);
    }

    @GetMapping("/demand-zones/schedules/{demandZoneName}/slots")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    List getAvailableSlots(@PathVariable String demandZoneName, @RequestParam(name = "start_date", defaultValue = "", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate) throws URISyntaxException {
        return halolabHubService.getAvailableSlots(demandZoneName, startDate);
    }

    @GetMapping("/packages")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    PaginatedHalolabHubSearchResult searchLabPackages(@RequestParam(value = "search_text", required = false) String searchText,
                                                      @RequestParam(defaultValue = "", required = false) String display,
                                                      @RequestParam(defaultValue = "active,inactive", required = false) String statuses,
                                                      @RequestParam(value = "per_page", defaultValue = "10", required = false) final String perPage,
                                                      @RequestParam(value = "page_no", defaultValue = "1", required = false) final String pageNumber,
                                                      @RequestParam(value = "sort_field", defaultValue = "", required = false) String sortField,
                                                      @RequestParam(value = "sort_order", defaultValue = "desc", required = false) String sortOrder,
                                                      @RequestParam(value = "sla_unit", required = false) String sla_unit,
                                                      @RequestParam(value = "sla_value", required = false) String sla_value,
                                                      @RequestParam(value = "display_order", required = false) String display_order,
                                                      @RequestParam(defaultValue = "", required = false) String inventory_types
    ) {

        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only")
                && !authorizationService.isAuthorized("lab_service","package_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return halolabHubService.searchAllPackages(searchText, display, statuses,
                Integer.valueOf(pageNumber), Integer.valueOf(perPage), sortField, sortOrder, sla_unit, sla_value, display_order, inventory_types);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_list', 'view_only')")
    @GetMapping("/tests")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    PaginatedLabTestSearchResult searchHaloLabTests(@RequestParam(value = "search_text", required = false) String searchText,
                                                    @RequestParam(value = "universal_standard_code", required = false) String universalStandardCode,
                                                    @RequestParam(defaultValue = "", required = false) String display,
                                                    @RequestParam(defaultValue = "active,inactive", required = false) String statuses,
                                                    @RequestParam(value = "per_page", defaultValue = "20", required = false) final String perPage,
                                                    @RequestParam(value = "page_no", defaultValue = "1", required = false) final String pageNumber
    ) {
        return halolabHubService.searchHaloLabTests(searchText, universalStandardCode, display, statuses,
                Integer.valueOf(pageNumber), Integer.valueOf(perPage));
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_view', 'view_only')")
    @GetMapping("/tests/{externalLabTestId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    LabTest getHaloLabTest(@PathVariable String externalLabTestId) {
        return halolabHubService.getHaloLabTestByExternalId(externalLabTestId);
    }

    @GetMapping("/packages/{externalLabPackageId}/tests/cc")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    List<LabTest> getPackageTests(@PathVariable String externalLabPackageId) {
        return halolabHubService.getPackageTests(externalLabPackageId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_edit', 'restricted_write')")
    @PutMapping("/tests/{externalLabTestId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updateHaloLabTest(
            @RequestBody LabTest labTest, @PathVariable String externalLabTestId) {
        halolabHubService.updateLabTest(labTest, externalLabTestId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_add', 'restricted_write')")
    @PostMapping("/tests")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    LabTest addHaloLabTest(@RequestBody LabTest labTest) {
        return halolabHubService.createLabTest(labTest);
    }

    //    @PreAuthorize("@authorizationService.isAuthorized('halolab_service','halolab_hub_package_list', 'view_only')")
    @GetMapping("/cc/{halolabHubId}/packages/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    PaginatedHalolabHubPackageSearchResult getHalolabHubsPackage(@PathVariable String halolabHubId,
                                                                 @RequestParam(defaultValue = "", required = false, value = "featured") String featured,
                                                                 @RequestParam(value = "linked_statuses", defaultValue = "active,inactive") String statuses,
                                                                 @RequestParam(value = "populate_tests", defaultValue = "false") String populateTests,
                                                                 @RequestParam(value = "per_page", defaultValue = "20") final Integer perPage,
                                                                 @RequestParam(value = "name", defaultValue = "") String names,
                                                                 @RequestParam(value = "page_number", defaultValue = "1", required = false) final String pageNumber,
                                                                 @RequestParam(defaultValue = "", required = false) String inventory_types
                                                                 ) {
        return halolabHubService.getHalolabHubsPackage(halolabHubId, featured, statuses, populateTests, perPage, names, Integer.valueOf(pageNumber), inventory_types);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_edit', 'restricted_write')")
    @PostMapping("/tests/{externalLabTestId}/add_sections")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    Map addSectionInTest(@PathVariable String externalLabTestId, @RequestBody Map sectionRequest) {
        return halolabHubService.addSectionInTest(externalLabTestId, sectionRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_view', 'view_only')")
    @GetMapping("/tests/{externalLabTestId}/sections")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    List<Map> getAllTestSections(@PathVariable String externalLabTestId) {
        return halolabHubService.getAllTestSections(externalLabTestId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_edit', 'restricted_write')")
    @PatchMapping("/tests/{externalLabTestId}/sections/{sectionId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updateTestSectionById(@PathVariable String externalLabTestId, @PathVariable String sectionId, @RequestBody Map sectionRequest) {
        halolabHubService.updateTestSectionById(externalLabTestId, sectionId, sectionRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','test_edit', 'restricted_write')")
    @PatchMapping("/tests/{externalLabTestId}/sections/{sectionId}/lines")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updateTestSectionLine(@PathVariable String externalLabTestId, @PathVariable String sectionId, @RequestBody List<Map> sectionLineRequest) {
        halolabHubService.updateTestSectionLine(externalLabTestId, sectionId, sectionLineRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_add', 'restricted_write')")
    @PostMapping("/cc/{halolabHubId}/packages")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    HalolabHubPackage addHalolabHubPackage(@PathVariable String halolabHubId,
                                           @RequestBody HalolabHubPackage halolabPackage) {
        return halolabHubService.createHalolabPackage(halolabHubId, halolabPackage);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_edit', 'restricted_write')")
    @PutMapping("/cc/{halolabHubId}/packages/{packageId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updateHalolabHubPackage(@RequestBody HalolabHubPackage halolabPackage, @PathVariable String halolabHubId, @PathVariable String packageId) {
        halolabHubService.updateHalolabPackage(halolabHubId, halolabPackage, packageId);
    }


    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_add', 'restricted_write')")
    @PostMapping("/packages")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    Package addPackage(@RequestBody Package labPackage) {
        return halolabHubService.createPackage(labPackage);
    }

    //    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_edit', 'restricted_write')")
    @PutMapping("/packages/{externalPackageId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updatePackage(@PathVariable String externalPackageId,
                       @RequestBody Package labPackage) {
        halolabHubService.updatePackage(externalPackageId, labPackage);
    }


    @GetMapping("/packages/{externalPackageId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    Package getPackage(@PathVariable String externalPackageId) {
        return halolabHubService.getPackageByExternalId(externalPackageId);
    }


    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_view', 'view_only')")
    @GetMapping("/packages/{externalPackageId}/tests")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    PackageTestView[] getPackageTests(@PathVariable String externalPackageId, @RequestParam(value = "name", defaultValue = "") String name) {
        return halolabHubService.getPackageTests(externalPackageId, name);
    }


    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_edit', 'restricted_write')")
    @PostMapping("/packages/{externalPackageId}/tests")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    void addPackageTests(@PathVariable String externalPackageId, @RequestBody ArrayList<PackageTest> packageTest) {
        halolabHubService.addPackageTests(externalPackageId, packageTest);
    }


    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_edit', 'restricted_write')")
    @PutMapping("/packages/{externalPackageId}/tests/{externalTestId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void updatePackageTest(@PathVariable String externalPackageId, @PathVariable String externalTestId,
                           @RequestBody PackageTest packageTest) {
        halolabHubService.updatePackageTest(externalPackageId, externalTestId, packageTest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','package_edit', 'restricted_write')")
    @PutMapping("/packages/cc/multi_get/packages")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CODI)
    public Map<String, String> multiGetPackageNamesForConditionList(@RequestBody List<String> packageIdList) {
        if (packageIdList == null || packageIdList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> packages = halolabHubService.getPackageList(packageIdList);
        return packages;
    }

    @PutMapping("/packages/cc/multi_get")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CODI)
    public List<Map> multiGetPackages(@RequestBody List<String> packageIdList) {
        return halolabHubService.multiGetPackages(packageIdList);
    }

    @PutMapping("/cc/search/inventory")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public List<Map> searchPackagesAndTests(@RequestParam(name="latitude", defaultValue = "", required = true) String latitude,
                                      @RequestParam(name="longitude", defaultValue = "", required = true) String longitude, @RequestBody Map requestBody) throws URISyntaxException {
        return halolabHubService.searchPackagesAndTests(latitude, longitude, requestBody);
    }

    // @PreAuthorize("@authorizationService.isAuthorized('lab_service','calendar_view', 'view_only')")
    @PutMapping("/calendars/events")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public List<Map<String, Object>> searchCalenderEvents(@RequestBody CalendarEventSearchRequest searchRequest) throws IOException {
        return halolabHubService.searchHaloLabEvents(searchRequest);
    }

    @PostMapping("/calendars/events")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    public void createEvent(@RequestBody CalendarEventRequest eventRequest) throws IOException {
        this.halolabHubService.createEvent(eventRequest);
    }

    @PatchMapping("/calendars/events")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public void editEvent(@RequestBody CalenderEventUpdateRequest eventRequest) throws IOException {
        this.halolabHubService.updateEvent(eventRequest);
    }

    @PatchMapping("/calendars/{calendarId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public void editEvent(@RequestBody CalenderTimeZoneUpdateRequest eventRequest, @PathVariable String calendarId) throws IOException {
        this.halolabHubService.updateTimeZone(eventRequest, calendarId);
    }

    @PatchMapping("/calendars/medical-staffs/time-zone")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public void updateMedicalStaffsTimeZone(@RequestBody List<CalenderTimeZoneUpdateRequest> eventRequest) throws IOException {
        this.halolabHubService.updateMedicalStaffsTimeZone(eventRequest);
    }
}
