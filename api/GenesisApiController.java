package com.halodoc.batavia.controller.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.halodoc.batavia.entity.Ingestion;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.genesis.IngestionTypesConfiguration;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.genesis.GenesisService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("api/v1/genesis")
@RestController
public class GenesisApiController extends HalodocBaseApiController {

    private final GenesisService genesisService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public GenesisApiController(GenesisService genesisService) {
        this.genesisService = genesisService;
    }

    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<Ingestion> getGenesisList(@RequestParam(name = "ingestion_type", required = false) String ingestionType,
                                                     @RequestParam(required = false, defaultValue = "1") int page_no,
                                                     @RequestParam(required = false, defaultValue = "10") int per_page) throws URISyntaxException {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only")
                && !authorizationService.isAuthorized("configuration", "genesis_ingestions", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return this.genesisService.getGenesisDocuments(ingestionType, page_no, per_page);
    }

    @GetMapping("/supportedIngestionTypes")
    @PreAuthorize("@authorizationService.isAuthorized('configuration','genesis_ingestions', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public IngestionTypesConfiguration getIngestionTypes() {
        return genesisService.getIngestionTypes();
    }

    @GetMapping("/{ingestionId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Ingestion get(@PathVariable String ingestionId) {
        if (!authorizationService.isAuthorized("insurance", "formulary_view", "view_only") && !authorizationService.isAuthorized("configuration",
                "genesis_ingestions", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return genesisService.getFormularyIngestion(ingestionId);
    }

    @PostMapping
    @ApiCategory (value = ApiType.FILE_UPLOAD, verticalName = Vertical.INS)
    public Ingestion uploadFile(@RequestParam MultipartFile file, @RequestParam String ingestion_type, @RequestParam(name = "X-File-Type", required = false) String fileType,
                                @RequestParam String user_comments, @RequestParam(required = false) String ingestion_data) throws IOException {
        if (!authorizationService.isAuthorized("insurance", "formulary_edit", "restricted_write")
                && !authorizationService.isAuthorized("insurance", "hospital_template_add", "restricted_write")
                && !authorizationService.isAuthorized("insurance", "hospital_template_edit", "restricted_write")
                && !authorizationService.isAuthorized("insurance", "insurance_provider_add", "restricted_write")
                && !authorizationService.isAuthorized("insurance", "insurance_provider_edit", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "pd_catalog_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "pd_merchant_and_merchant_catalog_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "pd_product_recommendation_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "pd_geo_zone_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "pd_subscription_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "pd_transaction_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "insurance_administration_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "diagnosis_service_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "personnel_and_procedure_catalog_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "personnel_and_procedure_business_model_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "hospital_catalog_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "recon_catalog_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "additional_incentive_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "mini_consultation_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "appointment_bulk_order_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "cd_catalog_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "cd_business_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "cd_payroll_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_catalog_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_shipment_cost_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_product_recommendation_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_product_and_merchant_mapping_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_credit_limits_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_discrepancy_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_lpms_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "coupon_issuance_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_distributor_and_merchant_mapping_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "lpms_program_participant_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_reward_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_shipment_quantity_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "cd_sumatra_control_panel", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "medisend_sumatra_control_panel_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "pd_sumatra_control_panel_view", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "insurance_dco_recon", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "lab_catalog", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "lab_order_actions", "restricted_write")
                && !authorizationService.isAuthorized("configuration", "sumatra_midtrans_payment_gateway","restricted_write")
                && !authorizationService.isAuthorized("configuration", "sumatra_xendit_payment_gateway","restricted_write")
                && !authorizationService.isAuthorized("configuration", "sumatra_va_bca_payment_gateway","restricted_write")
                && !authorizationService.isAuthorized("configuration","cd_lab_package_icd_10_mapping","restricted_write")
                && !authorizationService.isAuthorized("configuration","hl_soft_delete_entity_sumatra","restricted_write")
                && !authorizationService.isAuthorized("configuration","hl_reconcile_entity_sumatra","restricted_write")
                && !authorizationService.isAuthorized("configuration","sumatra_lab_customer_mapping","restricted_write")
                && !authorizationService.isAuthorized("configuration","sumatra_ar_marking_ingestions","restricted_write")
                && !authorizationService.isAuthorized("configuration","sumatra_ap_marking_ingestions","restricted_write")
                && !authorizationService.isAuthorized("configuration","principal_product_mapping","restricted_write")
                && !authorizationService.isAuthorized("configuration","hl_do_si_automation","restricted_write")
                && !authorizationService.isAuthorized("configuration", "purchase_order_detail_ingestion_sumatra", "restricted_write")
                && !authorizationService.isAuthorized("configuration","demand_zone_package_details_update","restricted_write")
                && !authorizationService.isAuthorized("configuration","pd_ins_provider_group_mapping","restricted_write")
                && !authorizationService.isAuthorized("configuration","principal_user_merchant_location_mapping","restricted_write")
                && !authorizationService.isAuthorized("configuration","tpa_ins_scheme","restricted_write")
                && !authorizationService.isAuthorized("configuration","customer_tax_master","restricted_write")
                && !authorizationService.isAuthorized("configuration","recon_master_scheme_sumatra","restricted_write")
                && !authorizationService.isAuthorized("configuration","tpa_sumatra_ingestion_entity","restricted_write")
                && !authorizationService.isAuthorized("configuration", "affiliate_program", "restricted_write")

        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return genesisService.uploadFile(file, ingestion_type, fileType, user_comments, ingestion_data);
    }

    @PutMapping("/multi-get-signed-url")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<Map> getSignedUrl(@RequestBody List<String> documentIds){
            return genesisService.getSignedUrl(documentIds);
    }
}
