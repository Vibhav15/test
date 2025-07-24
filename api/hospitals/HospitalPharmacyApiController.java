package com.halodoc.batavia.controller.api.hospitals;

import java.net.URISyntaxException;
import java.util.List;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.apache.commons.lang.StringUtils;
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
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.maluku.HospitalPharmacyUser;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.aru.AruService;
import com.halodoc.batavia.service.bali.logan.BaliLoganService;
import com.halodoc.batavia.service.timor.MerchantsService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping ("api/v1/hospital/pharmacy")
@RestController
@Slf4j
public class HospitalPharmacyApiController extends HalodocBaseApiController {
    @Autowired
    MerchantsService merchantsService;

    @Autowired
    private AruService aruService;

    @Autowired
    private BaliLoganService baliLoganService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping ("/{merchantId}/locations/{merchantLocationId}/users")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<HospitalPharmacyUser> getHospitalPharmacyUsers(@PathVariable String merchantId, @PathVariable String merchantLocationId)
            throws URISyntaxException {
        return aruService.fetchHospitalUsers(merchantId, merchantLocationId, "MEDICINE_DELIVERY");
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_user_add', 'restricted_write')")
    @PostMapping ("/users")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public void createHospitalPharmacyUser(@RequestBody HospitalPharmacyUser pharmacyUser) throws URISyntaxException {
        aruService.createHospitalPharmacyUser(pharmacyUser);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_user_edit', 'restricted_write')")
    @PutMapping ("/users/{gpId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void updateHospitalPharmacyUser(@PathVariable String gpId, @RequestBody HospitalPharmacyUser pharmacyUser) throws URISyntaxException {
        aruService.updatePharmacyUser(gpId, pharmacyUser);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_user_list', 'view_only')")
    @GetMapping ("/users")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<HospitalPharmacyUser> listHospitalPharmacyUser(
            @RequestParam (required = false, name = "page_no", defaultValue = "1") Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer perPage,
            @RequestParam (required = false, defaultValue = "") String status, @RequestParam (required = false, defaultValue = "") String gpid,
            @RequestParam (required = false, defaultValue = "") String role) throws URISyntaxException {
        List<HospitalPharmacyUser> pharmacyUsers;
        pharmacyUsers = aruService.list(status, role, pageNo, perPage, gpid);
        pharmacyUsers.parallelStream().forEach(pharmacyUser -> {
            if (StringUtils.isNotBlank(pharmacyUser.getGpid())) {
                try {
                    aruService.updateUserDetail(pharmacyUser);
                } catch (Exception ex) {
                    log.error("Could not find pharmacy user info for '" + pharmacyUser.getGpid() + "': " + ex.getMessage());
                }
            }
        });

        PaginatedResult<HospitalPharmacyUser> output = new PaginatedResult<>();
        output.setResult(pharmacyUsers);
        output.setNextPage(pharmacyUsers.size() == perPage);
        return output;
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_user_view', 'view_only')")
    @GetMapping ("/users/{gpid}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public HospitalPharmacyUser getPharmacyUser(@PathVariable String gpid, @RequestParam (required = false, defaultValue = "") String status)
            throws URISyntaxException {
        HospitalPharmacyUser pharmacyUser = aruService.getUser(gpid);
        aruService.updateUserDetail(pharmacyUser);
        return pharmacyUser;
    }
}
