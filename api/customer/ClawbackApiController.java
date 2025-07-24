package com.halodoc.batavia.controller.api.customer;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.scrooge.Billings;
import com.halodoc.batavia.entity.scrooge.Clawback;
import com.halodoc.batavia.entity.scrooge.VoidClawbackBody;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.scrooge.ClawbackService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/internal/clawbacks")
public class ClawbackApiController extends HalodocBaseApiController {

    @Autowired private ClawbackService clawbackService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/{customerBillingId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Clawback getClawbacks(@PathVariable String customerBillingId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "customer_list", "view_only")
                && !authorizationService.isAuthorized("insurance", "insurance_provider_list", "view_only")
                && !authorizationService.isAuthorized("contact_doctor","consultations", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return clawbackService.getClawbackBiling(customerBillingId);
    }

    @PostMapping("/void")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public VoidClawbackBody voidClawbacks(@RequestBody VoidClawbackBody voidClawbackBody) {
        if (!authorizationService.isAuthorized("customer", "customer_list", "restricted_write")
                && !authorizationService.isAuthorized("insurance", "insurance_provider_edit", "restricted_write")
                && !authorizationService.isAuthorized("contact_doctor","consultations", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return clawbackService.voidClawbackBody(voidClawbackBody);
    }

    @GetMapping("/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public Billings searchClawbacks(@RequestParam(required = true, name = "entity_id", defaultValue = "") String entityId,
                                 @RequestParam(required = false, name = "entity_type", defaultValue = "") String entityType,
                                 @RequestParam(required = false, name = "billing_statuses", defaultValue = "") String billingStatuses,
                                 @RequestParam(required = false, name = "customer_billing_id", defaultValue = "") String customerBillingId,
                                 @RequestParam(required = false, name = "service_reference_id", defaultValue = "") String serviceReferenceId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("customer", "customer_list", "view_only")
                && !authorizationService.isAuthorized("insurance", "insurance_provider_list", "view_only")
                && !authorizationService.isAuthorized("contact_doctor","consultations", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return clawbackService.searchClawback(entityType, billingStatuses, serviceReferenceId, entityId, customerBillingId);
    }
}
