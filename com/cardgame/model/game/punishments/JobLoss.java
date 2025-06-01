package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class JobLoss implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - office setting
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(220, 220, 220),
                0, HEIGHT, new Color(180, 180, 180)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw office walls
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);
        
        // Draw floor
        g2d.setColor(new Color(150, 150, 150));
        g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);
        
        // Draw office desk
        g2d.setColor(new Color(120, 80, 40));
        g2d.fillRect(WIDTH / 2 - 60, HEIGHT / 2 - 20, 120, 10);
        
        // Draw desk legs
        g2d.fillRect(WIDTH / 2 - 55, HEIGHT / 2 - 10, 10, 30);
        g2d.fillRect(WIDTH / 2 + 45, HEIGHT / 2 - 10, 10, 30);
        
        // Draw computer on desk
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(WIDTH / 2 - 30, HEIGHT / 2 - 40, 40, 20);
        g2d.fillRect(WIDTH / 2 - 20, HEIGHT / 2 - 20, 20, 5);
        
        // Draw screen content (termination notice)
        g2d.setColor(new Color(200, 200, 255));
        g2d.fillRect(WIDTH / 2 - 28, HEIGHT / 2 - 38, 36, 16);
        
        g2d.setColor(new Color(255, 0, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 6));
        g2d.drawString("TERMINATED", WIDTH / 2 - 25, HEIGHT / 2 - 28);
        
        // Draw boss character
        int bossX = WIDTH / 2 + 70;
        int bossY = HEIGHT / 2 + 20;
        
        // Boss head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(bossX - 15, bossY - 30, 30, 30);
        
        // Boss hair
        g2d.setColor(new Color(100, 100, 100));
        g2d.fillRect(bossX - 15, bossY - 30, 30, 10);
        
        // Boss face
        g2d.setColor(Color.BLACK);
        // Eyes
        g2d.fillOval(bossX - 8, bossY - 22, 5, 5);
        g2d.fillOval(bossX + 3, bossY - 22, 5, 5);
        
        // Stern mouth
        g2d.drawLine(bossX - 5, bossY - 10, bossX + 5, bossY - 10);
        
        // Boss body
        g2d.setColor(new Color(50, 50, 150)); // Blue suit
        g2d.fillRect(bossX - 20, bossY, 40, 40);
        
        // Boss tie
        g2d.setColor(new Color(200, 0, 0));
        int[] xPoints = {bossX, bossX + 5, bossX, bossX - 5};
        int[] yPoints = {bossY, bossY + 15, bossY + 30, bossY + 15};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        // Boss arm pointing to door
        g2d.setColor(new Color(255, 200, 150));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(bossX, bossY + 10, bossX + 40, bossY - 10);
        
        // Draw fired employee
        int employeeX = WIDTH / 2 - 30;
        int employeeY = HEIGHT / 2 + 20;
        
        // Animate employee walking away sadly
        employeeX -= frame % 60;
        
        // Employee head (hanging down in sadness)
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(employeeX - 10, employeeY - 30, 20, 20);
        
        // Employee face
        g2d.setColor(Color.BLACK);
        // Eyes (downcast)
        g2d.fillOval(employeeX - 6, employeeY - 25, 3, 3);
        g2d.fillOval(employeeX + 3, employeeY - 25, 3, 3);
        
        // Sad mouth
        g2d.drawArc(employeeX - 5, employeeY - 20, 10, 5, 0, 180);
        
        // Employee body
        g2d.setColor(new Color(100, 100, 100)); // Gray suit
        g2d.fillRect(employeeX - 15, employeeY, 30, 40);
        
        // Employee arms (carrying box of belongings)
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(employeeX - 15, employeeY + 10, employeeX - 25, employeeY + 20);
        g2d.drawLine(employeeX + 15, employeeY + 10, employeeX + 25, employeeY + 20);
        
        // Box of belongings
        g2d.setColor(new Color(180, 140, 80));
        g2d.fillRect(employeeX - 20, employeeY + 20, 40, 30);
        
        // Items sticking out of box
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(employeeX - 15, employeeY + 15, 10, 5); // paper
        
        g2d.setColor(new Color(0, 200, 0));
        g2d.fillOval(employeeX + 5, employeeY + 15, 10, 10); // plant
        
        g2d.setColor(new Color(200, 0, 0));
        g2d.fillRect(employeeX, employeeY + 15, 5, 10); // coffee mug
        
        // Draw door
        g2d.setColor(new Color(150, 100, 50));
        g2d.fillRect(20, HEIGHT / 2 - 70, 40, 80);
        
        // Door handle
        g2d.setColor(new Color(200, 200, 0));
        g2d.fillOval(50, HEIGHT / 2 - 30, 5, 5);
        
        // Draw EXIT sign
        g2d.setColor(new Color(255, 0, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("EXIT", 25, HEIGHT / 2 - 50);
        
        // Draw speech bubble from boss
        String[] phrases = {
            "You're fired!",
            "Clear your desk!",
            "Security will escort you!",
            "Return your keycard!",
            "We're downsizing!"
        };
        
        // Select phrase based on frame
        String phrase = phrases[(frame / 30) % phrases.length];
        
        // Draw speech bubble
        g2d.setColor(new Color(255, 255, 255));
        int bubbleWidth = g2d.getFontMetrics().stringWidth(phrase) + 20;
        int bubbleHeight = 25;
        int bubbleX = bossX - bubbleWidth / 2 + 10;
        int bubbleY = bossY - 60;
        
        g2d.fillRoundRect(bubbleX, bubbleY, bubbleWidth, bubbleHeight, 10, 10);
        
        // Draw bubble pointer
        int[] pointerX = {bossX, bossX + 5, bossX - 5};
        int[] pointerY = {bossY - 30, bubbleY + bubbleHeight, bubbleY + bubbleHeight};
        g2d.fillPolygon(pointerX, pointerY, 3);
        
        // Draw text in bubble
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString(phrase, bubbleX + 10, bubbleY + 17);
        
        // Draw thought bubble from employee
        String[] thoughts = {
            "But I have bills...",
            "My family...",
            "After 10 years...",
            "No severance?",
            "How will I survive?"
        };
        
        // Only show thought bubble if employee is still visible
        if (employeeX > 0) {
            // Select thought based on frame
            String thought = thoughts[(frame / 30 + 2) % thoughts.length];
            
            // Draw thought bubble
            g2d.setColor(new Color(255, 255, 255));
            int thoughtWidth = g2d.getFontMetrics().stringWidth(thought) + 20;
            int thoughtHeight = 25;
            int thoughtX = employeeX - thoughtWidth / 2;
            int thoughtY = employeeY - 60;
            
            g2d.fillRoundRect(thoughtX, thoughtY, thoughtWidth, thoughtHeight, 10, 10);
            
            // Draw thought bubble connector (circles)
            for (int i = 0; i < 3; i++) {
                int size = 6 - i * 2;
                g2d.fillOval(employeeX - size/2, thoughtY + thoughtHeight + i * 5, size, size);
            }
            
            // Draw text in thought bubble
            g2d.setColor(new Color(0, 0, 0));
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString(thought, thoughtX + 10, thoughtY + 17);
        }
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(200, 0, 0));
        String title = "JOB LOSS";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

        g2d.dispose();
        return image;
    }

    @Override
    public String getDescription() {
        return "Job Loss";
    }
}
