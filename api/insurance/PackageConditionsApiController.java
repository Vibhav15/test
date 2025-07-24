package com.halodoc.batavia.controller.api.insurance;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.halolab.PaginatedHalolabHubSearchResult;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;


@RequestMapping("api/v1/insurance/package-conditions")
@RestController
@Slf4j
public class PackageConditionsApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping("/list")
    Map getPackageConditions(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                        @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                        @RequestParam (required = false, name = "name") String name,
                                        @RequestParam(required = false, name = "statuses", defaultValue = "1") String statuses) throws URISyntaxException {
        return misoolCatalogService.getPackageConditions(pageNo, perPage, name, statuses);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'restricted_write')")
    @PostMapping ("/add")
    Map<String, Object> addPackageCondition(@RequestBody Map packageCondition) throws URISyntaxException {
        return misoolCatalogService.addPackageCondition(packageCondition);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping ("/{externalId}")
    Map getPackageCondition(@PathVariable String externalId) throws URISyntaxException {
        return misoolCatalogService.getPackageCondition(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'restricted_write')")
    @PutMapping ("/edit/{externalId}")
    Map updatePackageCondition(@PathVariable String externalId,
                               @RequestBody Map packageCondition) throws URISyntaxException {
        return misoolCatalogService.updatePackageCondition(externalId, packageCondition);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping ("/multi-get")
    Map getLinkedPackageConditions( @RequestBody List<String> externalIds) throws URISyntaxException {
        return misoolCatalogService.multigetPackageCondition(externalIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping("/packages")
    Map searchHomecarePackages(@RequestParam(value = "search_text", required = false) String searchText,
                                                      @RequestParam(value = "per_page", defaultValue = "10", required = false) final String perPage,
                                                      @RequestParam(value = "page_no", defaultValue = "1", required = false) final String pageNumber
    ) {
        return misoolCatalogService.searchHomecarePackages(searchText, Integer.valueOf(pageNumber), Integer.valueOf(perPage) );
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping("/{externalId}/revisions")
    Map getPackageConditionReviseReasons(@PathVariable String externalId,
                                         @RequestParam(value = "per_page", defaultValue = "10", required = false) final String perPage,
                                         @RequestParam(value = "page_no", defaultValue = "1", required = false) final String pageNumber
    ) throws URISyntaxException {
        return misoolCatalogService.getPackageConditionReviseReasons(externalId, Integer.valueOf(pageNumber), Integer.valueOf(perPage) );
    }
}
