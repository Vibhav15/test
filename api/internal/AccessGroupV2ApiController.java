package com.halodoc.batavia.controller.api.internal;

import java.net.URISyntaxException;

import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.internal.AccessGroupFeatureViewV2;
import com.halodoc.batavia.entity.internal.AccessGroupViewV2;
import com.halodoc.batavia.entity.internal.BataviaAccessGroupRequestV2;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.internal.BaliAccessControlService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.NotNull;

@RequestMapping ("api/v2/access_groups")
@RestController
public class AccessGroupV2ApiController {
    @Autowired
    private BaliAccessControlService baliAccessControlService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<AccessGroupViewV2> list(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "status") String status, @RequestParam (required = false, name = "name") String displayName)
            throws URISyntaxException {
        return baliAccessControlService.searchAccessRoles(pageNo, perPage, displayName, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_add', 'full')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public AccessGroupViewV2 createAccessRole(@RequestBody BataviaAccessGroupRequestV2 accessRole) throws URISyntaxException {
        return baliAccessControlService.createAccessRole(accessRole);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_edit', 'full')")
    @PatchMapping ("/{accessRoleId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public AccessGroupViewV2 updateAccessGroup(@PathVariable ("accessRoleId") @NotNull Long accessRoleId,
            @RequestBody BataviaAccessGroupRequestV2 accessRole) throws URISyntaxException {

        return baliAccessControlService.updateAccessRole(accessRoleId, accessRole);
    }

    @PreAuthorize("@authorizationService.isAuthorized('administration','user_view', 'view_only')")
    @GetMapping ("/{accessRoleId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public AccessGroupViewV2 getAccessRole(@PathVariable ("accessRoleId") @NotNull Long accessRoleId) throws URISyntaxException {

        return baliAccessControlService.getAccessRole(accessRoleId);
    }

    @GetMapping ("/{accessRoleId}/features")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<AccessGroupFeatureViewV2> getAccessRoleFeatures(@PathVariable ("accessRoleId") @NotNull Long accessRoleId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "status", defaultValue = "ACTIVE") String status,
            @RequestParam (required = false, name = "paginated", defaultValue = "true") Boolean paginated) throws URISyntaxException {

        if (!authorizationService.isAuthorized("administration", "user_view", "view_only")
                && !authorizationService.isAuthorized("administration", "user_add", "full")
                && !authorizationService.isAuthorized("administration", "user_edit", "full")) {

            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        if (paginated) {
            return baliAccessControlService.getAccessRoleFeatureMapping(accessRoleId, pageNo, perPage, status);
        } else {
            return baliAccessControlService.getAllAccessRoleFeatureMapping(accessRoleId, status);
        }
    }
}
