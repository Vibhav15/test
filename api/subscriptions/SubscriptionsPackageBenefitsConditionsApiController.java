package com.halodoc.batavia.controller.api.subscriptions;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.subscriptions.SubscriptionManagementService;
import com.halodoc.batavia.service.subscriptions.SubscriptionsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/subscriptions")
@RestController
@Slf4j
public class SubscriptionsPackageBenefitsConditionsApiController extends HalodocBaseApiController {
    @Autowired
    SubscriptionsService subscriptionService;

    @Autowired
    SubscriptionManagementService subscriptionManagementService;

    @Autowired
    AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_view', 'view_only')")
    @GetMapping ("/conditions")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<Map> getBenefitConditions() throws URISyntaxException {

        return subscriptionService.getBenefitConditions();
    }

    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> getSubscriptionDetails(
            @RequestParam (required = false, name = "statuses", defaultValue = "expired,activated") String status,
            @RequestParam (required = false, name = "pdStatuses", defaultValue = "expired,activated,cancelled,created,payment_failed,payment_processing") String pdStatus,
            @RequestParam (required = false, name = "entity_id", defaultValue = "") String entityId,
            @RequestParam (required = false, name = "details_required", defaultValue = "true") Boolean detailsRequired,
            @RequestParam (required = false, name = "perPage", defaultValue = "20") Integer per_page,
            @RequestParam (required = false, name = "pageNo", defaultValue = "1") Integer page_no,
            @RequestParam (required = false, name = "entityType", defaultValue = "user") String entityType,
            @RequestParam (required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam (required = false, name = "sortOrder", defaultValue = "desc") String sortOrder,
            @RequestParam (required = false, name = "package_source", defaultValue = "") String package_source) throws URISyntaxException {
        if (!authorizationService.isAuthorized("subscriptions", "subscriptions_view", "view_only") && !authorizationService.isAuthorized("customer",
                "customer_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return subscriptionManagementService.getSubscriptions(status, pdStatus, entityId, detailsRequired, per_page, page_no, entityType, sortBy,
                sortOrder, package_source);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_view', 'view_only')")
    @GetMapping ("/{entityId}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.SRE)
    public Map getSubscriptionDetailsByEntityId(@PathVariable String entityId,
            @RequestParam (required = false, name = "details_required", defaultValue = "true") Boolean detailsRequired) throws URISyntaxException {

        return subscriptionManagementService.getSubscriptionDetailsByEntityId(entityId, detailsRequired);
    }

}
