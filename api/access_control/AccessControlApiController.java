package com.halodoc.batavia.controller.api.access_control;

import java.net.URISyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.access_control.AccessRole;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AccessControlService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

@Controller
@RequestMapping ("api/v1/access-control")
@RestController
public class AccessControlApiController {
    @Autowired
    private AccessControlService accessControlService;

    @GetMapping ("/roles")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public List<AccessRole> getUserAccessRoles(@RequestParam (required = true, name = "service") final String service) throws URISyntaxException {
        return accessControlService.getUserAccessRoles(service);
    }
}
