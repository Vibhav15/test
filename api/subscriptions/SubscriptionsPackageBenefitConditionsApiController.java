package com.halodoc.batavia.controller.api.subscriptions;

import java.net.URISyntaxException;
import java.util.List;
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
public class SubscriptionsPackageBenefitConditionsApiController extends HalodocBaseApiController {

    @Autowired
    SubscriptionsService subscriptionService;

    @GetMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits/{benefitId}/conditions")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> paginatedProviderProgramPackageBenefitConditions(@PathVariable String providerId,
                                                                        @PathVariable String programId,
                                                                        @PathVariable String packageId,
                                                                        @PathVariable String benefitId,
                                                                        @RequestParam(required = false, name = "name") String name,
                                                                        @RequestParam(required = false, name = "perPage", defaultValue = "10") Integer per_page,
                                                                        @RequestParam(required = false, name = "pageNo", defaultValue = "1") Integer page_no) throws URISyntaxException {

        return subscriptionService.paginatedProviderProgramPackageBenefitConditions(providerId, programId, packageId, benefitId, name, per_page, page_no);
    }

    @GetMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits/{benefitId}/conditions/details")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> paginatedProviderProgramPackageBenefitConditionsDetails(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId,
                                                                       @PathVariable String benefitId,
                                                                       @RequestParam(required = false, name = "name") String name,
                                                                       @RequestParam(required = false, name = "perPage", defaultValue = "10") Integer per_page,
                                                                       @RequestParam(required = false, name = "pageNo", defaultValue = "1") Integer page_no) throws URISyntaxException {

        return subscriptionService.paginatedProviderProgramPackageBenefitConditionsDetails(providerId, programId, packageId, benefitId, name, per_page, page_no);
    }

    @PreAuthorize("@authorizationService.isAuthorized('subscriptions','subscriptions_add', 'restricted_write')")
    @PostMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits/{benefitId}/conditions")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map createProviderProgramPackageBenefitCondition(@PathVariable String providerId,
                                                            @PathVariable String programId,
                                                            @PathVariable String packageId,
                                                            @PathVariable String benefitId,
                                                    @RequestBody Map providerProgramPackageBenefitCondition) throws URISyntaxException {

        return subscriptionService.createProviderProgramPackageBenefitCondition(providerId, programId, packageId, benefitId, providerProgramPackageBenefitCondition);

    }

    @PreAuthorize("@authorizationService.isAuthorized('subscriptions','subscriptions_edit', 'restricted_write')")
    @PutMapping("/{providerId}/programs/{programId}/packages/{packageId}/benefits/{benefitId}/conditions/{conditionId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateProviderProgramPackageBenefitCondition(@PathVariable String providerId, @PathVariable String programId, @PathVariable String packageId,
                                                     @PathVariable String benefitId,
                                                     @PathVariable String conditionId,
                                                     @RequestBody List<Object> providerProgramPackageBenefitCondition) throws URISyntaxException {

        subscriptionService.updateProviderProgramPackageBenefitCondition(providerId, programId, packageId, benefitId, conditionId, providerProgramPackageBenefitCondition);
    }
}
