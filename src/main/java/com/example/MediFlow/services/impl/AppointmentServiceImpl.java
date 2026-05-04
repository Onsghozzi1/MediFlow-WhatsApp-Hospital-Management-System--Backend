package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.mapper.AppoimentMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.query.IAppoimentQuery;
import com.example.MediFlow.services.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements IAppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppoimentMapper appoimentMapper;
    @Autowired
    private IAppoimentQuery iAppoimentQuery;

    public AppoimentsDto create(AppoimentsDto appointmentDto) {
        appointmentDto.setStatus(Status.PENDING);
        Appointment appointmentEntity =
                appoimentMapper.mapToappointment(appointmentDto);
        Appointment savedAppointment =
                appointmentRepository.save(appointmentEntity);
        return appoimentMapper.mapTo_appoiment_DTO(savedAppointment);
    }

    @Override
    public ApoimentsResponse getAppointmentPagination(int pageNo, int pageSize, String sortBy, String sortDir, ApoimentFilter filter) {
        return iAppoimentQuery.getAppointmentPagination( pageNo, pageSize, sortBy, sortDir, filter);
    }


}
