package com.halodoc.batavia.controller.api.medisend;



import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.medisend.sell_out.MerchantSellOutTransaction;
import com.halodoc.batavia.entity.medisend.sell_out.MerchantSelloutTransactionPaginatedResult;
import com.halodoc.batavia.entity.medisend.sell_out.SellOutTransaction;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.medisend.SellOutRecordService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;


@Slf4j
@Controller
@RequestMapping("api/v1/medisend/sell-out")
@RestController
public class SellOutRecordController {


    @Autowired
    SellOutRecordService sellOutRecordService;

    @GetMapping("/transactions")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','sellout_record_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<SellOutTransaction> searchSellOut(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                             @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                             @RequestParam(required = false, name = "merchant_location_id", defaultValue = "") String merchantLocationId) throws URISyntaxException {
        return sellOutRecordService.getSellOutTransaction(perPage, pageNo, merchantLocationId);
    }

    @GetMapping("/merchant-locations/{merchant_location_id}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','sellout_record_view', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public MerchantSelloutTransactionPaginatedResult<MerchantSellOutTransaction> getMerchantSelloutTransaction(@PathVariable("merchant_location_id") String merchantLocationId, @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo, @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage, @RequestParam(required = false, name = "start_date", defaultValue = "") String startDate, @RequestParam(required = false, name = "end_date", defaultValue = "") String endDate) throws URISyntaxException {
        return sellOutRecordService.populateMerchantSelloutTransaction( pageNo, perPage, startDate, endDate, merchantLocationId);
    }

    @GetMapping("/merchant-locations/{merchant_location_id}/sell-out/{sell_out_id}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','sellout_record_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public MerchantSellOutTransaction getSelloutDetails(@PathVariable("merchant_location_id") String merchantLocationId, @PathVariable("sell_out_id") String sellOutId) throws URISyntaxException {
        return sellOutRecordService.getSelloutDetails(merchantLocationId, sellOutId);
    }
}
