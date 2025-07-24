package com.halodoc.batavia.controller.api.campaign;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
import com.halodoc.batavia.entity.coupon.Campaign;
import com.halodoc.batavia.entity.coupon.CampaignCondition;
import com.halodoc.batavia.entity.coupon.CampaignSegmentAction;
import com.halodoc.batavia.entity.coupon.CampaignSegments;
import com.halodoc.batavia.entity.coupon.Condition;
import com.halodoc.batavia.entity.coupon.Coupon;
import com.halodoc.batavia.entity.coupon.CouponAction;
import com.halodoc.batavia.entity.coupon.CouponCondition;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.CampaignService;
import com.halodoc.batavia.service.bali.logan.BaliLoganService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping ("api/v1/campaigns")
@RestController
@Slf4j
public class CampaignApiController extends HalodocBaseApiController {
    private final CampaignService campaignService;

    private final BaliLoganService baliLoganService;

    @Autowired
    public CampaignApiController(CampaignService campaignService, BaliLoganService baliLoganService) {
        this.campaignService = campaignService;
        this.baliLoganService = baliLoganService;
    }

    /************ Condition Management APIS **********/
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_list', 'view_only')")
    @GetMapping (value = "/conditions") // To get all condition
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<Condition> listConditions(@RequestParam (required = false, name = "name") String name,
            @RequestParam (required = false, name = "attribute") String attribute,
            @RequestParam (required = false, name = "page_number", defaultValue = "1") String pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") String perPage) {

        return campaignService.searchPaginatedConditions(name, attribute, pageNo, perPage);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_list', 'view_only')")
    @GetMapping (value = "/conditions/multi-get") // To get all condition
    @ApiCategory (value = ApiType.BULK_LOOKUP, verticalName = Vertical.CORE)
    public List<Condition> multiGetConditions(@RequestParam (required = false, name = "conditionIds") String conditionIds) {

        return campaignService.multiGetConditions(conditionIds);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_view', 'view_only')")
    @GetMapping (value = "/conditions/{conditionId}") // Fetches condition by id
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Condition getConditionById(@PathVariable Integer conditionId) {

        return campaignService.getConditionById(conditionId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_add', 'restricted_write')")
    @PostMapping (value = "/conditions") // Creates a Condition
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Condition createCondition(@RequestBody Condition condition) throws ParseException {

        return campaignService.createCondition(condition);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_edit', 'restricted_write')")
    @PutMapping (value = "/conditions/{id}") // Updates condition by id
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateCondition(@PathVariable Integer id, @RequestBody Condition condition) throws ParseException {

        campaignService.updateCondition(id, condition);
    }

    /************ End of Condition Management APIS **********/
    /************ Campaigns Management APIS Starts Here**********/
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_list', 'view_only')")
    @GetMapping ("/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<Campaign> searchCampaignsV2(
            @RequestParam (required = false, name = "search_by", defaultValue = "campaign") String searchBy,
            @RequestParam (required = false, name = "search_text") String searchText,
            @RequestParam (required = false, name = "status", defaultValue = "created,published,inactive,on_hold,closed") String status,
            @RequestParam (required = false, name = "page_number", defaultValue = "1") Integer page_number,
            @RequestParam (required = false, name = "per_page", defaultValue = "20") Integer per_page) {

        return campaignService.searchCampaignsV2(searchBy, searchText, status, page_number, per_page);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_view', 'view_only')")
    @GetMapping ("/{campaignId}") // Fetches campaign by Id
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Campaign getCampaignById(@PathVariable String campaignId) {

        return campaignService.getAllCampaignDataById(campaignId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_view', 'view_only')")
    @GetMapping ("/{campaignId}/coupons") // Fetches campaign coupons by Id
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<Coupon> getCampaignCouponsById(@PathVariable String campaignId,
            @RequestParam (required = false, name = "search_text") String searchText,
            @RequestParam (required = false, name = "page_number", defaultValue = "1") Integer page_number,
            @RequestParam (required = false, name = "per_page", defaultValue = "10") Integer per_page) {
        return campaignService.getCouponsByCampaign(campaignId, searchText, page_number, per_page);
    }

    // Creates a new campaign with campaign information
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_add', 'restricted_write')")
    @PostMapping
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Campaign save(@RequestBody Campaign campaign) {

        campaign.setRedeemedCount(0);
        campaign.setRedeemedAmount(BigDecimal.ZERO);

        if (campaign.getType().equals("cashback")) {
            campaign.setCurrency("COIN");
        } else {
            campaign.setCurrency("IDR");
        }

        Campaign createdCampaign = campaignService.createCampaign(campaign);
        return createdCampaign;

    }

    // updates a campaign
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_edit', 'restricted_write')")
    @PutMapping
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateCampaign(@RequestBody Campaign campaign) {

        campaignService.updateCampaign(campaign);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'restricted_write')")
    @PutMapping ("/{campaignId}/publish") // Updates campaign state to publish
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void publishCampaign(@PathVariable String campaignId) {
        campaignService.publishCampaign(campaignId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'restricted_write')")
    @PutMapping ("/{campaignId}/close") // Updates campaign state to close
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void closeCampaign(@PathVariable String campaignId) {
        campaignService.closeCampaign(campaignId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'restricted_write')")
    @PutMapping ("/{campaignId}/inactivate") // Updates campaign state to inactive
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void activateCampaign(@PathVariable String campaignId) throws Exception {

        campaignService.inactivateCampaign(campaignId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'restricted_write')")
    @PutMapping ("/{campaignId}/hold") // Updates campaign state to hold
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void holdCampaign(@PathVariable String campaignId) {
        campaignService.holdCampaign(campaignId);
    }

    /************ End of Campaign Management APIS **********/
    /************ Campaign Condition Management APIS Starts Here**********/
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_list', 'view_only')")
    @GetMapping (value = "/{campaignId}/conditions") // Fetches all campaign conditions by Id
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public List<CampaignCondition> getCampaignConditions(@PathVariable String campaignId) {
        return campaignService.getCampaignConditions(campaignId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_add', 'restricted_write')")
    @PostMapping (value = "/{campaignId}/conditions") // Creates a Campaign Condition
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public CampaignCondition createCampaignCondition(@PathVariable String campaignId, @RequestBody CampaignCondition campaignCondition)
            throws ParseException {

        return campaignService.createCampaignCondition(campaignId, campaignCondition);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_edit', 'restricted_write')")
    @PutMapping (value = "/{campaignId}/conditions/{conditionId}") // updates Campaign Condition
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateCampaignCondition(@PathVariable String campaignId, @PathVariable String conditionId,
            @RequestBody CampaignCondition campaignCondition) throws ParseException {

        campaignService.updateCampaignCondition(campaignId, campaignCondition);
    }

    /************ End of Campaign Condition Management APIS**********/
    /************ Coupons Management API's Starts Here **************/
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_coupon_view', 'view_only')")
    @GetMapping (value = "/{campaignId}/coupons/{couponId}") // Fetches campaign coupon by Id
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public Coupon getCampaignCouponById(@PathVariable String campaignId, @PathVariable String couponId) {

        Coupon coupon = campaignService.getCampaignCouponById(campaignId, couponId);
        if (coupon != null) {
            coupon.setAttributes(campaignService.getCouponAttributes(couponId));
        }
        return coupon;
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_coupon_add', 'restricted_write')")
    @PostMapping (value = "/{campaignId}/coupons") // Creates a Campaign Coupon
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public Coupon createCampaignCoupon(@PathVariable String campaignId, @RequestBody Coupon coupon) throws ParseException {

        return campaignService.createCoupon(campaignId, coupon);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_coupon_edit', 'restricted_write')")
    @PutMapping (value = "/{campaignId}/coupons/{couponId}") // Updates a Campaign Coupon
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateCampaignCoupon(@PathVariable String campaignId, @PathVariable String couponId, @RequestBody Coupon coupon)
            throws ParseException {
        campaignService.updateCoupon(campaignId, couponId, coupon);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_coupon_actions', 'restricted_write')")
    @PutMapping (value = "/{campaignId}/coupons/{couponId}/{status}") // Updates Campaign Coupon state to publish
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void changeCampaignCouponStatus(@PathVariable String campaignId, @PathVariable String couponId, @PathVariable String status)
            throws ParseException {
        campaignService.changeCouponStatus(campaignId, couponId, status);
    }

    /************ End of Coupons Management API's **************/
    /************ Coupon Condition Management API's Starts Here **************/
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_add', 'restricted_write')")
    @PostMapping (value = "/{campaignId}/coupons/{couponId}/conditions") // Creates a Campaign Coupon Condition
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public CouponCondition createCampaignCouponCondition(@PathVariable String campaignId, @PathVariable String couponId,
            @RequestBody CouponCondition couponCondition) throws ParseException {

        return campaignService.createCampaignCouponCondition(campaignId, couponId, couponCondition);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_edit', 'restricted_write')")
    @PutMapping (value = "/{campaignId}/coupons/{couponId}/conditions/{conditionId}") // updates Campaign Coupon Condition
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateCampaignCondition(@PathVariable String campaignId, @PathVariable String couponId, @PathVariable String conditionId,
            @RequestBody CouponCondition couponCondition) throws ParseException {

        campaignService.updateCampaignCouponCondition(campaignId, couponId, conditionId, couponCondition);
    }

    /************ End of Coupon Condition Management API's **************/
    /************ Start of Cashback Segments API's **************/
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'view_only')")
    @GetMapping (value = "/segments")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.CORE)
    public PaginatedResult<CampaignSegments> getCampaignSegments(@RequestParam (name = "page_no", required = false, defaultValue = "1") int page_no,
            @RequestParam (name = "per_page", required = false, defaultValue = "10") int per_page) {

        return baliLoganService.getCampaignSegments(page_no, per_page);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'restricted_write')")
    @PostMapping (value = "/campaign-segments")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public Map updateSegmentIndex(@RequestBody CampaignSegmentAction campaignSegmentAction) throws ParseException {

        return campaignService.updateCampaignSegment(campaignSegmentAction);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'view_only')")
    @GetMapping (value = "/campaign-segments/{id}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public List getSegmentDetailByCampaignID(@PathVariable String id) {

        return campaignService.getSegmentDetailByCampaignID(id);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','campaign_actions', 'view_only')")
    @GetMapping (value = "/segments/{id}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public CampaignSegments getSegmentDetailsByID(@PathVariable String id) {

        return baliLoganService.getSegmentDetailsByID(id);
    }

    /************ End of Cashback Segments API's **************/
    /************ Coupon Action Management API's Starts Here **************/
    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_list', 'view_only')")
    @GetMapping (value = "/{campaignId}/coupons/{couponId}/actions")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public List<CouponAction> getAllCampaignCouponActions(@PathVariable String campaignId, @PathVariable String couponId) {

        return campaignService.getAllCampaignCouponActions(campaignId, couponId);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_add', 'restricted_write')")
    @PostMapping (value = "/{campaignId}/coupons/{couponId}/actions")
    @ApiCategory (value = ApiType.CREATE, verticalName = Vertical.CORE)
    public CouponAction createCampaignCouponAction(@PathVariable String campaignId, @PathVariable String couponId,
            @RequestBody CouponAction couponAction) throws ParseException {

        return campaignService.createCampaignCouponAction(campaignId, couponId, couponAction);
    }

    @PreAuthorize ("@authorizationService.isAuthorized('marketing','condition_edit', 'restricted_write')")
    @PutMapping (value = "/{campaignId}/coupons/{couponId}/actions/{actionId}")
    @ApiCategory (value = ApiType.UPDATE, verticalName = Vertical.CORE)
    public void updateCampaignCouponAction(@PathVariable String campaignId, @PathVariable String couponId, @PathVariable String actionId,
            @RequestBody CouponAction couponAction) throws ParseException {

        campaignService.updateCampaignCouponAction(campaignId, couponId, actionId, couponAction);
    }
    /************ End of Coupon Action Management API's **************/
}
