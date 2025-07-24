package com.halodoc.batavia.controller.api.payments;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.scrooge.PaymentsRefundRequest;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.scrooge.PaymentsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("api/v2/payments")
@RestController
public class PaymentsApiV2Controller {

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public PaymentsApiV2Controller(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/{orderId}/initiate_refund")
    @ApiCategory (value = ApiType.THIRD_PARTY, verticalName = Vertical.CORE)
    public Object initiateRefund(@PathVariable String orderId, @RequestBody PaymentsRefundRequest paymentsRefundRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "wallet_topup", "full")
                && !authorizationService.isAuthorized("hospitals_management", "hospital_appointment_refund", "full")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return paymentsService.initiateRefundV2(orderId, paymentsRefundRequest);
    }
}
