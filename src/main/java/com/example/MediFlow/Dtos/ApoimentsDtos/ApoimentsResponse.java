package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.Dtos.Patients.PatientDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApoimentsResponse {
    private List<Appoi_dto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private Long total_Appointments;
    private Long today_Appointments;
    private Long completed;
    private Long Upcoming;
}
