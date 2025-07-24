package com.halodoc.batavia.controller.api;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonView;
import com.halodoc.batavia.dto.ApiDto;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.internal.AccessGroupFeatureViewV2;
import com.halodoc.batavia.entity.internal.AccessGroupWithUserV2;
import com.halodoc.batavia.entity.internal.AccessLevelV2;
import com.halodoc.batavia.entity.internal.CCUser;
import com.halodoc.batavia.entity.internal.NewUserRequestV2;
import com.halodoc.batavia.entity.internal.UserDetail;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.UserService;
import com.halodoc.batavia.util.SpringSecurityUtil;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.NotNull;

@Controller
@RequestMapping ("api/v1/users")
@RestController
public class UserApiController extends HalodocBaseApiController {
    @Autowired
    UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<CCUser> users(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "email") String email,
            @RequestParam (required = false, name = "enabled", defaultValue = "true") Boolean enabled,
            @RequestParam (required = false, name = "role_id") String roleId,
            @RequestParam (required = false, name = "access_role_statuses", defaultValue = "active,inactive") String accessRoleStatuses) {
        if (StringUtils.isNotBlank(roleId)) {
            return userService.searchUsersByRoleId(pageNo, perPage, roleId);
        } else {
            return userService.listUser(pageNo, perPage, email, enabled, accessRoleStatuses);
        }
    }

    @GetMapping ("/download")
    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_list', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public ResponseEntity<byte[]> downloadUserReport(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "email") String email, @RequestParam (required = false, name = "enabled") Boolean enabled,
            @RequestParam (required = false, name = "access_role_statuses", defaultValue = "active") String accessRoleStatuses)
            throws URISyntaxException {
        final PaginatedResult<CCUser> users = userService.listUser(pageNo, perPage, email, enabled, accessRoleStatuses);
        return userService.downloadUserReport(users);
    }

    @GetMapping ("/access-group/{accessGroupId}")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    public List<AccessGroupWithUserV2> searchUsersWithSpecificAccessGroup(@PathVariable ("accessGroupId") String accessGroupId)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "case_monitoring_op_list", "view_only") && !authorizationService.isAuthorized("insurance",
                "claim_cashless_list", "view_only") && !authorizationService.isAuthorized("insurance", "claim_cashless_view",
                "view_only") && !authorizationService.isAuthorized("insurance", "claim_qc_team_add",
                "restricted_write") && !authorizationService.isAuthorized("insurance", "claim_reimbursement_edit",
                "restricted_write") && !authorizationService.isAuthorized("insurance", "claim_reimbursement_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return userService.searchUsersWithSpecificAccessGroup(accessGroupId);
    }

    @GetMapping ("/search-by-featureKey-accessLevel")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    public List<AccessGroupWithUserV2> getUsersWithFeatureKeyAndAccessLevel(@RequestParam (required = true, name = "feature_key") String featureKey,
            @RequestParam (required = true, name = "access_level") AccessLevelV2 accessLevel) throws URISyntaxException {
        return userService.getUsersWithFeatureKeyAndAccessLevel(featureKey, accessLevel);
    }

    @GetMapping ("/user-detail/{userId}")
    @JsonView ({ ApiDto.UserDetail.class })
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public UserDetail getUserById(@PathVariable ("userId") String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping ("/{email:.+}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.SRE)
    public CCUser getUser(@PathVariable ("email") @NotNull String email) {

        if (SpringSecurityUtil.isSelf(email) || authorizationService.isAuthorized("administration", "user_view", "restricted_write")) {
            return userService.getOAuth2UserDetailsWithRoles(email, false);
        }
        throw HalodocWebException.builder().statusCode(403).message("Do not have permission").build();
    }

    @GetMapping ("/{userId}/features")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<AccessGroupFeatureViewV2> getFeaturesByUserId(@PathVariable ("userId") @NotNull String userId) {
        return userService.getFeaturesByUserId(userId);
    }

    @PostMapping
    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_add', 'full')")
    @JsonView (ApiDto.UserDetail.class)
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public CCUser createUser(@RequestBody NewUserRequestV2 newUserRequest) throws URISyntaxException {
        return userService.create(newUserRequest);
    }

    @PatchMapping ("/{email:.+}")
    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_edit', 'full')")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateUser(@PathVariable ("email") String email, @RequestBody CCUser user) throws URISyntaxException {
        userService.updateUser(email, user);
    }

    @PatchMapping ("/{email}/settings")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public CCUser changeSettings(@PathVariable ("email") String email, @RequestBody CCUser user) {
        if (SpringSecurityUtil.isSelf(email) || authorizationService.isAuthorized("administration", "user_view", "restricted_write")) {
            return userService.updateUserSettings(email, user);
        } else {
            throw HalodocWebException.builder().statusCode(403).message("Do not have permission").build();
        }
    }

    @GetMapping ("/profile")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Map getProfile() {
        return this.userService.getProfileDetails();
    }

    @GetMapping ("/configs/allowed-domains")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public List<String> getAllowDomains() {
        return this.userService.getAllowDomains();
    }
}
