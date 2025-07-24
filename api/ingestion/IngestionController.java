package com.halodoc.batavia.controller.api.ingestion;

import java.net.URISyntaxException;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.ingestion.EmailAutoIngestionConfig;
import com.halodoc.batavia.entity.ingestion.EmailAutoIngestionConfigRequest;
import com.halodoc.batavia.entity.ingestion.EmailAutoIngestionSearchResponse;
import com.halodoc.batavia.entity.ingestion.SFTPAutoIngestionConfig;
import com.halodoc.batavia.entity.ingestion.SFTPAutoIngestionConfigRequest;
import com.halodoc.batavia.entity.ingestion.SFTPAutoIngestionSearchResponse;
import com.halodoc.batavia.service.ingestion.IngestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping ("api/v1/ingestions")
@RestController
@Slf4j
public class IngestionController {
    @Autowired
    private IngestionService ingestionService;

    /********************************** Email config: START **********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_add', 'restricted_write')")
    @PostMapping ("email-ingestion-config")
    EmailAutoIngestionConfig createEmailAutoIngestionConfig(@RequestBody EmailAutoIngestionConfigRequest ingestionConfigRequest) {
        return ingestionService.createEmailAutoIngestionConfig(ingestionConfigRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_edit', 'restricted_write')")
    @PutMapping ("email-ingestion-config/{externalId}")
    EmailAutoIngestionConfig updateEmailAutoIngestionConfig(@PathVariable String externalId,
            @RequestBody EmailAutoIngestionConfigRequest ingestionConfigRequest) {
        return ingestionService.updateEmailAutoIngestionConfig(externalId, ingestionConfigRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_view', 'view_only')")
    @GetMapping ("email-ingestion-config/{externalId}")
    EmailAutoIngestionConfig getEmailAutoIngestionConfigById(@PathVariable String externalId) {
        return ingestionService.getEmailAutoIngestionConfigById(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_view', 'view_only')")
    @GetMapping ("email-ingestion-configs")
    PaginatedResult<EmailAutoIngestionSearchResponse> searchEmailAutoIngestionConfigs(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") int pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") int perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String providerExternalId) throws URISyntaxException {
        return ingestionService.searchEmailAutoIngestionConfigs(pageNo, perPage, name, providerExternalId);
    }

    /********************************** Email config: END **********************************/
    /********************************** SFTP config: START **********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_add', 'restricted_write')")
    @PostMapping ("sftp-ingestion-config")
    SFTPAutoIngestionConfig createSFTPAutoIngestionConfig(@RequestBody SFTPAutoIngestionConfigRequest ingestionConfigRequest) {
        return ingestionService.createSFTPAutoIngestionConfig(ingestionConfigRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_edit', 'restricted_write')")
    @PutMapping ("sftp-ingestion-config/{externalId}")
    SFTPAutoIngestionConfig updateSFTPAutoIngestionConfig(@PathVariable String externalId,
            @RequestBody SFTPAutoIngestionConfigRequest ingestionConfigRequest) {
        return ingestionService.updateSFTPAutoIngestionConfig(externalId, ingestionConfigRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_view', 'view_only')")
    @GetMapping ("sftp-ingestion-config/{externalId}")
    SFTPAutoIngestionConfig getSFTPAutoIngestionConfigById(@PathVariable String externalId) {
        return ingestionService.getSFTPAutoIngestionConfigById(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','member_auto_ingestion_view', 'view_only')")
    @GetMapping ("sftp-ingestion-configs")
    PaginatedResult<SFTPAutoIngestionSearchResponse> searchSFTPAutoIngestionConfigs(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") int pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") int perPage, @RequestParam (required = false) String name,
            @RequestParam (required = false) String providerExternalId) throws URISyntaxException {
        return ingestionService.searchSFTPAutoIngestionConfigs(pageNo, perPage, name, providerExternalId);
    }

    /********************************** SFTP config: END **********************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/providers/{insuranceProviderId}/entities/{entityId}/products/{productExternalId}/policy-data-ingestion")
    PaginatedResult<Map> getProductBulkUploadResult(@RequestParam (required = false, name = "page_no", defaultValue = "1") int pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") int perPage, @PathVariable @NotBlank String insuranceProviderId,
            @PathVariable @NotBlank String entityId, @PathVariable @NotBlank String productExternalId) throws URISyntaxException {
        return ingestionService.getProductBulkUploadResult(pageNo, perPage, insuranceProviderId, entityId, productExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PostMapping ("/providers/{insuranceProviderId}/entities/{entityId}/products/{productExternalId}/policy-data-ingestion")
    Map upsertProductBulkUpload(@RequestParam (required = false, name = "page_no", defaultValue = "1") int pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") int perPage, @PathVariable @NotBlank String insuranceProviderId,
            @PathVariable @NotBlank String entityId, @PathVariable @NotBlank String productExternalId, @RequestBody Map request) throws URISyntaxException {
        return ingestionService.productBulkUpload(insuranceProviderId, entityId, productExternalId, request);
    }
}
