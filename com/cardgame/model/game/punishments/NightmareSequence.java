package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Nightmare Sequence punishment animation
 */
public class NightmareSequence implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;
    private static final Random random = new Random();

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - dark, spooky bedroom
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(10, 0, 20),
                0, HEIGHT, new Color(30, 0, 50)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw moonlight from window
        g2d.setColor(new Color(100, 100, 200, 50));
        int[] xPoints = {WIDTH - 100, WIDTH - 20, WIDTH - 20, WIDTH - 100};
        int[] yPoints = {50, 30, HEIGHT - 30, HEIGHT - 50};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        // Draw window frame
        g2d.setColor(new Color(60, 40, 20));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(WIDTH - 100, 30, 80, HEIGHT - 60);
        g2d.drawLine(WIDTH - 60, 30, WIDTH - 60, HEIGHT - 30);
        g2d.drawLine(WIDTH - 100, HEIGHT / 2, WIDTH - 20, HEIGHT / 2);
        
        // Draw moon in window
        g2d.setColor(new Color(220, 220, 255));
        g2d.fillOval(WIDTH - 80, 50, 40, 40);
        
        // Draw some clouds occasionally passing over moon
        if (frame % 60 < 30) {
            g2d.setColor(new Color(50, 50, 80, 150));
            g2d.fillOval(WIDTH - 90 + (frame % 30), 45, 30, 20);
            g2d.fillOval(WIDTH - 70 + (frame % 30), 55, 40, 25);
        }
        
        // Draw bed
        g2d.setColor(new Color(60, 40, 20));
        g2d.fillRect(30, HEIGHT - 60, 150, 20); // bed frame
        
        g2d.setColor(new Color(200, 200, 220));
        g2d.fillRect(30, HEIGHT - 70, 150, 10); // mattress
        
        g2d.setColor(new Color(100, 100, 180));
        g2d.fillRect(30, HEIGHT - 90, 150, 20); // blanket
        
        // Draw pillow
        g2d.setColor(new Color(220, 220, 240));
        g2d.fillOval(40, HEIGHT - 95, 40, 20);
        
        // Draw person in bed (tossing and turning)
        int personX = 100;
        int personY = HEIGHT - 80;
        
        // Animate tossing and turning
        int tossAngle = (int) (Math.sin(frame * 0.1) * 10);
        
        // Draw body under covers with movement
        g2d.setColor(new Color(150, 150, 200));
        g2d.fillOval(personX - 30 + tossAngle, personY - 5, 60, 20);
        
        // Draw head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(personX - 10 + tossAngle, personY - 25, 20, 20);
        
        // Draw distressed face
        g2d.setColor(Color.BLACK);
        // Eyes (sometimes open, sometimes closed - blinking in fear)
        if (frame % 10 < 5) {
            // Eyes open wide in fear
            g2d.fillOval(personX - 6 + tossAngle, personY - 22, 4, 4);
            g2d.fillOval(personX + 2 + tossAngle, personY - 22, 4, 4);
        } else {
            // Eyes squeezed shut
            g2d.drawLine(personX - 6 + tossAngle, personY - 20, personX - 2 + tossAngle, personY - 22);
            g2d.drawLine(personX + 2 + tossAngle, personY - 22, personX + 6 + tossAngle, personY - 20);
        }
        
        // Mouth (grimacing)
        g2d.drawLine(personX - 5 + tossAngle, personY - 15, personX + 5 + tossAngle, personY - 15);
        
        // Draw sweat drops
        g2d.setColor(new Color(200, 200, 255));
        for (int i = 0; i < 3; i++) {
            if ((frame + i * 7) % 15 < 7) {
                g2d.fillOval(
                        personX - 15 + (int) (Math.random() * 30) + tossAngle,
                        personY - 25 + (int) (Math.random() * 10),
                        3, 6);
            }
        }
        
        // Draw nightmare thought bubble with scary images
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(personX + 30, personY - 60, 80, 50);
        
        // Draw scary elements in the thought bubble (rotating based on frame)
        int nightmareType = (frame / 20) % 5;
        
        switch (nightmareType) {
            case 0: // Spiders
                drawSpiders(g2d, personX + 70, personY - 35, frame);
                break;
            case 1: // Falling
                drawFalling(g2d, personX + 70, personY - 35, frame);
                break;
            case 2: // Chased
                drawChased(g2d, personX + 70, personY - 35, frame);
                break;
            case 3: // Teeth falling out
                drawTeethFalling(g2d, personX + 70, personY - 35, frame);
                break;
            case 4: // Public speaking unprepared
                drawPublicSpeaking(g2d, personX + 70, personY - 35, frame);
                break;
        }
        
        // Draw scary shadows in the room corners
        g2d.setColor(new Color(0, 0, 0, 150));
        
        // Top left corner shadow
        g2d.fillOval(-20, -20, 100, 100);
        
        // Bottom shadows
        g2d.fillOval(-10, HEIGHT - 40, 60, 60);
        g2d.fillOval(WIDTH - 50, HEIGHT - 30, 70, 50);
        
        // Draw monster under the bed (occasionally peeking out)
        if (frame % 40 > 30) {
            g2d.setColor(new Color(100, 0, 0));
            g2d.fillOval(50, HEIGHT - 45, 20, 10);
            
            // Eyes
            g2d.setColor(new Color(255, 255, 0));
            g2d.fillOval(55, HEIGHT - 43, 4, 4);
            g2d.fillOval(62, HEIGHT - 43, 4, 4);
        }
        
        // Draw clock showing middle of the night
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(20, 30, 40, 20);
        
        g2d.setColor(new Color(255, 0, 0));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
        
        // Clock time (always showing between 2:00 and 4:00 AM)
        int hour = 2 + (frame / 30) % 3;
        int minute = (frame % 60);
        String time = String.format("%d:%02d", hour, minute);
        g2d.drawString(time, 25, 45);
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(200, 0, 200));
        String title = "NIGHTMARE SEQUENCE";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

        g2d.dispose();
        return image;
    }
    
    /**
     * Helper method to draw spiders nightmare
     */
    private void drawSpiders(Graphics2D g2d, int centerX, int centerY, int frame) {
        // Draw multiple spiders
        for (int i = 0; i < 5; i++) {
            int x = centerX + (int) (Math.cos(frame * 0.05 + i) * 20);
            int y = centerY + (int) (Math.sin(frame * 0.05 + i) * 15);
            
            // Spider body
            g2d.setColor(new Color(0, 0, 0));
            g2d.fillOval(x - 5, y - 5, 10, 10);
            
            // Spider legs
            g2d.setStroke(new BasicStroke(1));
            for (int leg = 0; leg < 8; leg++) {
                double angle = Math.PI * 2 * leg / 8;
                int legX = (int) (Math.cos(angle) * 10);
                int legY = (int) (Math.sin(angle) * 10);
                
                // Add wiggle to legs
                int wiggle = (int) (Math.sin((frame + leg * 10) * 0.2) * 2);
                
                g2d.drawLine(x, y, x + legX + wiggle, y + legY);
            }
        }
        
        // Draw web in background
        g2d.setColor(new Color(200, 200, 200, 100));
        for (int i = 0; i < 5; i++) {
            g2d.drawOval(centerX - 30 + i * 6, centerY - 20 + i * 4, 60 - i * 12, 40 - i * 8);
        }
        
        // Draw radiating web lines
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            int x = (int) (Math.cos(angle) * 30);
            int y = (int) (Math.sin(angle) * 20);
            g2d.drawLine(centerX, centerY, centerX + x, centerY + y);
        }
    }
    
    /**
     * Helper method to draw falling nightmare
     */
    private void drawFalling(Graphics2D g2d, int centerX, int centerY, int frame) {
        // Draw a person falling
        int fallY = (frame % 30) * 2;
        
        // Draw clouds passing by
        g2d.setColor(new Color(200, 200, 200, 150));
        g2d.fillOval(centerX - 30, centerY - 25 - fallY, 20, 15);
        g2d.fillOval(centerX + 10, centerY - 15 - fallY, 25, 20);
        
        // Draw person
        g2d.setColor(new Color(0, 0, 0));
        
        // Head
        g2d.fillOval(centerX - 5, centerY - 5 + fallY, 10, 10);
        
        // Flailing limbs
        int armAngle = (frame % 10) * 36; // 0-360 degrees
        int legAngle = ((frame + 5) % 10) * 36; // 0-360 degrees offset from arms
        
        // Arms
        g2d.drawLine(centerX, centerY + fallY, 
                centerX + (int) (Math.cos(Math.toRadians(armAngle)) * 10),
                centerY + fallY + (int) (Math.sin(Math.toRadians(armAngle)) * 10));
        g2d.drawLine(centerX, centerY + fallY, 
                centerX + (int) (Math.cos(Math.toRadians(armAngle + 180)) * 10),
                centerY + fallY + (int) (Math.sin(Math.toRadians(armAngle + 180)) * 10));
        
        // Legs
        g2d.drawLine(centerX, centerY + 5 + fallY, 
                centerX + (int) (Math.cos(Math.toRadians(legAngle)) * 15),
                centerY + 5 + fallY + (int) (Math.sin(Math.toRadians(legAngle)) * 15));
        g2d.drawLine(centerX, centerY + 5 + fallY, 
                centerX + (int) (Math.cos(Math.toRadians(legAngle + 180)) * 15),
                centerY + 5 + fallY + (int) (Math.sin(Math.toRadians(legAngle + 180)) * 15));
        
        // Draw ground approaching
        g2d.setColor(new Color(100, 50, 0));
        g2d.fillRect(centerX - 40, centerY + 30, 80, 10);
    }
    
    /**
     * Helper method to draw being chased nightmare
     */
    private void drawChased(Graphics2D g2d, int centerX, int centerY, int frame) {
        // Draw running person
        int runX = centerX - 10;
        int runY = centerY;
        
        // Animate running
        int legAngle = (int) (Math.sin(frame * 0.5) * 30);
        int armAngle = (int) (Math.sin(frame * 0.5 + Math.PI) * 30);
        
        // Draw head
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillOval(runX - 5, runY - 15, 10, 10);
        
        // Draw body
        g2d.drawLine(runX, runY - 5, runX, runY + 10);
        
        // Draw arms
        g2d.drawLine(runX, runY, 
                runX + (int) (Math.cos(Math.toRadians(armAngle + 90)) * 10),
                runY + (int) (Math.sin(Math.toRadians(armAngle + 90)) * 10));
        
        // Draw legs
        g2d.drawLine(runX, runY + 10, 
                runX + (int) (Math.cos(Math.toRadians(legAngle + 90)) * 15),
                runY + 10 + (int) (Math.sin(Math.toRadians(legAngle + 90)) * 15));
        g2d.drawLine(runX, runY + 10, 
                runX + (int) (Math.cos(Math.toRadians(-legAngle + 90)) * 15),
                runY + 10 + (int) (Math.sin(Math.toRadians(-legAngle + 90)) * 15));
        
        // Draw monster chasing
        int monsterX = runX - 30;
        int monsterY = runY;
        
        // Draw monster body (pulsating)
        int size = 15 + (int) (Math.sin(frame * 0.2) * 5);
        g2d.setColor(new Color(100, 0, 0));
        g2d.fillOval(monsterX - size/2, monsterY - size/2, size, size);
        
        // Draw monster eyes
        g2d.setColor(new Color(255, 255, 0));
        g2d.fillOval(monsterX - 5, monsterY - 5, 4, 4);
        g2d.fillOval(monsterX + 1, monsterY - 5, 4, 4);
        
        // Draw monster teeth
        g2d.setColor(new Color(255, 255, 255));
        for (int i = 0; i < 5; i++) {
            g2d.fillRect(monsterX - 7 + i * 3, monsterY + 2, 2, 4);
        }
        
        // Draw tendrils/arms reaching out
        g2d.setColor(new Color(100, 0, 0));
        for (int i = 0; i < 3; i++) {
            int tendrilLength = 10 + (int) (Math.sin(frame * 0.3 + i) * 5);
            g2d.drawLine(monsterX, monsterY, 
                    monsterX + tendrilLength, 
                    monsterY - 5 + i * 5);
        }
    }
    
    /**
     * Helper method to draw teeth falling out nightmare
     */
    private void drawTeethFalling(Graphics2D g2d, int centerX, int centerY, int frame) {
        // Draw face outline
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(centerX - 20, centerY - 20, 40, 40);
        
        // Draw eyes (wide with shock)
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillOval(centerX - 12, centerY - 10, 8, 8);
        g2d.fillOval(centerX + 4, centerY - 10, 8, 8);
        
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillOval(centerX - 9, centerY - 7, 4, 4);
        g2d.fillOval(centerX + 7, centerY - 7, 4, 4);
        
        // Draw open mouth in horror
        g2d.setColor(new Color(150, 0, 0));
        g2d.fillOval(centerX - 10, centerY + 5, 20, 15);
        
        // Draw gums
        g2d.setColor(new Color(255, 150, 150));
        g2d.fillRect(centerX - 10, centerY + 5, 20, 5);
        
        // Draw falling teeth
        g2d.setColor(new Color(255, 255, 255));
        for (int i = 0; i < 5; i++) {
            // Randomize falling position and speed
            int toothX = centerX - 8 + i * 4;
            int toothY = centerY + 10 + ((frame + i * 5) % 20);
            
            // Draw tooth
            g2d.fillRect(toothX, toothY, 3, 5);
        }
        
        // Draw hand touching mouth in panic
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(centerX + 15, centerY + 10, 10, 10);
        
        // Draw fingers
        for (int i = 0; i < 3; i++) {
            g2d.drawLine(centerX + 20, centerY + 15,
                    centerX + 15 + i * 5, centerY + 20);
        }
    }
    
    /**
     * Helper method to draw public speaking nightmare
     */
    private void drawPublicSpeaking(Graphics2D g2d, int centerX, int centerY, int frame) {
        // Draw podium
        g2d.setColor(new Color(100, 50, 0));
        g2d.fillRect(centerX - 15, centerY, 30, 20);
        
        // Draw person behind podium
        // Head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(centerX - 10, centerY - 20, 20, 20);
        
        // Draw panicked face
        g2d.setColor(new Color(0, 0, 0));
        // Eyes wide with panic
        g2d.fillOval(centerX - 6, centerY - 15, 4, 4);
        g2d.fillOval(centerX + 2, centerY - 15, 4, 4);
        
        // Open mouth
        g2d.drawOval(centerX - 3, centerY - 8, 6, 6);
        
        // Draw sweat
        g2d.setColor(new Color(200, 200, 255));
        for (int i = 0; i < 3; i++) {
            if ((frame + i * 5) % 10 < 5) {
                g2d.fillOval(centerX - 10 + i * 10, centerY - 20, 3, 6);
            }
        }
        
        // Draw audience as silhouettes
        g2d.setColor(new Color(0, 0, 0));
        for (int i = 0; i < 8; i++) {
            int x = centerX - 30 + i * 8;
            int y = centerY + 15;
            g2d.fillOval(x, y, 6, 6);
        }
        
        // Draw speech bubble with "Uhh..."
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillOval(centerX + 15, centerY - 15, 25, 15);
        
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
        g2d.drawString("Uhh...", centerX + 18, centerY - 5);
        
        // Draw blank notes/paper
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(centerX - 5, centerY + 5, 10, 10);
    }

    @Override
    public String getDescription() {
        return "Nightmare Sequence";
    }
}
