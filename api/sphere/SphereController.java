package com.halodoc.batavia.controller.api.sphere;


import com.halodoc.batavia.constant.SpherePromptTemplateType;
import com.halodoc.batavia.entity.sphere.PromptTemplatesMap;
import com.halodoc.batavia.entity.sphere.SpherePromptTemplate;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.sphere.SphereService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/v1/sphere")
@RestController
public class SphereController {
    @Autowired
    private SphereService sphereService;
    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/{prompt_template_name}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','prompt_management', 'view_only')")
    public Map getPromptTemplateById(@PathVariable ("prompt_template_name") @NotNull SpherePromptTemplateType promptTemplateName) {
        return sphereService.getPromptTemplateById(promptTemplateName);
    }

    @GetMapping("/templates")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','prompt_management', 'view_only')")
    public List<PromptTemplatesMap> getPromptTemplatesMap() {
        return sphereService.getPromptTemplatesMap();
    }

    @PutMapping("/{prompt_template_name}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor','prompt_management', 'restricted_write')")
    public void updatePromptTemplate(@PathVariable ("prompt_template_name") @NotNull SpherePromptTemplateType promptTemplateName, @RequestBody SpherePromptTemplate requestParams)  throws URISyntaxException {
        Map<String, Object> responseObj = new HashMap<>();

        sphereService.updatePromptTemplate(promptTemplateName, requestParams);
    }
}
