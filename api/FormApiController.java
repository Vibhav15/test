package com.halodoc.batavia.controller.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.halodoc.batavia.entity.ImageUpload;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.entity.configuration.forms_module.FormConfiguration;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.FormService;
import com.halodoc.batavia.service.ImageUploadService;
import com.halodoc.config.ConfigClient;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RestController
@RequestMapping (value = "api/v1/form")
@Tag (name = "form", description = "Operations of digital clinic")
public class FormApiController extends HalodocBaseApiController {
    private FormService formService;

    private ImageUploadService uploaderService;

    @Autowired
    private ConfigClient<BataviaAppConfiguration> configClient;

    @Autowired
    public FormApiController(FormService formService, ImageUploadService uploaderService) {
        this.formService = formService;
        this.uploaderService = uploaderService;
    }

    @GetMapping ("/group/{formExternalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_group_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Map getFormGroup(@PathVariable String formExternalId) {
        return formService.getFormGroup(formExternalId);
    }

    @GetMapping ("/individual/{formExternalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_view', 'view_only')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Map getForm(@PathVariable String formExternalId) {
        return formService.getForm(formExternalId);
    }

    @PostMapping ("/individual/create")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_update', 'view_only')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Map createForm(@RequestBody Map formDetail) {
        return formService.createForm(formDetail);
    }

    @PostMapping ("/group/create")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_group_update', 'view_only')")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Map createGroupForm(@RequestBody Map formDetail) {
        return formService.createFormGroup(formDetail);
    }

    @PutMapping ("/individual/{formExternalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_update', 'view_only')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Map editForm(@PathVariable String formExternalId, @RequestBody Map formDetail) {
        return formService.editForm(formExternalId, formDetail);
    }

    @PutMapping ("/group/{formExternalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_group_update', 'view_only')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Map editFormGroup(@PathVariable String formExternalId, @RequestBody Map formDetail) {
        return formService.editFormGroup(formExternalId, formDetail);
    }

    @DeleteMapping ("/group/{formExternalId}")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_group_update', 'view_only')")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Map deleteFormGroup(@PathVariable String formExternalId) {
        return formService.deleteFormGroup(formExternalId);
    }

    @GetMapping("/individual/list")
    @PreAuthorize("@authorizationService.isAuthorized('configuration','form_module_view', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<Map> getFormList(@RequestParam(name = "name", required = false) String searchText,
                                            @RequestParam(name = "page_no", required = false, defaultValue = "1") int pageNumber,
                                            @RequestParam(name = "per_page", required = false, defaultValue = "10") int page,
                                            @RequestParam(name = "status", required = false) String status) {
        Map<String, Object> request = new HashMap<>();

        request.put("page_no", pageNumber);
        request.put("per_page", page);

        if (StringUtils.isNotBlank(searchText)) {
            request.put("name", searchText);
        }

        if (StringUtils.isNotBlank(status)) {
            request.put("status", status);
        }

        return formService.getFormList(request);
    }

    @GetMapping ("/group/list")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_group_view', 'view_only')")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<Map> getFormGroupList(@RequestParam (name = "name", required = false, defaultValue = "") String searchText,
            @RequestParam (name = "page_no", required = false, defaultValue = "1") int pageNumber,
            @RequestParam (name = "per_page", required = false, defaultValue = "10") int page) {
        return formService.getFormGroupList(searchText, pageNumber, page);
    }

    @PostMapping ("/upload")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_update', 'view_only')")
    @ApiCategory (value = ApiType.FILE_UPLOAD, verticalName = Vertical.SRE)
    public ImageUpload uploadImage(@RequestParam ("file") MultipartFile file, @RequestParam (required = false, name = "X-File-Type") String xFileType,
            @RequestParam (required = false, name = "X-File-Name") String xFileName,
            @RequestParam (required = false, name = "X-Access-Control", defaultValue = "public") String xAccessControl) throws IOException {

        return uploaderService.upload(file.getInputStream(), "form", xFileType, xFileName, xAccessControl);
    }

    @GetMapping ("/config")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','form_module_update', 'restricted_write')")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public FormConfiguration getFormConfiguration() {
        FormConfiguration formConfiguration = configClient.getAppConfig().getFormConfiguration();
        return FormConfiguration.builder().questionTypes(formConfiguration.getQuestionTypes()).elementConfig(formConfiguration.getElementConfig())
                                .build();
    }
}
