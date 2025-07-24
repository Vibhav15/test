package com.halodoc.batavia.controller.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.halodoc.batavia.entity.ImageUpload;
import com.halodoc.batavia.entity.halolab.AttachmentUpload;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.ImageUploadService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("api/v1/upload")
@RestController
@Slf4j
public class ImageUploadAPIController extends HalodocBaseApiController {
    private final ImageUploadService uploaderService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public ImageUploadAPIController(ImageUploadService imageUploadService) {
        this.uploaderService = imageUploadService;
    }

    @GetMapping("/file/{uploadFolderName}/{fileExternalId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public ImageUpload getDocumentByFolderAndID(@PathVariable() String uploadFolderName, @PathVariable() String fileExternalId) {
        return uploaderService.getDocumentByFolderAndID(uploadFolderName, fileExternalId);
    }

    @GetMapping("/file/{fileExternalId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public ImageUpload getSignedDocumentByID(@PathVariable() String fileExternalId) {
        return uploaderService.getSignedDocumentByID(fileExternalId);
    }

    @PostMapping("/images/{upload_folder_name}")
    @ApiCategory (value = ApiType.FILE_UPLOAD, verticalName = Vertical.SRE)
    public ImageUpload uploadImage(@RequestParam("image") MultipartFile image,
                                   @PathVariable String upload_folder_name,
                                   @RequestParam(required = false, name = "X-File-Type") String xFileType,
                                   @RequestParam(required = false, name = "X-File-Name") String xFileName,
                                   @RequestParam(required = false, name = "X-Access-Control", defaultValue = "public") String xAccessControl,
                                   @RequestParam(name = "business_unit") String businessUnit,
                                   @RequestParam(name = "feature_name") String featureName) throws IOException {
        if (!authorizationService.isAuthorized(businessUnit, featureName, "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return uploaderService.upload(image.getInputStream(), upload_folder_name, xFileType, xFileName, xAccessControl);
    }

    @PostMapping ("/images/{upload_folder_name}/articles")
    @ApiCategory (value = ApiType.FILE_UPLOAD, verticalName = Vertical.SRE)
    public Map uploadImageArticles(@RequestParam ("image") MultipartFile image, @PathVariable String upload_folder_name,
            @RequestParam (required = false, name = "X-File-Type") String xFileType,
            @RequestParam (required = false, name = "X-File-Name") String xFileName, @RequestParam (name = "business_unit") String businessUnit,
            @RequestParam (name = "feature_name") String featureName) throws IOException {
        if (!authorizationService.isAuthorized(businessUnit, featureName, "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return uploaderService.uploadArticleImages(image, upload_folder_name, xFileType, xFileName);
    }

    @PostMapping("/file/{upload_folder_name}")
    @ApiCategory (value = ApiType.FILE_UPLOAD, verticalName = Vertical.SRE)
    public ImageUpload uploadFile(@RequestParam("content") String content,
                                  @PathVariable String upload_folder_name,
                                  @RequestParam(required = false, name = "X-File-Type") String xFileType,
                                  @RequestParam(required = false, name = "X-File-Name") String xFileName,
                                  @RequestParam(required = false, name = "X-Access-Control", defaultValue = "public") String xAccessControl,
                                  @RequestParam(name = "business_unit") String businessUnit,
                                  @RequestParam(name = "feature_name") String featureName) throws IOException {
        if (!authorizationService.isAuthorized(businessUnit, featureName, "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return uploaderService.upload(stream, upload_folder_name, xFileType, xFileName, xAccessControl);
    }

    @PostMapping("/lab-result-attachment")
    @ApiCategory (value = ApiType.FILE_UPLOAD, verticalName = Vertical.SRE)
    public AttachmentUpload uploadLabResultAttachmentFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("attributes") String attributesData) throws IOException {
        if (!authorizationService.isAuthorized("lab_service", "order_actions", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }

        try {
            return uploaderService.uploadAttachmentFile(file, attributesData);
        } catch (Exception  ex) {
            throw ex;
        }

    }
}
