package com.halodoc.batavia.controller.api.wallet;

import java.net.URISyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.scrooge.CoinExpiration;
import com.halodoc.batavia.entity.scrooge.CoinTransaction;
import com.halodoc.batavia.entity.scrooge.WalletCompartment;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.scrooge.WalletService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shrikara
 * @since 14/09/23.
 */

@Slf4j
@Controller
@RequestMapping ("api/v1/coins")
@RestController
public class CoinApiController {
    @Autowired
    private WalletService walletService;

    @Autowired
    public CoinApiController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @GetMapping ("/balance")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public List<WalletCompartment> coinBalance(@RequestParam (required = true, name = "user_type", defaultValue = "consumer") String userType,
            @RequestParam(required = true, name = "user_id") String userId, @RequestParam(required = true, name = "compartment_type") String compartmentType) throws URISyntaxException {

        return walletService.getWalletBalance(userType, userId, compartmentType);

    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @GetMapping ("/transactions")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<CoinTransaction> transactions(@RequestParam (required = true, name = "user_type", defaultValue = "consumer") String userType,
            @RequestParam(required = true, name = "user_id") String userId,
            @RequestParam(required = false, name ="page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name ="per_page", defaultValue = "10") Integer perPage,
            @RequestParam(required = false, name = "sort_field", defaultValue = "transaction_time") String sortField,
            @RequestParam(required = false, name = "sort_order", defaultValue = "desc") String sortOrder,
            @RequestParam(required = true, name = "transaction_type", defaultValue = "all") String transaction_type) throws URISyntaxException {


        return walletService.getCoinTransactions(userType , userId, transaction_type, pageNo, perPage, sortField, sortOrder);
    }

    @PreAuthorize("@authorizationService.isAuthorized('customer','customer_view', 'view_only')")
    @GetMapping ("/transactions/expiring")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<CoinExpiration> coinExpiration(@RequestParam (required = true, name = "user_type", defaultValue = "consumer") String userType,
            @RequestParam(required = true, name = "user_id") String userId) throws URISyntaxException {


        return walletService.getCoinExpiration(userType , userId);
    }
}
