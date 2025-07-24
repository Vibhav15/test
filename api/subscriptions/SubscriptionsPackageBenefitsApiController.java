package com.halodoc.batavia.controller.api.subscriptions;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.subscriptions.SubscriptionsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/subscriptions/providers")
@RestController
@Slf4j
public class SubscriptionsPackageBenefitsApiController extends HalodocBaseApiController {

    @Autowired
    SubscriptionsService subscriptionService;

    @PreAuthorize("@authorizationService.isAuthorized('marketing','campaign_list', 'view_only')")
    @GetMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> paginatedProviderProgramPackageBenefits(@PathVariable String providerId,
                                                                 @PathVariable String programId,
                                                                 @PathVariable String packageId,
                                                                 @RequestParam(required = false, name = "name") String name,
                                                                 @RequestParam(required = false, name = "perPage", defaultValue = "10") Integer per_page,
                                                                 @RequestParam(required = false, name = "pageNo", defaultValue = "1") Integer page_no) throws URISyntaxException {

        return subscriptionService.paginatedProviderProgramPackageBenefits(providerId, programId, packageId, name, per_page, page_no);
    }

    @GetMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits/{benefitId}/details")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.SRE)
    public Map getProviderProgramPackageBenefitDetailsById(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId, @PathVariable String benefitId) throws URISyntaxException {

        return subscriptionService.getProviderProgramPackageBenefitDetailsById(providerId, programId, packageId, benefitId);
    }

    @PostMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map createProviderProgramPackageBenefits(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId,
                                                    @RequestBody Map providerProgramPackageBenefit) throws URISyntaxException {

        return subscriptionService.createProviderProgramPackageBenefits(providerId, programId, packageId, providerProgramPackageBenefit);

    }


    @PutMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits/{benefitId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateProviderProgramPackageBenefits(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId,
                                             @PathVariable String benefitId,
                                             @RequestBody Map providerProgramPackageBenefit) throws URISyntaxException {

        return subscriptionService.updateProviderPackageBenefits(providerId, programId, packageId, benefitId, providerProgramPackageBenefit);
    }


}
