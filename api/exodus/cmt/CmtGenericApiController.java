package com.halodoc.batavia.controller.api.exodus.cmt;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.cmt.CMTService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/insurance/cmt/cases")
public class CmtGenericApiController extends HalodocBaseApiController {
    @Autowired
    private CMTService cmtService;

    @GetMapping ("{caseExternalId}/notifications")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getActiveCaseNotificationsFromHospital(@PathVariable String caseExternalId) throws URISyntaxException {
        return cmtService.getActiveCaseNotificationsFromHospital(caseExternalId);
    }

    @PatchMapping ("{caseExternalId}/notifications/{notificationExternalId}/view")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void markCaseNotificationAsViewed(@PathVariable String caseExternalId, @PathVariable String notificationExternalId) throws URISyntaxException {
        cmtService.markCaseNotificationAsViewed(caseExternalId, notificationExternalId);
    }

    @PatchMapping ("{caseExternalId}/notifications/view")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void viewAllCaseNotification(@PathVariable String caseExternalId) throws URISyntaxException {
        cmtService.markAllCaseNotificationAsViewed(caseExternalId);
    }
}
