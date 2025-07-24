package com.halodoc.batavia.controller.api.exodus.insurance;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.misool.catalog.ExodusInsuranceSharedBenefit;
import com.halodoc.batavia.entity.exodus.misool.catalog.SharedBenefitRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/exodus/insurance/products")
public class ExodusSharedBenefitController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping("/{productId}/plans/{planId}/shared-benefits")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusInsuranceSharedBenefit> getSharedBenefit(@PathVariable String productId, @PathVariable String planId,
                                                                   @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                   @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                                   @RequestParam (required = false, name = "client_benefit_code") String clientBenefitCode) throws URISyntaxException {
        return exodusMisoolCatalogService.getSharedBenefit(productId, planId, pageNo, perPage, clientBenefitCode);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping ("/{productId}/plans/{planId}/shared-benefits")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createSharedBenefit(@PathVariable String productId, @PathVariable String planId, @RequestBody SharedBenefitRequest request)
            throws URISyntaxException {
        return exodusMisoolCatalogService.createSharedBenefit(productId, planId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{productId}/plans/{planId}/shared-benefits/{sharedBenefitId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateSharedBenefit(@PathVariable String productId, @PathVariable String planId, @PathVariable String sharedBenefitId,
                            @RequestBody SharedBenefitRequest request) throws URISyntaxException {
        return exodusMisoolCatalogService.updateSharedBenefit(productId, planId, sharedBenefitId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @DeleteMapping ("/{productId}/plans/{planId}/shared-benefits/{sharedBenefitId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteSharedBenefit(@PathVariable String productId,
                             @PathVariable String planId, @PathVariable String sharedBenefitId) throws URISyntaxException {
        exodusMisoolCatalogService.deleteSharedBenefit(productId, planId, sharedBenefitId);
    }
}
