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
import com.halodoc.batavia.entity.misool.invoice.InvoiceCondition;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.misool_catalog.MisoolCatalogService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/invoice-condition")
@RestController
@Slf4j
public class InvoiceConditionController extends HalodocBaseApiController {
    @Autowired
    private MisoolCatalogService misoolCatalogService;

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_condition_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<InvoiceCondition> getConditions(@RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, name = "name", defaultValue = "") String name,
            @RequestParam (required = false, name = "service_type", defaultValue = "") String serviceType,
            @RequestParam (required = false, name = "status", defaultValue = "") String status) throws URISyntaxException {

        return misoolCatalogService.getInvoiceConditions(pageNo, perPage, name, status, serviceType);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_condition_view', 'view_only')")
    @GetMapping ("{name}")
    InvoiceCondition getInvoiceCondition(@PathVariable String name) {

        return misoolCatalogService.getInvoiceCondition(name);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_condition_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.INS)
    InvoiceCondition saveInvoiceCondition(@RequestBody InvoiceCondition invoiceCondition) {
        return misoolCatalogService.createInvoiceCondition(invoiceCondition);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_condition_edit', 'restricted_write')")
    @PutMapping ("{invoiceId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    InvoiceCondition updateInvoiceCondition(@PathVariable String invoiceId, @RequestBody InvoiceCondition invoiceCondition) {
        return misoolCatalogService.updateInvoiceCondition(invoiceId, invoiceCondition);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','invoice_condition_edit', 'restricted_write')")
    @DeleteMapping ("{invoiceId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteInvoiceCondition(@PathVariable String invoiceId) {
        misoolCatalogService.removeInvoiceCondition(invoiceId);
    }

}
