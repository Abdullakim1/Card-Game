package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Physical Labor punishment animation
 */
public class PhysicalLabor implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - dirt/ground
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(100, 70, 40),
                0, HEIGHT, new Color(60, 40, 20)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw sky
        g2d.setColor(new Color(180, 200, 220));
        g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);

        // Draw sun
        g2d.setColor(new Color(255, 200, 50));
        g2d.fillOval(WIDTH - 50, 20, 30, 30);

        // Draw heat waves from sun
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            int x1 = WIDTH - 35 + (int) (Math.cos(angle) * 20);
            int y1 = 35 + (int) (Math.sin(angle) * 20);
            int x2 = WIDTH - 35 + (int) (Math.cos(angle) * 35);
            int y2 = 35 + (int) (Math.sin(angle) * 35);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Draw ground with texture
        g2d.setColor(new Color(80, 60, 30));
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * WIDTH);
            int y = HEIGHT / 2 + (int) (Math.random() * HEIGHT / 2);
            g2d.fillOval(x, y, 4, 2);
        }

        // Draw pile of rocks
        for (int i = 0; i < 20; i++) {
            int size = 10 + (int) (Math.random() * 15);
            int x = 30 + (int) (Math.random() * 60);
            int y = HEIGHT - 40 - size / 2 + (int) (Math.random() * 20);
            g2d.setColor(new Color(
                    100 + (int) (Math.random() * 50),
                    100 + (int) (Math.random() * 50),
                    100 + (int) (Math.random() * 50)
            ));
            g2d.fillOval(x, y, size, size);
        }

        // Draw another pile of rocks (destination)
        for (int i = 0; i < 15; i++) {
            int size = 10 + (int) (Math.random() * 15);
            int x = WIDTH - 80 + (int) (Math.random() * 60);
            int y = HEIGHT - 40 - size / 2 + (int) (Math.random() * 20);
            g2d.setColor(new Color(
                    100 + (int) (Math.random() * 50),
                    100 + (int) (Math.random() * 50),
                    100 + (int) (Math.random() * 50)
            ));
            g2d.fillOval(x, y, size, size);
        }

        // Draw stick figure person
        int personX = 100 + (int) (Math.sin(frame * 0.1) * 10); // Swaying motion
        int personY = HEIGHT - 70;

        // Animate the stick figure to look like it's working
        int armAngle = (int) (Math.sin(frame * 0.2) * 30);
        int legAngle = (int) (Math.sin(frame * 0.2) * 15);

        // Draw head
        g2d.setColor(Color.BLACK);
        g2d.fillOval(personX - 10, personY - 30, 20, 20);

        // Draw body
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(personX, personY - 10, personX, personY + 20);

        // Draw arms with animation
        g2d.drawLine(personX, personY, 
                personX + (int) (Math.cos(Math.toRadians(armAngle + 150)) * 25),
                personY + (int) (Math.sin(Math.toRadians(armAngle + 150)) * 25));
        g2d.drawLine(personX, personY, 
                personX + (int) (Math.cos(Math.toRadians(armAngle + 30)) * 25),
                personY + (int) (Math.sin(Math.toRadians(armAngle + 30)) * 25));

        // Draw legs with animation
        g2d.drawLine(personX, personY + 20, 
                personX + (int) (Math.cos(Math.toRadians(legAngle + 210)) * 30),
                personY + 20 + (int) (Math.sin(Math.toRadians(legAngle + 210)) * 30));
        g2d.drawLine(personX, personY + 20, 
                personX + (int) (Math.cos(Math.toRadians(legAngle + 330)) * 30),
                personY + 20 + (int) (Math.sin(Math.toRadians(legAngle + 330)) * 30));

        // Draw rock being carried
        if (frame % 40 < 20) {
            g2d.setColor(new Color(120, 120, 120));
            g2d.fillOval(
                    personX + (int) (Math.cos(Math.toRadians(armAngle + 90)) * 15),
                    personY + (int) (Math.sin(Math.toRadians(armAngle + 90)) * 15),
                    15, 15);
        }

        // Draw sweat drops
        g2d.setColor(new Color(200, 200, 255));
        for (int i = 0; i < 3; i++) {
            if ((frame + i * 10) % 30 < 15) {
                g2d.fillOval(
                        personX - 5 + (int) (Math.random() * 10),
                        personY - 20 + (int) (Math.random() * 10),
                        3, 6);
            }
        }

        // Draw overseer with whip
        int overseerX = WIDTH - 80;
        int overseerY = HEIGHT - 70;

        // Draw overseer head
        g2d.setColor(Color.BLACK);
        g2d.fillOval(overseerX - 10, overseerY - 30, 20, 20);

        // Draw overseer body
        g2d.drawLine(overseerX, overseerY - 10, overseerX, overseerY + 20);

        // Draw overseer arms
        g2d.drawLine(overseerX, overseerY, overseerX - 15, overseerY - 5);
        
        // Animate whip arm
        int whipAngle = (int) (Math.sin(frame * 0.1) * 30) - 30;
        g2d.drawLine(overseerX, overseerY, 
                overseerX + (int) (Math.cos(Math.toRadians(whipAngle)) * 25),
                overseerY + (int) (Math.sin(Math.toRadians(whipAngle)) * 25));

        // Draw whip
        g2d.setColor(new Color(100, 50, 0));
        g2d.setStroke(new BasicStroke(1.5f));
        
        int whipEndX = overseerX + (int) (Math.cos(Math.toRadians(whipAngle)) * 25);
        int whipEndY = overseerY + (int) (Math.sin(Math.toRadians(whipAngle)) * 25);
        
        // Whip curve
        Path2D whipPath = new Path2D.Double();
        whipPath.moveTo(whipEndX, whipEndY);
        
        double controlX = whipEndX + (Math.cos(Math.toRadians(whipAngle - 20)) * 40);
        double controlY = whipEndY + (Math.sin(Math.toRadians(whipAngle - 20)) * 40);
        
        whipPath.quadTo(
            controlX, controlY,
            whipEndX - 50 + (int)(Math.sin(frame * 0.3) * 30), 
            whipEndY + 30 + (int)(Math.cos(frame * 0.3) * 20)
        );
        
        g2d.draw(whipPath);

        // Draw overseer legs
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(overseerX, overseerY + 20, overseerX - 15, overseerY + 50);
        g2d.drawLine(overseerX, overseerY + 20, overseerX + 15, overseerY + 50);

        // Draw sun rays
        g2d.setColor(new Color(255, 255, 200, 100));
        for (int i = 0; i < 5; i++) {
            int alpha = 150 - i * 30;
            g2d.setColor(new Color(255, 255, 200, alpha));
            g2d.fillRect(0, i * 10, WIDTH, 5);
        }

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(255, 50, 50));
        String title = "PHYSICAL LABOR";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

        // Draw caption
        g2d.setFont(new Font("Arial", Font.ITALIC, 16));
        g2d.setColor(new Color(255, 255, 255));
        String[] captions = {
                "Enjoy your new rock collection hobby!",
                "Moving rocks from A to B... and back again!",
                "You'll develop muscles you never knew existed!",
                "It's just like a gym, but free!",
                "Think of it as natural CrossFit!"
        };
        String caption = captions[(frame / 20) % captions.length];
        g2d.drawString(caption, (WIDTH - g2d.getFontMetrics().stringWidth(caption)) / 2, HEIGHT - 20);

        g2d.dispose();
        return image;
    }

    @Override
    public String getDescription() {
        return "Physical Labor";
    }
}
