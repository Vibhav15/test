package com.halodoc.batavia.controller.api.cms;

import java.net.URISyntaxException;
import java.util.List;
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
import com.halodoc.batavia.entity.cms.section.Section;
import com.halodoc.batavia.entity.cms.section.SectionContent;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.cms.SectionService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/section")
@RestController
@Slf4j
public class SectionController extends HalodocBaseApiController {
    @Autowired
    SectionService sectionService;

    @Autowired
    AuthorizationService authorizationService;

    @GetMapping("/sections")
    @PreAuthorize ("@authorizationService.isAuthorized('cms','cms_privacy_policy_view', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    PaginatedResult<Section> getSections(@RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo, @RequestParam(required = false, name="statuses", defaultValue = "active") String statuses,@RequestParam( name="type") String type) throws URISyntaxException{
        return sectionService.getSections(pageNo,perPage,statuses,type);
    }

    @GetMapping ("/{sectionId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    Section getSection(@PathVariable String sectionId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("marketing","cms_promo_view", "view_only")
                && !authorizationService.isAuthorized("cms","cms_terms_conditions_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return sectionService.getSection(sectionId);
    }

    @PutMapping ("/{sectionId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    Section updateSection(@PathVariable String sectionId, @RequestBody Section section)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("marketing","cms_promo_edit", "restricted_write")
                && !authorizationService.isAuthorized("cms","cms_terms_conditions_edit", "restricted_write")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return sectionService.updateSection(sectionId, section);
    }

    @GetMapping("/{sectionId}/contents")
    @PreAuthorize ("@authorizationService.isAuthorized('cms','cms_privacy_policy_view', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    PaginatedResult<SectionContent> getSectionContentsBySectionId(@PathVariable @NotNull String sectionId,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name="statuses", defaultValue = "active") String statuses) throws URISyntaxException{
        return sectionService.getSectionContentsBySectionId(sectionId,pageNo,perPage,statuses);
    }

    @GetMapping("/section-contents/{slug}")
    @PreAuthorize ("@authorizationService.isAuthorized('cms','cms_privacy_policy_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    SectionContent getSectionContentBySlug(@PathVariable @NotNull String slug){
        return sectionService.getSectionContentBySlug(slug);
    }

    @PostMapping("/{sectionId}/contents")
    @PreAuthorize ("@authorizationService.isAuthorized('cms','cms_privacy_policy_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    void createSectionContent(@PathVariable @NotNull String sectionId, @RequestBody List<SectionContent> createSectionContentRequest){
        sectionService.createSectionContent(sectionId,createSectionContentRequest);
    }
}
