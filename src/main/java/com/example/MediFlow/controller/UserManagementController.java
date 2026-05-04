package com.example.MediFlow.controller;

import com.example.MediFlow.Dtos.Password_dto;
import com.example.MediFlow.Dtos.ResponseEmail;
import com.example.MediFlow.Dtos.role_dto.RoleDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.Dtos.user_dto.User_validate_Dto;
import com.example.MediFlow.entity.enums.Roles;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.services.UserService;
import com.example.MediFlow.utility.AppConstants;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/userManagement")
@Validated
public class UserManagementController {
    @Autowired
    private UserService userIService;
    @Autowired
    private UserRepository userRepository;
    @RequestMapping(value = "/UserPagination", method = RequestMethod.POST)
    public AdminResponseDto getProjectPagination(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestBody(required = false) AdminFilter filtre
    ) {
        return userIService.getUserPagination(pageNo, pageSize, sortBy, sortDir,filtre);
    }

    @PostMapping("/change-role")
    public ResponseEntity<RoleDto> changeUserRole(
            @RequestParam Roles roles,
            @RequestParam Long userId
    ) {
        return userIService.changeUserRole(roles,userId);
    }

    @PostMapping("/change-account-status")
    public ResponseEntity<User_validate_Dto> changeAccountStatus(
            @RequestParam Boolean status,
            @RequestParam Long userId
    ) {
        return userIService.changeAccountStatus(status,userId);
    }
    @GetMapping("/resolve")
    public ResponseEmail resolveUser(

            @RequestParam(required = false) String email
    ) throws IOException {

        if (email != null) {
            return userIService.findByEmail(email);
        }
        throw new BadRequestException("email required");
    }
    /***** FORGOT PASSWORD ******/
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Password_dto password_dto,
            @RequestParam String email) {

        try {
            userIService.change_password(password_dto, email);
            return ResponseEntity.ok().build(); // 200
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage()); // 400
        } catch (MessagingException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while sending email"); // 500
        }
    }






}
