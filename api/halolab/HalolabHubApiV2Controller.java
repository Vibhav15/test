package com.halodoc.batavia.controller.api.halolab;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.halolab.AvailableSlots;
import com.halodoc.batavia.entity.halolab.PaginatedHalolabHubSearchResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.halolab.HalolabHubService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("api/v2/halolabs/halolab-hubs")
@RestController
public class HalolabHubApiV2Controller extends HalodocBaseApiController {
    private HalolabHubService halolabHubService;

    @Autowired
    public HalolabHubApiV2Controller(HalolabHubService halolabHubService) {
        this.halolabHubService = halolabHubService;
    }

    @GetMapping("/demand-zones/schedules/{demandZoneId}/availability")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    List<AvailableSlots> getSlotAvailability(
            @PathVariable String demandZoneId,
            @RequestParam(value = "inventory_types", required = true) String inventoryTypes
    ) throws URISyntaxException {

        return halolabHubService.getAvailableDatesV2(demandZoneId, inventoryTypes);
    }

    @GetMapping("/demand-zones/schedules/{demandZoneId}/slots")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CODI)
    List getAvailableSlots(
            @PathVariable String demandZoneId,
            @RequestParam(name = "start_date", defaultValue = "", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
            @RequestParam(value = "inventory_types", required = true) String inventoryTypes
    ) throws URISyntaxException {
        return halolabHubService.getAvailableSlotsV2(demandZoneId, startDate, inventoryTypes);
    }
}
