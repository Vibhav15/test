package com.halodoc.batavia.controller.api.exodus.cmt;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.cmt.CmtOpItemEligibilityRequestService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/cmt/op/cases/{caseId}/item-eligibility-requests")
public class CmtOutpatientItemsEligibilityRequestApiController extends HalodocBaseApiController {
    @Autowired
    private CmtOpItemEligibilityRequestService cmtOpItemEligibilityRequestService;

    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createItemEligibilityRequest(@PathVariable String caseId, @RequestBody Map request) throws URISyntaxException {
        return cmtOpItemEligibilityRequestService.createItemEligibilityRequest(caseId, request);
    }

    @GetMapping ("/{requestId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getItemEligibilityRequest(@PathVariable String caseId, @PathVariable String requestId) throws URISyntaxException {
        return cmtOpItemEligibilityRequestService.getItemEligibilityRequest(caseId, requestId);
    }

    @PutMapping ("/{requestId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateItemEligibilityRequest(@PathVariable String caseId, @PathVariable String requestId, @RequestBody Map request)
            throws URISyntaxException {
        return cmtOpItemEligibilityRequestService.updateItemEligibilityRequest(caseId, requestId, request);
    }

    @PatchMapping ("/{requestId}/view")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map markItemEligibilityRequestAsViewed(@PathVariable String caseId, @PathVariable String requestId) throws URISyntaxException {
        return cmtOpItemEligibilityRequestService.markItemEligibilityRequestAsViewed(caseId, requestId);
    }

    @PatchMapping ("/{requestId}/assign")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map assignOpenOrInProgressItemEligibilityRequest(@PathVariable String caseId, @PathVariable String requestId) throws URISyntaxException {
        return cmtOpItemEligibilityRequestService.assignOpenOrInProgressItemEligibilityRequest(caseId, requestId);
    }

    @PatchMapping ("/assign-upcoming")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map assignUpcomingItemEligibilityRequest(@PathVariable String caseId) throws URISyntaxException {
        return cmtOpItemEligibilityRequestService.assignUpcomingItemEligibilityRequest(caseId);
    }
}
