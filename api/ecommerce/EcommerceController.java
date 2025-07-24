package com.halodoc.batavia.controller.api.ecommerce;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.cms.stores.Store;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.timor.PopupStoresService;
import com.halodoc.batavia.service.timor.TimorEcomGatewayService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.Map;

@RequestMapping("api/v1/ecommerce")
@RestController
public class EcommerceController extends HalodocBaseApiController {
    private final TimorEcomGatewayService timorEcomGatewayService;


    @Autowired
    public EcommerceController(TimorEcomGatewayService timorEcomGatewayService) {
        this.timorEcomGatewayService = timorEcomGatewayService;
    }


    @GetMapping("/stores/configuration")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public Map getStoreConfig(@RequestParam(required = false, name = "channel", defaultValue = "shopee") String channel
    ) throws URISyntaxException {

        return timorEcomGatewayService.getEcomStoreConfig(channel);

    }

}
