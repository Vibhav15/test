package com.halodoc.batavia.controller.api.exodus.insurance;

import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.misool.catalog.*;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.ImageUploadService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import com.halodoc.batavia.service.ingestion.IngestionService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/exodus/preferred-providers")
public class ExodusPreferredProviderApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @Autowired
    private IngestionService ingestionService;

    @Autowired
    private ImageUploadService uploaderService;

    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<PreferredProviderList> searchPreferredProviders(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam (required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogService.searchPreferredProviders(pageNo, perPage, name);
    }

    @GetMapping("/{id}")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PreferredProviderList getPreferredProviderById(@PathVariable String id) throws URISyntaxException{
        return exodusMisoolCatalogService.getPreferredProviderById(id);
    }

    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createPreferredProvider(@RequestBody PreferredProviderRequest preferredProviderRequest) throws URISyntaxException{
        return exodusMisoolCatalogService.createPreferredProvider(preferredProviderRequest);
    }

    @PutMapping("/{id}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updatePreferredProvider(@PathVariable String id, @RequestBody PreferredProviderRequest preferredProviderRequest) throws URISyntaxException{
        return exodusMisoolCatalogService.updatePreferredProvider(id, preferredProviderRequest);
    }

    @GetMapping("/{id}/hospitals")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<PreferredProviderHospitalList> getPreferredProviderHospitals(@PathVariable String id,
                                                                                 @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                                 @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                                                 @RequestParam (required = false, name = "hld_code") String hldCode,
                                                                                 @RequestParam (required = false, name = "provider_group_name") String providerGroupName,
                                                                                 @RequestParam (required = false, name = "provider_group_location") String providerGroupLocation) throws URISyntaxException {
        return exodusMisoolCatalogService.searchPreferredProviderHospitals(pageNo, perPage, id, hldCode, providerGroupName, providerGroupLocation);
    }

    @PostMapping("/{id}/hospitals")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map addHospitalToPreferredProvider(@PathVariable String id, @RequestBody PreferredProviderHospitalRequest preferredProviderHospitalRequest) throws URISyntaxException{
        return exodusMisoolCatalogService.addHospitalToPreferredProvider(id, preferredProviderHospitalRequest);
    }

    @DeleteMapping("/{id}/hospitals/{hospitalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void removeHospitalFromPreferredProvider(@PathVariable String id, @PathVariable String hospitalId) throws URISyntaxException{
        exodusMisoolCatalogService.removeHospitalMapping(id, hospitalId);
    }

    @GetMapping("/{id}/hospitals/{hospitalId}/attributes")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<PreferredProviderHospitalCopayList> getPreferredProviderHospitalCopayLists(@PathVariable String id,
                                                                                 @PathVariable String hospitalId,
                                                                                 @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                                 @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage
                                                                                 ) throws URISyntaxException {
        return exodusMisoolCatalogService.getCopayAttributes(pageNo, perPage, id, hospitalId);
    }

    @PostMapping("/{id}/hospitals/{hospitalId}/attributes")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    String addHospitalCopay(@PathVariable String id, @PathVariable String hospitalId, @RequestBody AddPreferredProviderHospitalCopay preferredProviderHospitalRequest) throws URISyntaxException{
        return exodusMisoolCatalogService.addCopayAttribute(id, hospitalId, preferredProviderHospitalRequest);
    }

    @PutMapping("/{id}/hospitals/{hospitalId}/attributes/{attributeId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateHospitalCopay(@PathVariable String id, @PathVariable String hospitalId, @PathVariable String attributeId, @RequestBody AddPreferredProviderHospitalCopay preferredProviderHospitalRequest) throws URISyntaxException{
        exodusMisoolCatalogService.updateCopayAttribute(id, hospitalId, attributeId, preferredProviderHospitalRequest);
    }

    @DeleteMapping("/{id}/hospitals/{hospitalId}/attributes/{attributeId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteHospitalCopay(@PathVariable String id, @PathVariable String hospitalId, @PathVariable String attributeId) throws URISyntaxException{
        exodusMisoolCatalogService.deleteCopayAttribute(id, hospitalId, attributeId);
    }

    @PutMapping("/attributes/multi-get")
    List<PreferredProviderCopayAttributeFlag> multiGetCopayAttributes(@RequestBody List<PreferredProviderCopayAttributeFlagReq> request) throws URISyntaxException{
        return exodusMisoolCatalogService.multiGetCopayAttributes(request);
    }
    
    @GetMapping("/ingestion-history")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Map> getIngestionHistory(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                             @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return ingestionService.getPreferredProviderIngestionHistory(pageNo, perPage);
    }

    @PostMapping("/{id}/bulk-upload")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.INS)
    Map preferredProviderBulkUpload(@PathVariable String id, @RequestBody Map request) throws URISyntaxException{
        return ingestionService.preferredProviderBulkUpload(id, request);
    }

    @GetMapping("/dropdown-list")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<PreferredProviderDropdownList> getPreferredProviderDropdownList(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                                    @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage
                                                                                    ) throws URISyntaxException {
        return exodusMisoolCatalogService.getPreferredProviderDropdownList(pageNo, perPage);
    }

    @PutMapping("/ingestion-history")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    ResponseEntity<String> getIngestionHistoryDownloadURL(@RequestBody String documentExternalId) throws URISyntaxException {
        return uploaderService.getSignedDocumentURL(documentExternalId);
    }

    @PostMapping("/get-name-from-ids")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    List<PreferredProviderDropdownList> getPreferredProviderNameFromIds(@RequestBody List<String> preferredProviderIds) throws URISyntaxException {
        return exodusMisoolCatalogService.getPreferredProviderNameFromIds(preferredProviderIds);
    }

    @PostMapping("/hospital/{providerLocationId}/mappings")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    MultiHospitalMappingRequest mapHospitalCatalog(@PathVariable String providerLocationId, @RequestBody MultiHospitalMappingRequest multiHospitalMappingRequest) throws URISyntaxException {
        return exodusMisoolCatalogService.mapHospitalCatalog(providerLocationId, multiHospitalMappingRequest);
    }

    @GetMapping("/hospital/{providerLocationId}/mappings")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<PreferredProviderDropdownList> getMappedHospitalCatalogs(@PathVariable String providerLocationId,
                                                                             @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                             @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer perPage) throws URISyntaxException {
        return exodusMisoolCatalogService.getMappedHospitalCatalogs(providerLocationId, pageNo, perPage);
    }


}
