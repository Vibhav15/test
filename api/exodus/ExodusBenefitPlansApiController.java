package com.halodoc.batavia.controller.api.exodus;

import java.net.URISyntaxException;
import java.util.List;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/plans")
public class ExodusBenefitPlansApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @GetMapping ("/{planExternalId}/benefits")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<List> benefitPlan(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "benefit_code", defaultValue = "") String benefitCode,
            @RequestParam (required = false, name = "benefit_name", defaultValue = "") String benefitName,
            @RequestParam (required = false, name = "insurance_provider_id", defaultValue = "") String insProviderID,
            @PathVariable String planExternalId) throws URISyntaxException {
        return exodusMisoolCatalogService.getPlanBenefits(pageNo, perPage, benefitName, benefitCode, planExternalId, insProviderID);
    }
}
