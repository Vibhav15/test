package com.halodoc.batavia.controller.api.exodus.claims;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.claims.ClaimRoomBoardRequest;
import com.halodoc.batavia.entity.exodus.claims.ClaimRoomBoardResponse;
import com.halodoc.batavia.entity.exodus.claims.RoomClassMasterData;
import com.halodoc.batavia.entity.exodus.claims.SearchRoomClassId;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.bintan.HospitalProviderLocationService;
import com.halodoc.batavia.service.exodus.misool.ExodusMisoolCatalogService;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTPAClaimService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v1/exodus/claim-reimbursement/room-board")
public class ClaimRoomBoardApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusTPAClaimService exodusTPAClaimService;

    @Autowired
    private HospitalProviderLocationService hospitalProviderLocationService;

    @Autowired
    private ExodusMisoolCatalogService exodusMisoolCatalogService;

    /**************************************** Room and Board : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/{claimId}/rooms")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    ClaimRoomBoardResponse createRoomBoardService(@PathVariable String claimId, @RequestBody ClaimRoomBoardRequest claimRoomBoardRequest)
            throws URISyntaxException {
        return exodusTPAClaimService.createRoomBoardService(claimId, claimRoomBoardRequest);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PatchMapping ("/{claimId}/rooms/{roomId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    ClaimRoomBoardResponse updateRoomBoardService(@PathVariable String claimId, @PathVariable String roomId, @RequestBody Map roomBoardReq)
    throws URISyntaxException {
        return exodusTPAClaimService.updateRoomBoardService(claimId, roomId, roomBoardReq);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/rooms/{roomId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimRoomBoardResponse getRoomBoardDetails(@PathVariable String claimId, @PathVariable String roomId) throws URISyntaxException {
        return exodusTPAClaimService.getRoomBoardDetails(claimId, roomId);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_view', 'view_only')")
    @GetMapping ("/{claimId}/rooms")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimRoomBoardResponse[] getRoomBoardList(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getRoomBoardList(claimId);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @DeleteMapping ("/{claimId}/rooms/{roomId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteRoomBoardService(@PathVariable String claimId, @PathVariable String roomId) throws URISyntaxException {
        exodusTPAClaimService.deleteRoomBoardService(claimId, roomId);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'view_only')")
    @PutMapping ("/{providerId}/room-class-id")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    PaginatedResult<SearchRoomClassId> searchRoomClassId(@PathVariable String providerId, @RequestBody Map searchRoomClass)
    throws URISyntaxException {
        return hospitalProviderLocationService.searchRoomClassId(providerId, searchRoomClass);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'restricted_write')")
    @PostMapping ("/{providerLocationId}/room-class-id")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    RoomClassMasterData createRoomClassId(@PathVariable String providerLocationId, @RequestBody RoomClassMasterData roomClassMasterReq)
    throws URISyntaxException {
        return hospitalProviderLocationService.createRoomClassId(providerLocationId, roomClassMasterReq);
    }
    
    @PreAuthorize ("@authorizationService.isAuthorized('insurance','claim_reimbursement_add', 'view_only')")
    @PutMapping ("/{providerId}/room-board-benefit-info/{productId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    List<Map> getRoomBoardBenefitInfo(@PathVariable String providerId, @PathVariable String productId, @RequestBody Map benefitReq)
            throws URISyntaxException {
        return exodusMisoolCatalogService.getRoomBoardBenefitInfo(providerId, productId, benefitReq);
    }

    /**************************************** Room and Board : END ****************************************/
}
