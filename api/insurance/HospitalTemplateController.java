package com.halodoc.batavia.controller.api.insurance;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.HospitalTemplate;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RequestMapping("api/v1/hospital-templates")
@RestController
@Slf4j
public class HospitalTemplateController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','hospital_template_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<HospitalTemplate> getHospitalTemplates(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                           @RequestParam(required = false, name = "search_term", defaultValue = "") String searchTerm) throws URISyntaxException {

        return misoolCatalogService.getHospitalTemplates(pageNo, perPage, searchTerm);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','hospital_template_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    void addHospitalTemplate(@RequestBody Map hospitalTemplate) throws URISyntaxException {

        misoolCatalogService.addHospitalTemplate(hospitalTemplate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','hospital_template_edit', 'restricted_write')")
    @PatchMapping("{externalId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    HospitalTemplate updateHospitalTemplate(@PathVariable String externalId, @RequestBody Map hospitalTemplate) throws URISyntaxException {

        return misoolCatalogService.updateHospitalTemplate(externalId, hospitalTemplate);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','hospital_template_list', 'view_only')")
    @PutMapping("{externalId}/download")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    Map downloadHospitalTemplate(@PathVariable String externalId) throws URISyntaxException {

        return misoolCatalogService.downloadHospitalTemplate(externalId);
    }
}
