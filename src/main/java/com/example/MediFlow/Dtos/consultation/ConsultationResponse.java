package com.example.MediFlow.Dtos.consultation;

import com.example.MediFlow.Dtos.ApoimentsDtos.Appoi_dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultationResponse {
    private List<ConsultationDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private Long total_Consultation;
    private Long today_Consultation;
    private Long completed;
    private Long Upcoming;
}
