package com.halodoc.batavia.controller.api.doctor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.Doctor;
import com.halodoc.batavia.entity.bintan.catalog.DoctorAttributes;
import com.halodoc.batavia.entity.bintan.catalog.DoctorPackage;
import com.halodoc.batavia.entity.bintan.catalog.DoctorPreferences;
import com.halodoc.batavia.entity.bintan.catalog.DoctorProfileSections;
import com.halodoc.batavia.entity.bintan.catalog.DoctorSchedule;
import com.halodoc.batavia.entity.bintan.catalog.DoctorTemplate;
import com.halodoc.batavia.entity.bintan.catalog.DoctorTemplateCategory;
import com.halodoc.batavia.entity.bintan.catalog.Identity;
import com.halodoc.batavia.entity.bintan.catalog.Lead;
import com.halodoc.batavia.entity.bintan.catalog.LinkDoctorPersonnelParam;
import com.halodoc.batavia.entity.bintan.catalog.Personnel;
import com.halodoc.batavia.entity.bintan.catalog.Preferences;
import com.halodoc.batavia.entity.bintan.catalog.SearchDoctor;
import com.halodoc.batavia.entity.bintan.catalog.UpdateIdentityRequest;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.entity.finance.DoctorReconCatalog;
import com.halodoc.batavia.entity.finance.PaymentWithholdRequest;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.IdentityService;
import com.halodoc.batavia.service.bintan.BintanSearchService;
import com.halodoc.batavia.service.bintan.DoctorService;
import com.halodoc.batavia.service.bintan.ProviderLocationPersonnelService;
import com.halodoc.batavia.service.bintan.discovery.BintanDiscoveryService;
import com.halodoc.batavia.service.device.DeviceService;
import com.halodoc.batavia.service.finance.ReconCatalogService;
import com.halodoc.config.ConfigClient;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Slf4j
@RequestMapping("api/v1/doctors")
@RestController
public class DoctorApiController extends HalodocBaseApiController {
    private final DoctorService doctorService;
    private final ReconCatalogService reconCatalogService;
    private final IdentityService bintanIdentityService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private BintanSearchService bintanSearchService;

    @Autowired
    private BintanDiscoveryService bintanDiscoveryService;

    private ConfigClient<BataviaAppConfiguration> configClient;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ProviderLocationPersonnelService providerLocationPersonnelService;

    @Autowired
    public DoctorApiController(ConfigClient<BataviaAppConfiguration> configClient, DoctorService doctorService,
            ReconCatalogService reconCatalogService, IdentityService bintanIdentityService) {
        this.doctorService = doctorService;
        this.reconCatalogService = reconCatalogService;
        this.configClient = configClient;
        this.bintanIdentityService = bintanIdentityService;
    }

    private static class CreateDoctorPersonnelParam {
        public Doctor doctor;

        public Personnel personnel;
    }

    private String getHalodocProviderId() {
        return this.configClient.getAppConfig().getProviderLocationPersonnelConfiguration().getHalodocProviderId();
    }

    private String getHalodocProviderLocationId() {
        return this.configClient.getAppConfig().getProviderLocationPersonnelConfiguration().getHalodocProviderLocationId();
    }

    private String getHalodocDepartmentId() {
        return this.configClient.getAppConfig().getProviderLocationPersonnelConfiguration().getHalodocDepartmentId();
    }

    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<SearchDoctor> searchDoctor(
            @RequestParam(required = false) String searchText,
            @RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false, name = "doctor_package_ids") String doctorPackageIds,
            @RequestParam (required = false, defaultValue = "active,inactive") String status,
            @RequestParam (required = false, defaultValue = "") String display,
            @RequestParam (required = false, name = "attribute_key", defaultValue = "") String attributeKey,
            @RequestParam (required = false, name = "attribute_value", defaultValue = "") String attributeValue,
            @RequestParam (required = false) int page_no, @RequestParam (required = false) int per_page,
            @RequestParam (required = false, name = "is_private_practice", defaultValue = "") String isPrivatePractice

    ) {
        if (!authorizationService.isAuthorized("contact_doctor", "doctor_list", "view_only")
                && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")
                && !authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        Map<String, Object> request = new HashMap<>();

        request.put("page_no", page_no);
        request.put("per_page", per_page);
        request.put("capabilities", "physical,chat,audio,video");

        if (StringUtils.isNotBlank(searchText)) {
            request.put("search_text", searchText);
        }

        if (StringUtils.isBlank(searchText) && StringUtils.isNotBlank(name)) {
            request.put("search_text", name);
        }

        if (StringUtils.isNotBlank(status)) {
            request.put("statuses", status);
        }

        if (StringUtils.isNotBlank(display)) {
            request.put("display", display);
        }

        if (StringUtils.isNotBlank(doctorPackageIds)) {
            request.put("doctor_package_ids", doctorPackageIds);
        }

        if (StringUtils.isNotBlank(attributeKey)) {
            request.put("attribute_key", attributeKey);
            request.put("attribute_value", attributeValue);
        }

        if (StringUtils.isNotBlank(isPrivatePractice)) {
            request.put("is_private_practice", isPrivatePractice);
        }
        PaginatedResult<SearchDoctor> paginatedResult = bintanSearchService.doctorSearch(request);
        return paginatedResult;
    }

    @PutMapping ("/multi-get")
    public List<Doctor> getDoctorsByExternalIds(@RequestBody List<String> externalIds) {
        if (!authorizationService.isAuthorized("subscriptions", "subscriptions_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return doctorService.getDoctorsByExternalIds(externalIds);
    }

    @GetMapping ("/search-in-bd")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<Map> searchDoctorInBintanDiscovery(
            @RequestParam(required = false) String searchText,
            @RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false, name = "doctor_package_ids") String doctorPackageIds,
            @RequestParam (required = false, defaultValue = "active,inactive") String status,
            @RequestParam (required = false, defaultValue = "") String display,
            @RequestParam (required = false, name = "attribute_key", defaultValue = "") String attributeKey,
            @RequestParam (required = false, name = "attribute_value", defaultValue = "") String attributeValue,
            @RequestParam (required = false) int page_no, @RequestParam (required = false) int per_page,
            @RequestParam (required = false, name = "is_midwife", defaultValue = "") String is_midwife,
            @RequestParam (required = false, name = "is_private_practice", defaultValue = "") String isPrivatePractice

    ) {
        if (!authorizationService.isAuthorized("contact_doctor", "doctor_list", "view_only") && !authorizationService.isAuthorized("marketing",
                "condition_add", "restricted_write") && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        Map<String, Object> request = new HashMap<>();

        request.put("page_no", page_no);
        request.put("per_page", per_page);
        request.put("capabilities", "physical,chat,audio,video");

        if (StringUtils.isNotBlank(searchText)) {
            request.put("search_text", searchText);
        }

        if (StringUtils.isBlank(searchText) && StringUtils.isNotBlank(name)) {
            request.put("search_text", name);
        }

        if (StringUtils.isNotBlank(status)) {
            request.put("statuses", status);
        }

        if (StringUtils.isNotBlank(display)) {
            request.put("display", display);
        }

        if (StringUtils.isNotBlank(doctorPackageIds)) {
            request.put("doctor_package_ids", doctorPackageIds);
        }

        if (StringUtils.isNotBlank(attributeKey)) {
            request.put("attribute_key", attributeKey);
            request.put("attribute_value", attributeValue);
        }

        if (StringUtils.isNotBlank(is_midwife)) {
            request.put("is_midwife", is_midwife);
        }
        if (StringUtils.isNotBlank(isPrivatePractice)) {
            request.put("is_private_practice", isPrivatePractice);
        }
        PaginatedResult<Map> paginatedResult = bintanDiscoveryService.doctorSearch(request);
        return paginatedResult;
    }


    @GetMapping("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<Doctor> listDoctor(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "doctor_package_ids") String doctorPackageIds,
            @RequestParam(required = false, defaultValue = "active,inactive") String status,
            @RequestParam(required = false, defaultValue = "") String display,
            @RequestParam(required = false, name="attribute_key",  defaultValue = "") String attributeKey,
            @RequestParam(required = false, name="attribute_value", defaultValue = "") String attributeValue,
            @RequestParam(required = false) int page_no,
            @RequestParam(required = false) int per_page,
            @RequestParam(required = false, name = "is_midwife" ) String is_midwife
    ) {
        if (!authorizationService.isAuthorized("contact_doctor", "doctor_list", "view_only")
                && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "condition_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }


        Map<String, Object> request = new HashMap<>();

        request.put("page_no", page_no);
        request.put("per_page", per_page);

        if (StringUtils.isNotBlank(searchText)) {
            request.put("search_text", searchText);
        }

        if (StringUtils.isBlank(searchText) && StringUtils.isNotBlank(name)) {
            request.put("search_text", name);
        }

        if (StringUtils.isNotBlank(status)) {
            request.put("statuses", status);
        }

        if (StringUtils.isNotBlank(display)) {
            request.put("display", display);
        }

        if (StringUtils.isNotBlank(doctorPackageIds)) {
            request.put("doctor_package_ids", doctorPackageIds);
        }

        if (StringUtils.isNotBlank(attributeKey)) {
            request.put("attribute_key", attributeKey);
            request.put("attribute_value", attributeValue);
        }

        if (StringUtils.isNotBlank(is_midwife)) {
            request.put("is_midwife", is_midwife);
        }

        PaginatedResult<Doctor> paginatedResult = doctorService.pagenatedList(request);
        return paginatedResult;
    }

    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_add', 'restricted_write')")
    public Doctor createDoctor(@RequestBody CreateDoctorPersonnelParam createDoctorPersonnelParam) throws IOException, URISyntaxException {

        // create doctor
        return doctorService.save(createDoctorPersonnelParam.doctor, createDoctorPersonnelParam.personnel);
    }

    @GetMapping("/{id}")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_view', 'view_only')")
    public Doctor detailDoctor(@PathVariable Long id) throws URISyntaxException {
        return doctorService.get(id);
    }

    @GetMapping("/external/{id}")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CODI)
    public Doctor detailDoctorByExternal(@PathVariable String id) throws URISyntaxException {

        if (!authorizationService.isAuthorized("contact_doctor", "consultations", "view_only") && !authorizationService.isAuthorized("contact_doctor",
                "doctor_view", "view_only") && !authorizationService.isAuthorized("subscriptions", "subscriptions_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return doctorService.getByExternal(id);
    }

    @PutMapping("/{id}/schedule/update")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public Map<String, Object> updateDoctorSchedule(@PathVariable Long id, @RequestBody DoctorSchedule schedule) throws IOException {
        Map map = new HashMap();
        Map<String, Object> responseObj = new HashMap<>();
        doctorService.updateSchedule(id, schedule.getId(), schedule);

        map.put("message", "successfully updated doctor schedule.");
        map.put("success", true);

        responseObj.put("data", map);
        responseObj.put("status", "success");

        return responseObj;
    }

    @PostMapping("/{id}/schedule/add")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_add', 'restricted_write')")
    public List<DoctorSchedule> addDoctorSchedule(@PathVariable Long id, @RequestBody List<DoctorSchedule> schedules) throws IOException {
        return doctorService.createSchedule(id, schedules);
    }

    @PutMapping("/{id}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_edit', 'restricted_write')")
    public Map updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) throws IOException {
        Map map = new HashMap();

        //update doctor
        try {
            doctorService.update(id, doctor);
        } catch (Exception ex) {
            log.error("error update doctor id {}: {}", id, ex.getMessage());
            map.put("message", ex.getMessage());
            throw ex;
        }

        //update profile section
        try {
            DoctorProfileSections doctorProfileSections = doctor.getDoctorProfileSections();

            if (doctorProfileSections != null) {
                doctorService.updateProfileSections(id, doctorProfileSections);
            }


        } catch (Exception ex) {
            log.error("error update doctor profile section {}: {}", id, ex.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> objectMap = mapper.readValue(ex.getMessage(), new TypeReference<Map<String, Object>>() {
            });
            map.put("message", objectMap.get("message"));
            throw ex;
        }

        this.updateDoctorPackage(id, doctor);

        return response(map, Boolean.TRUE);
    }

    /************* Doctor Leads API's ****************/

    /************* Doctor Leads API's ****************/

    @GetMapping("/leads")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<Lead> listDoctorLeads(@RequestParam(required = false) String statuses,
                                                 @RequestParam(name = "identity_value", required = false) String identityValue,
                                                 @RequestParam(required = false) int page_no, @RequestParam(required = false) int per_page) {

        if (!authorizationService.isAuthorized("contact_doctor","new_doctors", "view_only")
                && !authorizationService.isAuthorized("contact_doctor","doctor_edit", "restricted_write")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        Map<String, Object> request = new HashMap<>();

        request.put("page_no", page_no);
        request.put("per_page", per_page);

        if (StringUtils.isNotBlank(statuses)) {
            request.put("statuses", statuses);
        }

        if (StringUtils.isNotBlank(identityValue)) {
            request.put("identity_value", identityValue);
        }

        return doctorService.pagenatedLeadsList(request);
    }

    @PutMapping("/leads/{id}/reject")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','appointment_leads_action', 'restricted_write')")
    public void rejectLead(@PathVariable String id, @RequestBody Map<String, Object> rejectRequest) throws IOException {

        Map<String, String> request = new HashMap<>();

        request.put("type", "cs_rejected");
        request.put("reason", rejectRequest.get("reject_reason").toString());
        request.put("comments", rejectRequest.get("reject_comments").toString());

        doctorService.rejectLead(id, request);

    }

    private void newUpdateDoctor(Long id, Doctor doctor) {
        try {
            doctorService.updateProfile(id, doctor);
        } catch (Exception ex) {
            log.error("error update doctor id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

    private void createOrUpdateDoctorAttribute(Long id, List<DoctorAttributes> doctorAttributes) {
        try {
            doctorService.createOrUpdateDoctorAttribute(id, doctorAttributes);
        } catch (Exception ex) {
            log.error("error update attributes : {}", ex.getMessage());
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    @PutMapping("/{id}/profile")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_edit', 'restricted_write')")
    public Doctor updateDoctorProfile(@PathVariable Long id, @RequestBody Doctor doctor) throws IOException, URISyntaxException {

        this.newUpdateDoctor(id, doctor);

        try {
            if (doctor.getDoctorProfileSections() != null) {
                doctorService.updateProfileSpecialitiesAndSip(id, doctor.getDoctorProfileSections());
            }
        } catch (Throwable t) {
            log.error("Failed to create doctor profile section", t);
            throw t;
        }
        return detailDoctor(id);
    }

    @PutMapping("/link_doctor_personnel")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_edit', 'restricted_write')")
    public void linkDoctorPersonnel(@RequestBody LinkDoctorPersonnelParam updateDoctorPersonnelParam) throws IOException, URISyntaxException {
        doctorService.linkDoctorToPersonnel(updateDoctorPersonnelParam.personnelToUnlink, updateDoctorPersonnelParam.personnelToLink,
                updateDoctorPersonnelParam.doctorExternalId);
    }

    private void updateDoctorPackage(Long id, Doctor doctor) {
        try {
            List<DoctorPackage> existingPackages = doctor.getPackages()
                    .stream().filter(p -> p.getId() != null).collect(Collectors.toList());
            List<DoctorPackage> newPackages = doctor.getPackages()
                    .stream().filter(p -> p.getId() == null).collect(Collectors.toList());

            List<DoctorPackage> newSavedPackages = Collections.emptyList();

            if (newPackages != null && !newPackages.isEmpty()) {
                //create base package and connect to doctor
                newSavedPackages = doctorService.createNewBasePackageAndConnectToDoctor(doctor,
                        newPackages, existingPackages.size());
            }
            existingPackages.forEach(doctorPackage -> {
                doctorService.updatePackage(id, doctorPackage.getId(), doctorPackage);
            });
            existingPackages.addAll(newSavedPackages);
            doctor.setPackages(existingPackages);
        } catch (Exception ex) {
            log.error("error update doctor package {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

    @PutMapping("/{id}/setting")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public Doctor updateDoctorSetting(@PathVariable Long id, @RequestBody Doctor doctor) throws IOException, URISyntaxException {
        this.createOrUpdateDoctorAttribute(id, doctor.getAttributes());
        this.newUpdateDoctor(id, doctor);
        this.updateDoctorPackage(id, doctor);

        if (doctor.getStatus() == "inactive") {
            //  Unlinking personnel from provider: halodoc
            List<String> personnelIdList = new ArrayList<String>();
            personnelIdList.add(doctor.getLinkedPersonnel().getExternalId());

            providerLocationPersonnelService.unlinkMultiplePersonnels(getHalodocProviderId(), getHalodocProviderLocationId(), getHalodocDepartmentId(), personnelIdList);
        }

        return detailDoctor(id);
    }

    @PutMapping("/{id}/online_con")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public Doctor updateDoctorOnlineCon(@PathVariable Long id, @RequestBody Doctor doctor) throws IOException, URISyntaxException {
        try {
            doctorService.updateProfile(id, doctor);
        } catch (Exception ex) {
            log.error("error updating doctor meta_keyword anf display order {}: {}", id, ex.getMessage());
            throw ex;
        }


        DoctorProfileSections doctorProfileSections = doctor.getDoctorProfileSections();

        doctorService.updateEducation(id, doctorProfileSections);
        doctorService.updateExperience(id, doctorProfileSections);
        doctorService.updatePlaceOfPractice(id, doctorProfileSections);
        return detailDoctor(id);
    }

    @PutMapping("/{id}/additional_information")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public Doctor updateAdditionalInformation(@PathVariable Long id, @RequestBody Doctor doctor) throws IOException, URISyntaxException {
        DoctorProfileSections doctorProfileSections = doctor.getDoctorProfileSections();

        doctorService.updateAdditionalInfo(id, doctorProfileSections);
        return detailDoctor(id);
    }

    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    @PatchMapping("/{doctorId}/identity")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public Identity updateDoctorLogin(@PathVariable String doctorId, @RequestBody UpdateIdentityRequest identities) throws IOException, URISyntaxException {

        // Inactivate old identity
        if (identities.oldIdentity != null && StringUtils.isNotBlank(identities.oldIdentity.getExternalId())) {
            try {
                identities.oldIdentity.setStatus("inactive");
                doctorService.updateDoctorIdentity(doctorId, identities.oldIdentity);
            } catch (Exception ex) {
                log.error("Error while inactivating current doctor identity {}: {}", doctorId, ex.getMessage());
                throw ex;
            }
        }

        // Create a new identity if it doesn't exist
        final Identity newIdentity;
        try {
            identities.newIdentity.setIdentifier(null);
            identities.newIdentity.setExternalId(null);

            Doctor doctor = doctorService.getByExternal(doctorId);

            Optional<Identity> identityOptional = doctor.getIdentities().stream()
                    .filter(i -> i.getValue().equals(identities.newIdentity.getValue())).findFirst();

            if (identityOptional.isPresent()) {
                newIdentity = identityOptional.get();
                newIdentity.setStatus("active");
                doctorService.updateDoctorIdentity(doctorId, newIdentity);
            } else {
                // This will create or use existing
                newIdentity = doctorService.createNewIdentity(doctorId, identities.newIdentity);
            }
        } catch (Throwable t) {


            if (identities.oldIdentity != null && StringUtils.isNotBlank(identities.oldIdentity.getExternalId())) {
                identities.oldIdentity.setStatus("active");
                log.debug("Rolling back update identity {}", identities.oldIdentity);
                doctorService.updateDoctorIdentity(doctorId, identities.oldIdentity);
            }

            throw t;
        }


        return newIdentity;
    }

    @GetMapping("/{externalId}/{type}/preferences")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CODI)
    public DoctorPreferences getDoctorPreferences(@PathVariable String externalId,
                                                  @PathVariable String type,
                                                  @RequestParam(name = "page_no", defaultValue = "1", required = false) Integer pageNo,
                                                  @RequestParam(name = "per_page", defaultValue = "100", required = false) Integer perPage) throws URISyntaxException {

        if (!authorizationService.isAuthorized("contact_doctor","doctor_settings_view", "view_only")
                && !authorizationService.isAuthorized("contact_doctor","doctor_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return doctorService.getDoctorPreferences(externalId, type,
                pageNo, perPage);
    }


    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    @PostMapping("/{externalId}/preferences")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public List<Preferences> createOrUpdateDoctorPreferences(@PathVariable String externalId,
                                                             @RequestBody List<Preferences> preferences) {


        List<Preferences> existingPreferences = preferences.stream().filter(p -> StringUtils.isNotBlank(p.getExternal_id()))
                .collect(Collectors.toList());


        List<Preferences> newPreferences = preferences.stream()
                .filter(p -> StringUtils.isBlank(p.getExternal_id()))
                .collect(Collectors.toList());


        if (existingPreferences != null && !existingPreferences.isEmpty()) {
            existingPreferences.forEach(p -> {
                doctorService.updateDoctorPreference(externalId, p.getExternal_id(), p);
            });
        }

        if (newPreferences != null && !newPreferences.isEmpty()) {

            newPreferences = doctorService.createDoctorPreferences(externalId,
                    newPreferences);
        }

        if (existingPreferences == null) {
            existingPreferences = new ArrayList<>();
        }
        if (newPreferences != null && !newPreferences.isEmpty()) {
            existingPreferences.addAll(newPreferences);
        }
        return existingPreferences;
    }

    @PatchMapping("/{externalId}/template-categories/{category_id}/templates/{template_id}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    public DoctorTemplate updateDoctorTemplate(@PathVariable String externalId,
                                               @PathVariable String category_id,
                                               @PathVariable String template_id,
                                               @RequestBody DoctorTemplate doctorTemplateRequest
    ) throws URISyntaxException {


        return doctorService.updateDoctorTemplate(externalId, category_id, template_id, doctorTemplateRequest);
    }

    @PutMapping("/{externalId}/templates")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public List<DoctorTemplate> getAllDoctorTemplates(@PathVariable String externalId,
                                                      @NotNull @Valid @RequestBody Map categoryDetailsRequest) throws URISyntaxException {


        return doctorService.getAllDoctorTemplates(externalId, categoryDetailsRequest);
    }

    @GetMapping("/template-categories")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<DoctorTemplateCategory> getDoctorTemplateCategories(
            @RequestParam(name = "page_no", defaultValue = "1", required = false) Integer pageNo,
            @RequestParam(name = "per_page", defaultValue = "100", required = false) Integer perPage,
            @RequestParam(name = "statuses", required = false) String statuses,
            @RequestParam(name = "names", required = false) String names,
            @RequestParam(name = "sort_field", required = false) String sort_field,
            @RequestParam(name = "sort_order", required = false) String sort_order) throws URISyntaxException {

        if (!authorizationService.isAuthorized("contact_doctor","doctor_categories_list", "view_only")
                && !authorizationService.isAuthorized("contact_doctor","doctor_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return doctorService.getDoctorTemplateCategories(pageNo, perPage, statuses, names, sort_field, sort_order);
    }

    @GetMapping("/external/{personnelId}/schedules")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public List detail(@PathVariable String personnelId) {
        return doctorService.getExternalDoctorSchedules(personnelId);
    }

    @GetMapping("/{externalId}/categories")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public Map categories(@PathVariable String externalId,
                          @RequestParam(name = "doctor_category_statuses", defaultValue = "active", required = false) String doctor_category_statuses)
            throws URISyntaxException {

        if (!authorizationService.isAuthorized("contact_doctor","doctor_categories_list", "view_only")
                && !authorizationService.isAuthorized("contact_doctor","doctor_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return doctorService.getDoctorCategories(externalId, doctor_category_statuses);
    }

    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    @PutMapping("/{doctorExtId}/categories/{categoryExtId}/link")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public Map linkToCategory(@PathVariable String doctorExtId, @PathVariable String categoryExtId) {
        return doctorService.linkToCategory(doctorExtId, categoryExtId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_settings_edit', 'restricted_write')")
    @PutMapping("/{doctorExtId}/categories/{categoryExtId}/unlink")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public Map unlinkToCategory(@PathVariable String doctorExtId, @PathVariable String categoryExtId) {
        return doctorService.unlinkToCategory(doctorExtId, categoryExtId);
    }

    @PutMapping("/{doctorId}/specialities/multi-link")
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public void linkDoctorSpecialities(@PathVariable String doctorId,
                                               @RequestBody List<String> specialityExternalIds) throws URISyntaxException {
        doctorService.linkDoctorSpecialities(doctorId, specialityExternalIds);
    }

    @PutMapping("/{doctorId}/specialities/multi-unlink")
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public void unlinkDoctorSpecialities(@PathVariable String doctorId,
                                                 @RequestBody List<String> specialityExternalIds) throws URISyntaxException {
        doctorService.unlinkDoctorSpecialities(doctorId, specialityExternalIds);
    }

    @GetMapping("/deleted/list")
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','account_deletion', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<Doctor> deletedDoctors(
            @RequestParam(required = false) int page_no,
            @RequestParam(required = false) int per_page
    ) {

        Map<String, Object> request = new HashMap<>();

        request.put("page_no", page_no);
        request.put("per_page", per_page);

        return doctorService.deletedDoctors(request);
    }

    @PutMapping("/reactivate/{doctorExtId}")
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','account_deletion', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public Map reactivateDoctor(@PathVariable String doctorExtId) {
        return doctorService.reactivateDoctor(doctorExtId);
    }

    @GetMapping("/recon-information/{id}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    public DoctorReconCatalog getDoctorReconCatalog(@PathVariable String id) throws URISyntaxException {
            if (!authorizationService.isAuthorized("contact_doctor", "doctor_recon_information_view", "view_only")
            && !authorizationService.isAuthorized("contact_doctor", "doctor_view", "view_only")) {
        throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
    }
        return reconCatalogService.getDoctorReconCatalog(id);
    }

    @PatchMapping("/recon-information/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','doctor_recon_information_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    public void updateDoctorPaymentWithhold(@PathVariable String id, @RequestBody PaymentWithholdRequest doctorPaymentWithholdReq) throws URISyntaxException {
        reconCatalogService.updateDoctorPaymentWithhold(id, doctorPaymentWithholdReq);
    }

    @PutMapping("/{doctorId}/device/{deviceId}/logout")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    void logoutDoctor(@PathVariable String doctorId, @PathVariable String deviceId) {
        bintanIdentityService.logoutDoctor(doctorId, deviceId);
    }
}
