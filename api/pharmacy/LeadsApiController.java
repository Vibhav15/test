package com.halodoc.batavia.controller.api.pharmacy;


import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.oms.Order;
import com.halodoc.batavia.entity.oms.OrderCancel;
import com.halodoc.batavia.entity.oms.OrderItem;
import com.halodoc.batavia.entity.oms.leads.Lead;
import com.halodoc.batavia.entity.oms.leads.LeadRejectRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.timor.LeadsService;
import com.halodoc.batavia.service.timor.TimorOmsService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/v1/leads")
@RestController
@Slf4j
public class LeadsApiController extends HalodocBaseApiController {


    private final LeadsService leadsService;
    private final TimorOmsService timorOmsService;


    @Autowired
    public LeadsApiController(LeadsService leadsService, TimorOmsService timorOmsService) {
        this.leadsService = leadsService;
        this.timorOmsService = timorOmsService;
    }


    @GetMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','lead_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<Lead> listLeads(@RequestParam(required = false, name = "lead_id") String leadId,
                                           @RequestParam(required = false, name = "start_lead_date")
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                           @RequestParam(required = false, name = "end_lead_date")
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                           @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                           @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                           @RequestParam(required = false, name = "customer_entity_id") String customerEntityId,
                                           @RequestParam(required = false, name = "status",
                                                   defaultValue = "rejected,processed,pending") String status,
                                           @RequestParam(required = false, name = "sort_field", defaultValue = "created_at") String sortField,
                                           @RequestParam(required = false, name = "sources", defaultValue = "") String sources,
                                           @RequestParam(required = false, name = "attribute_source", defaultValue = "") String attributeSource,
                                           @RequestParam(required = false, name = "sort_order") String sortOrder) throws URISyntaxException {

        if (!StringUtils.isBlank(leadId)) {
            Lead lead = leadsService.getLead(leadId);
            PaginatedResult<Lead> paginatedResult = new PaginatedResult<>();
            paginatedResult.setNextPage(false);
            paginatedResult.setResult(Collections.singletonList(lead));
            return paginatedResult;
        } else {
            return leadsService.search(startDate, endDate, pageNo, perPage, customerEntityId,
                    status, sortField, sortOrder, sources, attributeSource);
        }
    }


    @GetMapping("/{leadId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','lead_view', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public Lead getLead(@PathVariable String leadId) throws URISyntaxException  {
        return leadsService.getLead(leadId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','lead_view', 'view_only')")
    @GetMapping("/{orderId}/documents")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public List getOrderDocuments(@PathVariable String orderId) {
        return leadsService.getLeadsDocument(orderId);
    }

    @PutMapping("/{leadId}/reject")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','lead_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.PD)
    public void rejectLead(@PathVariable String leadId, @RequestBody(required = false) LeadRejectRequest leadRejectRequest) throws URISyntaxException {
        Lead lead = leadsService.getLead(leadId);
        if (!StringUtils.isBlank(lead.getCustomerOrderId())) {
            Order order = timorOmsService.getOrderStatusById(lead.getCustomerOrderId());
            if (order.getStatus().equals(Order.OMS_STATUS_CREATED)
                    || order.getStatus().equals(Order.OMS_STATUS_APPROVED)) {
                OrderCancel orderCancel = new OrderCancel();
                orderCancel.setType(OrderCancel.TYPE_CS);
                orderCancel.setReason(leadRejectRequest.getReason());
                orderCancel.setNote(leadRejectRequest.getComments());
                Set<String> groupIdSet = order.getItems().stream()
                        .map(OrderItem::getGroupId).collect(Collectors.toSet());
                groupIdSet.forEach(gp -> {
                    try {
                        timorOmsService.abandonOrder(lead.getCustomerOrderId(), gp, orderCancel);
                    } catch (Exception e) {
                        log.error("Failed reject order {}", lead.getCustomerOrderId());
                    }
                });

            }
        } else {
            leadsService.reject(leadId, leadRejectRequest);
        }
    }
}
