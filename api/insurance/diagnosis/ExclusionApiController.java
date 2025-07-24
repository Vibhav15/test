package com.halodoc.batavia.controller.api.insurance.diagnosis;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.diagnosis.LinkedExclusion;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/exclusions")
@RestController
@Slf4j
public class ExclusionApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("{entityType}/{entityId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    LinkedExclusion getExclusionLink(@PathVariable String entityType, @PathVariable String entityId) throws URISyntaxException {

        return misoolCatalogService.getLinkedExclusion(entityType, entityId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    LinkedExclusion saveExclusionLink(@RequestBody LinkedExclusion linkedExclusion) throws URISyntaxException {
        return misoolCatalogService.saveExclusionLink(linkedExclusion);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("{exclusionId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void editExclusion(@PathVariable String exclusionId, @RequestBody LinkedExclusion linkedExclusion) throws URISyntaxException {
        misoolCatalogService.updateExclusionLink(exclusionId, linkedExclusion);
    }


}
