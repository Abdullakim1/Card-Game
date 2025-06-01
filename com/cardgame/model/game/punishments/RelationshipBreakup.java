package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class RelationshipBreakup implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - sad gradient
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(70, 70, 100),
                0, HEIGHT, new Color(30, 30, 50)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw rain
        g2d.setColor(new Color(200, 200, 255, 100));
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * WIDTH);
            int y = (frame * 5 + i * 10) % HEIGHT;
            g2d.drawLine(x, y, x - 2, y + 10);
        }

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(255, 50, 50));
        String title = "RELATIONSHIP BREAKUP";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

        // Draw broken heart
        g2d.setColor(new Color(200, 0, 0));

        // Left half of heart
        g2d.fillArc(120, 60, 30, 30, 0, 180);
        g2d.fillArc(105, 60, 30, 30, 0, 180);
        g2d.fillRect(105, 75, 45, 30);
        g2d.fillArc(105, 90, 45, 30, 180, 180);

        // Right half of heart (shifted away from left half)
        g2d.fillArc(170, 60, 30, 30, 0, 180);
        g2d.fillArc(155, 60, 30, 30, 0, 180);
        g2d.fillRect(155, 75, 45, 30);
        g2d.fillArc(155, 90, 45, 30, 180, 180);

        // Draw crack between heart halves
        g2d.setColor(new Color(30, 0, 50)); // Background color for crack
        g2d.setStroke(new BasicStroke(3));

        // Jagged crack
        int[] xPoints = {150, 145, 155, 145, 155, 150};
        int[] yPoints = {60, 75, 90, 105, 120, 135};
        g2d.drawPolyline(xPoints, yPoints, 6);

        // Draw couple silhouettes walking away from each other
        g2d.setColor(new Color(0, 0, 0));

        // Left person (walking left)
        int leftPersonX = 80 - (frame % 30);
        g2d.fillOval(leftPersonX, 150, 20, 20); // head
        g2d.fillRect(leftPersonX + 5, 170, 10, 30); // body

        // Right person (walking right)
        int rightPersonX = 200 + (frame % 30);
        g2d.fillOval(rightPersonX, 150, 20, 20); // head
        g2d.fillRect(rightPersonX + 5, 170, 10, 30); // body

        // Draw text messages between them that fade
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        // Calculate alpha for fading
        int alpha1 = Math.max(0, 255 - (frame % 60) * 8);
        int alpha2 = Math.max(0, 255 - ((frame + 30) % 60) * 8);

        // Message bubbles
        g2d.setColor(new Color(200, 200, 200, alpha1));
        g2d.fillRoundRect(110, 140, 80, 20, 10, 10);
        g2d.setColor(new Color(0, 0, 0, alpha1));
        g2d.drawString("It's not you, it's me", 115, 155);

        g2d.setColor(new Color(200, 200, 200, alpha2));
        g2d.fillRoundRect(110, 170, 80, 20, 10, 10);
        g2d.setColor(new Color(0, 0, 0, alpha2));
        g2d.drawString("We need to talk...", 115, 185);

        // Draw falling tears
        g2d.setColor(new Color(100, 200, 255, 150));
        for (int i = 0; i < 10; i++) {
            int x = 50 + (i * 20);
            int y = (i * 15 + frame * 2) % HEIGHT;
            g2d.fillOval(x, y, 3, 6);
        }

        // Draw crossed-out photos
        g2d.setColor(Color.WHITE);
        g2d.fillRect(40, 80, 40, 30); // photo frame left
        g2d.fillRect(220, 80, 40, 30); // photo frame right

        // Draw red X over photos
        g2d.setColor(new Color(200, 0, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(40, 80, 80, 110); // X on left photo
        g2d.drawLine(80, 80, 40, 110);
        g2d.drawLine(220, 80, 260, 110); // X on right photo
        g2d.drawLine(260, 80, 220, 110);

        // Draw changing messages at bottom
        String[] messages = {
                "They never loved you anyway",
                "We're just victims of love",
                "Now, there's nothing we can do",
                "They blocked your number",
                "This is where it ends"
        };

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(200, 0, 0));
        String message = messages[(frame / 12) % messages.length];
        g2d.drawString(message, (WIDTH - g2d.getFontMetrics().stringWidth(message)) / 2, HEIGHT - 20);

        g2d.dispose();
        return image;
    }

    @Override
    public String getDescription() {
        return "Relationship Breakup";
    }
}
