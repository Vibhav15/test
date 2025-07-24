package com.halodoc.batavia.controller.api.cms;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
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
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.cms.banner.BannerSegment;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BannerConfiguration;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.cms.TernateCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/cms/banners")
@RestController
@Slf4j
public class TernateBannerController extends HalodocBaseApiController {
    @Autowired
    TernateCatalogService catalogService;

    @Autowired
    private AuthorizationService authorizationService;

    @PutMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map<String, Object>> paginatedArticles(@RequestBody Map searchRequest) throws URISyntaxException {
        return catalogService.searchBanners(searchRequest);
    }

    @GetMapping ("{id}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Map getBannerById(@PathVariable (value = "id") String id) throws URISyntaxException {

        if (!authorizationService.isAuthorized("marketing", "banner_edit", "restricted_write")
                && !authorizationService.isAuthorized("marketing", "banner_view", "view_only")) {

            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return catalogService.getBannerById(id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing', 'banner_add', 'restricted_write')")
    @PostMapping ("create")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map createBanner(@RequestBody Map searchRequest) throws URISyntaxException {
        return catalogService.createBanner(searchRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing', 'banner_edit', 'restricted_write')")
    @PutMapping ("update/{id}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateBanner(@PathVariable (value = "id") String id, @RequestBody Map updateRequest) throws URISyntaxException {
        return catalogService.updateBanner(updateRequest, id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','banner_view', 'view_only')")
    @GetMapping ("/segments")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<BannerSegment> getSegmentsByType(@RequestParam (value = "type", required = true) String type) {
        return catalogService.getSegmentByType(type);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','banner_view', 'view_only')")
    @GetMapping ("/segments/all")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Map getAllSegments() {
        return catalogService.getAllSegments();
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','banner_view', 'view_only')")
    @GetMapping ("/config")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public BannerConfiguration getBannerConfiguration() {
        return catalogService.getBannerConfiguration();
    }
}
