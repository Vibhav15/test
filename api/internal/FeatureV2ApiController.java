package com.halodoc.batavia.controller.api.internal;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.internal.FeatureV2;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.internal.BaliAccessControlService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

@RequestMapping ("api/v2/features")
@RestController
public class FeatureV2ApiController {
    @Autowired
    private BaliAccessControlService baliAccessControlService;

    @GetMapping
    @PreAuthorize ("@authorizationService.isAuthorized('administration','user_list', 'view_only')")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<FeatureV2> list(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = true, name = "paginated", defaultValue = "false") Boolean paginated) throws URISyntaxException {
        if (paginated) {
            return baliAccessControlService.searchFeaturesPaginated(pageNo, perPage);
        } else {
            return baliAccessControlService.fetchAllFeaturesAsPaginatedResult();
        }
    }
}
