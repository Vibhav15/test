package com.halodoc.batavia.controller.api.insurance.misool_v1;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.Provider;
import com.halodoc.batavia.entity.misool.ProviderBenefit;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool.MisoolService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/insurance")
@RestController
@Slf4j
public class BenefitProviderApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolService misoolService;

    // Provider Start
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_provider_list', 'view_only')")
    @GetMapping ("/benefit-providers")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Provider> getProviders(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "status") String status,
            @RequestParam (required = false, name = "type") String type, @RequestParam (required = false, name = "licences") String licences,
            @RequestParam (required = false, name = "komodo_ids") String komodoIds) throws URISyntaxException {
        return misoolService.getProviders(name, pageNo, perPage, status, type, licences, komodoIds);
    }

    @GetMapping ("/benefit-providers/{benefitProviderId}/benefits")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','benefit_provider_list', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ProviderBenefit> getUnlinkedBenefitsByProvider(@PathVariable String benefitProviderId,
            @RequestParam (name = "page_no", required = false) String pageNo, @RequestParam (name = "per_page", required = false) String perPage)
            throws URISyntaxException {
        List<String> benefitProviderIds = new ArrayList<>();
        benefitProviderIds.add(benefitProviderId);
        return misoolService.getUnlinkedBenefitsByProviders(benefitProviderIds, pageNo, perPage);
    }
}
