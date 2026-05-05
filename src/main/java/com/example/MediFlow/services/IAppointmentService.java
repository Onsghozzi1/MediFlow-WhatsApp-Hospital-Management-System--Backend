package com.example.MediFlow.services;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;

public interface IAppointmentService {
    AppoimentsDto create(AppoimentsDto appointmentDto);
    public ApoimentsResponse getAppointmentPagination(int pageNo, int pageSize, String sortBy, String sortDir, ApoimentFilter filter);
    public void changeDeleteStatus(Long id);
    public AppoimentsDto update(Long id, AppoimentsDto dto);
}
