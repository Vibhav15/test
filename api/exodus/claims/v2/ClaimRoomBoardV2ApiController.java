package com.halodoc.batavia.controller.api.exodus.claims.v2;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTPAClaimService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping ("api/v2/exodus/claim-reimbursement/room-board")
public class ClaimRoomBoardV2ApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusTPAClaimService exodusTPAClaimService;

    @GetMapping ("/{claimId}/rooms")
    Map getRoomBoardListV2(@PathVariable String claimId) throws URISyntaxException {
        return exodusTPAClaimService.getRoomBoardListV2(claimId);
    }
}
