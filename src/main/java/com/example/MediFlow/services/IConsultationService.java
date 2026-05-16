package com.example.MediFlow.services;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.consultation.*;
import com.example.MediFlow.entity.Consultation;

public interface IConsultationService {
    public void saveAnswer(ConsultationPayloadDTO dto);
    public ConsultationResponse getConsultationPagination(int pageNo, int pageSize, String sortBy, String sortDir, ConsultationFilter filter);
    void endConsultation(Long consultationId);
    public ConsultationDTO startConsultation(Long appointmentId);
}
