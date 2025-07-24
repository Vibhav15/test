package com.halodoc.batavia.controller.api.cms;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.constant.CommonConstants;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.diagnosis.DiagnosisCode;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.DiagnosisService;
import com.halodoc.batavia.service.cms.TernateArticleService;
import com.halodoc.batavia.service.cms.TernateDiscoveryService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/cms/articles")
@RestController
@Slf4j
public class TernateArticleController extends HalodocBaseApiController {

    @Autowired
    TernateArticleService ternateArticleService;

    @Autowired
    TernateDiscoveryService ternateDiscoveryService;

    @Autowired
    DiagnosisService diagnosisService;


    @GetMapping
    @PreAuthorize("@authorizationService.isAuthorized('article_management','article_list', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map<String, Object>> getArticles(@RequestParam(required = false, name = "statuses", defaultValue = CommonConstants.ARTICLE_STATUS_PUBLISHED) String statuses,
                                                               @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(required = false, name = "per_page", defaultValue = "" + CommonConstants.ARTICLE_DEFAULT_LIMIT_PERPAGE) Integer perPage,
                                                               @RequestParam(required = false, name = "channel") String channel,
                                                               @RequestParam(required = false, name = "type") String type,
                                                               @RequestParam(required = false, name = "search_text") String search_text,
                                                               @RequestParam(required = false, name = "category_id") String category_id,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                               @RequestParam(required = false, name = "sort_by") String sortBy,
                                                               @RequestParam(required = false, name = "sort_order", defaultValue = "asc") String sortOrder
    )
            throws URISyntaxException {
        if(!StringUtils.isEmpty(search_text)){
            return ternateDiscoveryService.searchArticles(statuses,pageNo,perPage,channel,search_text,category_id,startDate,endDate);
        }
        return ternateArticleService.getArticles(statuses, pageNo, perPage, channel, type, category_id, startDate, endDate, sortBy, sortOrder);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('article_management','article_list', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Map getArticleById(@PathVariable(value = "id") String id) throws URISyntaxException {
        return ternateArticleService.getArticleById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('article_management','article_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateArticle(@RequestBody Map articleRequest, @PathVariable String id) throws URISyntaxException {
        return ternateArticleService.updateArticle(articleRequest, id);
    }

    @PutMapping("/sequence")
    @PreAuthorize("@authorizationService.isAuthorized('article_management','article_list', 'view_only')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateArticleSequence(@RequestBody List<Map> articleRequest) throws URISyntaxException {
        ternateArticleService.updateArticleSequence(articleRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.isAuthorized('article_management','article_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void deleteArticle(@PathVariable String id) throws URISyntaxException {
        ternateArticleService.deleteArticle(id);
    }

    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("@authorizationService.isAuthorized('article_management','article_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateArticleStatus(@PathVariable String id, @PathVariable String status) throws URISyntaxException {
        return ternateArticleService.updateArticleStatus(id, status);
    }

    @GetMapping("/recommended/diagnosis-codes")
    @PreAuthorize("@authorizationService.isAuthorized('article_management','article_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    PaginatedResult<DiagnosisCode> getRecommendedDiagnosisCodes(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "search_query", defaultValue = "") String searchQuery) throws URISyntaxException {
        return diagnosisService.getRecommendedDiagnosisCodes(pageNo, perPage, searchQuery);
    }
}
