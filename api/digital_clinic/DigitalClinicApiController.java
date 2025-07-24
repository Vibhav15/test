package com.halodoc.batavia.controller.api.digital_clinic;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.configuration.DigitalClinicConfiguration;
import com.halodoc.batavia.service.digital_clinic.DigitalClinicService;
//import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping(value = "api/v1/digital-clinic")
@Tag(name = "digital-clinic", description = "Operations of digital clinic")
public class DigitalClinicApiController extends HalodocBaseApiController {

    private DigitalClinicService digitalClinicService;

    @Autowired
    public DigitalClinicApiController(DigitalClinicService digitalClinicService) {
        this.digitalClinicService = digitalClinicService;
    }

    @GetMapping("/configs")
    public DigitalClinicConfiguration getConfig() {
        return digitalClinicService.getConfig();
    }
}
