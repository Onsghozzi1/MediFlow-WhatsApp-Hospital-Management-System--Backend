package com.example.MediFlow.Dtos.consultation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

    @Getter
    @Setter
    public class ConsultationPayloadDTO {
        private Long appointmentId;
        private Long consultationId;
        private Long patientId ;
        private List<ConsultationAnswerDTO> answers;
        private List<PrescriptionDTO> prescriptions;

    }

