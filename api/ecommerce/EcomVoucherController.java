package com.halodoc.batavia.controller.api.ecommerce;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.dto.ecommerce.EcomVoucherCreateDTO;
import com.halodoc.batavia.dto.ecommerce.EcomVoucherResponseDTO;
import com.halodoc.batavia.dto.ecommerce.EcomVoucherUpdateDTO;
import com.halodoc.batavia.entity.cms.merchants.MerchantLocation;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.service.ecommerce.EcomVoucherService;
import com.halodoc.batavia.service.scrooge.PaymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/ecom-vouchers")
public class EcomVoucherController extends HalodocBaseApiController {

    private final EcomVoucherService ecomVoucherService;

    @Autowired
    public EcomVoucherController(EcomVoucherService ecomVoucherService) {
        this.ecomVoucherService = ecomVoucherService;
    }
    @PreAuthorize("@authorizationService.isAuthorized('marketing','ecommerce_vouchers', 'view_only')")
    @GetMapping("/search")
    public PaginatedResult<EcomVoucherResponseDTO> searchPaymentDetails(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                        @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                                        @RequestParam(required = false, name = "voucher_code") String voucherCode,
                                                                        @RequestParam(required = false, name = "ecom_provider", defaultValue = "shopee") String ecomProvider) throws URISyntaxException {
        return ecomVoucherService.paginatedList(ecomProvider,voucherCode, pageNo, perPage);
    }
    @PreAuthorize("@authorizationService.isAuthorized('marketing','ecommerce_vouchers', 'restricted_write')")
    @PostMapping()
    EcomVoucherResponseDTO saveEcomVoucher( @RequestBody EcomVoucherCreateDTO ecomVoucherRequest) throws URISyntaxException {
        return ecomVoucherService.createEcomVoucher(ecomVoucherRequest);
    }

    @PreAuthorize("@authorizationService.isAuthorized('marketing','ecommerce_vouchers', 'restricted_write')")
    @PutMapping("/{voucherId}")
    public EcomVoucherResponseDTO updateEcomVoucher(@PathVariable String voucherId,
                                                      @RequestBody EcomVoucherUpdateDTO ecomVoucherRequest) throws URISyntaxException {
        return ecomVoucherService.updateEcomVoucher(voucherId, ecomVoucherRequest);
    }

}
