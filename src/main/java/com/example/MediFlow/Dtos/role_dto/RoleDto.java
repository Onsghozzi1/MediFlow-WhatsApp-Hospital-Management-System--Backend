package com.example.MediFlow.Dtos.role_dto;

import com.example.MediFlow.entity.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private Long idUser;
    private Roles roleTypes;

}
