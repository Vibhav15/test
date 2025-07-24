package com.halodoc.batavia.controller.api.pharmacy;


import com.halodoc.batavia.entity.ImageUpload;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.pharmacy.DocumentTransformation;
import com.halodoc.batavia.entity.pharmacy.DocumentTransformationRequest;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.ImageUploadService;
import com.halodoc.batavia.service.pharmacy.DocumentTransformationService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Controller
@RequestMapping("api/v1/document-transformation")
@RestController
public class DocumentTransformationApiController {


    @Autowired
    DocumentTransformationService documentTransformationService;
    @Autowired
    ImageUploadService uploaderService;

    @GetMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','document_transformation', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.PD)
    public PaginatedResult<DocumentTransformation> getDocumentTransformations(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(required = false, name = "per_page", defaultValue = "10") Integer perPage,
                                                                 @RequestParam(required = false, name = "type", defaultValue = "replenishment") String type) throws URISyntaxException {
        return documentTransformationService.getDocumentTransformations(perPage, pageNo, type);
    }

    @PostMapping
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','document_transformation', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.PD)
    public void addDocument(@RequestBody DocumentTransformationRequest request) {
        documentTransformationService.addDocument(request);
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','document_transformation', 'view_only')")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.PD)
    public DocumentTransformation get(@PathVariable String documentId) {
        return documentTransformationService.getDocumentTransformation(documentId);
    }

    @PostMapping("/upload")
    @PreAuthorize("@authorizationService.isAuthorized('pharmacy_delivery','document_transformation', 'restricted_write')")
    @ApiCategory(value = ApiType.FILE_UPLOAD, verticalName = Vertical.PD)
    public ImageUpload uploadImage(@RequestParam("file") MultipartFile file,
                                   @RequestParam(required = false, name = "X-File-Type") String xFileType,
                                   @RequestParam(required = false, name = "X-File-Name") String xFileName,
                                   @RequestParam(required = false, name = "X-Access-Control", defaultValue = "public") String xAccessControl) throws IOException {

        return uploaderService.upload(file.getInputStream(), "document-transformation", xFileType, xFileName, xAccessControl);
    }
}
