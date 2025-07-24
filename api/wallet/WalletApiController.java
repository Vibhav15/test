package com.halodoc.batavia.controller.api.wallet;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.scrooge.WalletCompartment;
import com.halodoc.batavia.entity.scrooge.WalletTopup;
import com.halodoc.batavia.entity.scrooge.WalletTransaction;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.scrooge.PaymentsService;
import com.halodoc.batavia.service.scrooge.WalletService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

/**
 * @author harish
 * @since 08/01/19.
 */

@Slf4j
@Controller
@RequestMapping ("api/v1/wallets")
@RestController
public class WalletApiController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    public WalletApiController(WalletService walletService, PaymentsService paymentsService) {

        this.walletService = walletService;
        this.paymentsService = paymentsService;
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @GetMapping ("/balance")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public List<WalletCompartment> walletBalance(@RequestParam (required = true, name = "user_type", defaultValue = "consumer") String userType,
            @RequestParam(required = true, name = "user_id") String userId) throws URISyntaxException {

        return walletService.getWalletBalance(userType, userId,"");

    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @GetMapping ("/transactions")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<WalletTransaction> transactions(@RequestParam (required = true, name = "user_type", defaultValue = "consumer") String userType,
            @RequestParam(required = true, name = "user_id") String userId,
            @RequestParam(required = false, name ="page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name ="per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "sort_field", defaultValue = "transaction_time") String sortField,
            @RequestParam(required = false, name = "sort_order", defaultValue = "desc") String sortOrder,
            @RequestParam(required = true, name = "transaction_type", defaultValue = "all") String transaction_type) throws URISyntaxException {


        return walletService.getWalletTransactions(userType , userId, transaction_type, pageNo, perPage, sortField, sortOrder);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','wallet_topup', 'full')")
    @PostMapping ("/payments/wallet-topup")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Map walletTopup(@RequestBody WalletTopup walletTopupRequest) throws URISyntaxException {

        return paymentsService.walletTopup(walletTopupRequest);
    }
}
