package com.halodoc.batavia.controller.api.exodus.claims;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.exodus.claims.ClaimDistribution;
import com.halodoc.batavia.entity.exodus.claims.ClaimSummaryView;
import com.halodoc.batavia.entity.exodus.claims.ClaimTeamDetailById;
import com.halodoc.batavia.entity.exodus.claims.ClaimTeamRequest;
import com.halodoc.batavia.entity.exodus.claims.LinkedProvidersToTeam;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.exodus.tpa_claim.ExodusTPAClaimService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/claim-auto-assignment")
public class ClaimAssignmentApiController extends HalodocBaseApiController {
    @Autowired
    private ExodusTPAClaimService exodusTPAClaimService;

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_add', 'restricted_write')")
    @PostMapping("/create-team")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map createClaimTeam(@RequestBody ClaimTeamRequest requestBody) throws URISyntaxException {
        return exodusTPAClaimService.createClaimTeam(requestBody);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_assignment_view', 'view_only')")
    @GetMapping("/teams")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimTeamRequest[] getAllClaimTeamList() throws URISyntaxException {
        return exodusTPAClaimService.getAllClaimTeamList();
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_assignment_view', 'view_only')")
    @GetMapping("/team/{teamId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimTeamDetailById getClaimTeamDetailsById(@PathVariable String teamId) throws URISyntaxException {
        return exodusTPAClaimService.getClaimTeamDetailsById(teamId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_assignment_view', 'view_only')")
    @GetMapping("/team/{teamId}/providers")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    LinkedProvidersToTeam[] getLinkedProvidersTeam(@PathVariable String teamId) throws URISyntaxException {
        return exodusTPAClaimService.getLinkedProvidersTeam(teamId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_edit', 'view_only')")
    @PutMapping("/team/{teamId}/providers")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    LinkedProvidersToTeam[] linkProvidersToTeam(@PathVariable String teamId, @RequestBody LinkedProvidersToTeam reqBody)
            throws URISyntaxException {
        return exodusTPAClaimService.linkProvidersToTeam(teamId, reqBody);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_edit', 'view_only')")
    @PutMapping("/team/{teamId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    Map updateClaimTeamDetails(@PathVariable String teamId, @RequestBody ClaimTeamRequest reqBody)
            throws URISyntaxException {
        return exodusTPAClaimService.updateClaimTeamDetails(teamId, reqBody);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_edit', 'restricted_write')")
    @DeleteMapping("/team/{teamId}/providers")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteLinkedProvidersToTeam(@PathVariable String teamId, @RequestBody List<String> reqBody)
            throws URISyntaxException {
        exodusTPAClaimService.deleteLinkedProvidersToTeam(teamId, reqBody);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_edit', 'restricted_write')")
    @DeleteMapping("/team/{teamId}")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    void deleteClaimTeam(@PathVariable String teamId) throws URISyntaxException {
        exodusTPAClaimService.deleteClaimTeam(teamId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_edit', 'restricted_write')")
    @PostMapping("/team/{teamId}/claim-distribution")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    Map claimDistributionToTeam(@PathVariable String teamId, @RequestBody ClaimDistribution requestBody)
            throws URISyntaxException {
        return exodusTPAClaimService.claimDistributionToTeam(requestBody, teamId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_edit', 'view_only')")
    @GetMapping("/team/{teamId}/claims/{assignmentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getClaimDistributionStatus(@PathVariable String teamId, @PathVariable String assignmentId)
            throws URISyntaxException {
        return exodusTPAClaimService.getClaimDistributionStatus(teamId, assignmentId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_assignment_view', 'view_only')")
    @GetMapping("/team/{teamId}/assignment-summary")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimSummaryView[] getTeamClaimSummary(@PathVariable String teamId) throws URISyntaxException {
        return exodusTPAClaimService.getTeamClaimSummary(teamId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_assignment_view', 'view_only')")
    @GetMapping("/team/{teamId}/claim-count")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    Map getClaimCountDetails(@PathVariable String teamId) throws URISyntaxException {
        return exodusTPAClaimService.getClaimCountDetails(teamId);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_assignment_view', 'view_only')")
    @PutMapping("/claims/assignment-summaries")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimSummaryView[] claimAssignmentSummaries(@RequestBody String[] reqBody) throws URISyntaxException {
        return exodusTPAClaimService.claimAssignmentSummaries(reqBody);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_add', 'view_only')")
    @PutMapping("/provider-team-linking")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    LinkedProvidersToTeam[] getProviderTeamLinking(@RequestBody String[] reqBody) throws URISyntaxException {
        return exodusTPAClaimService.getProviderTeamLinking(reqBody);
    }

    @PreAuthorize("@authorizationService.isAuthorized('insurance','claim_qc_team_add', 'view_only')")
    @PutMapping("/analyst-team-linked-details")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    ClaimSummaryView[] getAnalystTeamLinking(@RequestBody String[] reqBody) throws URISyntaxException {
        return exodusTPAClaimService.getAnalystTeamLinking(reqBody);
    }

}
