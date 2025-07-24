package com.halodoc.batavia.controller.api.hospitals;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
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
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.exodus.claims.RoomClassMasterData;
import com.halodoc.batavia.entity.exodus.claims.SearchRoomClassId;
import com.halodoc.batavia.service.bintan.HospitalProviderLocationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping ("api/v1/hospitals/provider-location/{providerLocationId}/room-classes")
@RestController
public class HospitalProvidersRoomBoardApiController extends HalodocBaseApiController {
    @Autowired
    private HospitalProviderLocationService hospitalProviderLocationService;

    /**************************************** Room and Board Master : START ****************************************/
    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_view', 'view_only')")
    @PutMapping ()
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    PaginatedResult<SearchRoomClassId> searchRoomClassId(@PathVariable String providerLocationId, @RequestBody Map searchRoomClass)
            throws URISyntaxException {

        return hospitalProviderLocationService.searchRoomClassId(providerLocationId, searchRoomClass);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_add', 'restricted_write')")
    @PostMapping ()
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    RoomClassMasterData createRoomClassId(@PathVariable String providerLocationId, @RequestBody RoomClassMasterData roomClassMasterReq)
            throws URISyntaxException {
        return hospitalProviderLocationService.createRoomClassId(providerLocationId, roomClassMasterReq);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_view', 'view_only')")
    @GetMapping ("/{roomBoardId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    RoomClassMasterData getRoomBoardDetails(@PathVariable String providerLocationId, @PathVariable String roomBoardId) throws URISyntaxException {
        return hospitalProviderLocationService.getRoomBoardDetails(providerLocationId, roomBoardId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @PatchMapping ("/{roomBoardId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    RoomClassMasterData updateRoomBoardDetail(@PathVariable String providerLocationId, @PathVariable String roomBoardId,
            @RequestBody Map roomBoardReq) throws URISyntaxException {
        return hospitalProviderLocationService.updateRoomBoardDetail(providerLocationId, roomBoardId, roomBoardReq);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_edit', 'restricted_write')")
    @DeleteMapping ("/{roomBoardId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteRoomBoard(@PathVariable String providerLocationId, @PathVariable String roomBoardId) throws URISyntaxException {
        hospitalProviderLocationService.deleteRoomBoard(providerLocationId, roomBoardId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('hospitals_management','hospital_provider_location_add', 'restricted_write')")
    @PostMapping ("/ingestion")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    List saveBulkMasterData(@PathVariable String providerLocationId, @RequestBody Map bulkUploadReq) throws URISyntaxException {
        return hospitalProviderLocationService.saveBulkMasterData(providerLocationId, bulkUploadReq);
    }

    /**************************************** Room and Board : END ****************************************/
}
