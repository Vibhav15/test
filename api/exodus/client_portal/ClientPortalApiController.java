package com.halodoc.batavia.controller.api.exodus.client_portal;

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
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.client_portal.ClientPortalService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RestController
@RequestMapping ("api/v1/client/user")
public class ClientPortalApiController extends HalodocBaseApiController {
    @Autowired
    private ClientPortalService clientPortalService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','client_portal_member_list', 'view_only')")
    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> searchCMTOutpatientList(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "status", defaultValue = DEFAULT_STRING) String status,
            @RequestParam (required = false, name = "user_name", defaultValue = DEFAULT_STRING) String userName,
            @RequestParam (required = false, name = "client_name", defaultValue = DEFAULT_STRING) String clientName) throws URISyntaxException {

        return clientPortalService.searchClientPortalList(pageNo, perPage, clientName, userName, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','client_portal_member_view', 'view_only')")
    @GetMapping ("/insurance-provider")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> getInsuranceProvider(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "name", defaultValue = DEFAULT_STRING) String name) throws URISyntaxException {
        return clientPortalService.getInsuranceProvider(pageNo, perPage, name);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','client_portal_member_create', 'restricted_write')")
    @PostMapping ("/create")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createUser(@RequestBody Map request) throws URISyntaxException {
        return clientPortalService.createUser(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','client_portal_member_edit', 'restricted_write')")
    @PutMapping ("/update")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateUser(@RequestBody Map request) throws URISyntaxException {
        clientPortalService.updateUser(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','client_portal_member_edit', 'restricted_write')")
    @PostMapping("/{userId}/password")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public Map resetPasswordForUser(@PathVariable String userId) throws URISyntaxException {
        return clientPortalService.resetPassword(userId);
    }
}
