package com.halodoc.batavia.controller.api.subscriptions;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.subscriptions.SchemeService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/subscriptions/schemes")
@RestController
@Slf4j
public class subscriptionsSchemesApiController extends HalodocBaseApiController {
    @Autowired
    SchemeService schemeService;

    @Autowired
    AuthorizationService authorizationService;

    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> getSchemes(@RequestParam (required = false, name = "statuses") String status,
            @RequestParam (required = false, name = "name", defaultValue = "") String name,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "sort_by", defaultValue = "created_at") String sortBy,
            @RequestParam (required = false, name = "sort_order", defaultValue = "desc") String sortOrder) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_view", "view_only") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.searchSchemes(status, name, perPage, pageNo, sortBy, sortOrder);
    }

    @GetMapping ("/{schemeId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Map getScheme(@PathVariable String schemeId) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_view", "view_only") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.getScheme(schemeId);
    }

    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map addScheme(@RequestBody Map scheme) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_add", "restricted_write") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.saveScheme(scheme);
    }

    @PutMapping ("/{schemeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateScheme(@PathVariable String schemeId, @RequestBody Map scheme) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_add", "restricted_write") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.updateScheme(schemeId, scheme);
    }

    @GetMapping ("/{schemeId}/benefits")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> getSchemeBenefits(@PathVariable String schemeId,
            @RequestParam (required = false, name = "statuses", defaultValue = "active") String status,
            @RequestParam (required = false, name = "perPage", defaultValue = "10") Integer per_page,
            @RequestParam (required = false, name = "pageNo", defaultValue = "1") Integer page_no) throws URISyntaxException {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_view", "view_only") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.getSchemeBenefits(schemeId, status, per_page, page_no);
    }

    @PostMapping ("/{schemeId}/benefits")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map addSchemeBenefits(@PathVariable String schemeId, @RequestBody Map benefit) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_add", "restricted_write") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_add", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.addSchemeBenefit(schemeId, benefit);
    }

    @PutMapping ("benefits/conditions/{conditionId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateSchemeBenefitCondition(@PathVariable String conditionId, @RequestBody Map request) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_add", "restricted_write") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.updateSchemeBenefitCondition(conditionId, request);
    }

    @PutMapping ("benefits/{benefitId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public Map updateSchemeBenefit(@PathVariable String benefitId, @RequestBody Map schemeBenefit) {
        if (!authorizationService.isAuthorized("pharmacy_delivery", "product_add", "restricted_write") && !authorizationService.isAuthorized(
                "subscriptions", "subscriptions_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return schemeService.updateSchemeBenefit(benefitId, schemeBenefit);
    }

    @GetMapping ("/conditions")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public List<Map> searchConditions(@RequestParam (required = false, name = "name", defaultValue = "") String name,
            @RequestParam (required = false, name = "perPage", defaultValue = "10") Integer per_page,
            @RequestParam (required = false, name = "pageNo", defaultValue = "1") Integer page_no) throws URISyntaxException {

        return schemeService.searchConditions(name, per_page, page_no);
    }

}
