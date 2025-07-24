package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.procedure.*;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.MedicalProcedureService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("api/v1/medical-procedures")
@RestController
public class MedicalProcedureApiController extends HalodocBaseApiController {

    @Autowired
    private MedicalProcedureService medicalProcedureService;

    @Autowired
    private AuthorizationService authorizationService;

    @PutMapping("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<MedicalProcedureListItem> getMedicalProcedureList(@RequestBody MedicalProcedureRequestParam medicalProcedureRequestParam) throws URISyntaxException {
        return medicalProcedureService.getMedicalProcedureList(medicalProcedureRequestParam);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_view', 'view_only')")
    @GetMapping("/{procedureId}/provider-locations")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Map> getProcedureProviderLocationList(@PathVariable String procedureId, @RequestParam(defaultValue = "10", required = false) Integer per_page,
                                                        @RequestParam(defaultValue = "1", required = false) Integer page_no,
                                                        @RequestParam(name = "name", required = false) String name,
                                                        @RequestParam(name = "status", required = false) String status) throws URISyntaxException {
        return medicalProcedureService.getProcedureProviderLocations(procedureId, page_no, per_page, status, name);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_view', 'view_only')")
    @GetMapping("/provider/{providerId}/location/{locationId}/provider-location-procedures")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Map> getProviderLocationProcedures(@PathVariable String providerId,
                                                              @PathVariable String locationId,
                                                              @RequestParam(defaultValue = "10", required = false) Integer per_page,
                                                                 @RequestParam(defaultValue = "1", required = false) Integer page_no,
                                                                 @RequestParam(name = "name", required = false) String name,
                                                                 @RequestParam(name = "status", required = false) String status) throws URISyntaxException {
        return medicalProcedureService.getProviderLocationProcedures(providerId, locationId, page_no, per_page, status, name);
    }

    @GetMapping("/{procedureId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public MedicalProcedure getMedicalProcedure(@PathVariable String procedureId) throws URISyntaxException {

        if (!authorizationService.isAuthorized("hospitals_management", "procedure_view", "view_only") && !authorizationService.isAuthorized(
                "hospitals_management", "hospital_appointment_list", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return medicalProcedureService.getProcedure(procedureId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_add', 'restricted_write')")
    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public MedicalProcedure createMedicalProcedure(@RequestBody MedicalProcedure procedure) {
        return  medicalProcedureService.createProcedure(procedure);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_edit', 'restricted_write')")
    @PatchMapping("/{procedureId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void updateMedicalProcedure(@PathVariable String procedureId, @RequestBody MedicalProcedure procedure) {
        medicalProcedureService.updateProcedure(procedureId, procedure);
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_edit', 'restricted_write')")
    @PatchMapping("/{procedureId}/provider-locations")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void updateProviderLocationProcedure(@PathVariable String procedureId, @RequestBody ProviderLocationProcedureDetail providerLocationProcedureDetail) {

        medicalProcedureService.updateProviderLocationProcedure(providerLocationProcedureDetail.providerId, providerLocationProcedureDetail.providerLocationId,
                procedureId, providerLocationProcedureDetail.getProviderLocationProcedure());

        if(providerLocationProcedureDetail != null && providerLocationProcedureDetail.procedureDepartments != null &&
                providerLocationProcedureDetail.procedureDepartments.size() > 0) {
            providerLocationProcedureDetail.procedureDepartments.forEach(providerLocationDepartmentProcedure -> {

                medicalProcedureService.updateProviderLocationDepartmentProcedure(providerLocationProcedureDetail.providerId, providerLocationProcedureDetail.providerLocationId,
                        providerLocationDepartmentProcedure.departmentId, procedureId, providerLocationDepartmentProcedure);

            });
        }

    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_edit', 'restricted_write')")
    @PostMapping("/{procedureId}/provider-locations/variant")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public void createProviderAndProcedureVariants(@PathVariable String procedureId, @RequestBody ProviderLocationProcedureDetail providerLocationProcedureDetail) {

        medicalProcedureService.createProviderAndProcedureVariants(providerLocationProcedureDetail.providerId, providerLocationProcedureDetail.providerLocationId,
                procedureId, providerLocationProcedureDetail.getProviderLocationProcedureVariant());

        if(providerLocationProcedureDetail != null && providerLocationProcedureDetail.procedureDepartments != null &&
                providerLocationProcedureDetail.procedureDepartments.size() > 0) {
            providerLocationProcedureDetail.procedureDepartments.forEach(providerLocationDepartmentProcedure -> {

                medicalProcedureService.updateProviderLocationDepartmentProcedure(providerLocationProcedureDetail.providerId, providerLocationProcedureDetail.providerLocationId,
                        providerLocationDepartmentProcedure.departmentId, procedureId, providerLocationDepartmentProcedure);

            });
        }

    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','procedure_edit', 'restricted_write')")
    @PutMapping("/{procedureId}/provider-locations/variant/{variantId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void updateProviderAndProcedureVariants(@PathVariable String procedureId,@PathVariable String variantId, @RequestBody ProviderLocationProcedureDetail providerLocationProcedureDetail) {

        medicalProcedureService.updateProviderAndProcedureVariants(providerLocationProcedureDetail.providerId, providerLocationProcedureDetail.providerLocationId,
                procedureId, variantId, providerLocationProcedureDetail.getProviderLocationProcedureVariant());

        if(providerLocationProcedureDetail != null && providerLocationProcedureDetail.procedureDepartments != null &&
                providerLocationProcedureDetail.procedureDepartments.size() > 0) {
            providerLocationProcedureDetail.procedureDepartments.forEach(providerLocationDepartmentProcedure -> {

                medicalProcedureService.updateProviderLocationDepartmentProcedure(providerLocationProcedureDetail.providerId, providerLocationProcedureDetail.providerLocationId,
                        providerLocationDepartmentProcedure.departmentId, procedureId, providerLocationDepartmentProcedure);

            });
        }

    }

}
