package com.example.MediFlow.Dtos.consultation;

import com.example.MediFlow.entity.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Patient_consultation_Dtos {

    private String fullName;
    private String phone;
    private String whatsappNumber;
    private LocalDate birthDate;
    private Gender gender;
    private String medical_Record_ID;
    @Column(length = 10000000)
    @Lob
    private byte[] profilePicture;
    private String address;

}
