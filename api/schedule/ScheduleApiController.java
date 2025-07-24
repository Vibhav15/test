package com.halodoc.batavia.controller.api.schedule;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.entity.misool.BenefitCode;
import com.halodoc.batavia.entity.misool.Provider;
import com.halodoc.batavia.entity.omega.*;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.misool.MisoolService;
import com.halodoc.batavia.service.omega.OmegaService;
import com.halodoc.batavia.util.SpringSecurityUtil;
import com.halodoc.config.ConfigClient;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.NoSuchPaddingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1/schedules")
@RestController
@Slf4j
public class ScheduleApiController extends HalodocBaseApiController {

    private String getSecureToken() {
        return this.configClient.getAppConfig().getOmegaConfiguration().getSecureToken();
    }

    @Autowired
    private OmegaService omegaService;

    @Autowired
    private MisoolService misoolService;

    @Autowired
    private AuthorizationService authorizationService;

    private ConfigClient<BataviaAppConfiguration> configClient;

    @Autowired
    public ScheduleApiController(ConfigClient<BataviaAppConfiguration> configClient) {
        this.configClient = configClient;
    }

    @GetMapping
    @PreAuthorize("@authorizationService.isAuthorized('report','schedule_list','view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    PaginatedResult<Schedule> getSchedules(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "active") String status,
            @RequestParam(required = false, name = "type") String type) throws URISyntaxException {
        return omegaService.getSchedules(name, pageNo, perPage, status, type);
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('report','schedule_add','restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.ES)
    Schedule saveSchedules(@RequestBody Schedule schedule) throws URISyntaxException {
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        schedule.setCreatedBy(userName);
        schedule.setCreatorName(userName);
        return omegaService.saveSchedule(schedule);
    }

    @PutMapping("/{scheduleId}")
    @PreAuthorize("@authorizationService.isAuthorized('report','schedule_edit','restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)

    Schedule updateSchedules(@PathVariable String scheduleId, @RequestBody Schedule schedule)
            throws URISyntaxException {
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        schedule.setUpdatedBy(userName);
        schedule.setUpdaterName(userName);
        return omegaService.updateSchedule(scheduleId, schedule);
    }

    @PutMapping("/{scheduleId}/activate")
    @PreAuthorize("@authorizationService.isAuthorized('report','schedule_edit','restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)
    Schedule activateSchedule(@PathVariable String scheduleId, @RequestBody Schedule schedule)
            throws URISyntaxException {
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        schedule.setUpdatedBy(userName);
        schedule.setUpdaterName(userName);
        return omegaService.activateSchedule(scheduleId, schedule);
    }

    @PutMapping("/{scheduleId}/deactivate")
    @PreAuthorize("@authorizationService.isAuthorized('report','schedule_edit','restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.ES)
    Schedule deactivateSchedule(@PathVariable String scheduleId, @RequestBody Schedule schedule)
            throws URISyntaxException {
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        schedule.setUpdatedBy(userName);
        schedule.setUpdaterName(userName);
        return omegaService.deactivateSchedule(scheduleId, schedule);
    }

    @GetMapping("/{scheduleId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    Schedule getSchedule(@PathVariable String scheduleId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("report", "schedule_view", "view_only")
                && !authorizationService.isAuthorized("report", "manual_trigger", "restricted_write")
                && !authorizationService.isAuthorized("report", "schedule_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return omegaService.getSchedule(scheduleId);
    }

    @GetMapping("/insurance/{providerId}/benefit-codes")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    List<BenefitCode> getBenefitCodes(@PathVariable String providerId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("report", "schedule_view", "view_only")
                && !authorizationService.isAuthorized("report", "manual_trigger", "restricted_write")
                && !authorizationService.isAuthorized("report", "schedule_edit", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        PaginatedResult<Provider> providerPaginatedResult = misoolService.getProviders(null, 1, 500,
                "active", null, null, providerId);

        PaginatedResult<BenefitCode> benefitCodePaginatedResult = misoolService
                .getBenefitCodes(providerPaginatedResult.getResult().get(0).getExternalId());
        return benefitCodePaginatedResult.getResult();
    }

    @GetMapping("/types")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    @PreAuthorize("@authorizationService.isAuthorized('report','schedule_list','view_only')")
    PaginatedResult<Task> getTasks(@RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage)
            throws URISyntaxException {
        return omegaService.getTaskTypes(name, perPage);
    }

    @PostMapping("/tasks")
    @PreAuthorize("@authorizationService.isAuthorized('report','manual_trigger','restricted_write')")
    @ApiCategory(value = ApiType.ASYNC_EVENT_HANDLER, verticalName = Vertical.ES)
    Map submitTask(@RequestBody Schedule schedule) throws URISyntaxException {
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        schedule.setCreatedBy(userName);
        schedule.setCreatorName(userName);
        return omegaService.submitTaskRequest(schedule);
    }

    @GetMapping("/tasks/{identifier}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.ES)
    Map getTaskStatus(@PathVariable String identifier) throws URISyntaxException {
        return omegaService.getTaskStatus(identifier);
    }

    @GetMapping("/report")
    @PreAuthorize("@authorizationService.isAuthorized('report','history','view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.ES)
    PaginatedResult<Report> getTaskReports(
            @RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam(required = false, name = "sort_order", defaultValue = "desc") String sortOrder,
            @RequestParam(required = false, name = "sort_by", defaultValue = "created_at") String sortBy,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "statuses", defaultValue = "started,cancelled,completed,failed,submitted") String statuses,
            @RequestParam(required = false, name = "type") String type) throws URISyntaxException {
        return omegaService.getTasksReport(name, pageNo, perPage, statuses, type, sortBy, sortOrder);
    }

    @PostMapping("/secure-text")
    @ApiCategory(value = ApiType.SCHEDULER_SCRIPT, verticalName = Vertical.ES)
    Map secureTexts(@RequestBody SecureTextRequest secureTextRequest) {
        Map<String, String> secureTexts = new HashMap<>();

        secureTextRequest.getSource().forEach(source -> {
            try {
                if (secureTextRequest.getType().equals(SecureTextRequest.SecureTextType.encrypt)) {
                    secureTexts.put(source, encrypt(source));
                }

                if (secureTextRequest.getType().equals(SecureTextRequest.SecureTextType.decrypt)) {
                    secureTexts.put(source, decrypt(source));
                }
            } catch (NoSuchPaddingException ex1) {
                log.debug(ex1.getMessage());
            } catch (NoSuchAlgorithmException ex2) {
                log.debug(ex2.getMessage());
            } catch (InvalidKeyException ex3) {
                log.debug(ex3.getMessage());
            } catch (BadPaddingException ex4) {
                log.debug(ex4.getMessage());
            } catch (IllegalBlockSizeException ex5) {
                log.debug(ex5.getMessage());
            }
        });
        return secureTexts;
    }

    private String encrypt(String source) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(getSecureToken().getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encoded = cipher.doFinal(source.getBytes());
        return new String(Base64.encodeBase64(encoded));
    }

    private String decrypt(String source) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(getSecureToken().getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] encodedBytes = source.getBytes();
        byte[] base64decodedBytes = Base64.decodeBase64(encodedBytes);
        byte[] decoded = cipher.doFinal(base64decodedBytes);
        return new String(decoded);
    }
}
