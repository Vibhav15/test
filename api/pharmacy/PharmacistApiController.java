package com.halodoc.batavia.controller.api.pharmacy;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.cms.Pharmacist;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.*;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.halodoc.batavia.service.BuruService;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1/pharmacist")
@RestController
@Slf4j
public class PharmacistApiController extends HalodocBaseApiController {
    private final PharmacyService pharmacyService;
    private final BuruService buruService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public PharmacistApiController(PharmacyService pharmacyService, BuruService buruService) {
        this.pharmacyService = pharmacyService;
        this.buruService = buruService;
    }

    @GetMapping("/{merchantLocationId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    public List<Pharmacist> getPharmacist(@PathVariable String merchantLocationId) {
        return buruService.getPharmacist(merchantLocationId);
    }

    @PostMapping("/{merchantLocationId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public List<Pharmacist> updatePharmacist(@PathVariable String merchantLocationId, @RequestBody Pharmacist pharmacist) {
        return buruService.updatePharmacist(merchantLocationId, pharmacist);
    }

    @PostMapping("/{merchantId}/locations/{merchantLocationId}/upload")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public Map uploadPharmacistDocument(@PathVariable String merchantId, @PathVariable String merchantLocationId,
                                        @RequestHeader("X-Document-Type") String kycDocType,
                                        @RequestHeader("X-File-Type") String xFileType,
                                        @RequestHeader("Content-Length") final Long contentLength,
                                        InputStream fileStream) {
        return buruService.uploadPharmacistDocument(merchantId, merchantLocationId, kycDocType, xFileType, contentLength, fileStream);
    }

    @GetMapping("/{merchantId}/locations/{merchantLocationId}/documents/{documentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public String getKycDocUrl(@PathVariable String merchantId, @PathVariable String merchantLocationId,
                               @PathVariable String documentId) {
        return buruService.getPharmacistDocumentUrl(merchantId, merchantLocationId, documentId);
    }
}
