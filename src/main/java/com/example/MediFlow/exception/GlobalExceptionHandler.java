package com.example.MediFlow.exception;

import com.example.MediFlow.Dtos.ApiResponse;
import com.example.MediFlow.Dtos.Api_Response;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserServiceCustomException.class)
    public ResponseEntity<Map<String, Object>> handleUser(UserServiceCustomException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("code", ex.getErrorCode());
        error.put("status", ex.getErrorStatus().value());

        return ResponseEntity
                .status(ex.getErrorStatus())
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("message", "Internal server error");
        error.put("code", "SERVER_ERROR");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(AccountNotValidatedException.class)
    public ResponseEntity<?> handleAccountNotValidated(AccountNotValidatedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "message", ex.getMessage()
                ));
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> handleEmailNotFound(EmailNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDuplicate(DataIntegrityViolationException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Constraint violation: duplicate data");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {

            Map<String, String> details = new HashMap<>();
            details.put("message", error.getDefaultMessage());
            System.out.println("message "+error);
         //   details.put("code", error.getObjectName()); // 🔥 IMPORTANT

            errors.put(error.getField(), details);
        });

        return ResponseEntity.badRequest().body(
                new ApiResponse(
                        "VALIDATION_FAILED",
                        "Validation failed",
                        errors
                )
        );
    }
    @ExceptionHandler(PatientAlreadyExistsException.class)
    public ResponseEntity<?> handlePatientExists(PatientAlreadyExistsException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("code", "PATIENT_ALREADY_EXISTS");
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 is correct here
                .body(response);
    }
    @ExceptionHandler(AppointmentException.class)
    public ResponseEntity<?> handleAppointment(AppointmentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "code", "APPOINTMENT_EXISTS",
                        "message", ex.getMessage()
                ));
    }
}