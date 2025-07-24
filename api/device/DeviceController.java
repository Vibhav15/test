package com.halodoc.batavia.controller.api.device;

import java.net.URISyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.customer.DeviceInfo;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.device.DeviceService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("api/v1/devices")
@RestController
public class DeviceController extends HalodocBaseApiController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/{entity_id}/device-applications/{application_type}")
    public List<DeviceInfo> getDeviceApplicationInformation(@PathVariable("entity_id") @NotNull String entityId,
                                                            @PathVariable("application_type") @NotNull String applicationType,
                                                            @RequestParam(value = "account_status", required = false, defaultValue = "logged_in") String accountStatus) throws URISyntaxException {

        switch (applicationType) {
            case "customer":
                if (!authorizationService.isAuthorized("customer", "customer_view", "view_only")) {
                    throw HalodocWebException.builder().statusCode(403).message("Access Denied").code("403").build();
                }
                break;
            case "doctor":
                if (!authorizationService.isAuthorized("contact_doctor", "doctor_view", "view_only")) {
                    throw HalodocWebException.builder().statusCode(403).message("Access Denied").code("403").build();
                }
                break;
            case "pharmacy":
                if (!authorizationService.isAuthorized("pharmacy_delivery","merchant_location_view", "view_only")) {
                    throw HalodocWebException.builder().statusCode(403).message("Access Denied").code("403").build();
                }
                break;
            case "h4h":
                if (!authorizationService.isAuthorized("hospitals_management","hospital_pharmacy_view", "view_only")) {
                    throw HalodocWebException.builder().statusCode(403).message("Access Denied").code("403").build();
                }
                break;
            default:
                throw HalodocWebException.builder().statusCode(403).message("Access Denied").code("403").build();
        }


        return deviceService.getDeviceInfo(entityId, applicationType, accountStatus);
    }
}
