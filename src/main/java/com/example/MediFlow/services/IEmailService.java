package com.example.MediFlow.services;

import com.example.MediFlow.Dtos.MailDto;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface IEmailService {
    public void sendEmail(MailDto mail, String template, String img ) throws MessagingException, IOException;
    public void sendEmailAttachement(MailDto mail, String template, String img, byte[] attachmentBytes) throws MessagingException, IOException ;

}
