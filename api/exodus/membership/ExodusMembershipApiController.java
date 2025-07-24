package com.halodoc.batavia.controller.api.exodus.membership;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.entity.exodus.misool.catalog.MemberListResponse;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import com.halodoc.batavia.service.exodus.tpa_benefit.ExodusTPABenefitService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/members")
public class ExodusMembershipApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    @Autowired private ExodusTPABenefitService exodusTPABenefitService;

    @GetMapping ("/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<MemberListResponse> searchMembers(@RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage,
            @RequestParam (required = false, name = "insurance_provider_id") String insuranceProviderId, @RequestParam (required = false) String name,
            @RequestParam (required = false) String member_id, @RequestParam (required = false) String policy_number,
            @RequestParam (required = false) String phone_number, @RequestParam (required = false) String e_card_number,
            @RequestParam (required = false) String dob , @RequestParam(required = false) Boolean include_inactive)
            throws URISyntaxException {
        return exodusMisoolCatalogService.searchMembers(pageNo, perPage, insuranceProviderId, name, member_id, phone_number, policy_number,
                e_card_number, dob,include_inactive);
    }

    @GetMapping("{memberExternalId}/coverages")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List memberCoverages(@PathVariable String memberExternalId) throws URISyntaxException {
        return exodusMisoolCatalogService.getMemberCoverages(memberExternalId);
    }

    @GetMapping ("{memberExternalId}/member-plans")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    List getMemberPlansBenefitCode(@PathVariable String memberExternalId,
            @RequestParam (required = false, name = "coverage_type") String coverageType, @RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false, name = "is_room_and_board") Boolean isRoomAndBoard) throws URISyntaxException {
        return exodusMisoolCatalogService.getMemberPlansBenefitCode(memberExternalId, coverageType, name, isRoomAndBoard);
    }

    @PutMapping ("{memberExternalId}/benefit-limits")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    List getMemberBenefitLimits(@RequestBody Map req) {
        return exodusTPABenefitService.fetchMemberBenefitLimits(req);
    }

    @PutMapping ("diagnosis-exclusion-detail")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    Map getDiagnosisExclusionId(@RequestBody Map req) throws URISyntaxException {
        return exodusMisoolCatalogService.getDiagnosisExclusionId(req);
    }
}
