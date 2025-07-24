package com.halodoc.batavia.controller.api.subscriptions;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.subscriptions.SubscriptionManagementService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/subscriptions/customer")
@RestController
@Slf4j
public class SubscriptionsApiController extends HalodocBaseApiController {
    @Autowired
    SubscriptionManagementService subscriptionService;

    @Autowired
    AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_edit', 'restricted_write')")
    @PutMapping ("/{subscriptionId}/vases/{vasId}/activate")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void activateVas(@PathVariable String subscriptionId, @PathVariable String vasId, @RequestBody Map vasActivateRequest)
            throws URISyntaxException {
        subscriptionService.activateVas(subscriptionId, vasId, vasActivateRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_edit', 'restricted_write')")
    @PutMapping ("/{subscriptionId}/vases/{vasId}/workflow/{workflowId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateVasWorkflow(@PathVariable String subscriptionId, @PathVariable String vasId, @PathVariable String workflowId,
            @RequestBody Map vasWorkflowRequest) throws URISyntaxException {
        subscriptionService.vasWorkFlowStatusUpdate(subscriptionId, vasId, workflowId, vasWorkflowRequest);
    }

    @PutMapping ("/{subscriptionId}/cancel-product-subscription")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void cancelProductSubscription(@PathVariable String subscriptionId, @RequestBody Map cancelRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("subscriptions", "subscriptions_edit", "restricted_write") && !authorizationService.isAuthorized(
                "customer", "customer_view", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        subscriptionService.cancelProductSubscription(subscriptionId, cancelRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_view', 'view_only')")
    @GetMapping ("/{subscriptionId}/vases/{vasId}/documents")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.SRE)
    public Map getDocumentImageUrl(@PathVariable String subscriptionId, @PathVariable String vasId) throws URISyntaxException {
        return subscriptionService.getDocumentImageUrl(subscriptionId, vasId);
    }
}
