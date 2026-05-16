package com.example.MediFlow.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppService {

    @Value("${whatsapp.token}")
    private String token;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;
    public String sendMessage(String to, String message) {

        try {

            String url =
                    "https://graph.facebook.com/v25.0/"
                            + phoneNumberId +
                            "/messages";

            System.out.println("URL => " + url);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();

            body.put("messaging_product", "whatsapp");
            body.put("to", to);
            body.put("type", "text");

            Map<String, String> text = new HashMap<>();
            text.put("body", message);

            body.put("text", text);

            System.out.println(body);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class
                    );

            System.out.println(response.getBody());

            return response.getBody();

        } catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();
        }
    }
}