package com.halodoc.batavia.controller.api.pricing_engine;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.pricing_engine.EditPaymentConfigRequest;
import com.halodoc.batavia.entity.pricing_engine.EditScheduleDetails;
import com.halodoc.batavia.entity.pricing_engine.PaymentConfigDetails;
import com.halodoc.batavia.entity.pricing_engine.PaymentScheduleDetails;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.scrooge.PaymentsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("api/v1/payment-config")
@RestController
public class PaymentConfigController extends HalodocBaseApiController {
    @Autowired
    private PaymentsService paymentService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/payment-methods")
    @PreAuthorize("@authorizationService.isAuthorized('configuration','payment_configuration_view', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<PaymentConfigDetails> getPaymentMethods(@RequestParam(required = false, name = "per_page", defaultValue = "10") Integer per_page,
                                                                   @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer page_no) throws URISyntaxException {
        return paymentService.getPaymentMethods(per_page, page_no);
    }

    @GetMapping("/schedules")
    @PreAuthorize("@authorizationService.isAuthorized('configuration','payment_schedule_view', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<PaymentScheduleDetails> getSchedules(@RequestParam(required = false, name = "per_page", defaultValue = "10") Integer per_page,
                                                                     @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer page_no) throws URISyntaxException{
        return paymentService.getSchedules(per_page, page_no);
    }

    @PutMapping ("/edit/{paymentMethodId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','payment_configuration_edit','restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void editPaymentConfig(@PathVariable String paymentMethodId, @RequestBody EditPaymentConfigRequest editPaymentConfigRequest)
            throws URISyntaxException {
        paymentService.editPaymentConfig(paymentMethodId, editPaymentConfigRequest);
    }

    @PostMapping ("/create/schedule")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','payment_schedule_add','restricted_write')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public void createSchedule(@RequestBody EditPaymentConfigRequest editPaymentConfigRequest) throws URISyntaxException {
        paymentService.createSchedule(editPaymentConfigRequest);
    }

    @PutMapping ("/schedule/modify/{scheduleId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','payment_schedule_edit', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public String editScheduleConfig(@PathVariable String scheduleId, @RequestBody EditScheduleDetails editScheduleDetails)
            throws URISyntaxException {
        return paymentService.editSchedule(scheduleId, editScheduleDetails);
    }

    @DeleteMapping ("/schedule/delete/{scheduleId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','payment_schedule_delete', 'restricted_write')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public String deleteSchedule(@PathVariable String scheduleId) throws URISyntaxException{
        return paymentService.deleteSchedule(scheduleId);
    }

}
