package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.misool.catalog.BenefitConditionTemplate;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance/benefit-condition-template")
@RestController
@Slf4j
public class BenefitConditionsTemplateApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_conditions_template_list', 'view_only')")
    @GetMapping ("/list")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    Map getAllBenefitConditionTemplates(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "name") String name) throws URISyntaxException {
        return misoolCatalogService.getAllBenefitConditonTemplates(pageNo, perPage, name);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_conditions_template_view', 'view_only')")
    @GetMapping ("/{templateId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getBenefitConditionTemplate(@PathVariable String templateId) throws URISyntaxException {
        return misoolCatalogService.getBenefitConditionTemplate(templateId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_conditions_template_add', 'restricted_write')")
    @PostMapping ("/add")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    Map<String, Object> saveBenefitConditionTemplate(@RequestBody BenefitConditionTemplate benefitConditionTemplate) throws URISyntaxException {
        return misoolCatalogService.saveBenefitConditionTemplate(benefitConditionTemplate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_conditions_template_edit', 'restricted_write')")
    @PutMapping ("/edit/{templateId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    BenefitConditionTemplate updateBenefitConditionTemplate(@PathVariable String templateId,
            @RequestBody BenefitConditionTemplate benefitConditionTemplate) throws URISyntaxException {
        return misoolCatalogService.updateBenefitConditionTemplate(templateId, benefitConditionTemplate);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_conditions_template_list', 'view_only')")
    @GetMapping ("/linked-benefits")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    Map getLinkedTemplates(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "template_id") String templateId) throws URISyntaxException {
        return misoolCatalogService.getLinkedTemplates(pageNo, perPage, templateId);
    }

}
