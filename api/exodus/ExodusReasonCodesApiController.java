package com.halodoc.batavia.controller.api.exodus;

import java.net.URISyntaxException;
import java.util.Map;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTpaClaimReasonCodeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/reason-codes")
public class ExodusReasonCodesApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusTpaClaimReasonCodeService exodusTpaClaimReasonCodeService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> searchReasonCodes(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (name = "category") String reasonCategory, @RequestParam (name = "description_bahasa") String reasonDescription)
            throws URISyntaxException {
        return exodusTpaClaimReasonCodeService.searchReasonCodes(pageNo, perPage, reasonCategory, reasonDescription);
    }
}
