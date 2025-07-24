package com.halodoc.batavia.controller.api.remotelogs;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.customer.DeviceInfo;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.device.DeviceService;
import com.halodoc.batavia.service.remotelogs.DiagnosticLogsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RequestMapping("api/v1/diagnostics/logs")
@RestController
@Slf4j
public class DiagnosticLogsController extends HalodocBaseApiController {
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private DiagnosticLogsService diagnosticLogsService;

    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Map<String, Object>> getDiagnosticLogRequests(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                                         @RequestParam(required = false, name = "entity_id", defaultValue = "") String entityId,
                                                                         @RequestParam(required = false, name = "entity_type", defaultValue = "all") String entityType) throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer","customer_view", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery","merchant_location_view", "view_only")
                && !authorizationService.isAuthorized("contact_doctor","doctor_view", "view_only")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return diagnosticLogsService.getDiagnosticLogRequests(pageNo, perPage, entityType, entityId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    public void postDiagnosticLogRequest( @RequestBody Map<String, Object> diagnosticRequest) {
        diagnosticLogsService.postDiagnosticLogRequest(diagnosticRequest);
    }
}
