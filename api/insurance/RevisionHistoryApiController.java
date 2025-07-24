package com.halodoc.batavia.controller.api.insurance;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.misool.PaginatedRevisionReasonListOfEntity;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;
import java.util.Map;

@RequestMapping ("api/v1/entity")
@RestController
@Slf4j
public class RevisionHistoryApiController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @GetMapping ("{entityType}/{entityExternalId}/revisions")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedRevisionReasonListOfEntity getRevisionHistoryListForEntity(@PathVariable String entityType, @PathVariable String entityExternalId,
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage) throws URISyntaxException {
        return misoolCatalogService.getRevisionHistoryListForEntity(pageNo, perPage, entityType, entityExternalId);
    }

    @GetMapping ("{entityType}/{entityExternalId}/revision")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    Map getLatestRevisionHistoryForEntity(@PathVariable String entityType, @PathVariable String entityExternalId
            ) throws URISyntaxException {
        return misoolCatalogService.getLatestRevisionHistoryForEntity(entityType, entityExternalId);
    }
}
