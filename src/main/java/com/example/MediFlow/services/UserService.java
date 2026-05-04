package com.example.MediFlow.services;

import com.example.MediFlow.Dtos.JwtResponse;
import com.example.MediFlow.Dtos.Password_dto;
import com.example.MediFlow.Dtos.ResponseEmail;
import com.example.MediFlow.Dtos.role_dto.RoleDto;
import com.example.MediFlow.Dtos.user_dto.*;
import com.example.MediFlow.entity.enums.Roles;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface UserService {
    public UserDTO createUserAccount(UserRegisterDTO userRegisterDTO) throws Exception;
    public JwtResponse login(String email);
    public AdminResponseDto getUserPagination(int pageNo, int pageSize, String sortBy, String sortDir, AdminFilter filter);
    public ResponseEntity<RoleDto> changeUserRole(Roles roles, Long userId);
    public ResponseEntity<User_validate_Dto> changeAccountStatus(Boolean status, Long userId);
    public UserUpdateResponseDto updateUserByEmail(UserUpdateResponseDto userDTO) throws IOException;
    public ResponseEmail findByEmail(String  email) throws IOException;
    void change_password(Password_dto passwordDto, String email) throws MessagingException;
    public void requestPasswordReset(String email) throws Exception;
    public void resetPassword(String token, String newPassword)  throws Exception;

}
