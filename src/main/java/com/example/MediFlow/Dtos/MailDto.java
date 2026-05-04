package com.example.MediFlow.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {
    private String from;
    private String mailTo;
    private String subject;
    private String [] cc;
    private List<Object> attachments;
    private List<MultipartFile> attachmentFile;

    private Map<String, Object> props;


}
