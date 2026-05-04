package com.example.MediFlow.services.impl;


import com.example.MediFlow.services.IAvatarGeneratorService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class AvatarGeneratorServiceImpl implements IAvatarGeneratorService {

    public byte[] generateAvatar(String firstName, String lastName) throws Exception {
        int width = 200;
        int height = 200;

        // Create image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // Generate background color from name hash
        String nameHash = firstName + lastName;
        int hue = Math.abs(nameHash.hashCode()) % 360;
        Color bgColor = Color.getHSBColor(hue/360f, 0.7f, 0.7f);
        graphics.setColor(bgColor);
        graphics.fillRect(0, 0, width, height);

        // Create initials
        String initials = (firstName.isEmpty() ? "" : firstName.substring(0, 1)) +
                (lastName.isEmpty() ? "" : lastName.substring(0, 1));
        initials = initials.toUpperCase();

        // Draw text
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.BOLD, 72));
        FontMetrics fm = graphics.getFontMetrics();
        int textWidth = fm.stringWidth(initials);
        int x = (width - textWidth) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        graphics.drawString(initials, x, y);

        // Convert to byte array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } finally {
            graphics.dispose();
        }
    }
}
