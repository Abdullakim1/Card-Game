package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


public class AcademicFailure implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - classroom setting
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(220, 220, 200),
                0, HEIGHT, new Color(180, 180, 160)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw chalkboard
        g2d.setColor(new Color(50, 70, 50));
        g2d.fillRect(50, 20, WIDTH - 100, 80);
        
        // Draw wooden frame around chalkboard
        g2d.setColor(new Color(120, 80, 40));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRect(50, 20, WIDTH - 100, 80);
        
        // Draw "F" grade on chalkboard
        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        g2d.drawString("F", WIDTH / 2 - 20, 80);
        
        // Draw circled F
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(WIDTH / 2 - 30, 30, 60, 60);
        
        // Draw teacher at chalkboard
        int teacherX = WIDTH / 2 - 70;
        int teacherY = HEIGHT / 2;
        
        // Teacher head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(teacherX - 10, teacherY - 30, 20, 20);
        
        // Teacher glasses
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(teacherX - 8, teacherY - 25, 6, 6);
        g2d.drawOval(teacherX + 2, teacherY - 25, 6, 6);
        g2d.drawLine(teacherX - 2, teacherY - 22, teacherX + 2, teacherY - 22);
        g2d.drawLine(teacherX - 10, teacherY - 22, teacherX - 8, teacherY - 22);
        g2d.drawLine(teacherX + 8, teacherY - 22, teacherX + 10, teacherY - 22);
        
        // Teacher body
        g2d.setColor(new Color(100, 100, 150));
        g2d.fillRect(teacherX - 15, teacherY - 10, 30, 40);
        
        // Teacher arms
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(teacherX, teacherY, teacherX + 20, teacherY - 10);
        g2d.drawLine(teacherX, teacherY, teacherX - 10, teacherY + 10);
        
        // Teacher legs
        g2d.drawLine(teacherX - 10, teacherY + 30, teacherX - 10, teacherY + 50);
        g2d.drawLine(teacherX + 10, teacherY + 30, teacherX + 10, teacherY + 50);
        
        // Draw pointer stick
        g2d.setColor(new Color(200, 180, 120));
        g2d.drawLine(teacherX + 20, teacherY - 10, WIDTH / 2 - 30, 60);
        
        // Draw student desk
        g2d.setColor(new Color(150, 120, 80));
        g2d.fillRect(WIDTH / 2 + 20, HEIGHT / 2, 80, 10);
        g2d.fillRect(WIDTH / 2 + 30, HEIGHT / 2 + 10, 10, 40);
        g2d.fillRect(WIDTH / 2 + 80, HEIGHT / 2 + 10, 10, 40);
        
        // Draw student (looking dejected)
        int studentX = WIDTH / 2 + 60;
        int studentY = HEIGHT / 2 - 10;
        
        // Student head (hanging down in shame)
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(studentX - 10, studentY - 25, 20, 20);
        
        // Student face
        g2d.setColor(Color.BLACK);
        // Eyes (downcast)
        g2d.fillOval(studentX - 6, studentY - 20, 3, 3);
        g2d.fillOval(studentX + 3, studentY - 20, 3, 3);
        
        // Sad mouth
        g2d.drawArc(studentX - 5, studentY - 15, 10, 5, 0, 180);
        
        // Student body
        g2d.setColor(new Color(50, 150, 50));
        g2d.fillRect(studentX - 15, studentY, 30, 30);
        
        // Student arms (slumped on desk)
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(studentX - 20, studentY + 5, 10, 5);
        g2d.fillOval(studentX + 10, studentY + 5, 10, 5);
        
        // Student legs
        g2d.setColor(new Color(0, 0, 150));
        g2d.fillRect(studentX - 10, studentY + 30, 8, 20);
        g2d.fillRect(studentX + 2, studentY + 30, 8, 20);
        
        // Draw test paper on desk with F grade
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(studentX - 15, studentY + 5, 30, 20);
        
        g2d.setColor(new Color(255, 0, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("F", studentX - 5, studentY + 20);
        
        // Draw red marks all over the paper
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < 5; i++) {
            int x1 = studentX - 15 + (int)(Math.random() * 30);
            int y1 = studentY + 5 + (int)(Math.random() * 20);
            int x2 = x1 + 5;
            int y2 = y1 + 5;
            g2d.drawLine(x1, y1, x2, y2);
            g2d.drawLine(x1, y2, x2, y1);
        }
        
        // Draw other students (pointing and laughing)
        drawLaughingStudent(g2d, WIDTH / 2 + 120, HEIGHT / 2 - 10, frame);
        drawLaughingStudent(g2d, WIDTH / 2 + 30, HEIGHT / 2 + 60, frame);
        drawLaughingStudent(g2d, WIDTH / 2 + 90, HEIGHT / 2 + 60, frame);
        
        // Draw speech bubbles from other students
        String[] taunts = {
            "What a dummy!",
            "Failed again!",
            "Never gonna graduate!",
            "Total loser!",
            "Stupidest in class!"
        };
        
        // Draw a speech bubble from one of the students
        if (frame % 60 < 30) {
            String taunt = taunts[(frame / 60) % taunts.length];
            
            // Determine which student is speaking
            int studentNum = (frame / 60) % 3;
            int tauntX, tauntY;
            
            switch (studentNum) {
                case 0:
                    tauntX = WIDTH / 2 + 120;
                    tauntY = HEIGHT / 2 - 30;
                    break;
                case 1:
                    tauntX = WIDTH / 2 + 30;
                    tauntY = HEIGHT / 2 + 40;
                    break;
                default:
                    tauntX = WIDTH / 2 + 90;
                    tauntY = HEIGHT / 2 + 40;
                    break;
            }
            
            // Draw speech bubble
            g2d.setColor(new Color(255, 255, 255));
            int bubbleWidth = g2d.getFontMetrics().stringWidth(taunt) + 20;
            int bubbleHeight = 25;
            int bubbleX = tauntX - bubbleWidth / 2;
            int bubbleY = tauntY - 40;
            
            g2d.fillRoundRect(bubbleX, bubbleY, bubbleWidth, bubbleHeight, 10, 10);
            
            // Draw bubble pointer
            int[] xPoints = {tauntX, tauntX + 5, tauntX - 5};
            int[] yPoints = {tauntY - 15, bubbleY + bubbleHeight, bubbleY + bubbleHeight};
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            // Draw text in bubble
            g2d.setColor(new Color(0, 0, 0));
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(taunt, bubbleX + 10, bubbleY + 17);
        }
        
        // Draw thought bubble from failing student
        String[] thoughts = {
            "I studied so hard...",
            "My parents will kill me...",
            "No college for me...",
            "I'm so stupid...",
            "Life is over..."
        };
        
        // Select thought based on frame
        String thought = thoughts[(frame / 30) % thoughts.length];
        
        // Draw thought bubble
        g2d.setColor(new Color(255, 255, 255));
        int thoughtWidth = g2d.getFontMetrics().stringWidth(thought) + 20;
        int thoughtHeight = 25;
        int thoughtX = studentX - thoughtWidth / 2;
        int thoughtY = studentY - 50;
        
        g2d.fillRoundRect(thoughtX, thoughtY, thoughtWidth, thoughtHeight, 10, 10);
        
        // Draw thought bubble connector (circles)
        for (int i = 0; i < 3; i++) {
            int size = 6 - i * 2;
            g2d.fillOval(studentX - size/2, thoughtY + thoughtHeight + i * 5, size, size);
        }
        
        // Draw text in thought bubble
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.drawString(thought, thoughtX + 10, thoughtY + 17);
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(200, 0, 0));
        String title = "ACADEMIC FAILURE";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 180);

        g2d.dispose();
        return image;
    }
    
    
    private void drawLaughingStudent(Graphics2D g2d, int x, int y, int frame) {
        // Animate laughing
        int laughShake = (int) (Math.sin(frame * 0.3) * 2);
        
        // Student head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(x - 8 + laughShake, y - 20, 16, 16);
        
        // Student face
        g2d.setColor(Color.BLACK);
        // Eyes (squinting with laughter)
        g2d.drawLine(x - 5 + laughShake, y - 15, x - 2 + laughShake, y - 16);
        g2d.drawLine(x + 2 + laughShake, y - 16, x + 5 + laughShake, y - 15);
        
        // Laughing mouth
        g2d.drawArc(x - 5 + laughShake, y - 12, 10, 5, 0, -180);
        
        // Student body
        g2d.setColor(new Color((int)(Math.random() * 200), (int)(Math.random() * 200), (int)(Math.random() * 200)));
        g2d.fillRect(x - 10 + laughShake, y, 20, 25);
        
        // Student arms (pointing)
        g2d.setStroke(new BasicStroke(2));
        
        // Animate pointing
        int pointAngle = (int) (Math.sin(frame * 0.2) * 10);
        
        g2d.drawLine(x + laughShake, y + 5, 
                x - 15 + laughShake, 
                y + 5 + pointAngle);
        
        g2d.drawLine(x + laughShake, y + 5, 
                x + 15 + laughShake, 
                y + 5 - pointAngle);
    }

    @Override
    public String getDescription() {
        return "Academic Failure";
    }
}
