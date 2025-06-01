package com.cardgame.model.game.punishments;

import com.cardgame.model.game.PunishmentAnimation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Random;


public class FinancialRuin implements PunishmentAnimation {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;
    private static final Random random = new Random();

    @Override
    public BufferedImage renderFrame(int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - office/bank setting
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(240, 240, 240),
                0, HEIGHT, new Color(200, 200, 200)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw financial chart (going down)
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRect(WIDTH / 2 - 100, 50, 200, 100);
        
        g2d.setColor(new Color(0, 0, 0));
        g2d.drawRect(WIDTH / 2 - 100, 50, 200, 100);
        
        // Draw chart axes
        g2d.drawLine(WIDTH / 2 - 90, 140, WIDTH / 2 - 90, 60);
        g2d.drawLine(WIDTH / 2 - 90, 140, WIDTH / 2 + 90, 140);
        
        // Draw chart labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("TIME", WIDTH / 2, 155);
        
        // Rotate text for Y axis
        AffineTransform original = g2d.getTransform();
        g2d.rotate(-Math.PI / 2, WIDTH / 2 - 105, 100);
        g2d.drawString("MONEY", WIDTH / 2 - 105, 100);
        g2d.setTransform(original);
        
        // Draw declining graph line
        g2d.setColor(new Color(255, 0, 0));
        g2d.setStroke(new BasicStroke(2));
        
        int startX = WIDTH / 2 - 90;
        int endX = WIDTH / 2 + 90;
        int startY = 60;
        int endY = 140;
        
        // Create a path that starts high and crashes down
        Path2D path = new Path2D.Double();
        path.moveTo(startX, startY);
        
        // Add some random fluctuations but with a clear downward trend
        for (int x = startX + 10; x <= endX; x += 10) {
            double progress = (double)(x - startX) / (endX - startX);
            double crashPoint = 0.7; // When the big crash happens
            
            double y;
            if (progress < crashPoint) {
                // Small fluctuations before the crash
                y = startY + (endY - startY) * 0.3 * progress + (Math.sin(progress * 10) * 5);
            } else {
                // The crash
                y = startY + (endY - startY) * 0.3 * crashPoint + 
                    (endY - startY) * 0.7 * ((progress - crashPoint) / (1 - crashPoint)) + 
                    (Math.sin(progress * 5) * 3);
            }
            
            path.lineTo(x, y);
        }
        
        g2d.draw(path);
        
        g2d.setColor(new Color(200, 0, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("CRASH!", WIDTH / 2 - 20, 110);
        
        // Draw falling money
        g2d.setColor(new Color(50, 150, 50));
        for (int i = 0; i < 15; i++) {
            // Randomize position based on frame for animation
            int x = (i * 20 + frame * 2) % WIDTH;
            int y = (i * 30 + frame * 3) % HEIGHT;
            
            // Draw dollar bill
            g2d.fillRect(x, y, 15, 8);
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillOval(x + 3, y + 1, 6, 6);
            g2d.setColor(new Color(50, 150, 50));
        }
        
        // Draw person (depressed investor)
        int personX = 50;
        int personY = HEIGHT - 50;
        
        // Draw head (hanging down in despair)
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(personX - 10, personY - 30, 20, 20);
        
        // Draw sad face
        g2d.setColor(Color.BLACK);
        // Eyes
        g2d.fillOval(personX - 6, personY - 25, 3, 3);
        g2d.fillOval(personX + 3, personY - 25, 3, 3);
        
        // Frown
        g2d.drawArc(personX - 5, personY - 20, 10, 5, 0, 180);
        
        // Draw body (slumped over)
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(personX, personY - 10, personX, personY + 20);
        
        // Draw arms (hanging down)
        g2d.drawLine(personX, personY, personX - 15, personY + 15);
        g2d.drawLine(personX, personY, personX + 15, personY + 15);
        
        // Draw legs
        g2d.drawLine(personX, personY + 20, personX - 10, personY + 40);
        g2d.drawLine(personX, personY + 20, personX + 10, personY + 40);
        
        // Draw empty pockets (turned inside out)
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(personX - 18, personY + 15, 6, 8);
        g2d.fillRect(personX + 12, personY + 15, 6, 8);
        
        // Draw bill collector/banker
        int bankerX = WIDTH - 70;
        int bankerY = HEIGHT - 50;
        
        // Draw banker head
        g2d.setColor(new Color(255, 200, 150));
        g2d.fillOval(bankerX - 10, bankerY - 30, 20, 20);
        
        // Draw banker face (stern expression)
        g2d.setColor(Color.BLACK);
        // Eyes
        g2d.fillOval(bankerX - 6, bankerY - 25, 3, 3);
        g2d.fillOval(bankerX + 3, bankerY - 25, 3, 3);
        
        // Frown
        g2d.drawLine(bankerX - 5, bankerY - 18, bankerX + 5, bankerY - 18);
        
        // Draw banker body
        g2d.drawLine(bankerX, bankerY - 10, bankerX, bankerY + 20);
        
        // Draw banker arms
        g2d.drawLine(bankerX, bankerY, bankerX - 15, bankerY + 5);
        g2d.drawLine(bankerX, bankerY, bankerX + 15, bankerY + 5);
        
        // Draw banker legs
        g2d.drawLine(bankerX, bankerY + 20, bankerX - 10, bankerY + 40);
        g2d.drawLine(bankerX, bankerY + 20, bankerX + 10, bankerY + 40);
        
        // Draw banker's clipboard/bill
        g2d.setColor(new Color(255, 255, 220));
        g2d.fillRect(bankerX - 25, bankerY, 20, 25);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(bankerX - 25, bankerY, 20, 25);
        
        // Draw lines on the bill
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < 5; i++) {
            g2d.drawLine(bankerX - 22, bankerY + 5 + i * 4, bankerX - 8, bankerY + 5 + i * 4);
        }
        
        // Draw dollar sign on bill
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("$", bankerX - 20, bankerY + 15);
        
        // Draw speech bubble from banker
        String[] demands = {
            "Pay up now!",
            "You're bankrupt!",
            "We're repossessing!",
            "Foreclosure time!",
            "Your credit is ruined!"
        };
        
        // Select demand based on frame
        String demand = demands[(frame / 30) % demands.length];
        
        // Draw speech bubble
        g2d.setColor(new Color(255, 255, 255));
        int bubbleWidth = g2d.getFontMetrics().stringWidth(demand) + 20;
        int bubbleHeight = 25;
        int bubbleX = bankerX - bubbleWidth / 2;
        int bubbleY = bankerY - 60;
        
        g2d.fillRoundRect(bubbleX, bubbleY, bubbleWidth, bubbleHeight, 10, 10);
        
        // Draw bubble pointer
        int[] xPoints = {bankerX, bankerX + 5, bankerX - 5};
        int[] yPoints = {bankerY - 30, bubbleY + bubbleHeight, bubbleY + bubbleHeight};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Draw text in bubble
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString(demand, bubbleX + 10, bubbleY + 17);
        
        // Draw thought bubble from poor person
        String[] thoughts = {
            "I'm ruined...",
            "All my savings...",
            "Should've bought gold...",
            "Living in a box now...",
            "Can't afford ramen..."
        };
        
        // Select thought based on frame
        String thought = thoughts[(frame / 30 + 2) % thoughts.length];
        
        // Draw thought bubble
        g2d.setColor(new Color(255, 255, 255));
        int thoughtWidth = g2d.getFontMetrics().stringWidth(thought) + 20;
        int thoughtHeight = 25;
        int thoughtX = personX - thoughtWidth / 2;
        int thoughtY = personY - 60;
        
        g2d.fillRoundRect(thoughtX, thoughtY, thoughtWidth, thoughtHeight, 10, 10);
        
        // Draw thought bubble connector (circles)
        for (int i = 0; i < 3; i++) {
            int size = 6 - i * 2;
            g2d.fillOval(personX - size/2, thoughtY + thoughtHeight + i * 5, size, size);
        }
        
        // Draw text in thought bubble
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.drawString(thought, thoughtX + 10, thoughtY + 17);
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(150, 0, 0));
        String title = "FINANCIAL RUIN";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

        g2d.dispose();
        return image;
    }

    @Override
    public String getDescription() {
        return "Financial Ruin";
    }
}
