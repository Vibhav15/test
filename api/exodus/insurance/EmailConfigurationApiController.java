package com.halodoc.batavia.controller.api.exodus.insurance;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.communication.ExodusEmailConfiguration;
import com.halodoc.batavia.entity.exodus.product.ProductEntity;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.communication.ExodusCommunicationService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/email-configuration")
public class EmailConfigurationApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @Autowired
    private ExodusCommunicationService exodusCommunicationService;
    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<ExodusEmailConfiguration> searchEmailConfiguration(
            @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "pic_name", defaultValue = DEFAULT_STRING) String picName,
            @RequestParam (required = false, name = "insurance_provider_id", defaultValue = DEFAULT_STRING)@NotBlank String insuranceProviderId,
            @RequestParam (required = false, name = "product_name", defaultValue = DEFAULT_STRING) String productName,
            @RequestParam (required = false, name = "email", defaultValue = DEFAULT_STRING) String email,
            @RequestParam (required = false, name = "status", defaultValue = DEFAULT_STRING) String status) throws URISyntaxException {
        return exodusCommunicationService.searchEmailConfiguration(pageNo, perPage, picName, insuranceProviderId, productName, email, status);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_add', 'restricted_write')")
    @PostMapping
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ExodusEmailConfiguration createWhatsappConfiguration(@RequestBody ExodusEmailConfiguration request) throws URISyntaxException {
        return exodusCommunicationService.createEmail(request);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/{templateExternalId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ExodusEmailConfiguration getEmailConfigById(@PathVariable (required = true, name = "templateExternalId") String templateExternalId)
            throws URISyntaxException {
        return exodusCommunicationService.getEmailConfigById(templateExternalId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','insurance_provider_edit', 'restricted_write')")
    @PutMapping ("/{templateExternalId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateEmailComfiguratuin(@PathVariable (required = true) @NotBlank String templateExternalId, @RequestBody ExodusEmailConfiguration request)
            throws URISyntaxException {
        return exodusCommunicationService.updateEmailConfiguration(templateExternalId, request);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','insurance_provider_view', 'view_only')")
    @GetMapping ("/products/{providerExternalId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<ProductEntity> getProductsByProviderId(@PathVariable (required = true, name = "providerExternalId") @NotBlank String providerExternalId,
            @RequestParam (required = false) String name) throws URISyntaxException {
        return exodusMisoolCatalogService.getProductsByProviderId(providerExternalId, name);
    }

}
