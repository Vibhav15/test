package com.halodoc.batavia.controller.api.pharmacy;

import java.net.URISyntaxException;
import java.util.List;


import com.halodoc.batavia.entity.pharmacy.BusinessUnit;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
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
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.pharmacy.Principal;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.pharmacy.PrincipalManagementService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping ("api/v1/principal-management")
@RestController
public class PrincipalManagementController {
    @Autowired
    PrincipalManagementService principalManagementService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping ()
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Principal> search(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "principal_management", "view_only") && !authorizationService.isAuthorized(
                "pharmacy_delivery", "principal_user", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return principalManagementService.search(pageNo, perPage, name, status);
    }

    @PostMapping ()
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Principal create(@RequestBody Principal principal) throws URISyntaxException {
        return principalManagementService.create(principal);
    }

    @PutMapping ("/{externalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Principal update(@PathVariable String externalId, @RequestBody Principal principal) throws URISyntaxException {
        return principalManagementService.update(externalId, principal);
    }

    @GetMapping ("/{externalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Principal getById(@PathVariable String externalId) throws URISyntaxException {
        return principalManagementService.getById(externalId);
    }

    @PostMapping ("/{externalId}/business-units")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE,  verticalName = Vertical.PD)
    public BusinessUnit createBusinessUnit(@PathVariable String externalId, @RequestBody BusinessUnit businessUnit) throws  URISyntaxException {
        return principalManagementService.createBusinessUnit(externalId, businessUnit);
    }

    @PutMapping ("/{externalId}/business-units/{businessUnitId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE,  verticalName = Vertical.PD)
    public BusinessUnit updateBusinessUnit(@PathVariable String externalId, @PathVariable String businessUnitId, @RequestBody BusinessUnit businessUnit) throws URISyntaxException {
        return principalManagementService.updateBusinessUnit(externalId, businessUnitId, businessUnit);
    }

    @GetMapping ("/{externalId}/business-units")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_management', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP,  verticalName = Vertical.PD)
    public List<BusinessUnit> getBusinessUnitById(@PathVariable String externalId, @RequestParam (required = false, name = "status") String status) throws URISyntaxException {
        return principalManagementService.getBusinessUnitById(externalId, status);
    }
}
