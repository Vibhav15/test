package com.halodoc.batavia.controller.api.insurance;

import com.halodoc.batavia.entity.misool.catalog.BenefitConditionTemplate;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RequestMapping("api/v1/insurance/package-condition-template")
@RestController
@Slf4j
public class PackageConditionTemplateApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping("/list")
    Map getAllPackageConditionTemplates(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                        @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                        @RequestParam (required = false, name = "name") String name,
                                        @RequestParam (required = false, name = "package_ids") String packageIds) throws URISyntaxException {
        return misoolCatalogService.getAllPackageConditionTemplates(pageNo, perPage, name, packageIds);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping ("/{templateId}")
    Map getPackageConditionTemplate(@PathVariable String templateId) throws URISyntaxException {
        return misoolCatalogService.getPackageConditionTemplate(templateId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'restricted_write')")
    @PostMapping ("/add")
    Map<String, Object> savePackageConditionTemplate(@RequestBody Map packageConditionTemplate) throws URISyntaxException {
        return misoolCatalogService.savePackageConditionTemplate(packageConditionTemplate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'restricted_write')")
    @PutMapping ("/edit/{templateId}")
    Map updatePackageConditionTemplate(@PathVariable String templateId,
                                       @RequestBody Map packageConditionTemplate) throws URISyntaxException {
        return misoolCatalogService.updatePackageConditionTemplate(templateId, packageConditionTemplate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','package_condition', 'view_only')")
    @GetMapping ("/{templateId}/linked-package-conditions")
    Map getLinkedPackageConditions(@PathVariable String templateId,
                                   @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                   @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return misoolCatalogService.getLinkedPackageConditionTemplates(pageNo, perPage, templateId);
    }
}
