package com.halodoc.batavia.controller.api.nudges;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.nudges.NudgeService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("api/v1/nudges")
@RestController
@Slf4j
public class NudgeController extends HalodocBaseApiController {

    @Autowired
    NudgeService nudgeService;

    @Autowired
    AuthorizationService authorizationService;

    enum Features{
        ticker_view,ticker_add,ticker_edit,nudge_list,nudge_add,nudge_edit
    }

    enum AccessLevel{
        none,full,restricted_write,view_only
    }

    static final private String businessUnit="nudge_management";

    @GetMapping("nudge-type")
    @PreAuthorize("@authorizationService.isAuthorized('nudge_management','nudge_list', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<String> getNudgeTypes()
            throws URISyntaxException {
        return nudgeService.getNudgeTypes();
    }

    @GetMapping("templates")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public List<Object> getTemplates(@RequestParam(required = false, name = "status") String status,
                                                             @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                             @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage
    )
            throws URISyntaxException {
        return nudgeService.getTemplates(status, pageNo, perPage);
    }

    @PostMapping("templates")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map<String, Object> createTemplate(@RequestBody Map<String, Object> template)
            throws URISyntaxException {
        return nudgeService.createTemplate(template);
    }

    @GetMapping("nudge-templates/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public PaginatedResult<Map<String, Object>> searchNudgeTemplates(@RequestParam(required = false, name = "status") String status,
                                                                     @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                     @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                                     @RequestParam(required = false, name = "name") String name,
                                                                     @RequestParam(required=false, name="channel") String channel
    )
            throws URISyntaxException {
        if(!StringUtils.isBlank(channel) && channel.equals("ticker")) {
            if (!authorizationService.isAuthorized(businessUnit, Features.ticker_view.toString(), AccessLevel.view_only.toString())) {
                throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
            }
        }
        else
        if(!authorizationService.isAuthorized(businessUnit,Features.nudge_list.toString(),AccessLevel.view_only.toString())){
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return nudgeService.searchNudgeTemplates(status, pageNo, perPage, name,channel);
    }

    @GetMapping("events")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<String> getNudgeTemplateEvents(@RequestParam(required = false, name = "status") String status,
                                                                       @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                       @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                                       @RequestParam(required = false, name = "name") String name
    )
            throws URISyntaxException {
        return nudgeService.getNudgeTemplateEvents(status, pageNo, perPage);
    }

    @GetMapping("schedule-by-events")
    @PreAuthorize("@authorizationService.isAuthorized('nudge_management','nudge_list', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<String> getNudgeTemplateScheduleByEvents(@RequestParam(required = false, name = "status") String status,
                                               @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                               @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                               @RequestParam(required = false, name = "name") String name
    )
            throws URISyntaxException {
        return nudgeService.getNudgeTemplateScheduleByEvents(status, pageNo, perPage);
    }

    @GetMapping("nudge-templates/{nudge_template_id}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public Map<String, Object> getNudgeTemplateById(
            @PathVariable(required = true, name = "nudge_template_id") String nudgeTemplateId,
            @RequestParam(required=false, name="channel") String channel)
            throws URISyntaxException {

        if(!StringUtils.isBlank(channel) && channel.equals("ticker")) {
            if (!authorizationService.isAuthorized(businessUnit, Features.ticker_view.toString(), AccessLevel.view_only.toString())) {
                throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
            }
        }
        else
        if(!authorizationService.isAuthorized(businessUnit,Features.nudge_list.toString(),AccessLevel.view_only.toString())){
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return nudgeService.getNudgeTemplateById(nudgeTemplateId);
    }

    @PostMapping("nudge-templates")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.SRE)
    public Map<String, Object> createNudgeTemplate(
            @RequestBody Map<String, Object> nudgeTemplateBody,
            @RequestParam(required=false, name="channel") String channel
            )
            throws URISyntaxException {

        if(!StringUtils.isBlank(channel) && channel.equals("ticker")) {
            if (!authorizationService.isAuthorized(businessUnit, Features.ticker_add.toString(), AccessLevel.restricted_write.toString())) {
                throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
            }
        }
        else
        if(!authorizationService.isAuthorized(businessUnit,Features.nudge_add.toString(),AccessLevel.restricted_write.toString())){
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        return nudgeService.createNudgeTemplate(nudgeTemplateBody);
    }

    @PutMapping("nudge-templates/{nudge_template_id}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.SRE)
    public void updateNudgeTemplate(
            @PathVariable(name = "nudge_template_id") String nudgeTemplateId,
            @RequestBody Map<String, Object> nudgeTemplateBody,
            @RequestParam(required=false, name="channel") String channel
            )
            throws URISyntaxException {

        if(!StringUtils.isBlank(channel) && channel.equals("ticker")) {
            if (!authorizationService.isAuthorized(businessUnit, Features.ticker_edit.toString(), AccessLevel.restricted_write.toString())) {
                throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
            }
        }
        else
        if(!authorizationService.isAuthorized(businessUnit,Features.nudge_edit.toString(),AccessLevel.restricted_write.toString())){
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        nudgeService.updateNudgeTemplate(nudgeTemplateId, nudgeTemplateBody);
    }

}
