package com.halodoc.batavia.controller.api.cms;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.constant.CommonConstants;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.cms.EmailCollection;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.cms.EmailCollectionService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/cms/email-collection")
@RestController
@Slf4j
public class EmailCollectionController extends HalodocBaseApiController {

    @Autowired
    EmailCollectionService emailCollectionService;

    @GetMapping
    @PreAuthorize("@authorizationService.isAuthorized('article_management','email_collection_list', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<EmailCollection> searchEmails(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(required = false, name = "per_page", defaultValue = "" + CommonConstants.ARTICLE_DEFAULT_LIMIT_PERPAGE) Integer perPage
    )
            throws URISyntaxException {
        return emailCollectionService.searchEmails(pageNo, perPage);
    }

    @GetMapping("/export")
    @PreAuthorize("@authorizationService.isAuthorized('article_management','email_collection_export', 'view_only')")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.SRE)
    public String exportEmails() throws HalodocWebException {
        return emailCollectionService.exportEmails();
    }
}
