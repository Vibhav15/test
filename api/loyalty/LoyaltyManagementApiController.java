package com.halodoc.batavia.controller.api.loyalty;

import java.net.URISyntaxException;


import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.loyalty.LoyaltyReward;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.loyalty.LoyaltyManagementService;

@RequestMapping ("api/v1/loyalty")
@RestController
public class LoyaltyManagementApiController {

    @Autowired
    private LoyaltyManagementService loyaltyManagementService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping ("/rewards")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    LoyaltyReward getLoyaltyRewardDetails(
            @RequestParam (required = true, name = "service_reference_id") String service_reference_id,
            @RequestParam (required = true, name = "entity_id") String entity_id,
    @RequestParam (required = true, name = "service_type") String service_type)throws URISyntaxException {
        if (!authorizationService.isAuthorized("contact_doctor","consultations", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return loyaltyManagementService.getLoyaltyRewardInfo(service_reference_id, entity_id, service_type);
    }
}
