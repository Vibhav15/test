package com.halodoc.batavia.controller.api.insurance;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.catalog.InternalBenefitCode;
import com.halodoc.batavia.entity.misool.catalog.InternalBenefitCodeResponse;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;

@RequestMapping ("api/v1/insurance/internal-benefit-codes")
@RestController
@Slf4j
public class InternalBenefitCodesApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @Autowired
    private AuthorizationService authorizationService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','internal_benefit_code_list','view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<InternalBenefitCodeResponse> getInternalBenefitCodeList(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "search_text") String search_text) throws URISyntaxException {

        return misoolCatalogService.getInternalBenefitCodesList(search_text, pageNo, perPage);
    }

    @GetMapping ("/{internalBenefitCodeId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InternalBenefitCodeResponse getInternalBenefitCode(@PathVariable String internalBenefitCodeId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "internal_benefit_code_edit", "restricted_write") && !authorizationService
                .isAuthorized("insurance", "internal_benefit_code_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();

        }
        return misoolCatalogService.getInternalBenefitCode(internalBenefitCodeId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','internal_benefit_code_add','restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    InternalBenefitCodeResponse createInternalBenefitCode(@RequestBody InternalBenefitCode req) {
        return misoolCatalogService.createInternalBenefitCode(req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','internal_benefit_code_edit','restricted_write')")
    @PutMapping ("/{internalBenefitCodeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateInternalBenefitCode(@PathVariable String internalBenefitCodeId, @RequestBody InternalBenefitCode req) throws URISyntaxException {
        misoolCatalogService.updateInternalBenefitCode(internalBenefitCodeId, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','internal_benefit_code_edit','restricted_write')")
    @DeleteMapping ("/{internalBenefitCodeId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteInternalBenefitCode(@PathVariable String internalBenefitCodeId) {

        misoolCatalogService.deleteInternalBenefitCode(internalBenefitCodeId);
    }
}
