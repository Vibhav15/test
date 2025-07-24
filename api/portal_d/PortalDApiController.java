package com.halodoc.batavia.controller.api.portal_d;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.portal_d.DistributorUser;
import com.halodoc.batavia.entity.portal_d.DistributorUserActivation;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.portal_d.PortalDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RequestMapping("api/v1/sellers")
@RestController
@Slf4j
public class PortalDApiController extends HalodocBaseApiController {
    @Autowired
    private PortalDService portalDService;


    @GetMapping("/users")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<DistributorUser> getDistributorUser(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                               @RequestParam(required = false, defaultValue = "") String status) throws URISyntaxException {
        PaginatedResult<DistributorUser> distributorUser = portalDService.getDistributorUsers(pageNo, perPage,status);
        return distributorUser;
    }

    @PutMapping("/users/{gpid}/activate")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map activateDistributorUser(@PathVariable String gpid, @RequestBody DistributorUserActivation distributorUserActivation) {
        return portalDService.activateDistributorUser(gpid,distributorUserActivation);
    }

    @PutMapping("/users/{gpid}/deactivate")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public  Map  deactivateDistributorUser(@PathVariable String gpid) throws URISyntaxException {
        return portalDService.deactivateDistributorUser(gpid);
    }
}
