package com.halodoc.batavia.controller.api.medisend;

import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.medisend.*;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.medisend.LoyaltyPointManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping ("api/v1/medisend/point-program")
@RestController
public class PointProgramController {
    @Autowired
    LoyaltyPointManagementService loyaltyPointManagementService;

    @GetMapping ("/{programId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public PointProgram getPointProgramById(@PathVariable String programId) throws URISyntaxException {
        return loyaltyPointManagementService.getPointProgramById(programId);
    }

    @GetMapping ("/search")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<PointProgram> getPointPrograms(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
            @RequestParam (required = false, name = "title") String title,
            @RequestParam (required = false, name = "sort_by", defaultValue = "end_date") String sortBy,
            @RequestParam (required = false, name = "sort_order", defaultValue = "desc") String sortOrder) throws URISyntaxException {
        return loyaltyPointManagementService.getPointPrograms(pageNo, perPage, title, sortBy, sortOrder);
    }

    @GetMapping("/{programId}/program-merchants")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map getProgramMerchants(@PathVariable String programId,
                                                    @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                    @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                    @RequestParam (required = false, name = "sort_by", defaultValue = "id") String sortBy,
                                                    @RequestParam (required = false, name = "sort_order", defaultValue = "desc") String sortOrder,
                                                    @RequestParam (required = false, name = "entity_type", defaultValue = "") String entityType,
                                                    @RequestParam (required = false, name = "entity_id", defaultValue = "") String entityId,
                                                    @RequestParam (required = false, name = "status", defaultValue = "ACTIVE") String status) throws URISyntaxException {
        return loyaltyPointManagementService.getProgramMerchants(programId, perPage, pageNo, sortBy, sortOrder, entityType, entityId, status);
    }

    @PostMapping ()
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public PointProgram createPointPrograms(@RequestBody PointProgram pointProgram) throws URISyntaxException {
        return loyaltyPointManagementService.createPointProgram(pointProgram);
    }

    @PutMapping ("/{programId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public PointProgram editPointPrograms(@PathVariable String programId, @RequestBody PointProgram pointProgram) throws URISyntaxException {
        return loyaltyPointManagementService.editPointProgram(programId, pointProgram);
    }

    @PostMapping ("/{programId}/conditions")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public PointProgramCondition createPointProgramCondition(@PathVariable String programId, @RequestBody PointProgramCondition pointProgramCondition)
            throws URISyntaxException {
        return loyaltyPointManagementService.createPointProgramCondition(programId, pointProgramCondition);
    }

    @PostMapping("/{programId}/add-merchants")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addProgramMerchantCategory(@PathVariable String programId, @RequestBody List<PointProgramEntity> requestBody) throws URISyntaxException {
        loyaltyPointManagementService.addProgramMerchantCategory(programId, requestBody);
    }

    @PutMapping ("/{programId}/conditions/{conditionId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public PointProgramCondition updatePointProgramCondition(@PathVariable String programId, @PathVariable String conditionId,
            @RequestBody PointProgramCondition pointProgramCondition) throws URISyntaxException {
        return loyaltyPointManagementService.updatePointProgramCondition(programId, conditionId, pointProgramCondition);
    }

    @PutMapping("/{programId}/condition-criteria")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateProductIDs(@PathVariable String programId, @RequestBody PointProductRequest pointProductRequest) throws URISyntaxException {
        loyaltyPointManagementService.updateProductIDs(programId, pointProductRequest);
    }

    @PutMapping("/{programId}/condition-criteria/principal")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateProductIDsV2(@PathVariable String programId, @RequestBody PrincipalPointProductRequest principalPointProductRequest) throws URISyntaxException {
        loyaltyPointManagementService.updatePrincipalProductIDs(programId, principalPointProductRequest);
    }

    @DeleteMapping ("/{programId}/conditions/{conditionId}")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void deletePointProgramCondition(@PathVariable String programId, @PathVariable String conditionId) throws URISyntaxException {
        loyaltyPointManagementService.deletePointProgramCondition(programId, conditionId);
    }

    @PutMapping ("/{programId}/general-conditions")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public PointProgramGeneralCondition updatePointProgramGeneralCondition(@PathVariable String programId,
            @RequestBody PointProgramGeneralCondition pointProgramGeneralCondition) throws URISyntaxException {
        return loyaltyPointManagementService.updatePointProgramGeneralCondition(programId, pointProgramGeneralCondition);
    }

    @PutMapping("/segment-status-update")
    @PreAuthorize ("@authorizationService.isAuthorized('pharmacy_delivery','point_program_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateSegmentStatus(@RequestBody Map requestBody) throws URISyntaxException {
        loyaltyPointManagementService.updateSegmentStatus(requestBody);
    }
}
