package com.halodoc.batavia.controller.api.customer;

import java.net.URISyntaxException;
import java.util.List;

import com.halodoc.batavia.entity.bali.logan.Patient;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bali.logan.Customer;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.CustomerService;
import com.halodoc.batavia.service.bali.logan.BaliLoganService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v2/customers")
@RestController
@Slf4j
public class CustomerV2ApiController extends HalodocBaseApiController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private BaliLoganService baliLoganService;

    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Customer saveCustomer(@RequestBody Customer user) throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "customer_view", "restricted_write") && !authorizationService.isAuthorized("lab_service",
                "halolab_order_create", "restricted_write") && !authorizationService.isAuthorized("hospitals_management", "hospital_pharmacy_add",
                "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return baliLoganService.addCustomerV2(user);
    }

    @PutMapping("/{userId}/patient/{patientId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updatePatient(@RequestBody Patient patient, @PathVariable String userId , @PathVariable String patientId) throws URISyntaxException {
        baliLoganService.updatePatientV2(patient, userId,patientId);
    }

    @PutMapping("/{id}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateCustomer(@PathVariable String id, @RequestBody Customer user) throws URISyntaxException {
        baliLoganService.updateCustomerV2(user, id);
    }

    @GetMapping("/{customerId}/patients")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    @PreAuthorize("@authorizationService.isAuthorized('customer', 'customer_view', 'view_only')")
    public List<Patient> getPatientsV2(@PathVariable String customerId) throws URISyntaxException {
        return baliLoganService.getPatientsV2(customerId);
    }
}
