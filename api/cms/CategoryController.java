package com.halodoc.batavia.controller.api.cms;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.cms.ArticleCategoryService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/internal/categories")
@RestController
@Slf4j
public class CategoryController extends HalodocBaseApiController {

    @Autowired
    ArticleCategoryService categoryService;

    @GetMapping ("group/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult getCategoryGroupSearch(@RequestParam(value="name", defaultValue="", required=false) String name,
                                                  @RequestParam(value="page", defaultValue="0", required=false) Integer page,
                                             @RequestParam(value="limit", defaultValue="10", required=false) Integer limit) throws URISyntaxException {
        return categoryService.searchCategoryGroups(name, page, limit);
    }

    @PatchMapping(path="group/update/{name}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Map updateSendToRssFlag(@PathVariable(value="name") String name, @RequestBody Map categoryGroup) throws URISyntaxException  {
        return categoryService.updateSendToRssFlag(name, categoryGroup);

    }

    @PostMapping(path="group")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Map createCategoryGroup(@RequestBody Map categoryGroup) throws URISyntaxException  {
        return categoryService.createCategoryGroup(categoryGroup);
    }

}
