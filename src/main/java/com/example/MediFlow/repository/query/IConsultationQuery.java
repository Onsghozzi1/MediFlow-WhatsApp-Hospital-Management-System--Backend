package com.example.MediFlow.repository.query;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.consultation.ConsultationFilter;
import com.example.MediFlow.Dtos.consultation.ConsultationResponse;

public interface IConsultationQuery {
    ConsultationResponse getConsultationPagination(int pageNo, int pageSize, String sortBy, String sortDir, ConsultationFilter filter);

}
