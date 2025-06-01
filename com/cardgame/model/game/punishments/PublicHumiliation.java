package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class PublicHumiliation implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - stage setting
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(20, 20, 50),
                0, HEIGHT, new Color(50, 50, 100)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw stage
        g2d.setColor(new Color(150, 100, 50));
        g2d.fillRect(0, HEIGHT - 60, WIDTH, 60);

        // Draw curtains
        g2d.setColor(new Color(180, 0, 0));
        g2d.fillRect(0, 0, 30, HEIGHT - 60);
        g2d.fillRect(WIDTH - 30, 0, 30, HEIGHT - 60);

        // Draw stage spotlights
        for (int i = 0; i < 3; i++) {
            int x = 50 + i * 100;
            // Flashing effect
            boolean isOn = (frame + i) % 10 < 8;
            
            if (isOn) {
                // Draw light cone
                GradientPaint lightGradient = new GradientPaint(
                        x, 0, new Color(255, 255, 200, 150),
                        x, HEIGHT - 60, new Color(255, 255, 200, 0)
                );
                g2d.setPaint(lightGradient);
                
                int[] xPoints = {x - 40, x + 40, x};
                int[] yPoints = {HEIGHT - 60, HEIGHT - 60, 0};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
            
            // Draw spotlight fixture
            g2d.setColor(new Color(50, 50, 50));
            g2d.fillRect(x - 10, 0, 20, 10);
        }

        // Draw audience silhouettes
        g2d.setColor(new Color(10, 10, 30));
        for (int row = 0; row < 3; row++) {
            int y = HEIGHT - 40 - row * 15;
            for (int i = 0; i < 15; i++) {
                int x = 10 + i * 20;
                g2d.fillOval(x, y, 15, 10);
                g2d.fillRect(x + 5, y + 5, 5, 10);
            }
        }

        // Draw person on stage (the victim)
        int personX = WIDTH / 2;
        int personY = HEIGHT - 80;

        // Animate the person to look embarrassed (shifting weight)
        int sway = (int) (Math.sin(frame * 0.1) * 3);

        // Draw head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(personX - 10 + sway, personY - 30, 20, 20);

        // Draw blushing cheeks
        g2d.setColor(new Color(255, 100, 100, 150));
        g2d.fillOval(personX - 8 + sway, personY - 20, 6, 4);
        g2d.fillOval(personX + 2 + sway, personY - 20, 6, 4);

        // Draw embarrassed expression
        g2d.setColor(Color.BLACK);
        // Eyes
        g2d.fillOval(personX - 6 + sway, personY - 25, 4, 4);
        g2d.fillOval(personX + 2 + sway, personY - 25, 4, 4);
        
        // Mouth (changing between embarrassed expressions)
        if (frame % 30 < 15) {
            // Nervous smile
            g2d.drawArc(personX - 5 + sway, personY - 18, 10, 5, 0, -180);
        } else {
            // Awkward frown
            g2d.drawArc(personX - 5 + sway, personY - 15, 10, 5, 0, 180);
        }

        // Draw body
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(personX + sway, personY - 10, personX + sway, personY + 20);

        // Draw arms (covering face in embarrassment)
        int armAngle = 30 + (int) (Math.sin(frame * 0.2) * 10);
        g2d.drawLine(personX + sway, personY, 
                personX - 15 + sway, personY - 10);
        g2d.drawLine(personX + sway, personY, 
                personX + 15 + sway, personY - 10);

        // Draw legs (shifting nervously)
        int legSway = (int) (Math.sin(frame * 0.3) * 5);
        g2d.drawLine(personX + sway, personY + 20, 
                personX - 10 + legSway + sway, personY + 50);
        g2d.drawLine(personX + sway, personY + 20, 
                personX + 10 - legSway + sway, personY + 50);

        // Draw sweat drops
        g2d.setColor(new Color(200, 200, 255));
        for (int i = 0; i < 5; i++) {
            if ((frame + i * 7) % 20 < 10) {
                g2d.fillOval(
                        personX - 15 + (int) (Math.random() * 30) + sway,
                        personY - 20 + (int) (Math.random() * 20),
                        3, 6);
            }
        }

        // Draw pointing fingers from audience
        g2d.setColor(new Color(255, 200, 150));
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 8; i++) {
            int startX = 30 + i * 35;
            int startY = HEIGHT - 30 - (i % 3) * 15;
            
            // Animate pointing
            int pointAngle = (int) (Math.sin((frame + i * 10) * 0.1) * 10);
            
            g2d.drawLine(startX, startY, 
                    startX + (int) (Math.cos(Math.toRadians(pointAngle + 45)) * 30),
                    startY + (int) (Math.sin(Math.toRadians(pointAngle + 45)) * 30));
        }

        // Draw laughing symbols
        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String[] laughSymbols = {"HA!", "LOL!", "HAHA!", "OMG!", "ROFL!"};
        
        for (int i = 0; i < 10; i++) {
            if ((frame + i * 5) % 30 < 15) {
                int x = 20 + (int) (Math.random() * (WIDTH - 40));
                int y = HEIGHT - 20 - (int) (Math.random() * 60);
                String symbol = laughSymbols[i % laughSymbols.length];
                g2d.drawString(symbol, x, y);
            }
        }

        String[] embarrassingStatements = {
                "I still sleep with a nightlight!",
                "I failed kindergarten twice!",
                "I can't tie my own shoes!",
                "I'm afraid of butterflies!",
                "I still call my mom for laundry help!"
        };
        
        // Select statement based on frame
        String statement = embarrassingStatements[(frame / 40) % embarrassingStatements.length];
        
        // Draw speech bubble
        g2d.setColor(new Color(255, 255, 255));
        int bubbleWidth = g2d.getFontMetrics().stringWidth(statement) + 20;
        int bubbleHeight = 30;
        int bubbleX = personX - bubbleWidth / 2;
        int bubbleY = personY - 60;
        
        g2d.fillRoundRect(bubbleX, bubbleY, bubbleWidth, bubbleHeight, 10, 10);
        
        // Draw bubble pointer
        int[] xPoints = {personX, personX + 5, personX - 5};
        int[] yPoints = {personY - 30, bubbleY + bubbleHeight, bubbleY + bubbleHeight};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Draw text in bubble
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(statement, bubbleX + 10, bubbleY + 20);

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(255, 50, 50));
        String title = "PUBLIC HUMILIATION";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

        g2d.dispose();
        return image;
    }

    @Override
    public String getDescription() {
        return "Public Humiliation";
    }
}
