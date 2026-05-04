package com.example.MediFlow.Dtos.user_dto;

import com.example.MediFlow.entity.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminFilter {
    private Long idUser ;
    private String name ;
    private String email ;
    private Roles roleDto;



}
