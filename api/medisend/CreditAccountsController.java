package com.halodoc.batavia.controller.api.medisend;

import com.halodoc.batavia.entity.medisend.OverDuePayment;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.medisend.CreditAccountsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("api/v1/medisend/credit-accounts")
@RestController
public class CreditAccountsController {
    @Autowired
    private CreditAccountsService creditAccountsService;


    @GetMapping("/")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List getCreditAccounts(
            @RequestParam(required = true, name = "entity_id", defaultValue = "") String entityId,
            @RequestParam(required = true, name = "entity_type", defaultValue = "") String entityType
    ) throws URISyntaxException {
        return  creditAccountsService.getCreditAccounts(entityType, entityId);
    }

    @PutMapping("/{entityAccountId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public Map setCreditAccounts(
                            @PathVariable String entityAccountId,
                            @RequestBody Map creditAccount) throws URISyntaxException {
         return creditAccountsService.setCreditAccounts(entityAccountId, creditAccount);
    }

    @PutMapping("/{entityAccountId}/activate")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void activateCreditAccounts(@PathVariable String entityAccountId ,@RequestBody Map requesBody) throws URISyntaxException{
         creditAccountsService.activateCreditAccount(entityAccountId,requesBody);
    }

    @PutMapping("/{entityAccountId}/block")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void blockCreditAccounts(@PathVariable String entityAccountId,@RequestBody Map requesBody) throws URISyntaxException{
        creditAccountsService.blockCreditAccounts(entityAccountId,requesBody);
    }

    @PutMapping("/{entityAccountId}/suspend")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_edit', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void suspendCreditAccounts(@PathVariable String entityAccountId,@RequestBody Map requesBody) throws URISyntaxException{
        creditAccountsService.suspendCreditAccounts(entityAccountId,requesBody);
    }

    @GetMapping("{entityAccountId}/transactions/overdue")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','merchant_location_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public OverDuePayment getOverDueInvocies(@PathVariable String entityAccountId) throws URISyntaxException{
        return creditAccountsService.getOverDueInvocies(entityAccountId);
    }
}
