package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.MailDto;
import com.example.MediFlow.services.IEmailService;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
@Service
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;


    public void sendEmail(MailDto mail, String template, String img ) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(mail.getProps());
        context.setVariable("mail", mail.getMailTo());
        context.setVariable("baseUrl", "http://localhost:8080"); //
        String html = templateEngine.process(template, context);
        helper.setTo(mail.getMailTo());
        if(mail.getCc()!=null)
            helper.setCc(mail.getCc());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());
        helper.addInline("im",  new ClassPathResource("static/images/"+img));
        emailSender.send(message);
    }
    public void sendEmailAttachement(MailDto mail, String template, String img, byte[] attachmentBytes) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(mail.getProps());
        String html = templateEngine.process(template, context);
        helper.setTo(mail.getMailTo());
        if(mail.getCc() != null)
            helper.setCc(mail.getCc());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());
        //   helper.addInline("im", new ClassPathResource("img/" + img));

        // Adding the attachment
        if (attachmentBytes != null && attachmentBytes.length > 0) {
            String attachmentName = "attachment.pdf"; // You might want to dynamically set this based on your needs
            helper.addAttachment(attachmentName, new ByteArrayResource(attachmentBytes));
        }

        emailSender.send(message);
    }

}
