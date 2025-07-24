package com.halodoc.batavia.controller.api.exodus.insurance;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.entity.exodus.misool.catalog.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.claims.ClaimReimbursementDetails;
import com.halodoc.batavia.entity.exodus.claims.MiscellaneousItem;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/clinical-pathway")
public class ClinicalPathwayApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    /**************************************** Category: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_category_list', 'view_only')")
    @PutMapping ("/category-list")
    PaginatedResult<ClinicalPathwayCategoryList> searchClinicalPathwayCategoryList(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage, @RequestBody Map req)
            throws URISyntaxException {

        return exodusMisoolCatalogService.searchClinicalPathwayCategoryList(pageNo, perPage, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_view_category', 'view_only')")
    @GetMapping ("/category/{externalId}")
    ClinicalPathwayCategoryList getCategoryDetails(@PathVariable String externalId) throws URISyntaxException {
        return exodusMisoolCatalogService.getCategoryDetails(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_edit_category', 'restricted_write')")
    @PutMapping ("/category/{externalId}")
    ClinicalPathwayCategoryList updateClinicalPathwayCategory(@PathVariable String externalId, @RequestBody ClinicalPathwayCategoryRequest request)
            throws URISyntaxException {
        return exodusMisoolCatalogService.updateClinicalPathwayCategory(externalId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_add_category', 'restricted_write')")
    @PostMapping ("/category")
    ClinicalPathwayCategoryList createClinicalPathwayCategory(@RequestBody ClinicalPathwayCategoryRequest request) throws URISyntaxException {
        return exodusMisoolCatalogService.createClinicalPathwayCategory(request);
    }

    /**************************************** Category: END ****************************************/
    /**************************************** Sub Category: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_category_list', 'view_only')")
    @PutMapping ("/{categoryExternalId}/sub-category-list")
    PaginatedResult<ClinicalPathwaySubcategoryResponse> searchClinicalPathwaySubCategoryList(@PathVariable String categoryExternalId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage, @RequestBody Map req)
            throws URISyntaxException {

        return exodusMisoolCatalogService.searchClinicalPathwaySubCategoryList(categoryExternalId, pageNo, perPage, req);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_add_category', 'restricted_write')")
    @PostMapping ("/{categoryExternalId}/sub-category")
    ClinicalPathwaySubcategoryResponse createClinicalPathwaySubCategory(@PathVariable String categoryExternalId, @RequestBody Map request)
            throws URISyntaxException {
        return exodusMisoolCatalogService.createClinicalPathwaySubCategory(categoryExternalId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_edit_category', 'restricted_write')")
    @DeleteMapping ("/category/{categoryExternalId}/sub-category/{externalId}")
    void deleteClinicalPathwaySubCategory(@PathVariable String categoryExternalId, @PathVariable String externalId) throws URISyntaxException {
        exodusMisoolCatalogService.deleteClinicalPathwaySubCategory(externalId, categoryExternalId);
    }

    /**************************************** Sub Category: END ****************************************/
    /**************************************** Clinical Pathway Ingestion: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_category_list', 'view_only')")
    @GetMapping ("/{externalId}/ingestion-history")
    PaginatedResult<ClinicalPathwayIngestionHistory> searchClinicalPathwayIngestionHistory(@PathVariable String externalId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {

        return exodusMisoolCatalogService.searchClinicalPathwayIngestionHistory(pageNo, perPage, externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_add_category', 'restricted_write')")
    @PostMapping ("/post-ingestion")
    Map postClinicalPathwayIngestion(@RequestBody Map request) throws URISyntaxException {
        return exodusMisoolCatalogService.postClinicalPathwayIngestion(request);
    }
    /**************************************** Clinical Pathway Ingestion: END ****************************************/
    /**************************************** Catalog: START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_catalog_list', 'view_only')")
    @GetMapping ("/{externalId}/catalog-list")
    PaginatedResult<ClinicalPathwayCatalogResponse> searchClinicalPathwayCatalogList(@PathVariable String externalId,
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "search_text", defaultValue = DEFAULT_STRING) String searchText) throws URISyntaxException {

        return exodusMisoolCatalogService.searchClinicalPathwayCatalogList(pageNo, perPage, externalId, searchText);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_edit_catalog', 'restricted_write')")
    @DeleteMapping ("/{catalogExternalId}/delete-catalog")
    void deleteClinicalPathwayCatalog(@PathVariable String catalogExternalId) throws URISyntaxException {
        exodusMisoolCatalogService.deleteClinicalPathwayCatalog(catalogExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_add_catalog', 'restricted_write')")
    @PostMapping ("/catalog-create")
    ClinicalPathwayCatalog createCliniPathWayCatalog(@RequestBody ClinicalPathwayCatalog request) throws URISyntaxException {
        return exodusMisoolCatalogService.createCliniPathWayCatalog(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_view_catalog', 'view_only')")
    @GetMapping ("/catalog/{catalogExternalId}")
    ClinicalPathwayCatalog fetchClinicalPathWayCatalog(@PathVariable String catalogExternalId) throws URISyntaxException {
        return exodusMisoolCatalogService.fetchClinicalPathWayCatalog(catalogExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_edit_catalog', 'restricted_write')")
    @PutMapping ("{hospitalExternalId}/catalog-update/{catalogExternalId}")
    ClinicalPathwayCatalog updateCliniPathWayCatalog(@PathVariable String hospitalExternalId, @PathVariable String catalogExternalId, @RequestBody ClinicalPathwayCatalog request) throws URISyntaxException {
        return exodusMisoolCatalogService.updateCliniPathWayCatalog(hospitalExternalId, catalogExternalId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_catalog_list', 'view_only')")
    @PostMapping ("/treatment-list/{catalogExternalId}")
    PaginatedResult<TreatmentList> fetchTreatmentList(
            @PathVariable("catalogExternalId") String catalogExternalId,
            @RequestParam(value = "per_page", defaultValue = "10") int perPage,
            @RequestParam(value = "page_no", defaultValue = "1") int pageNo,
            @RequestBody Map filterRequest
    ) throws URISyntaxException {
        return exodusMisoolCatalogService.fetchTreatmentList(catalogExternalId, perPage, pageNo, filterRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_add_catalog', 'restricted_write')")
    @PostMapping ("/create-treatment")
    List<TreatmentList> createTreatment(@RequestBody Map request) throws URISyntaxException {
        return exodusMisoolCatalogService.createTreatment(request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_edit_catalog', 'restricted_write')")
    @PutMapping ("{catalogExternalId}/update-treatment/{treatmentExternalId}")
    List<TreatmentList> updateTreatment(@PathVariable String catalogExternalId, @PathVariable String treatmentExternalId, @RequestBody Map request) throws URISyntaxException {
        return exodusMisoolCatalogService.updateTreatment(catalogExternalId, treatmentExternalId, request);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_edit_catalog', 'restricted_write')")
    @DeleteMapping ("/{catalogExternalId}/treatment-delete/{treatmentExternalId}")
    void deleteTreatment(@PathVariable String catalogExternalId, @PathVariable String treatmentExternalId) throws URISyntaxException {
        exodusMisoolCatalogService.deleteTreatment(catalogExternalId, treatmentExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','clinical_pathway_catalog_list', 'view_only')")
    @GetMapping ("/{catalogExternalId}/get-treatment/{treatmentExternalId}")
    List<TreatmentList> getTreatment(@PathVariable String catalogExternalId, @PathVariable String treatmentExternalId) throws URISyntaxException {
        return exodusMisoolCatalogService.getTreatment(catalogExternalId, treatmentExternalId);
    }

    /**************************************** Catalog: END ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','case_monitoring_ip_view', 'view_only')")
    @PostMapping ("/search-clinical-pathway")
    Map getCmtInpatientCatalogInfo(@RequestBody Map request) throws URISyntaxException {
        return exodusMisoolCatalogService.getCmtInpatientCatalogInfo(request);
    }




}


