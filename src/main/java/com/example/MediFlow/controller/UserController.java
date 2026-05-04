package com.example.MediFlow.controller;
import com.example.MediFlow.Dtos.ApiResponse;
import com.example.MediFlow.Dtos.JwtResponse;
import com.example.MediFlow.Dtos.loginDtos.LoginRequestDTO;
import com.example.MediFlow.Dtos.user_dto.UserDTO;
import com.example.MediFlow.Dtos.user_dto.UserRegisterDTO;
import com.example.MediFlow.Dtos.user_dto.UserUpdateResponseDto;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.exception.AccountNotValidatedException;
import com.example.MediFlow.exception.EmailNotFoundException;
import com.example.MediFlow.repository.UserRepository;
import org.springframework.validation.annotation.Validated;
import com.example.MediFlow.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;


@RequestMapping("/api/v1/auth/")
@Validated
@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;

    /******Create new user******/
    @PostMapping("/user/client")
    public ResponseEntity<ApiResponse> registerUser(
            @Valid @RequestBody UserRegisterDTO dto) throws Exception {

        userService.createUserAccount(dto);

        return ResponseEntity.ok(
                new ApiResponse("SUCCESS", "User created successfully",null)
        );
    }
    /**********Login**********/
    @PostMapping("/token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginData) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginData.getEmail(),
                            loginData.getPassword()
                    )
            );

            JwtResponse response = userService.login(loginData.getEmail());

            User user = userRepository.findByEmail(loginData.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (user.getProfilePicture() != null) {
                String base64Avatar = Base64.getEncoder().encodeToString(user.getProfilePicture());
                response.setAvatarUrl("data:image/png;base64," + base64Avatar);
            }

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "بيانات تسجيل الدخول غير صحيحة",
                            "code", "BAD_LOGIN_CREDENTIALS"
                    ));
        }
    }

    /***UPDATE USER****/
    @PutMapping("/updateUser")
    public ResponseEntity<UserUpdateResponseDto> updateUser(@Valid @RequestBody UserUpdateResponseDto userDTO) throws IOException {
        UserUpdateResponseDto updatedUser = userService.updateUserByEmail(userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/validate")
    public String validate(@RequestParam Long token) {
        User user = userRepository.findByTokenToValidate(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        // Vérifier expiration (optionnel)
        if (user.getValidateCodeCreationDate()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }

        // Activer compte
        user.setIsValidated(true);

        // supprimer token
        user.setTokenToValidate(null);

        userRepository.save(user);

        return "redirect:/login";
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws Exception {
        try {
            userService.requestPasswordReset(email);

            return ResponseEntity.ok(
                    Map.of("message", "Email sent successfully")
            );

        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Email not found"));

        } catch (AccountNotValidatedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unexpected error"));
        }}
}