package com.halodoc.batavia.controller.api.subscriptions;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.subscriptions.SubscriptionsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/subscriptions/vas")
@RestController
@Slf4j
public class VasApiController extends HalodocBaseApiController {

    @Autowired
    SubscriptionsService subscriptionService;

    @PreAuthorize("@authorizationService.isAuthorized('subscriptions','subscriptions_list', 'view_only')")
    @GetMapping
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map> getPaginatedVas(@RequestParam(required = false, name = "name") String name,
                                                                 @RequestParam(required = false, name = "statuses", defaultValue = "active,inactive") String status,
                                                                 @RequestParam(required = false, name = "perPage", defaultValue = "10") Integer per_page,
                                                                 @RequestParam(required = false, name = "pageNo", defaultValue = "1") Integer page_no,
                                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                                 @RequestParam(required = false, defaultValue = "created_at") String sort_by, @RequestParam(required = false, defaultValue = "desc") String sort_order) throws URISyntaxException {

        return subscriptionService.paginatedVasList( name, status, per_page, page_no, startDate, endDate, sort_by, sort_order);
    }

    @PreAuthorize("@authorizationService.isAuthorized('subscriptions','subscriptions_list', 'view_only')")
    @GetMapping("/{vasId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Map getVasById(@PathVariable String vasId) throws URISyntaxException {

        return subscriptionService.getVasById(vasId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('subscriptions','subscriptions_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map createVas(@RequestBody Map vasRequest) throws URISyntaxException {

        return subscriptionService.createVas(vasRequest);

    }

    @PreAuthorize("@authorizationService.isAuthorized('subscriptions','subscriptions_edit', 'restricted_write')")
    @PutMapping("/{vasId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateVas(@PathVariable String vasId, @RequestBody Map vasRequest) throws URISyntaxException {
        subscriptionService.updateVas(vasId, vasRequest);
    }

}
