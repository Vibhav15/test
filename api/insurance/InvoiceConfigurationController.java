package com.halodoc.batavia.controller.api.insurance;

import java.net.URISyntaxException;
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
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.misool.invoice.Entity;
import com.halodoc.batavia.entity.misool.invoice.InvoiceConfiguration;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/invoice-configuration")
@RestController
@Slf4j
public class InvoiceConfigurationController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<InvoiceConfiguration> getConfigurations(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo, @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name", defaultValue = "") String name, @RequestParam (required = false, name = "status", defaultValue = "") String status,
            @RequestParam (required = false, name = "entity_id", defaultValue = "") String entityId, @RequestParam (required = false, name = "entity_type", defaultValue = "") String entityType,@RequestParam (required = false, name = "service_type") String serviceType) {

        return misoolCatalogService.getInvoiceConfigurations(pageNo, perPage, name, status, entityType, entityId, serviceType);
    }



    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_view', 'view_only')")
    @GetMapping ("{externalId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    InvoiceConfiguration getInvoiceConfiguration(@PathVariable String externalId) {

        return misoolCatalogService.getInvoiceConfiguration(externalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    InvoiceConfiguration saveInvoiceConfiguration(@RequestBody InvoiceConfiguration invoiceConfiguration) {
        return misoolCatalogService.createInvoiceConfiguration(invoiceConfiguration);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_edit', 'restricted_write')")
    @PutMapping ("{configId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void updateInvoiceConfiguration(@PathVariable String configId, @RequestBody InvoiceConfiguration invoiceConfig) {
        misoolCatalogService.updateInvoiceConfiguration(configId, invoiceConfig);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_edit', 'restricted_write')")
    @DeleteMapping ("{configId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteInvoiceConfiguration(@PathVariable String configId) {
        misoolCatalogService.removeInvoiceConfiguration(configId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_view', 'view_only')")
    @GetMapping ("{externalId}/entity/{entityType}")
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<Entity> getLinkedEntities(@PathVariable String externalId, @PathVariable String entityType, @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage) throws URISyntaxException {

        return misoolCatalogService.getInvoiceConfigurationLinkedEntities(externalId, entityType, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_view', 'view_only')")
    @GetMapping ("{externalId}/entity/{entityType}/{entityExternalId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Entity getLinkedEntityDetails(@PathVariable String externalId, @PathVariable String entityType, @PathVariable String entityExternalId) throws URISyntaxException {

        return misoolCatalogService.getInvoiceConfigurationLinkedEntityDetails(externalId, entityType, entityExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_edit', 'restricted_write')")
    @PutMapping ("{externalId}/entity/{entityType}/{entityId}")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    void addEntityLink(@PathVariable String externalId, @PathVariable String entityType, @PathVariable String entityId) {

        misoolCatalogService.addInvoiceConfigurationEntityLink(externalId, entityType, entityId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_configuration_edit', 'restricted_write')")
    @DeleteMapping ("{externalId}/entity/{entityType}/{entityId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void removeEntityLink(@PathVariable String externalId, @PathVariable String entityType, @PathVariable String entityId) {

        misoolCatalogService.removeInvoiceConfigurationEntityLink(externalId, entityType, entityId);
    }

}
