package com.example.MediFlow.repository.query;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;

public interface IAppoimentQuery {
    ApoimentsResponse getAppointmentPagination(int pageNo, int pageSize, String sortBy, String sortDir, ApoimentFilter filter);

}
