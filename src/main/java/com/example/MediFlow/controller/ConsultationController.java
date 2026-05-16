package com.example.MediFlow.controller;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.consultation.*;
import com.example.MediFlow.entity.Consultation;
import com.example.MediFlow.services.IConsultationService;
import com.example.MediFlow.utility.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/consultation/")
@Validated
@RestController
public class ConsultationController {
    @Autowired
    private IConsultationService iConsultationService;

    @PostMapping("/answer")
    public ResponseEntity<?> saveAnswer(
            @RequestBody ConsultationPayloadDTO dto) {

        iConsultationService.saveAnswer(dto);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/ConsultationPagination", method = RequestMethod.POST)
    public ConsultationResponse getConsultationPagination(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestBody(required = false) ConsultationFilter filtre
    ) {
        return iConsultationService.getConsultationPagination(pageNo, pageSize, sortBy, sortDir,filtre);
    }


    @PostMapping("/start/{appointmentId}")
    public ConsultationDTO start(@PathVariable Long appointmentId) {
        return iConsultationService.startConsultation(appointmentId);
    }
    @PostMapping("/end/{consultationId}")
    public void end(@PathVariable Long consultationId) {
        iConsultationService.endConsultation(consultationId);
    }
}
