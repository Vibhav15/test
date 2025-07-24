package com.halodoc.batavia.controller.api.customer;

import com.halodoc.audit.sdk.Auditor;
import com.halodoc.audit.sdk.annotaion.AuditedAccess;
import com.halodoc.audit.sdk.annotaion.AuditedEntityId;
import com.halodoc.audit.sdk.constant.Action;
import com.halodoc.audit.sdk.constant.ActorType;
import com.halodoc.audit.sdk.constant.ChannelType;
import com.halodoc.audit.sdk.constant.EntityType;
import com.halodoc.audit.sdk.constant.EventType;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.exception.custom.BadRequestExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.ImageUpload;
import com.halodoc.batavia.entity.bali.logan.Customer;
import com.halodoc.batavia.entity.bali.logan.CustomerBlockRequest;
import com.halodoc.batavia.entity.bali.logan.CustomerDeleteRequest;
import com.halodoc.batavia.entity.bali.logan.Patient;
import com.halodoc.batavia.entity.cms.PharmacyUser;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.entity.configuration.CustomerMFEConfiguration;
import com.halodoc.batavia.entity.customer.CustomerTransactions;
import com.halodoc.batavia.entity.customer.DeviceInfo;
import com.halodoc.batavia.entity.customer.PagedTransactionParams;
import com.halodoc.batavia.entity.customer.ProductRemainingBalance;
import com.halodoc.batavia.entity.customer.RemainingBalanceRequest;
import com.halodoc.batavia.entity.digital_clinic.Treatments;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.BuruService;
import com.halodoc.batavia.service.CustomerService;
import com.halodoc.batavia.service.PasswordManagementService;
import com.halodoc.batavia.service.aru.AruService;
import com.halodoc.batavia.service.bali.logan.BaliLoganService;
import com.halodoc.batavia.service.device.DeviceService;
import com.halodoc.batavia.service.digital_clinic.TreatmentService;
import com.halodoc.batavia.service.ktp.KTPService;
import com.halodoc.batavia.service.membership.MembershipService;
import com.halodoc.config.ConfigClient;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("api/v1/customers")
@RestController
@Slf4j
public class CustomerApiController extends HalodocBaseApiController {

    private final CustomerService customerService;
    private final DeviceService deviceService;
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private BaliLoganService baliLoganService;

    @Autowired
    private BuruService buruService;

    @Autowired
    private KTPService ktpService;

    @Autowired
    private PasswordManagementService passwordManagementService;

    @Autowired
    private TreatmentService treatmentService;

    @Autowired
    private ConfigClient<BataviaAppConfiguration> bataviaAppConfig;

    @Autowired
    private AruService aruService;

    @Autowired
    private Auditor auditor;

    @Autowired
    public CustomerApiController(CustomerService customerService,
            DeviceService deviceService) {
        this.customerService = customerService;
        this.deviceService = deviceService;
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_list', 'view_only')")
    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Customer> listCustomer(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "firstName", defaultValue = "") String firstName,
            @RequestParam(required = false, name = "lastName", defaultValue = "") String lastName,
            @RequestParam(required = false, name = "phoneNumber", defaultValue = "") String phoneNumber,
            @RequestParam(required = false, name = "patientId", defaultValue = "") String patientId,
            @RequestParam(required = false, name = "user_name", defaultValue = "") String userName,
            @RequestParam(required = false, name = "identity_type", defaultValue = "") String identityType)
            throws URISyntaxException {

        return baliLoganService.getCustomers(firstName, lastName, userName, identityType, phoneNumber, patientId,
                pageNo, perPage);
    }

    @GetMapping("/configs")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public CustomerMFEConfiguration getCustomerMFEConfig() {
        return bataviaAppConfig.getAppConfig().getCustomerMFEConfiguration();
    }

    @GetMapping("/autocomplete")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public List<Customer> autocomplete(@RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false, name = "user_name", defaultValue = "") String userName,
            @RequestParam(required = false, name = "identity_type", defaultValue = "") String identityType)
            throws URISyntaxException {

        PaginatedResult<Customer> paginatedResult = baliLoganService.getCustomers(name, null, userName, identityType,
                phone, null, 1, 50);

        return paginatedResult.getResult();
    }

    @GetMapping("/phoneautocomplete")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public List<PharmacyUser> phoneautocomplete(@RequestParam(required = false) String status,
            @RequestParam(required = false) String per_page,
            @RequestParam(required = false) String page_no,
            @RequestParam(required = false) String phone) throws URISyntaxException {

        PaginatedResult<PharmacyUser> paginatedResult = buruService.getListOfPharmacyUsers(status, per_page, page_no,
                phone);

        return paginatedResult.getResult();
    }

    @GetMapping("/{userId}")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CORE)
    public Customer detailCustomer(@PathVariable String userId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "customer_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "hd_go_order_list", "view_only")
                && !authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")
                && !authorizationService.isAuthorized("lab_service", "order_list", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_list", "view_only")
                && !authorizationService.isAuthorized("hospitals_management", "hospital_appointment_list",
                        "view_only")
                && !authorizationService.isAuthorized("insurance", "analyst_claims_view", "view_only")
                && !authorizationService.isAuthorized("insurance", "supervisor_claims_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.customer_data_accessed, Action.get, ActorType.cc_user, EntityType.customer,
                userId, ChannelType.http);

        Customer customer = baliLoganService.getCustomer(userId);
        List<DeviceInfo> deviceInfoList = Collections.emptyList();
        try {
            deviceInfoList = deviceService.getDeviceInfo(userId, "customer", null);
        } catch (Exception e) {

        }

        if (deviceInfoList != null && deviceInfoList.size() > 0) {
            deviceInfoList.sort(Comparator.comparing(DeviceInfo::getUpdatedAt).reversed());

            DeviceInfo deviceInfo = deviceInfoList.get(0);
            String device = String.format("HD %s app/%s/%s %s", deviceInfo.getApplicationType(),
                    deviceInfo.getApplicationVersionName(), deviceInfo.getOperatingSystem(),
                    deviceInfo.getOperatingSystemVersion());
            customer.setDeviceInfo(device);
        }
        return customer;
    }

    @GetMapping("/{customerId}/patients")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CORE)
    public List<Patient> getPatients(@PathVariable @NotBlank String customerId,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "customer_view", "view_only")
                && !authorizationService.isAuthorized("lab_service", "halolab_order_create", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_actions", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.customer_patients_accessed, Action.get, ActorType.cc_user, EntityType.customer,
                customerId, ChannelType.http);

        return baliLoganService.getPatients(customerId);
    }

    @GetMapping("/{id}/policies")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<Map<String, Object>> getInsurancePolicies(@PathVariable String id) {
        if (!authorizationService.isAuthorized("customer", "customer_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery",
                        "order_actions", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_view",
                        "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_create_reorder",
                        "restricted_write")
                && !authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        auditor.publishEvent(EventType.customer_policies_accessed, Action.get, ActorType.cc_user, EntityType.customer,
                id, ChannelType.http);

        return membershipService.getUserPolicies(id);
    }

    @PutMapping("/remaining-balance")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    // TODO: TO be cross checked by insurance team
    public List<ProductRemainingBalance> getRemainingBalance(
            @RequestBody RemainingBalanceRequest remainingBalanceRequest) {
        return membershipService.getRemainingBalance(remainingBalanceRequest);
    }

    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Customer saveCustomer(@RequestBody Customer user) throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "customer_view", "restricted_write")
                && !authorizationService.isAuthorized("lab_service", "halolab_order_create", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return baliLoganService.addCustomer(user);
    }

    @PostMapping("/{customerId}/secondary_patient")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Patient createSecondaryPatient(@PathVariable String customerId, @RequestBody Patient secondaryPatient)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "customer_view", "restricted_write")
                && !authorizationService.isAuthorized("lab_service", "halolab_order_create", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_create_reorder",
                        "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        try {
            return baliLoganService.addPatient(secondaryPatient, customerId);
        } catch (HalodocWebException ex) {
            if (ex.getStatusCode() == HttpStatus.FORBIDDEN.value()
                    || ex.getStatusCode() == HttpStatus.CONFLICT.value()) {
                throw new BadRequestExpectedException(ex.getMessage(), ex.getCode(), ex.getHeader(),
                        ex.getStatusCode());
            } else {
                throw ex;
            }
        }
    }

    @PatchMapping("/{userId}/patients/{patientId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Patient updatePatient(@RequestBody Patient patient, @PathVariable String userId,
            @PathVariable String patientId) throws URISyntaxException {
        return baliLoganService.updatePatient(patient, userId, patientId);
    }

    @PutMapping("/{id}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Customer updateCustomer(@PathVariable String id, @RequestBody Customer user) throws URISyntaxException {
        return baliLoganService.updateCustomer(user, id);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_user_add', 'restricted_write')")
    @PostMapping("/{userId}/password")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public Map createPasswordForUser(@PathVariable String userId) throws URISyntaxException {
        return passwordManagementService.createPasswordForBaliUser(userId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_user_edit', 'restricted_write')")
    @PatchMapping("/{userId}/password")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public Map resetPasswordForUser(@PathVariable String userId) throws URISyntaxException {
        return aruService.resetPasswordForBaliUser(userId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'restricted_write')")
    @PatchMapping("/{userId}/block")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void blockUser(@PathVariable String userId, @RequestBody CustomerBlockRequest customerBlockRequest)
            throws URISyntaxException {
        baliLoganService.blockCustomer(customerBlockRequest, userId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'restricted_write')")
    @PatchMapping("/{userId}/unblock")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void unBlockUser(@PathVariable String userId, @RequestBody CustomerBlockRequest customerUnblockRequest)
            throws URISyntaxException {
        baliLoganService.unblockCustomer(customerUnblockRequest, userId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_deletion', 'full')")
    @PatchMapping("/{userId}/suspend")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void deleteUser(@PathVariable String userId, @RequestBody CustomerDeleteRequest customerDeleteRequest)
            throws URISyntaxException {
        baliLoganService.deleteCustomer(customerDeleteRequest, userId);
    }

    @GetMapping("/{customerId}/patients/{patientId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Object getPatientById(@PathVariable String customerId, @PathVariable String patientId)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "customer_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_actions", "view_only")
                && !authorizationService.isAuthorized("lab_service", "order_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return baliLoganService.getPatient(customerId, patientId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @PutMapping("/{id}/transactions")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.CORE)
    public CustomerTransactions getTransactions(@PathVariable String id,
            @RequestBody Map<String, PagedTransactionParams> transactionParamsMap) {
        return customerService.getTransactions(id, transactionParamsMap);
    }

    @GetMapping("/{entity_id}/device_applications")
    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    // TODO: Duplicate, remove and use from Device Controller
    public List<DeviceInfo> getDeviceApplicationInformation(@PathVariable("entity_id") @NotNull String entityId)
            throws URISyntaxException {
        return deviceService.getDeviceInfo(entityId, "customer", "logged_in");
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view','view_only')")
    @PatchMapping("/{userId}/ktp/allow/verification")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Map allowKtpVerification(@PathVariable String userId) throws URISyntaxException {
        return ktpService.allowKtpVerification(userId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view','view_only')")
    @PatchMapping("/{userId}/ktp/unverify")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Map unverifyKtp(@PathVariable String userId) throws URISyntaxException {
        return ktpService.unverifyKtp(userId);
    }

    @GetMapping("/{userId}/file/{fileExternalId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public ImageUpload getCustomerDocumentsByID(@PathVariable() String userId, @PathVariable() String fileExternalId)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("lab_service", "order_view", "view_only")
                && !authorizationService.isAuthorized("lab_service", "halolab_order_create", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return baliLoganService.getCustomerDocumentsByID(userId, fileExternalId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view','view_only')")
    @GetMapping("/{patientId}/treatments")
    @AuditedAccess(eventType = EventType.customer_treatments_accessed, action = Action.get, actorType = ActorType.cc_user, channelType = ChannelType.http, entityType = EntityType.customer)
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.DC)
    public PaginatedResult<Treatments> getTreatments(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "1") Integer perPage,
            @RequestParam(required = false, name = "statuses", defaultValue = "activated,on_hold,expired") String status,
            @AuditedEntityId @PathVariable(name = "patientId") String patientId) throws URISyntaxException {
        return treatmentService.getPatientTreatments(patientId, pageNo, perPage, status);
    }
}
