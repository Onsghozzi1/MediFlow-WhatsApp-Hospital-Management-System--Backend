package com.example.MediFlow.repository.query;

import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;

public interface IUserQuery {
    AdminResponseDto getAdminPagination(int pageNo, int pageSize, String sortBy, String sortDir, AdminFilter filter);

}
