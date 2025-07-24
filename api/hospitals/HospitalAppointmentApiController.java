package com.halodoc.batavia.controller.api.hospitals;

import com.halodoc.batavia.controller.api.HalodocBaseApiController;
import com.halodoc.batavia.entity.appointment.HospitalAppointment;
import com.halodoc.batavia.entity.bali.logan.Customer;
import com.halodoc.batavia.entity.bintan.catalog.HospitalProviderLocation;
import com.halodoc.batavia.entity.bintan.discovery.PersonnelAvailability;
import com.halodoc.batavia.entity.bintan.discovery.ProcedureAvailability;
import com.halodoc.batavia.entity.common.PaginatedResult;
import com.halodoc.batavia.entity.configuration.BataviaAppConfiguration;
import com.halodoc.batavia.exception.HalodocWebException;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.AuthorizationService;
import com.halodoc.batavia.service.appointment.HospitalAppointmentService;
import com.halodoc.batavia.service.bali.logan.BaliLoganService;
import com.halodoc.batavia.service.bintan.ProviderLocationService;
import com.halodoc.batavia.service.bintan.discovery.BintanDiscoveryService;
import com.halodoc.batavia.util.SpringSecurityUtil;
import com.halodoc.config.ConfigClient;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/v1/hospital/appointments")
@Slf4j
public class HospitalAppointmentApiController extends HalodocBaseApiController {

    @Autowired
    private HospitalAppointmentService hospitalAppointmentService;

    @Autowired
    private BintanDiscoveryService bintanDiscoveryService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ProviderLocationService providerLocationService;

    @Autowired
    private BaliLoganService baliLoganService;

    @Autowired
    private ConfigClient<BataviaAppConfiguration> configClient;


    @GetMapping("/search")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_appointment_list', 'view_only')")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PaginatedResult<HospitalAppointment> getSla(@RequestParam(required = false, name = "page_no", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(required = false, name = "per_page", defaultValue = "20") Integer perPage,
                                                       @RequestParam(required = false, name = "start_booking_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startBookingDate,
                                                       @RequestParam(required = false, name = "end_booking_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endBookingDate,
                                                       @RequestParam(required = false, name = "start_appointment_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startAppointmentDate,
                                                       @RequestParam(required = false, name = "end_appointment_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endAppointmentDate,
                                                       @RequestParam(required = false, name = "customer_entity_id") String customerId,
                                                       @RequestParam(required = false, name = "customer_appointment_id") String customerAppointmentId,
                                                       @RequestParam(required = false, name = "statuses", defaultValue = "") String statuses,
                                                       @RequestParam(required = false, name = "types", defaultValue = "") String types,
                                                       @RequestParam(required = false, name = "sort_field", defaultValue = "") String sortField,
                                                       @RequestParam(required = false, name = "sort_order") String sortOrder,
                                                       @RequestParam(required = false, name = "provider_location_id") String providerLocationId,
                                                       @RequestParam(required = false, name = "inventory_type") String inventoryType,
                                                       @RequestParam(required = false, name = "is_followed_up") Boolean isFollowedUp,
                                                       @RequestParam(required = false,name = "concern_reason")String concernReason,
                                                       @RequestParam(required = false, name = "customer_booking_id") String customerBookingId) throws URISyntaxException {
        Long startBookDate, endBookDate, startAppointment, endAppointment;
        startBookDate = null;
        endBookDate = null;
        startAppointment = null;
        endAppointment = null;

        if (startBookingDate != null) {
            startBookDate = new DateTime(startBookingDate).withTimeAtStartOfDay().getMillis();
        }

        if (startAppointmentDate != null) {
            startAppointment = new DateTime(startAppointmentDate).withTimeAtStartOfDay().getMillis();
        }

        if (endBookingDate != null) {
            endBookDate = new DateTime(endBookingDate)
                    .withHourOfDay(23)
                    .withMinuteOfHour(59)
                    .withSecondOfMinute(59)
                    .getMillis();
        }

        if (endAppointmentDate != null) {
            endAppointment = new DateTime(endAppointmentDate)
                    .withHourOfDay(23)
                    .withMinuteOfHour(59)
                    .withSecondOfMinute(59)
                    .getMillis();
        }

        return hospitalAppointmentService.search(pageNo, perPage, customerId, statuses, types, startBookDate, endBookDate,
                startAppointment, endAppointment, sortField, sortOrder, customerAppointmentId, providerLocationId, customerBookingId, inventoryType, isFollowedUp, concernReason);

    }

    @GetMapping("/{appointmentId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public HospitalAppointment getAppointment(@PathVariable String appointmentId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        HospitalAppointment appointment = hospitalAppointmentService.getAppointment(appointmentId);
        loadCustomerInfo(appointment);
        return appointment;
    }

    @GetMapping("/appointment-booking/{bookingId}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Map getAppointmentBooking( @PathVariable String bookingId) throws URISyntaxException {
        if(authorizationService.isAuthorized("hospital_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        Map appointmentBooking = hospitalAppointmentService.getAppointmentBooking(bookingId);
        return appointmentBooking;

    }


    @GetMapping("/{appointmentId}/ktp")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Map getAppointmentKTP(@PathVariable String appointmentId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return hospitalAppointmentService.getAppointmentKTP(appointmentId);
    }

    @GetMapping("/{appointmentId}/report-attachments")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<Map> getAppointmentReportAttachments(@PathVariable String appointmentId) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return hospitalAppointmentService.getAppointmentReportAttachments(appointmentId);
    }

    @GetMapping("{orderType}/{bookingId}/documents/{docType}/{id}")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Map getBookingInvoice(@PathVariable String orderType, @PathVariable String bookingId, @PathVariable String docType, @PathVariable String id) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return hospitalAppointmentService.getBookingInvoice(orderType, bookingId, docType, id) ;
    }

    @PostMapping("/{bookingId}/payments/{appointmentId}/refund")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_order_actions', 'restricted_write')")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void refundAppointmentBooking(@PathVariable String bookingId, @PathVariable String appointmentId, @RequestBody Map<String, String> refundData) throws URISyntaxException {
        hospitalAppointmentService.refundAppointmentBooking(bookingId, appointmentId, refundData);
    }

    @PostMapping("/{bookingId}/notes")
    @PreAuthorize("@authorizationService.isAuthorized('hospitals_management','hospital_appointment_action', 'restricted_write')")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public void createCsNotes(@PathVariable String bookingId,@RequestBody Map<String, String> csNotesData) throws URISyntaxException{
        hospitalAppointmentService.createCsNotes(bookingId,csNotesData);
    }

    @GetMapping("/cancel-reasons")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Object getCancelAppointmentReasons() throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return this.configClient.getAppConfig().getAppointmentConfiguration().getCancellationReasons();
    }

    @GetMapping("/reschedule-reasons")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Object getRescheduleAppointmentReasons() throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return this.configClient.getAppConfig().getAppointmentConfiguration().getRescheduleReasons();
    }

    @GetMapping("/change-doctor-reasons")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public Object getChangeDoctorReasons() throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return this.configClient.getAppConfig().getAppointmentConfiguration().getChangeDoctorReasons();
    }

    @GetMapping("/{bookingId}/notes")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List<Map> getRaiseConcern(@PathVariable String bookingId) throws URISyntaxException{
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return hospitalAppointmentService.getAllNotes(bookingId);

    }


    @PutMapping("/appointment-bookings/{bookingId}/appointments/{appointmentId}/confirm")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void confirmAppointment(@PathVariable String appointmentId,@PathVariable String bookingId, @RequestBody Map confirmRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        hospitalAppointmentService.confirmAppointment(appointmentId,bookingId, confirmRequest);
    }

    @PutMapping("/{appointmentId}/attributes")
    @ApiCategory(value = ApiType.CREATE, verticalName = Vertical.INS)
    public void raiseConcern(@PathVariable String appointmentId, @RequestBody Map updateAttributes) throws URISyntaxException{
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")){
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        hospitalAppointmentService.raiseConcern(appointmentId,updateAttributes);
    }



    @PutMapping("/appointment-bookings/{bookingId}/appointments/{appointmentId}/complete")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void completeAppointment(@PathVariable String appointmentId,@PathVariable String bookingId, @RequestBody Map completeRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        hospitalAppointmentService.completeAppointment(appointmentId, bookingId,completeRequest);
    }

    @PutMapping("/{appointmentId}/follow-up")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void updateAppointmentFollowup(@PathVariable String appointmentId, @RequestBody Map updateRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        hospitalAppointmentService.updateAppointmentFollowup(appointmentId, updateRequest);
    }

    @PutMapping("/personnel/availability")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public PersonnelAvailability getPersonnelAvailability( @RequestBody Map<String, String> availabilityRequest)throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return bintanDiscoveryService.getPersonnelScheduleDates(availabilityRequest);
    }

    @PutMapping("/personnel/slots")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public List getPersonnelAvailableSlots( @RequestBody Map<String, String> slotsRequest)throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return bintanDiscoveryService.getPersonnelScheduleSlots(slotsRequest);
    }

    @PutMapping("/medical-procedure/availability")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public ProcedureAvailability getProcedureAvailability( @RequestBody Map<String, String> availabilityRequest)throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return bintanDiscoveryService.getProcedureScheduleDates(availabilityRequest);
    }

    @PutMapping("/medical-procedure/slots")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public List getProcedureAvailableSlots( @RequestBody Map<String, String> slotsRequest)throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return bintanDiscoveryService.getProcedureScheduleSlots(slotsRequest);
    }

    @PutMapping("/appointment-booking/{bookingId}/{appointmentId}/cancel")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void cancelAppointment(@PathVariable String bookingId, @PathVariable String appointmentId, @RequestBody Map confirmRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        hospitalAppointmentService.cancelAppointment(bookingId, confirmRequest);
    }

    @PutMapping("/appointment-booking/{bookingId}/{appointmentId}/reschedule")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void rescheduleAppointment(@PathVariable String bookingId, @PathVariable String appointmentId, @RequestBody Map resheduleRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        String userName = SpringSecurityUtil.getPartialNameFromEmail();

        resheduleRequest.put("rescheduled_by", userName);
        hospitalAppointmentService.rescheduleAppointment(bookingId, resheduleRequest);
    }

    @PutMapping("/appointment-booking/{bookingId}/{appointmentId}/change-doctor")
    @ApiCategory(value = ApiType.UPDATE, verticalName = Vertical.INS)
    public void changeDoctorAppointment(@PathVariable String bookingId, @PathVariable String appointmentId, @RequestBody Map completeRequest) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        String userName = SpringSecurityUtil.getPartialNameFromEmail();
        completeRequest.put("rescheduled_by",userName);
        hospitalAppointmentService.changeDoctorAppointment(bookingId, appointmentId, completeRequest);
    }

    @PutMapping("/change/personnel/search")
    @ApiCategory(value = ApiType.INTERNAL_SEARCH, verticalName = Vertical.INS)
    public Map  getPersonnelReplacement(@RequestBody Map personnelChangeRequest) throws URISyntaxException {
        if(!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_action", "restricted_write")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return bintanDiscoveryService.getPersonnelReplacement(personnelChangeRequest);
    }

    @GetMapping("/slugs")
    @ApiCategory(value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.INS)
    public List getSlugDetails(@RequestParam(required = true, name = "entity_type", defaultValue = "") String entityType,
                              @RequestParam(required = true, name = "entity_id", defaultValue = "") String entityId,
    @RequestParam(required = false, name = "active", defaultValue = "true") String active) throws URISyntaxException {
        if (!authorizationService.isAuthorized("hospitals_management", "hospital_appointment_view", "view_only")) {
            throw HalodocWebException.builder().statusCode(403).message("Access Denied").build();
        }
        return providerLocationService.getSlugs(entityType, entityId, active);
    }

    public void loadCustomerInfo(HospitalAppointment appointment) {
        Customer user = null;
        try {
            user = baliLoganService.getCustomer(appointment.getEntityId());
            appointment.setCustomer(user);

            Object patient = baliLoganService.getPatient(appointment.getEntityId(), appointment.getPatientId());
            if (patient != null) {
                appointment.setPatient(patient);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @PutMapping("/provider-locations-by-multi-get")
    @ApiCategory(value = ApiType.BULK_LOOKUP, verticalName = Vertical.INS)
    public List<HospitalProviderLocation> getProviderLocationsByMultiGet(@RequestBody List<String> providerLocationIds) throws URISyntaxException {
        return providerLocationService.getProviderLocationsByMultiGet(providerLocationIds);
    }
}
