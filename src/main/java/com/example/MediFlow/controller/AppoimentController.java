package com.example.MediFlow.controller;

import com.example.MediFlow.Dtos.ApiResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.*;
import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.PatientRepository;
import com.example.MediFlow.services.IAppointmentService;
import com.example.MediFlow.utility.AppConstants;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/Appointment/")
@Validated
@RestController
public class AppoimentController {
    @Autowired
    private IAppointmentService iAppointmentService ;
    @Autowired
    private AppointmentRepository patientRepository;
    @PostMapping("/add-appointment")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AppoimentsDto appointment) {

        iAppointmentService.create(appointment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Appointment created successfully","SUCESS",null));
    }
    @RequestMapping(value = "/AppointmentPagination", method = RequestMethod.POST)
    public ApoimentsResponse getAppointmentPagination(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestBody(required = false) ApoimentFilter filtre
    ) {
        return iAppointmentService.getAppointmentPagination(pageNo, pageSize, sortBy, sortDir,filtre);
    }
    @PutMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        iAppointmentService.changeDeleteStatus(id);
        return ResponseEntity.ok(
                new ApiResponse("Appointment deleted successfully", "deleted",null)
        );


    }
    @PutMapping("/edit-appointment/{id}")
    public ResponseEntity<ApiResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppoimentsDto dto) {
        iAppointmentService.update(id, dto);
        return ResponseEntity.ok(
                new ApiResponse(
                        "Appointment updated successfully",
                        "success",
                        null
                )
        );
    }
    @GetMapping("/get_calendar")
    public List<Appointment_calendar> getCalender() {
        return iAppointmentService.getAllAppointments();
    }


    @GetMapping("/patients_appointment")
    public List<AllPatients> getpatients_appointment() {
        return iAppointmentService.getAllAppointmentsPatient();
    }

    @GetMapping("/send")

    public ResponseEntity<String> sendSMS() {

        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"),
                System.getenv("TWILIO_AUTH_TOKEN"));

        Message.creator(new PhoneNumber("21629883670"),
                new PhoneNumber("<FROM number - ie your Twilio number"),
                "Hello from Twilio 📞").create();

        return new ResponseEntity<String>("Message sent successfully", HttpStatus.OK);
    }

//    public Map<String, String> sendWhatsApp(@RequestParam Long patientId) {
//
//        Appointment appointment =
//                patientRepository.findByPatient(patientId);
//
//        String message = "Bonjour " +
//                appointment.getPatient().getFullName();
//
//        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
//
//        String url = "https://wa.me/" +
//                appointment.getPatient().getWhatsappNumber() +
//                "?text=" +
//                encoded;
//
//        return Map.of("url", url);
//    }
}