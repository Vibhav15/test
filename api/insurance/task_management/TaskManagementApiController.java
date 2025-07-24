package com.halodoc.batavia.controller.api.insurance.task_management;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.halodoc.batavia.service.exodus.task_management.TaskManagementService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("api/v1/tasks")
@RestController
@Slf4j
public class TaskManagementApiController extends HalodocBaseApiController {
    @Autowired
    private TaskManagementService taskManagementService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','analyst_claims_view', 'view_only')")
    @PutMapping("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    Map getTasks(@RequestBody Map task) throws URISyntaxException {
        return taskManagementService.getTasks(task);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','analyst_claims_view', 'view_only')")
    @PutMapping("/search-assignee")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    Map searchAssigneeTask(@RequestBody Map request)throws URISyntaxException {
        return taskManagementService.searchAssigneeTask(request);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','analyst_claims_view', 'view_only')")
    @PutMapping("/search-keyword")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    Map searchKeyword(@RequestBody Map request)throws URISyntaxException {
        return taskManagementService.searchKeyword(request);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','analyst_claims_edit', 'restricted_write')")
    @PatchMapping ("/{taskExternalId}/action")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map taskAction(@PathVariable String taskExternalId, @RequestBody Map task) throws URISyntaxException {
        return taskManagementService.taskAction(taskExternalId, task);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','analyst_claims_edit', 'restricted_write')")
    @PatchMapping ("/{taskExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map taskUpdate(@PathVariable String taskExternalId,
            @RequestBody Map task) throws URISyntaxException {
        return taskManagementService.taskUpdate(taskExternalId, task);
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('insurance','analyst_claims_add', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public Map createTask(@RequestBody Map task) throws IOException, URISyntaxException {
        return taskManagementService.createTask(task);
    }
}
