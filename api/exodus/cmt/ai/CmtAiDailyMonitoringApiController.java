package com.halodoc.batavia.controller.api.exodus.cmt.ai;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.cmt.CMTService;
import com.halodoc.batavia.service.exodus.cmt.CMTSphereService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/cases/ai-requests/daily-monitoring")
public class CmtAiDailyMonitoringApiController extends HalodocBaseApiController {
    @Autowired
    private CMTService cmtService;

    @Autowired
    private CMTSphereService cmtSphereService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @GetMapping ("/{caseId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<Map> getCMTAiDailyMonitoring(@PathVariable String caseId, 
        @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
        @RequestParam (required = false, name = "per_page", defaultValue = "5") Integer perPage) throws URISyntaxException {
        return cmtService.getCMTAiDailyMonitoring(caseId, perPage, pageNo);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PatchMapping ("/save/{requestId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public List<Map> saveAiResult(@PathVariable String requestId, @RequestBody List<Map> dailyMonitoring) throws URISyntaxException  {
        return cmtService.saveAiResult(requestId, dailyMonitoring);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @PostMapping ("{caseId}/daily-monitoring-documents")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createCMTAiDailyMonitoring(@RequestBody Map req) throws URISyntaxException {
        return cmtService.createCMTAiDailyMonitoring(req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @DeleteMapping ("{caseId}/day/{dayId}/delete")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteCMTAiDailyMonitoring(@PathVariable String caseId, @PathVariable String dayId) throws URISyntaxException {
        cmtService.deleteCMTAiDailyMonitoring(caseId, dayId);
    }

    @PutMapping ("/recommendations")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Map getDMRecommendations(@RequestBody Map requestBody) throws URISyntaxException {
        return cmtService.getDMRecommendations(requestBody);
    }

    @PostMapping ("/human-assessments")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    public Map createHumanRecommendations(@RequestBody Map requestBody) throws URISyntaxException {
        return cmtService.createHumanRecommendations(requestBody);
    }

    @GetMapping ("/ocr-status/{requestId}")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Map getAiRequestStatus(@PathVariable String requestId) throws URISyntaxException {
        return cmtSphereService.getAiRequestStatus(requestId);
    }
}

