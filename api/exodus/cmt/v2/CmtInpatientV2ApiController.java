package com.halodoc.batavia.controller.api.exodus.cmt.v2;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.cmt.CmtSearch;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.cmt.CMTService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v2/case-monitoring/inpatient/case")
public class CmtInpatientV2ApiController extends HalodocBaseApiController {
    @Autowired
    private CMTService cmtService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_list', 'view_only')")
    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<CmtSearch> searchCMTInpatientList(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "member_external_id", defaultValue = DEFAULT_STRING) String memberExternalId,
            @RequestParam (required = false, name = "primary_diagnosis_id", defaultValue = DEFAULT_STRING) String primaryDiagnosisId,
            @RequestParam (required = false, name = "case_number", defaultValue = DEFAULT_STRING) String caseNumber,
            @RequestParam (required = false, name = "provider_location_id", defaultValue = DEFAULT_STRING) String providerLocationId,
            @RequestParam (required = false, name = "process_status", defaultValue = DEFAULT_STRING) String processStatus,
            @RequestParam (required = false, name = "admission_start_date", defaultValue = DEFAULT_STRING) String startDate,
            @RequestParam (required = false, name = "admission_end_date", defaultValue = DEFAULT_STRING) String endDate,
            @RequestParam (required = false, name = "member_name", defaultValue = DEFAULT_STRING) String memberName,
            @RequestParam (required = false, name = "investigation_status", defaultValue = DEFAULT_STRING) String investigationStatus,
            @RequestParam (required = false, name = "superseeding_status", defaultValue = DEFAULT_STRING) String superseedingStatus,
            @RequestParam (required = false, defaultValue = DEFAULT_STRING) String statuses,
            @RequestParam (required = false, name = "sorting_column", defaultValue = "") String sortingColumns,
            @RequestParam (required = false, name = "sorting_order", defaultValue = "DESC") String sortingOrders,
            @RequestParam (required = false, name = "source", defaultValue = "CMT") String source) throws URISyntaxException {
        return cmtService.searchCMTInpatientListV2(pageNo, perPage, memberExternalId, caseNumber, providerLocationId, processStatus, startDate,
                endDate, investigationStatus, superseedingStatus, statuses, sortingColumns, sortingOrders, source, primaryDiagnosisId, memberName);
    }
}
