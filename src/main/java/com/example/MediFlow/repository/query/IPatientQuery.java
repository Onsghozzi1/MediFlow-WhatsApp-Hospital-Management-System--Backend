package com.example.MediFlow.repository.query;

import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;

public interface IPatientQuery {
    PatientResponseDto getPatientPagination(int pageNo, int pageSize, String sortBy, String sortDir, PatientFilter filter);

}
