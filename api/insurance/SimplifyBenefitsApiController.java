package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import java.util.Map;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.entity.misool.catalog.CoveredHospitalsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.BenefitTemplate;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/{providerId}/templates")
@RestController
@Slf4j
public class SimplifyBenefitsApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("section/{sectionType}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<BenefitTemplate> getTemplates(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "template_type") String templateType,
            @RequestParam (required = false, name = "title") String title, @RequestParam (required = false, name = "lang") String lang,
            @PathVariable (name = "sectionType") String sectionType, @PathVariable (name = "providerId") String providerId)
            throws URISyntaxException {
        return misoolCatalogService.getTemplates(pageNo, perPage, providerId, sectionType, templateType, title, lang);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("{externalId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    BenefitTemplate getTemplate(@PathVariable String externalId) {
        return misoolCatalogService.getTemplate(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Object saveTemplate(@RequestBody Object templateRequest) {
        return misoolCatalogService.createTemplate(templateRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("{externalId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateTemplate(@PathVariable String externalId, @RequestBody Object instanceLimit) throws URISyntaxException {
        misoolCatalogService.updateTemplate(externalId, instanceLimit);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @PutMapping ("search/{sectionType}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult searchTemplates(@PathVariable (name = "sectionType") String sectionType, @RequestBody Object searchRequest,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage) throws URISyntaxException {
        return misoolCatalogService.searchTemplates(searchRequest, sectionType, perPage, pageNo);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @DeleteMapping ("{externalId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteTemplate(@PathVariable String externalId) {
        misoolCatalogService.deleteTemplateOrContent(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @GetMapping ("insurance-providers/mappings")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<CoveredHospitalsTemplate> getCoveredHospitalsContent(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                         @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                                         @RequestParam (required = false, name = "title") String title,
                                                                         @PathVariable (name = "providerId") String providerId)
            throws URISyntaxException {
        return misoolCatalogService.getCoveredHospitalsContent(pageNo, perPage, providerId, title);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_list', 'view_only')")
    @PutMapping("{externalId}/download")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map downloadCoveredHospitals(@PathVariable String externalId) throws URISyntaxException {
        return misoolCatalogService.downloadHospitalTemplate(externalId);
    }

}
