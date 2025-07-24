package com.halodoc.batavia.controller.api;

import com.halodoc.batavia.entity.bintan.catalog.ErxTemplate;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.device.DeviceService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.halodoc.batavia.service.bintan.CodiPrescriptionService;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/v1/erx")
@RestController
public class ErxAPIController extends HalodocBaseApiController{
    private final CodiPrescriptionService codiPrescriptionService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public ErxAPIController(CodiPrescriptionService codiPrescriptionService) {
        this.codiPrescriptionService = codiPrescriptionService;
    }

    @GetMapping("/list")
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor', 'erx_template_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CODI)
    public PaginatedResult<ErxTemplate> listErxTemplate(
            @RequestParam(required = false, name = "searchText") String searchText,
            @RequestParam(required = false) int page_no,
            @RequestParam(required = false) int per_page
    ) {

        Map<String, Object> request = new HashMap<>();

        if(StringUtils.isNotBlank(searchText)) {
            request.put("template_name", searchText);
        }

        request.put("page_no", page_no);
        request.put("per_page", per_page);

        return  codiPrescriptionService.erxList(request);
    }

    @DeleteMapping ("/{id}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.CODI)
    @PreAuthorize("@authorizationService.isAuthorized('contact_doctor', 'erx_template_edit', 'restricted_write')")
    public void deleteSequence(@PathVariable Long id) throws URISyntaxException {
        codiPrescriptionService.deleteErxTemplate(id);
    }


}
