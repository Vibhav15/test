package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.Procedure;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.bintan.ProcedureService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@RequestMapping("api/v1/procedures")
@RestController
public class ProceduresApiController extends HalodocBaseApiController {

    private final ProcedureService procedureService;

    @Autowired
    public ProceduresApiController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_view', 'view_only')")
    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Procedure> getProcedureList(@RequestParam Integer per_page, @RequestParam Integer page_no,
                                                              @RequestParam(name="names", required = false) String names,
                                                              @RequestParam(name="statuses", required = false, defaultValue = "active") String statuses,
                                                              @RequestParam(name="meta_description", required = false) String meta_description

    ) throws URISyntaxException {
        return procedureService.getProcedureList(page_no, per_page, statuses,names, meta_description);
    }
}
