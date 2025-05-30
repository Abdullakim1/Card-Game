package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Social Rejection punishment animation
 */
public class SocialRejection implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - party setting
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(30, 30, 60),
                0, HEIGHT, new Color(10, 10, 30)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw disco ball
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(WIDTH / 2 - 15, 20, 30, 30);
        
        // Draw disco ball reflections
        g2d.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 20; i++) {
            double angle = (Math.PI * 2 * i / 20) + (frame * 0.05);
            int x = WIDTH / 2 + (int) (Math.cos(angle) * 100);
            int y = HEIGHT / 2 + (int) (Math.sin(angle) * 70);
            g2d.fillOval(x - 5, y - 5, 10, 10);
        }
        
        // Draw party decorations
        g2d.setColor(new Color(255, 50, 50));
        g2d.fillRect(0, 50, WIDTH, 3);
        g2d.setColor(new Color(50, 50, 255));
        g2d.fillRect(0, 60, WIDTH, 3);
        g2d.setColor(new Color(50, 255, 50));
        g2d.fillRect(0, 70, WIDTH, 3);
        
        // Draw groups of people (moving slightly to music)
        // Group 1 - left side
        drawGroup(g2d, 70, HEIGHT - 60, 4, frame);
        
        // Group 2 - right side
        drawGroup(g2d, WIDTH - 70, HEIGHT - 60, 5, frame + 10);
        
        // Group 3 - center back
        drawGroup(g2d, WIDTH / 2, HEIGHT - 100, 6, frame + 5);
        
        // Draw lonely person (the victim) - alone in the corner
        int personX = 30;
        int personY = HEIGHT - 40;
        
        // Draw head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(personX - 10, personY - 30, 20, 20);
        
        // Draw sad face
        g2d.setColor(Color.BLACK);
        // Eyes
        g2d.fillOval(personX - 6, personY - 25, 3, 3);
        g2d.fillOval(personX + 3, personY - 25, 3, 3);
        
        // Frown
        g2d.drawArc(personX - 5, personY - 20, 10, 5, 0, 180);
        
        // Draw body (hunched over)
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(personX, personY - 10, personX, personY + 20);
        
        // Draw arms (crossed)
        g2d.drawLine(personX, personY, personX - 10, personY - 5);
        g2d.drawLine(personX, personY, personX + 10, personY - 5);
        
        // Draw legs
        g2d.drawLine(personX, personY + 20, personX - 10, personY + 40);
        g2d.drawLine(personX, personY + 20, personX + 10, personY + 40);
        
        // Draw thought bubble
        String[] thoughts = {
            "Nobody likes me...",
            "They're all avoiding me...",
            "I wasn't invited...",
            "They're laughing at me...",
            "I'm so alone..."
        };
        
        // Select thought based on frame
        String thought = thoughts[(frame / 30) % thoughts.length];
        
        // Draw thought bubble
        g2d.setColor(new Color(255, 255, 255));
        int bubbleWidth = g2d.getFontMetrics().stringWidth(thought) + 20;
        int bubbleHeight = 25;
        int bubbleX = personX - bubbleWidth / 2 + 20;
        int bubbleY = personY - 60;
        
        g2d.fillRoundRect(bubbleX, bubbleY, bubbleWidth, bubbleHeight, 10, 10);
        
        // Draw thought bubble connector (circles)
        for (int i = 0; i < 3; i++) {
            int size = 6 - i * 2;
            g2d.fillOval(personX + i * 5, bubbleY + bubbleHeight + i * 3, size, size);
        }
        
        // Draw text in thought bubble
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.drawString(thought, bubbleX + 10, bubbleY + 17);
        
        // Draw speech bubbles from groups
        String[] groupComments = {
            "Who invited them?",
            "Don't make eye contact!",
            "Let's move away...",
            "So awkward!",
            "Pretend we don't see them"
        };
        
        // Draw a speech bubble from one of the groups
        if (frame % 60 < 30) {
            String comment = groupComments[(frame / 60) % groupComments.length];
            
            // Determine which group is speaking
            int groupNum = (frame / 60) % 3;
            int groupX, groupY;
            
            switch (groupNum) {
                case 0:
                    groupX = 70;
                    groupY = HEIGHT - 80;
                    break;
                case 1:
                    groupX = WIDTH - 70;
                    groupY = HEIGHT - 80;
                    break;
                default:
                    groupX = WIDTH / 2;
                    groupY = HEIGHT - 120;
                    break;
            }
            
            // Draw speech bubble
            g2d.setColor(new Color(255, 255, 255));
            int commentWidth = g2d.getFontMetrics().stringWidth(comment) + 20;
            int commentHeight = 25;
            int commentX = groupX - commentWidth / 2;
            int commentY = groupY - 40;
            
            g2d.fillRoundRect(commentX, commentY, commentWidth, commentHeight, 10, 10);
            
            // Draw bubble pointer
            int[] xPoints = {groupX, groupX + 5, groupX - 5};
            int[] yPoints = {groupY - 20, commentY + commentHeight, commentY + commentHeight};
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            // Draw text in bubble
            g2d.setColor(new Color(0, 0, 0));
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(comment, commentX + 10, commentY + 17);
        }
        
        // Draw phone with social media notifications (all negative)
        g2d.setColor(new Color(30, 30, 30));
        g2d.fillRoundRect(WIDTH - 60, 20, 40, 70, 5, 5);
        
        g2d.setColor(new Color(100, 100, 255));
        g2d.fillRect(WIDTH - 55, 30, 30, 50);
        
        // Draw social media notifications
        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(new Font("Arial", Font.PLAIN, 7));
        
        String[] notifications = {
            "0 new messages",
            "Friend request denied",
            "You were untagged",
            "Group removed you",
            "Event uninvited you"
        };
        
        for (int i = 0; i < 3; i++) {
            int index = (i + frame / 20) % notifications.length;
            g2d.drawString(notifications[index], WIDTH - 54, 40 + i * 15);
        }
        
        // Draw "invisible wall" between person and groups
        if (frame % 40 < 20) {
            g2d.setColor(new Color(255, 0, 0, 30));
            g2d.fillRect(50, 0, 3, HEIGHT);
        }
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(100, 100, 255));
        String title = "SOCIAL REJECTION";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

        g2d.dispose();
        return image;
    }
    
    /**
     * Helper method to draw a group of people
     */
    private void drawGroup(Graphics2D g2d, int centerX, int centerY, int count, int frame) {
        // Draw each person in the group
        for (int i = 0; i < count; i++) {
            // Calculate position in a rough circle
            double angle = (Math.PI * 2 * i) / count;
            int x = centerX + (int) (Math.cos(angle) * 20);
            int y = centerY + (int) (Math.sin(angle) * 15);
            
            // Add some movement based on frame
            int sway = (int) (Math.sin((frame + i * 10) * 0.1) * 3);
            
            // Draw head
            g2d.setColor(new Color(255, 200, 150));
            g2d.fillOval(x - 8 + sway, y - 25, 16, 16);
            
            // Draw happy face
            g2d.setColor(Color.BLACK);
            // Eyes
            g2d.fillOval(x - 4 + sway, y - 22, 2, 2);
            g2d.fillOval(x + 2 + sway, y - 22, 2, 2);
            
            // Smile
            g2d.drawArc(x - 4 + sway, y - 18, 8, 4, 0, -180);
            
            // Draw body
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x + sway, y - 9, x + sway, y + 15);
            
            // Draw arms (moving to music)
            int armSway = (int) (Math.sin((frame + i * 20) * 0.2) * 5);
            g2d.drawLine(x + sway, y, x - 10 + armSway + sway, y + 5);
            g2d.drawLine(x + sway, y, x + 10 - armSway + sway, y + 5);
            
            // Draw legs (dancing)
            int legSway = (int) (Math.sin((frame + i * 15) * 0.15) * 5);
            g2d.drawLine(x + sway, y + 15, x - 5 + legSway + sway, y + 30);
            g2d.drawLine(x + sway, y + 15, x + 5 - legSway + sway, y + 30);
        }
    }

    @Override
    public String getDescription() {
        return "Social Rejection";
    }
}
