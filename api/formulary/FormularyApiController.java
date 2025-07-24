package com.halodoc.batavia.controller.api.formulary;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.bintan.catalog.DoctorPackageExtended;
import com.halodoc.batavia.entity.bintan.catalog.HospitalProvider;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.formulary.Formulary;
import com.halodoc.batavia.entity.formulary.FormularyProduct;
import com.halodoc.batavia.entity.misool.BenefitFormularyLink;
import com.halodoc.batavia.entity.misool.ProviderBenefit;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.bintan.DoctorPackagesService;
import com.halodoc.batavia.service.bintan.ProviderService;
import com.halodoc.batavia.service.formulary.FormularyService;
import com.halodoc.batavia.service.misool.MisoolService;
import com.halodoc.batavia.util.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/formularies")
@RestController
@Slf4j
public class FormularyApiController extends HalodocBaseApiController {
    private static class BenefitCrudParam {
        public List<BenefitFormularyLink> benefits;

        public Formulary formulary;
    }

    private static class PackageCrudParam {
        public List<String> packageIds;

        public Formulary formulary;
    }

    @Autowired
    private FormularyService formularyService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private MisoolService misoolService;

    @Autowired
    private DoctorPackagesService doctorPackagesService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<Formulary> getFormularies(@RequestParam Map<String, String> requestParams) throws URISyntaxException {
        return formularyService.getFormularies(requestParams);
    }

    @PostMapping
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Formulary saveFormulary(@RequestBody Formulary formulary) throws URISyntaxException {
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        formulary.setCreatedBy(userName);
        return formularyService.saveFormulary(formulary);
    }

    @PutMapping ("/{formularyId}")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Formulary updateFormulary(@PathVariable String formularyId, @RequestBody Formulary formulary) throws URISyntaxException {
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        formulary.setUpdatedBy(userName);
        return formularyService.updateFormulary(formularyId, formulary);
    }

    @GetMapping ("/{formularyId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Formulary getFormulary(@PathVariable String formularyId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService
                .isAuthorized("contact_doctor", "doctor_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return formularyService.getFormulary(formularyId);
    }

    @GetMapping ("/{formularyId}/products")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult getFormularyProducts(@PathVariable String formularyId, @RequestParam Map<String, String> requestParams)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService
                .isAuthorized("contact_doctor", "doctor_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return formularyService.getFormularyProducts(formularyId, requestParams);
    }

    @GetMapping ("/{formularyId}/products/{productId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    FormularyProduct getFormularyProduct(@PathVariable String formularyId, @PathVariable String productId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService
                .isAuthorized("contact_doctor", "doctor_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return formularyService.getFormularyProduct(formularyId, productId);
    }

    @PutMapping ("/{formularyId}/copy-products")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void copyFormularyProducts(@PathVariable String formularyId, @RequestBody List<String> formularyIds) throws URISyntaxException {
        formularyService.copyFormularyProducts(formularyId, formularyIds);
    }

    @GetMapping ("/hospitals/providers")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<HospitalProvider> getProvidersList(@RequestParam Integer per_page, @RequestParam Integer page_no,
            @RequestParam (name = "names", required = false) String names, @RequestParam (name = "statuses", required = false) String statuses,
            @RequestParam (name = "providerLocationName", required = false) String providerLocationName) throws URISyntaxException {
        return providerService.getProvidersList(page_no, per_page, statuses, names, providerLocationName);
    }

    @PutMapping ("/search-by-products")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Map searchByProducts(@RequestBody ArrayList<String> productIds) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService
                .isAuthorized("contact_doctor", "doctor_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return formularyService.searchByProducts(productIds);
    }

    @GetMapping ("/{formularyId}/benefits")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ProviderBenefit> getBenefitsLinkedToFormulary(@PathVariable String formularyId,
            @RequestParam (name = "page_no", required = false) String pageNo, @RequestParam (name = "per_page", required = false) String perPage)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService
                .isAuthorized("contact_doctor", "doctor_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return misoolService.getBenefitsByFormularyId(formularyId, pageNo, perPage);
    }

    @GetMapping ("/{formularyId}/packages")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<DoctorPackageExtended> getPackagesLinkedToFormulary(@PathVariable String formularyId,
            @RequestParam (name = "page_no", required = false) String pageNo, @RequestParam (name = "per_page", required = false) String perPage)
            throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService
                .isAuthorized("contact_doctor", "doctor_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return doctorPackagesService.getPackagesByFormularyId(formularyId, pageNo, perPage);
    }

    @GetMapping ("/locations/{providerLocationId}/packages")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<DoctorPackageExtended> gePackagesByLocationId(@PathVariable String providerLocationId,
            @RequestParam (name = "page_no", required = false) String pageNo, @RequestParam (name = "per_page", required = false) String perPage)
            throws URISyntaxException {
        return doctorPackagesService.getPackagesByLocationId(providerLocationId, null, null, pageNo, perPage);
    }

    @PostMapping ("/{formularyId}/benefits")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    void addBenefitsToFormulary(@PathVariable String formularyId, @RequestBody BenefitCrudParam benefitCrudParam) throws URISyntaxException {
        misoolService.addBenefitsToFormulary(formularyId, benefitCrudParam.benefits, benefitCrudParam.formulary);
    }

    @PatchMapping ("/{formularyId}/benefits")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void removeBenefitsFromFormulary(@PathVariable String formularyId, @RequestBody BenefitCrudParam benefitCrudParam) throws URISyntaxException {
        misoolService.removeBenefitsFromFormulary(formularyId, benefitCrudParam.benefits, benefitCrudParam.formulary);
    }

    @PutMapping ("/{formularyId}/packages/multi_link")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void addPackagesToFormulary(@PathVariable String formularyId, @RequestBody PackageCrudParam packageCrudParam) throws URISyntaxException {
        doctorPackagesService.linkPackagesToFormulary(formularyId, packageCrudParam.packageIds, packageCrudParam.formulary);
    }

    @PutMapping ("/{formularyId}/packages/multi_unlink")
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','formulary_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void removePackagesFromFormulary(@PathVariable String formularyId, @RequestBody PackageCrudParam packageCrudParam) throws URISyntaxException {
        doctorPackagesService.unlinkPackagesFromFormulary(formularyId, packageCrudParam.packageIds, packageCrudParam.formulary);
    }
    @PutMapping ("/search-by-packages")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Map searchByPackages(@RequestBody ArrayList<String> productIds) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService
                .isAuthorized("lab_service", "package_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return formularyService.searchByPackages(productIds);
    }
}
