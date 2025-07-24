package com.halodoc.batavia.controller.api.pharmacy;



import com.halodoc.batavia.entity.cms.Classification;
import com.halodoc.batavia.entity.cms.Product;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.ClassificationsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/v1/classifications")
@RestController
public class ClassificationsApiController {

    @Autowired
    private AuthorizationService authorizationService;

    private final ClassificationsService classificationsService;


    @Autowired
    public ClassificationsApiController(ClassificationsService classificationsService) {
        this.classificationsService = classificationsService;
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public Classification addClassification(@RequestBody Map<String, String> classification) {
        return classificationsService.create(classification);
    }

    @PutMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<Classification> classificationsMultiGet(@RequestBody List<String> classificationIds) {
        return classificationsService.getClassificationListByIds(classificationIds);
    }

    @PutMapping("/{classificationId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_add', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void updateClassification(@PathVariable String classificationId, @RequestBody Classification classification) {
        classificationsService.update(classificationId, classification);
    }

    @GetMapping("/search")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','product_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Classification> search(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false, defaultValue = "1") String pageNo,
            @RequestParam(required = false, defaultValue = "10") String perPage
    ) {
        PaginatedResult<Classification> paginatedResult = classificationsService.search(searchText, pageNo, perPage);
        return paginatedResult;
    }
}
