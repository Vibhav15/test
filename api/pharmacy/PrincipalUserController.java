package com.halodoc.batavia.controller.api.pharmacy;

import java.net.URISyntaxException;
import java.util.List;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.pharmacy.PrincipalMerchantLocation;
import com.halodoc.batavia.entity.pharmacy.PrincipalUser;
import com.halodoc.batavia.entity.pharmacy.PrincipalUserMerchantLocationMapping;
import com.halodoc.batavia.service.pharmacy.PrincipalUserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping ("api/v1/principal-user")
@RestController
public class PrincipalUserController {
    @Autowired
    PrincipalUserService principalUserService;

    @GetMapping ()
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_user', 'view_only')")
    public PaginatedResult<PrincipalUser> search(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "phone_number") String phoneNumber,
            @RequestParam (required = false, name = "status") List<String> statuses) throws URISyntaxException {
        return principalUserService.search(pageNo, perPage, phoneNumber, statuses);
    }

    @GetMapping("/users")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_user', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<PrincipalUser> getMapping(@RequestParam (required = false, name = "status") String status,
                                                     @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                     @RequestParam (required = false, name = "principal_id" ) String principalId,
                                                     @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                     @RequestParam (required = false, name = "phone_number" ) String phoneNumber) throws URISyntaxException {

        return principalUserService.getMapping(status,pageNo,perPage,principalId,phoneNumber);
    }

    @GetMapping ("/{gpid}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_user', 'view_only')")
    public PrincipalUser getByGpid(@PathVariable String gpid) throws URISyntaxException {
        return principalUserService.getByGpid(gpid);
    }

    @PutMapping ("/{gpid}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_user', 'restricted_write')")
    public PrincipalUser update(@PathVariable String gpid, @RequestBody PrincipalUser principalUser) throws URISyntaxException {
        return principalUserService.update(gpid, principalUser);
    }

    @PostMapping ("/{gpid}/merchant-locations")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_user', 'restricted_write')")
    public void mapToMerchantLocation(@PathVariable String gpid,
            @RequestBody PrincipalUserMerchantLocationMapping principalUserMerchantLocationMapping) throws URISyntaxException {
        principalUserService.mapToMerchantLocation(gpid, principalUserMerchantLocationMapping);
    }

    @DeleteMapping ("/{gpid}/merchant-locations/{merchantLocationId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_user', 'restricted_write')")
    public void unmapMerchantLocation(@PathVariable String gpid, @PathVariable String merchantLocationId) throws URISyntaxException {
        principalUserService.unmapMerchantLocation(gpid, merchantLocationId);
    }

    @GetMapping ("/{gpid}/merchant-locations")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','principal_user', 'view_only')")
    public PaginatedResult<PrincipalMerchantLocation> getMappedMerchantLocations(@PathVariable String gpid,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return principalUserService.getMappedMerchantLocations(gpid, pageNo, perPage);
    }
}
