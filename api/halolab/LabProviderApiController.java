package com.halodoc.batavia.controller.api.halolab;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.halolab.HalolabHub;
import com.halodoc.batavia.entity.halolab.PaginatedHalolabHubSearchResult;
import com.halodoc.batavia.entity.halolab.PaginatedHalolabProviderSearchResult;
import com.halodoc.batavia.entity.halolab.Provider;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.halolab.HalolabHubService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("api/v1/halolabs/lab-providers")
@RestController
public class LabProviderApiController extends HalodocBaseApiController {
    private HalolabHubService halolabHubService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public LabProviderApiController(HalolabHubService halolabHubService) {
        this.halolabHubService = halolabHubService;
    }

    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    PaginatedHalolabProviderSearchResult searchLabProvider(
            @RequestParam(value = "names") String names,
            @RequestParam(value = "per_page", defaultValue = "20") final Integer perPage,
            @RequestParam(name = "page_no", defaultValue = "1") final Integer pageNo
    ) {

        if (!authorizationService.isAuthorized("lab_service","provider_list", "view_only")
                && !authorizationService.isAuthorized("lab_service","order_list", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return halolabHubService.searchLabProvider(names, pageNo, perPage);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','provider_view', 'view_only')")
    @GetMapping("/{providerId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    Provider getLabProvider(@PathVariable String providerId) {
        return halolabHubService.getLabProvider(providerId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CODI)
    Provider createLabProvider(@RequestBody Provider provider) {
        return halolabHubService.createLabProvider(provider);
    }

    @PreAuthorize("@authorizationService.isAuthorized('lab_service','provider_edit', 'restricted_write')")
    @PutMapping("/{providerId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    Provider updateLabProvider(@PathVariable String providerId, @RequestBody Provider labProvider) {
        return halolabHubService.updateLabProvider(providerId, labProvider);
    }
}
