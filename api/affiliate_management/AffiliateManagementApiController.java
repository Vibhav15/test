package com.halodoc.batavia.controller.api.affiliate_management;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.dto.affiliate_management.AffiliateProgramDTO;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.affiliate_management.AffiliateManagementService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import jakarta.validation.constraints.Max;

@Controller
@RequestMapping ("api/v1/affiliate-management")
@RestController
public class AffiliateManagementApiController {
    @Autowired
    private AffiliateManagementService affiliateManagementService;

    @GetMapping ("/program")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<AffiliateProgramDTO> getAffiliatePrograms(@RequestParam (required = false, name = "page_number") Integer pageNumber,
            @RequestParam (required = false, name = "per_page") @Max (100) Integer perPage,
            @RequestParam (required = false, name = "name") String name, @RequestParam (required = false, name = "statuses") String statuses,
            @RequestParam (required = false, name = "start_time") Long startTime, @RequestParam (required = false, name = "end_time") Long endTime)
            throws URISyntaxException {
        return this.affiliateManagementService.getAffiliatePrograms(pageNumber, perPage, name, statuses, startTime, endTime);
    }

    @GetMapping ("/program/{programId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public AffiliateProgramDTO getAffiliateProgramById(@PathVariable String programId) throws URISyntaxException {
        return this.affiliateManagementService.getAffiliateProgramById(programId);
    }

    @PostMapping ("/program")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public AffiliateProgramDTO createAffiliateProgram(@RequestBody AffiliateProgramDTO request) throws URISyntaxException {
        return this.affiliateManagementService.createAffiliateProgram(request);
    }

    @PutMapping ("/program/{programId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateAffiliateProgram(@RequestBody AffiliateProgramDTO request, @PathVariable String programId) throws URISyntaxException {
        this.affiliateManagementService.updateAffiliateProgram(request, programId);
    }

    @PutMapping ("/program/{programId}/{status}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateAffiliateProgramStatus(@PathVariable String programId, @PathVariable String status) throws URISyntaxException {
        this.affiliateManagementService.updateAffiliateProgramStatus(programId, status);
    }
}
