package com.halodoc.batavia.controller.api.finance;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.finance.ReconCatalogService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Controller
@RequestMapping("api/v1/finance/recon-catalog")
@RestController
@Slf4j
public class ReconCatalogController extends HalodocBaseApiController {

    @Autowired
    ReconCatalogService reconCatalogService;

    @GetMapping("/bank-names")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP,verticalName = Vertical.ES)
    public List<String> getBankNameList() throws URISyntaxException {
        return reconCatalogService.getBankNameList();
    }
}
