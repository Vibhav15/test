package com.halodoc.batavia.controller.api.subscriptions;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.subscriptions.SubscriptionsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/subscriptions/providers")
@RestController
@Slf4j
public class SubscriptionsProgramPackageApiController extends HalodocBaseApiController {
    @Autowired
    SubscriptionsService subscriptionService;

    @Autowired
    AuthorizationService authorizationService;

    @GetMapping ("/{providerId}/programs/{programId}/packages")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> paginatedProviderProgramPackages(@PathVariable String providerId, @PathVariable String programId,
            @RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false, name = "statuses", defaultValue = "active,inactive") String status,
            @RequestParam (required = false, name = "perPage", defaultValue = "10") Integer per_page,
            @RequestParam (required = false, name = "pageNo", defaultValue = "1") Integer page_no,
            @RequestParam (required = false) @DateTimeFormat (pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam (required = false) @DateTimeFormat (pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam (required = false, name = "includePdSubscription") String include_pd_subscription,
            @RequestParam(required = false, name = "includeDigitalClinicPackage", defaultValue = "true") String include_digital_clinic_package,
                                                          @RequestParam (required = false, defaultValue = "created_at") String sort_by,
            @RequestParam (required = false, defaultValue = "desc") String sort_order) throws URISyntaxException {
        if (!authorizationService.isAuthorized("subscriptions", "subscriptions_list", "view_only") && !authorizationService.isAuthorized("marketing",
                "campaign_edit", "restricted_write") && !authorizationService.isAuthorized("marketing", "condition_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return subscriptionService.paginatedProviderProgramPackages(providerId, programId, name, status, per_page, page_no, startDate, endDate,
                include_pd_subscription, include_digital_clinic_package, sort_by, sort_order);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_view', 'view_only')")
    @GetMapping ("/{providerId}/programs/{programId}/packages/{packageId}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.SRE)
    public Map getProviderProgramPackageById(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId)
            throws URISyntaxException {

        return subscriptionService.getProviderProgramPackageById(providerId, programId, packageId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_view', 'view_only')")
    @GetMapping ("/{providerId}/programs/{programId}/packages/{packageId}/details")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.SRE)
    public Map getProviderProgramPackageByIdWithBenefitDetails(@PathVariable String providerId, @PathVariable String programId,
            @PathVariable String packageId) throws URISyntaxException {

        return subscriptionService.getProviderProgramPackageByIdWithBenefitDetails(providerId, programId, packageId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_add', 'restricted_write')")
    @PostMapping ("/{providerId}/programs/{programId}/packages")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map createProviderProgramPackage(@PathVariable String providerId, @PathVariable String programId, @RequestBody Map providerProgramPackage)
            throws URISyntaxException {

        return subscriptionService.createProviderProgramPackage(providerId, programId, providerProgramPackage);

    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/programs/{programId}/packages/{packageId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateProviderProgramPackage(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId,
            @RequestBody Map providerProgramPackage) throws URISyntaxException {

        subscriptionService.updateProviderProgramPackage(providerId, programId, packageId, providerProgramPackage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_edit', 'restricted_write')")
    @PutMapping ("/{providerId}/programs/{programId}/packages/{packageId}/attributes")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateProviderProgramPackageAttributes(@PathVariable String providerId, @PathVariable String programId,
            @PathVariable String packageId, @RequestBody List<Object> providerProgramPackageAttributes) throws URISyntaxException {

        subscriptionService.updateProviderProgramPackageAttributes(providerId, programId, packageId, providerProgramPackageAttributes);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('subscriptions','subscriptions_add', 'restricted_write')")
    @PutMapping ("/{providerId}/programs/{programId}/packages/{packageId}/vas")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public List<Map> linkVasToPackage(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId,
            @RequestBody List<Object> vasLinkRequest) throws URISyntaxException {

        return subscriptionService.linkVasToPackage(providerId, programId, packageId, vasLinkRequest);

    }

}
