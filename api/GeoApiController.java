package com.halodoc.batavia.controller.api;

import java.net.URISyntaxException;
import java.util.HashMap;
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
import org.springframework.web.multipart.MultipartFile;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.datatable.TableOutput;
import com.halodoc.batavia.entity.geo.City;
import com.halodoc.batavia.entity.geo.Country;
import com.halodoc.batavia.entity.geo.District;
import com.halodoc.batavia.entity.geo.Geo;
import com.halodoc.batavia.entity.geo.LatLng;
import com.halodoc.batavia.entity.geo.PaginatedRegionRequest;
import com.halodoc.batavia.entity.geo.Place;
import com.halodoc.batavia.entity.geo.PlaceResult;
import com.halodoc.batavia.entity.geo.Region;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.GeoService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping ("api/v1/geo")
@RestController
public class GeoApiController extends BaseApiController {
    @Autowired
    private GeoService geoService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public TableOutput listAllZone() {
        List<Geo> geos = geoService.list();

        TableOutput output = new TableOutput();
        output.setData(geos);
        output.setTotal((long) geos.size());
        return output;
    }

    @GetMapping ("/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public List<Geo> search(@RequestParam (required = false) String name, @RequestParam (required = false) String city,
            @RequestParam (required = false) String latLng) {
        return geoService.list(name, city, latLng);
    }

    @GetMapping ("/{zoneId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<Geo> getZone(@PathVariable String zoneId) {
        return geoService.getZone(zoneId);
    }

    @PutMapping ("/geozones")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public Map listGeozoneByLatLng(@RequestBody LatLng latLng) {

        if (!authorizationService.isAuthorized("configuration","geo_upload", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery","merchant_location_add", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery","merchant_location_edit", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery","distributor_branch_add", "restricted_write")
                && !authorizationService.isAuthorized("pharmacy_delivery","order_create_reorder", "restricted_write")
                && !authorizationService.isAuthorized("lab_service","halolab_order_create", "restricted_write")
                && !authorizationService.isAuthorized("lab_service","provider_edit", "restricted_write")
                && !authorizationService.isAuthorized("hospitals_management","hospital_provider_edit", "restricted_write")
        ) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        Map result = new HashMap();
        result.put("data", geoService.listByLatLng(latLng.getLatitude(), latLng.getLongitude()));
        return result;
    }

    @PostMapping ("/upload")
    @PreAuthorize ("@authorizationService.isAuthorized('configuration','geo_upload', 'restricted_write')")
    @ApiCategory (value = ApiType.FILE_UPLOAD, verticalName = Vertical.SRE)
    public HashMap<String, Object> uploadKML(@RequestParam MultipartFile file, @RequestParam String name, @RequestParam String city)
            throws Exception {

        HashMap<String, Object> map = new HashMap<>();

        try {
            geoService.uploadKMLFile(file, name, city);

            map.put("status", "success");
        } catch (Exception ex) {
            map.put("status", "error");
            map.put("message", ex.getMessage());
        }
        return map;
    }

    /*******************************************
     * V1 GOOGLE MAPS MANAGEMENT APIs STARTS
     *******************************************/
    @GetMapping ("/address")
    @ApiCategory (value = ApiType.THIRD_PARTY, verticalName = Vertical.SRE)
    public PlaceResult getLocationFromCoordinates(@RequestParam (required = true, name = "latitude") Double latitude,
            @RequestParam (required = true, name = "longitude") Double longitude) throws URISyntaxException {
        return geoService.getLocationFromCoordinates(latitude, longitude);
    }

    @GetMapping ("/place_details")
    @ApiCategory (value = ApiType.THIRD_PARTY, verticalName = Vertical.SRE)
    public Place getPlaceDetailsById(@RequestParam String placeId) throws URISyntaxException {
        return geoService.getPlaceDetailsById(placeId);
    }

    @GetMapping ("/search_geo_location")
    @ApiCategory (value = ApiType.THIRD_PARTY, verticalName = Vertical.SRE)
    public PlaceResult searchGeoLocation(@RequestParam (required = true, name = "search_key") String searchKey,
            @RequestParam (required = false) Double latitude, @RequestParam (required = false) Double longitude) throws URISyntaxException {

        return geoService.searchGeoLocation(searchKey, latitude, longitude);
    }

    /*******************************************
     * V1 Cities/Districts
     *******************************************/
    @GetMapping ("/regions/search")
    @ApiCategory (value = ApiType.CONSUMER_SEARCH, verticalName = Vertical.SRE)
    public List<Region> searchRegions(@RequestParam (required = false, name = "name", defaultValue = "") String name) throws URISyntaxException {
        return geoService.searchRegions(name);
    }

    @GetMapping ("/cities/{cityId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public City getCity(@PathVariable String cityId) throws URISyntaxException {
        return geoService.getCity(cityId);
    }

    @GetMapping ("/districts/{districtId}")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public District getDistrict(@PathVariable String districtId) throws URISyntaxException {
        return geoService.getDistrict(districtId);
    }

    /*******************************************
     * V3 Regions
     *******************************************/
    @GetMapping ("regions")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    public List<Region> searchRegionsV3(@RequestParam (required = false, name = "name", defaultValue = "") String name,
            @RequestParam (required = false, name = "populate_slug", defaultValue = "false") String populateSlug,
            @RequestParam (required = false, name = "populate_country", defaultValue = "false") String populateCountry) throws URISyntaxException {
        return geoService.searchRegionsV3(name, populateSlug, populateCountry);
    }

    @GetMapping ("regions/{regionId}/cities")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<City> getCitiesByRegion(@PathVariable ("regionId") String regionId) {
        return geoService.searchCitiesByRegion(regionId);
    }

    @GetMapping ("cities/{cityId}/districts")
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.SRE)
    public List<District> getDistrictsByCity(@PathVariable ("cityId") String cityId) {
        return geoService.searchDistrictsByCity(cityId);
    }

    @PutMapping ("/regions/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    PaginatedResult<Region> searchPaginatedRegions(@RequestBody PaginatedRegionRequest request) throws URISyntaxException {
        return geoService.searchPaginatedRegions(request);
    }

    /*******************************************
     * Search Countries:START
     *******************************************/
    @PutMapping ("/countries/search")
    @ApiCategory (value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.SRE)
    PaginatedResult<Country> searchCountry(@RequestParam (required = false, name = "name", defaultValue = "") String name,
            @RequestBody List<String> code, @RequestParam (required = false, name = "page_no", defaultValue = DEFAULT_PAGE) Integer pageNo,
            @RequestParam (required = false, name = "per_page", defaultValue = DEFAULT_LIMIT) Integer perPage) throws URISyntaxException {
        return geoService.searchCountry(name, code, pageNo, perPage);
    }

    /*******************************************
     * Search Countries:END
     *******************************************/
}
