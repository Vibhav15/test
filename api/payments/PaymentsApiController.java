package com.halodoc.batavia.controller.api.payments;

import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.entity.configuration.PaymentMethodConfiguration;
import com.halodoc.batavia.entity.scrooge.PaymentsRefundRequest;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.scrooge.PaymentsService;
import com.halodoc.config.ConfigClient;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("api/v1/payments")
@RestController
public class PaymentsApiController {

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ConfigClient<BataviaAppConfiguration> bataviaAppConfig;

    @Autowired
    public PaymentsApiController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @GetMapping("/{orderId}/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<Object> searchPaymentDetails(@PathVariable String orderId, @RequestParam(required = false, name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(required = false, name = "perPage", defaultValue = "10") Integer perPage,
                                                        @RequestParam(required = false, name = "serviceType", defaultValue = "contact_doctor") String serviceType) throws URISyntaxException {

        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")
                && !authorizationService.isAuthorized("subscriptions", "subscriptions_view", "view_only")
                && !authorizationService.isAuthorized("lab_service", "order_view", "view_only")
                && !authorizationService.isAuthorized("contact_doctor", "consultations", "view_only")
                && !authorizationService.isAuthorized("pharmacy_delivery", "order_view", "view_only")) {

            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return paymentsService.searchPaymentDetails(orderId, pageNo, perPage, serviceType);
    }

    @PostMapping("/{orderId}/initiate_refund")
    @ApiCategory(value = ApiType.THIRD_PARTY, verticalName = Vertical.CORE)
    public Object initiateRefund(@PathVariable String orderId, @RequestBody PaymentsRefundRequest paymentsRefundRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("customer", "wallet_topup", "full")
                && !authorizationService.isAuthorized("hospitals_management", "hospital_appointment_refund", "full")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return paymentsService.initiateRefundV1(orderId, paymentsRefundRequest);
    }

    @PostMapping("/manual-refunds")
    @PreAuthorize("@authorizationService.isAuthorized('customer','wallet_topup', 'full')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Object initiateManualRefund(@RequestBody Map manualRefundRequest) throws URISyntaxException {
        return paymentsService.initiateManualRefund(manualRefundRequest);
    }

    @GetMapping("/payment-method/configs")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public PaymentMethodConfiguration getPaymentMethodConfig() {
        return bataviaAppConfig.getAppConfig().getPaymentMethodConfiguration();
    }
}
