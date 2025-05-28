package com.cardgame.model.game;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages the animated punishments shown when a player loses the game
 */
public class GameOutcome {
    private static final Random random = new Random();
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    // Animation properties
    private static int animationFrame = 0;
    private static final int MAX_FRAMES = 60;
    private static final List<PunishmentAnimation> animations = new ArrayList<>();
    private static boolean inRouletteMode = false;
    private static int rouletteFrame = 0;
    private static final int ROULETTE_DURATION = 120; // frames for the roulette animation
    private static int selectedAnimationIndex = -1;
    private static boolean animationFinalized = false;

    // Interface for punishment animations
    private interface PunishmentAnimation {
        BufferedImage renderFrame(int frame);

        String getDescription();
    }

    /**
     * Loads all punishment animations
     */
    public static void loadOutcomeImages() {
        if (!animations.isEmpty()) {
            return; // Already loaded
        }

        // Add all punishment animations
        animations.add(new PhysicalLabor());
        animations.add(new PublicHumiliation());
        animations.add(new FinancialRuin());
        animations.add(new SocialRejection());
        animations.add(new NightmareSequence());
        animations.add(new JobLoss());
        animations.add(new AcademicFailure());
        animations.add(new RelationshipBreakup());
    }

    /**
     * Resets the animation state to start a new animation sequence
     */
    public static void resetAnimation() {
        animationFrame = 0;
        inRouletteMode = true;
        rouletteFrame = 0;
        selectedAnimationIndex = -1;
        animationFinalized = false;
    }

    /**
     * Gets a random outcome image to display
     *
     * @return A random outcome image for the current animation frame
     */
    public static BufferedImage getRandomOutcomeImage() {
        if (animations.isEmpty()) {
            loadOutcomeImages();
        }

        // If we already have a finalized animation, just play that one
        if (animationFinalized && selectedAnimationIndex >= 0) {
            animationFrame = (animationFrame + 1) % MAX_FRAMES;
            return animations.get(selectedAnimationIndex).renderFrame(animationFrame);
        }

        // Start roulette mode if we're at the beginning
        if (animationFrame == 0 && !inRouletteMode) {
            inRouletteMode = true;
            rouletteFrame = 0;
            selectedAnimationIndex = -1;
        }

        // Handle roulette animation mode
        if (inRouletteMode) {
            rouletteFrame++;

            // Select the final animation when roulette ends
            if (rouletteFrame >= ROULETTE_DURATION) {
                inRouletteMode = false;
                selectedAnimationIndex = random.nextInt(animations.size());
                animationFrame = 0; // Reset to start the selected animation from beginning
                animationFinalized = true; // Mark that we've selected our final animation
                System.out.println("Selected punishment: " + animations.get(selectedAnimationIndex).getDescription());
                return animations.get(selectedAnimationIndex).renderFrame(animationFrame);
            }

            // During roulette, cycle through animations quickly
            // Speed decreases as we approach the end for a "slowing down" effect
            int cycleSpeed;
            if (rouletteFrame < ROULETTE_DURATION / 3) {
                cycleSpeed = 2; // Fast at first
            } else if (rouletteFrame < ROULETTE_DURATION * 2 / 3) {
                cycleSpeed = 4; // Medium speed
            } else {
                cycleSpeed = 8; // Slow at the end
            }

            int currentIndex = (rouletteFrame / cycleSpeed) % animations.size();
            return renderRouletteFrame(currentIndex, rouletteFrame);
        }

        // This should not happen, but just in case
        if (selectedAnimationIndex < 0) {
            selectedAnimationIndex = random.nextInt(animations.size());
            animationFinalized = true;
        }

        // Normal animation playback after selection
        animationFrame = (animationFrame + 1) % MAX_FRAMES;
        return animations.get(selectedAnimationIndex).renderFrame(animationFrame);
    }

    /**
     * Renders a frame of the roulette animation that cycles through all punishments
     *
     * @param currentIndex The current animation index to show
     * @param frame        The current frame number of the roulette animation
     * @return A composite image showing the roulette effect
     */
    private static BufferedImage renderRouletteFrame(int currentIndex, int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set background - dark gradient
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(20, 0, 30),
                WIDTH, HEIGHT, new Color(60, 0, 60)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw slot machine frame
        GradientPaint frameGradient = new GradientPaint(
                0, 0, new Color(180, 140, 20),
                WIDTH, HEIGHT, new Color(220, 180, 40)
        );
        g2d.setPaint(frameGradient);
        g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawRoundRect(10, 10, WIDTH - 20, HEIGHT - 20, 20, 20);

        // Draw inner frame
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(30, 50, WIDTH - 60, HEIGHT - 100, 10, 10);

        // Draw multiple animations to create a slot machine effect
        int visibleAnimations = 3; // Show 3 animations at once
        int animHeight = (HEIGHT - 100) / visibleAnimations;

        // Calculate which animations to show based on the current frame
        // The speed decreases as the roulette slows down
        double speedFactor;
        if (frame < ROULETTE_DURATION / 3) {
            speedFactor = 1.0; // Fast at first
        } else if (frame < ROULETTE_DURATION * 2 / 3) {
            speedFactor = 0.5; // Medium speed
        } else {
            speedFactor = 0.2; // Slow at the end
        }

        // Calculate the offset for the animation scroll effect
        double scrollOffset = (frame * speedFactor * 10) % animations.size();

        // Draw the visible animations in the slot machine window
        for (int i = 0; i < visibleAnimations + 1; i++) {
            int animIndex = (currentIndex + i) % animations.size();
            BufferedImage animFrame = animations.get(animIndex).renderFrame(frame % MAX_FRAMES);

            // Scale the animation to fit in the slot
            int targetWidth = WIDTH - 80;
            int targetHeight = animHeight - 10;

            // Calculate Y position with scrolling effect
            int y = 55 + (i * animHeight) - (int) (scrollOffset * animHeight / animations.size());

            // Only draw if it's visible in the frame
            if (y < HEIGHT - 50 && y + targetHeight > 50) {
                // Draw a scaled version of the animation
                g2d.drawImage(animFrame, 40, y, targetWidth, targetHeight, null);

                // Draw the name of the animation
                g2d.setColor(new Color(255, 255, 255));
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String description = animations.get(animIndex).getDescription();
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(description, (WIDTH - fm.stringWidth(description)) / 2, y + targetHeight - 5);
            }
        }

        // Draw selection indicator (highlighted middle slot)
        int middleY = 50 + (HEIGHT - 100) / 2;
        g2d.setColor(new Color(255, 0, 0, 100));
        g2d.fillRect(30, middleY - animHeight / 2, WIDTH - 60, animHeight);

        // Draw selection arrows pointing to the middle slot
        g2d.setColor(new Color(255, 0, 0));
        int arrowSize = 15;
        int arrowX1 = 20;
        int arrowX2 = WIDTH - 20;
        int arrowY = middleY;

        // Left arrow
        g2d.fillPolygon(
                new int[]{arrowX1, arrowX1 + arrowSize, arrowX1 + arrowSize},
                new int[]{arrowY, arrowY - arrowSize, arrowY + arrowSize},
                3
        );

        // Right arrow
        g2d.fillPolygon(
                new int[]{arrowX2, arrowX2 - arrowSize, arrowX2 - arrowSize},
                new int[]{arrowY, arrowY - arrowSize, arrowY + arrowSize},
                3
        );

        // Add decorative bolts to the frame
        g2d.setColor(new Color(100, 100, 100));
        int[] boltPositionsX = {20, WIDTH - 20, 20, WIDTH - 20};
        int[] boltPositionsY = {20, 20, HEIGHT - 20, HEIGHT - 20};
        for (int i = 0; i < 4; i++) {
            g2d.fillOval(boltPositionsX[i] - 5, boltPositionsY[i] - 5, 10, 10);
            g2d.setColor(new Color(150, 150, 150));
            g2d.fillOval(boltPositionsX[i] - 3, boltPositionsY[i] - 3, 6, 6);
            g2d.setColor(new Color(100, 100, 100));
        }

        // Draw flashing lights around the frame when nearing selection
        if (frame > ROULETTE_DURATION * 2 / 3) {
            int flashPhase = frame % 10;
            if (flashPhase < 5) {
                g2d.setColor(new Color(255, 0, 0, 150));
            } else {
                g2d.setColor(new Color(255, 255, 0, 150));
            }
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRoundRect(20, 20, WIDTH - 40, HEIGHT - 40, 15, 15);
        }

        // Add "spinning" text at the top
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(255, 50, 50));
        String spinText = "SELECTING PUNISHMENT";

        // Make the text pulse
        int pulseSize = (int) (Math.sin(frame * 0.2) * 4);
        g2d.setFont(new Font("Arial", Font.BOLD, 24 + pulseSize));

        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(spinText, (WIDTH - fm.stringWidth(spinText)) / 2, 35);

        // Add a progress indicator at the bottom
        int progressWidth = (int) ((float) frame / ROULETTE_DURATION * (WIDTH - 100));
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(50, HEIGHT - 40, WIDTH - 100, 10);
        g2d.setColor(new Color(200, 0, 0));
        g2d.fillRect(50, HEIGHT - 40, progressWidth, 10);

        g2d.dispose();
        return image;
    }

    /**
     * Physical Labor punishment animation
     */
    private static class PhysicalLabor implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // Set background - construction site
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(180, 180, 220), // Sky
                    0, HEIGHT, new Color(120, 100, 80) // Ground
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            
            // Draw title
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "PHYSICAL LABOR";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);
            
            // Draw sun with heat waves
            g2d.setColor(new Color(255, 200, 0));
            g2d.fillOval(20, 20, 30, 30);
            
            // Heat waves
            g2d.setStroke(new BasicStroke(1.5f));
            for (int i = 0; i < 8; i++) {
                double angle = Math.PI * 2 * i / 8;
                int waveLength = 10 + (frame % 10);
                g2d.drawLine(
                        35, 35,
                        35 + (int) (Math.cos(angle) * waveLength),
                        35 + (int) (Math.sin(angle) * waveLength)
                );
            }
            
            // Draw construction site elements
            // Ground
            g2d.setColor(new Color(150, 120, 90));
            g2d.fillRect(0, 150, WIDTH, 50);
            
            // Pile of bricks
            g2d.setColor(new Color(180, 80, 80));
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 3; j++) {
                    g2d.fillRect(200 + (j * 15) + (i % 2) * 7, 140 - (i * 10), 14, 8);
                }
            }
            
            // Wheelbarrow
            g2d.setColor(new Color(80, 80, 80));
            g2d.fillRect(160, 145, 40, 15); // Barrow
            g2d.fillOval(160, 155, 15, 15); // Wheel
            g2d.setColor(new Color(120, 100, 80));
            g2d.fillRect(190, 140, 5, 20); // Handle
            
            // Draw person (the loser) doing hard labor
            g2d.setColor(new Color(70, 70, 70));
            
            // Animate the person to show effort
            int personX = 100;
            int personY = 120;
            int armOffset = (int) (Math.sin(frame * 0.2) * 5);
            
            // Head
            g2d.fillOval(personX, personY, 20, 20);
            
            // Body
            g2d.fillRect(personX + 5, personY + 20, 10, 30);
            
            // Arms showing labor
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(personX + 10, personY + 25, personX - 5, personY + 35 + armOffset);
            g2d.drawLine(personX + 10, personY + 25, personX + 25, personY + 35 - armOffset);
            
            // Legs
            g2d.drawLine(personX + 10, personY + 50, personX, personY + 70);
            g2d.drawLine(personX + 10, personY + 50, personX + 20, personY + 70);
            
            // Draw sweat drops
            g2d.setColor(new Color(100, 200, 255));
            if (frame % 10 < 7) {
                g2d.fillOval(personX + 22, personY + 5, 3, 5);
                g2d.fillOval(personX - 3, personY + 10, 3, 5);
            }
            
            // Draw tools
            g2d.setColor(new Color(100, 100, 100));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(personX - 5, personY + 35 + armOffset, personX - 15, personY + 55 + armOffset); // Pickaxe handle
            g2d.setColor(new Color(150, 150, 150));
            g2d.fillRect(personX - 20, personY + 50 + armOffset, 10, 5); // Pickaxe head
            
            // Draw pile of dirt that grows
            g2d.setColor(new Color(120, 100, 80));
            int dirtHeight = 10 + (frame % 60) / 6;
            g2d.fillOval(50, 160 - dirtHeight, 40, dirtHeight * 2);
            
            // Draw changing messages at bottom
            String[] messages = {
                    "100 more hours of this",
                    "No breaks allowed",
                    "Your back will never recover",
                    "It's 105°F outside",
                    "You'll be digging until sunset"
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
            return "Physical Labor";
        }
    }
    
    /**
     * Public Humiliation punishment animation
     */
    private static class PublicHumiliation implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Set dark background for auditorium/stage setting
            g2d.setColor(new Color(20, 20, 30));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw stage
            g2d.setColor(new Color(120, 100, 80));
            g2d.fillRect(50, 180, 300, 100);

            // Draw spotlight effect
            RadialGradientPaint spotlight = new RadialGradientPaint(
                    new Point(WIDTH / 2, 150),
                    100,
                    new float[]{0.0f, 0.7f},
                    new Color[]{new Color(255, 255, 200, 180), new Color(255, 255, 200, 0)}
            );
            g2d.setPaint(spotlight);
            g2d.fillOval(WIDTH / 2 - 100, 50, 200, 200);

            // Draw audience silhouettes
            g2d.setColor(new Color(10, 10, 15));
            for (int i = 0; i < 20; i++) {
                int x = 20 + (i * 20);
                int y = 250 + (i % 3) * 15;
                g2d.fillOval(x, y, 15, 15); // head
                g2d.fillRect(x + 2, y + 15, 10, 20); // body
            }

            // Draw person on stage (the loser)
            g2d.setColor(new Color(70, 70, 70));
            int personX = WIDTH / 2 - 10;
            int personY = 150;
            g2d.fillOval(personX, personY, 20, 20); // head
            g2d.fillRect(personX, personY + 20, 20, 40); // body
            g2d.fillRect(personX - 15, personY + 30, 15, 5); // left arm
            g2d.fillRect(personX + 20, personY + 30, 15, 5); // right arm
            g2d.fillRect(personX, personY + 60, 8, 20); // left leg
            g2d.fillRect(personX + 12, personY + 60, 8, 20); // right leg

            // Draw laughter bubbles from audience
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String[] laughs = {"HA!", "LOL", "HAHA", "OMG"};
            for (int i = 0; i < 5; i++) {
                if ((frame + i * 10) % 60 < 30) { // Make them appear and disappear
                    int x = 30 + (i * 60);
                    int y = 230 - (i % 3) * 20;
                    g2d.drawString(laughs[i % laughs.length], x, y);
                }
            }

            // Draw pointing fingers from audience
            g2d.setColor(new Color(200, 180, 160));
            for (int i = 0; i < 3; i++) {
                int x1 = 50 + (i * 100);
                int y1 = 260 - (i % 2) * 30;
                int x2 = personX + 10;
                int y2 = personY + 30;

                // Calculate angle
                double angle = Math.atan2(y2 - y1, x2 - x1);

                // Draw pointing arm
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(x1, y1, x1 + (int) (Math.cos(angle) * 40), y1 + (int) (Math.sin(angle) * 40));
            }

            // Draw humiliating text that changes
            String[] insults = {
                    "TOTAL FAILURE",
                    "COMPLETE LOSER",
                    "UTTERLY PATHETIC",
                    "ABSOLUTELY WORTHLESS",
                    "ENTIRELY INCOMPETENT"
            };

            g2d.setColor(new Color(255, 50, 50));
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            String insult = insults[(frame / 12) % insults.length];
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(insult, (WIDTH - fm.stringWidth(insult)) / 2, 50);

            // Draw sweat drops on person
            if (frame % 15 < 10) {
                g2d.setColor(new Color(100, 200, 255));
                g2d.fillOval(personX + 22, personY + 10, 5, 8);
                g2d.fillOval(personX - 7, personY + 15, 5, 8);
            }

            g2d.dispose();
            return image;
        }

        @Override
        public String getDescription() {
            return "Public Humiliation";
        }
    }

    /**
     * Financial Ruin punishment animation
     */
    private static class FinancialRuin implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Set background - office/bank setting
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw title
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "FINANCIAL RUIN";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

            // Draw bank/office desk
            g2d.setColor(new Color(120, 80, 40));
            g2d.fillRect(50, 150, 200, 20);
            g2d.fillRect(60, 170, 10, 30);
            g2d.fillRect(230, 170, 10, 30);

            // Draw banker/official
            g2d.setColor(new Color(50, 50, 50));
            g2d.fillOval(200, 120, 30, 30); // head
            g2d.fillRect(205, 150, 20, 30); // body

            // Draw person (the loser)
            g2d.setColor(new Color(70, 70, 70));
            g2d.fillOval(100, 120, 30, 30); // head
            g2d.fillRect(105, 150, 20, 30); // body

            // Draw falling money
            g2d.setColor(new Color(100, 200, 100));
            for (int i = 0; i < 20; i++) {
                int x = (i * 20 + frame * 3) % WIDTH;
                int y = (i * 30 + frame * 5) % HEIGHT;

                // Make money bills fall
                g2d.fillRect(x, y, 15, 8);
                g2d.setColor(new Color(80, 180, 80));
                g2d.drawRect(x, y, 15, 8);
                g2d.setColor(new Color(100, 200, 100));
            }

            // Draw financial documents with red stamps
            g2d.setColor(Color.WHITE);
            g2d.fillRect(70, 100, 40, 30); // document 1
            g2d.fillRect(120, 90, 40, 30); // document 2

            // Draw red "FORECLOSED" and "BANKRUPT" stamps
            if (frame % 60 < 30) {
                g2d.setColor(new Color(200, 0, 0, 150));
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.rotate(Math.PI / 12, 90, 115);
                g2d.drawString("FORECLOSED", 70, 115);
                g2d.rotate(-Math.PI / 12, 90, 115);

                g2d.rotate(-Math.PI / 12, 140, 105);
                g2d.drawString("BANKRUPT", 120, 105);
                g2d.rotate(Math.PI / 12, 140, 105);
            }

            // Draw decreasing stock chart
            g2d.setColor(Color.WHITE);
            g2d.fillRect(180, 60, 100, 50);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(180, 60, 100, 50);

            // Draw falling stock line
            g2d.setColor(new Color(200, 0, 0));
            g2d.setStroke(new BasicStroke(2));
            int[] xPoints = {180, 200, 220, 240, 260, 280};
            int[] yPoints = {70, 65, 80, 75, 90, 105};
            g2d.drawPolyline(xPoints, yPoints, 6);

            // Draw dollar signs that fade away
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            for (int i = 0; i < 5; i++) {
                int x = 50 + (i * 50);
                int y = 50 + (i % 3) * 20;
                int alpha = 255 - ((frame + i * 10) % 60) * 4;
                if (alpha < 0) alpha = 0;

                g2d.setColor(new Color(0, 150, 0, alpha));
                g2d.drawString("$", x, y);
            }

            // Draw changing messages at bottom
            String[] messages = {
                    "All your assets have been seized",
                    "Your credit score is now 12",
                    "You owe $2,457,892 in debt",
                    "Your home has been repossessed",
                    "Your car has been repossessed"
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
            return "Financial Ruin";
        }
    }

    /**
     * Social Rejection punishment animation
     */
    private static class SocialRejection implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Set background - social media style
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(59, 89, 152), // Facebook blue
                    WIDTH, HEIGHT, new Color(40, 60, 110)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw title
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "SOCIAL REJECTION";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

            // Draw profile picture with X over it
            g2d.setColor(Color.WHITE);
            g2d.fillOval(WIDTH / 2 - 30, 50, 60, 60);
            g2d.setColor(Color.GRAY);
            g2d.fillOval(WIDTH / 2 - 25, 55, 50, 50);

            // Draw X over profile
            g2d.setColor(new Color(200, 0, 0));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawLine(WIDTH / 2 - 30, 50, WIDTH / 2 + 30, 110);
            g2d.drawLine(WIDTH / 2 + 30, 50, WIDTH / 2 - 30, 110);

            // Draw unfriend notifications
            g2d.setColor(new Color(240, 240, 240));
            for (int i = 0; i < 3; i++) {
                int y = 130 + i * 40;
                g2d.fillRoundRect(50, y, 200, 30, 10, 10);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));

                // Animate the unfriend messages
                if (frame > i * 10) {
                    String name = "Friend " + (i + 1);
                    g2d.drawString(name + " unfriended you", 60, y + 20);
                }

                g2d.setColor(new Color(240, 240, 240));
            }

            // Draw decreasing friend count
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            int friendCount = Math.max(0, 999 - frame * 7);
            g2d.drawString("Friends: " + friendCount, 50, 50);

            // Draw notification icons with increasing negative counts
            g2d.setColor(new Color(200, 0, 0));
            g2d.fillOval(WIDTH - 50, 20, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("" + (frame % 50 + 10), WIDTH - 45, 35);

            // Draw blocked messages
            g2d.setColor(new Color(200, 0, 0, 150));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            // Animate the blocked messages
            String[] blockMessages = {
                    "You have been blocked",
                    "Message not delivered",
                    "User has blocked you",
                    "Cannot send message",
                    "Connection rejected"
            };

            for (int i = 0; i < 2; i++) {
                int y = 250 + i * 30;
                if ((frame + i * 20) % 60 < 30) {
                    String message = blockMessages[(frame / 10 + i) % blockMessages.length];
                    g2d.drawString(message, 70, y);
                }
            }

            // Draw changing messages at bottom
            String[] messages = {
                    "Nobody wants to be your friend",
                    "You've been socially ostracized",
                    "Your social media presence is toxic",
                    "Everyone is ignoring your messages",
                    "You've been removed from all groups"
            };

            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.WHITE);
            String message = messages[(frame / 12) % messages.length];
            g2d.drawString(message, (WIDTH - g2d.getFontMetrics().stringWidth(message)) / 2, HEIGHT - 20);

            g2d.dispose();
            return image;
        }

        @Override
        public String getDescription() {
            return "Social Rejection";
        }
    }

    /**
     * Nightmare Sequence punishment animation
     */
    private static class NightmareSequence implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Dark background with occasional flashes
            if (frame % 20 == 0) {
                g2d.setColor(new Color(200, 0, 0, 100)); // Red flash
            } else if (frame % 15 == 0) {
                g2d.setColor(new Color(100, 0, 200, 100)); // Purple flash
            } else {
                g2d.setColor(new Color(10, 0, 20)); // Dark background
            }
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw title with shaky effect
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "NIGHTMARE";
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (WIDTH - fm.stringWidth(title)) / 2;
            int titleY = 30;

            // Shake the title
            int shakeX = random.nextInt(5) - 2;
            int shakeY = random.nextInt(5) - 2;
            g2d.drawString(title, titleX + shakeX, titleY + shakeY);

            // Draw creepy eyes that follow you
            for (int i = 0; i < 8; i++) {
                int x = 50 + (i * 30);
                int y = 70 + (i % 3) * 40;

                // Only show some eyes at a time for creepy effect
                if ((frame + i) % 10 < 7) {
                    // Eye whites
                    g2d.setColor(new Color(255, 255, 255, 150));
                    g2d.fillOval(x, y, 20, 15);

                    // Pupils that move
                    g2d.setColor(Color.BLACK);
                    int pupilX = x + 10 + (int) (Math.sin(frame * 0.1 + i) * 5);
                    int pupilY = y + 7 + (int) (Math.cos(frame * 0.15 + i) * 3);
                    g2d.fillOval(pupilX, pupilY, 6, 6);

                    // Bloodshot effect
                    g2d.setColor(new Color(200, 0, 0, 50));
                    g2d.drawLine(x, y + 7, x + 20, y + 7);
                    g2d.drawLine(x + 10, y, x + 10, y + 15);
                }
            }

            // Draw shadowy figures
            g2d.setColor(new Color(0, 0, 0, 150));
            for (int i = 0; i < 3; i++) {
                int x = 30 + (i * 80);
                int height = 80 + (int) (Math.sin(frame * 0.05 + i) * 20);

                // Draw shadow figure
                g2d.fillRect(x, HEIGHT - height, 40, height);

                // Draw head
                g2d.fillOval(x + 5, HEIGHT - height - 30, 30, 30);

                // Draw glowing eyes
                if (frame % 15 < 10) {
                    g2d.setColor(new Color(255, 0, 0));
                    g2d.fillOval(x + 12, HEIGHT - height - 20, 5, 5);
                    g2d.fillOval(x + 23, HEIGHT - height - 20, 5, 5);
                    g2d.setColor(new Color(0, 0, 0, 150));
                }
            }

            // Draw spooky messages that fade in and out
            String[] messages = {
                    "They're coming for you",
                    "You can't escape",
                    "Don't look behind you",
                    "It's watching you",
                    "You'll never wake up"
            };

            g2d.setFont(new Font("Arial", Font.BOLD, 16));

            for (int i = 0; i < 2; i++) {
                // Calculate alpha for fading
                int alpha = (int) (Math.sin((frame + i * 30) * 0.05) * 127 + 127);
                if (alpha < 0) alpha = 0;

                g2d.setColor(new Color(200, 0, 0, alpha));
                String message = messages[(frame / 20 + i) % messages.length];
                int msgX = 20 + (i * 50) % (WIDTH - 100);
                int msgY = 120 + (i * 40);
                g2d.drawString(message, msgX, msgY);
            }

            // Draw spiderwebs in corners
            g2d.setColor(new Color(200, 200, 200, 100));
            g2d.setStroke(new BasicStroke(1));

            // Top left web
            for (int i = 0; i < 8; i++) {
                g2d.drawLine(0, 0, i * 10, i * 8);
            }

            // Bottom right web
            for (int i = 0; i < 8; i++) {
                g2d.drawLine(WIDTH, HEIGHT, WIDTH - i * 10, HEIGHT - i * 8);
            }

            g2d.dispose();
            return image;
        }

        @Override
        public String getDescription() {
            return "Nightmare Sequence";
        }
    }

    /**
     * Job Loss punishment animation
     */
    private static class JobLoss implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Office background
            g2d.setColor(new Color(240, 240, 245));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw office wall and floor
            g2d.setColor(new Color(220, 220, 225));
            g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);
            g2d.setColor(new Color(180, 180, 190));
            g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

            // Draw title
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "YOU'RE FIRED!";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 30);

            // Draw office desk
            g2d.setColor(new Color(120, 80, 40));
            g2d.fillRect(50, 120, 200, 10);
            g2d.fillRect(60, 130, 10, 50);
            g2d.fillRect(230, 130, 10, 50);

            // Draw boss (angry)
            g2d.setColor(new Color(50, 50, 50));
            g2d.fillOval(200, 90, 30, 30); // head
            g2d.fillRect(205, 120, 20, 30); // body

            // Draw angry eyebrows on boss
            g2d.setColor(new Color(0, 0, 0));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(205, 100, 215, 105);
            g2d.drawLine(225, 100, 215, 105);

            // Draw angry mouth on boss
            g2d.drawLine(210, 110, 220, 110);

            // Draw fired employee (sad)
            g2d.setColor(new Color(70, 70, 70));
            g2d.fillOval(100, 90, 30, 30); // head
            g2d.fillRect(105, 120, 20, 30); // body

            // Draw sad face on employee
            g2d.setColor(new Color(0, 0, 0));
            g2d.drawOval(108, 100, 5, 5); // left eye
            g2d.drawOval(117, 100, 5, 5); // right eye

            // Draw frown that gets sadder with animation
            int frown = 5 + (int) (Math.sin(frame * 0.1) * 3);
            g2d.drawArc(105, 105, 20, frown * 2, 0, 180);

            // Draw box of belongings
            g2d.setColor(new Color(160, 120, 80));
            g2d.fillRect(80, 150, 30, 20);

            // Draw items sticking out of box
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRect(85, 140, 5, 10); // pencil
            g2d.setColor(new Color(0, 100, 0));
            g2d.fillOval(95, 145, 10, 10); // plant

            // Draw termination letter
            g2d.setColor(Color.WHITE);
            g2d.fillRect(150, 100, 40, 30);

            // Draw red "TERMINATED" stamp
            if (frame % 30 < 20) {
                g2d.setColor(new Color(200, 0, 0, 180));
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.rotate(Math.PI / 12, 170, 115);
                g2d.drawString("TERMINATED", 140, 115);
                g2d.rotate(-Math.PI / 12, 170, 115);
            }

            // Draw pointing finger from boss
            g2d.setColor(new Color(200, 180, 160));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(215, 120, 170, 130);

            // Draw door with "EXIT" sign
            g2d.setColor(new Color(100, 70, 40));
            g2d.fillRect(20, 80, 40, 100);
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("EXIT", 25, 70);

            // Draw security guard by the door
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillOval(25, 100, 20, 20); // head
            g2d.fillRect(30, 120, 10, 30); // body

            // Draw changing messages at bottom
            String[] messages = {
                    "Pack your things and leave",
                    "Your performance was terrible",
                    "We're downsizing... just you",
                    "Security will escort you out",
                    "Don't even ask for a reference"
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
            return "Job Loss";
        }
    }

    /**
     * Academic Failure punishment animation
     */
    private static class AcademicFailure implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Classroom background
            g2d.setColor(new Color(245, 245, 220)); // Beige wall
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw chalkboard
            g2d.setColor(new Color(40, 80, 40)); // Dark green
            g2d.fillRect(50, 30, 200, 100);
            g2d.setColor(new Color(60, 100, 60)); // Lighter green for border
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(50, 30, 200, 100);

            // Draw title
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "ACADEMIC FAILURE";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 20);

            // Draw "F" grade on chalkboard
            g2d.setColor(new Color(255, 255, 255));
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.drawString("F", 140, 100);

            // Draw circle around F
            g2d.drawOval(130, 50, 50, 60);

            // Draw teacher's desk
            g2d.setColor(new Color(120, 80, 40));
            g2d.fillRect(100, 140, 100, 10);
            g2d.fillRect(110, 150, 10, 30);
            g2d.fillRect(180, 150, 10, 30);

            // Draw teacher (disappointed)
            g2d.setColor(new Color(50, 50, 50));
            g2d.fillOval(180, 110, 30, 30); // head
            g2d.fillRect(185, 140, 20, 30); // body

            // Draw disappointed face on teacher
            g2d.setColor(new Color(0, 0, 0));
            g2d.drawOval(185, 120, 5, 5); // left eye
            g2d.drawOval(195, 120, 5, 5); // right eye
            g2d.drawLine(185, 130, 195, 130); // straight mouth

            // Draw student (failed)
            g2d.setColor(new Color(70, 70, 70));
            g2d.fillOval(100, 110, 30, 30); // head
            g2d.fillRect(105, 140, 20, 30); // body

            // Draw sad face on student
            g2d.setColor(new Color(0, 0, 0));
            g2d.drawOval(108, 120, 5, 5); // left eye
            g2d.drawOval(117, 120, 5, 5); // right eye

            // Draw tears that animate
            if (frame % 15 < 10) {
                g2d.setColor(new Color(100, 200, 255));
                g2d.fillOval(110, 125 + (frame % 15), 3, 5);
                g2d.fillOval(119, 125 + ((frame + 5) % 15), 3, 5);
            }

            // Draw frown that gets sadder with animation
            int frown = 5 + (int) (Math.sin(frame * 0.1) * 3);
            g2d.setColor(new Color(0, 0, 0));
            g2d.drawArc(105, 125, 20, frown * 2, 0, 180);

            // Draw failed test paper
            g2d.setColor(Color.WHITE);
            g2d.fillRect(70, 130, 40, 30);

            // Draw red "F" on test paper
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("F", 85, 150);

            // Draw classroom desks in background
            g2d.setColor(new Color(150, 120, 90));
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    g2d.fillRect(30 + i * 80, 170 + j * 20, 60, 10);
                }
            }

            // Draw changing messages at bottom
            String[] messages = {
                    "You've failed every class",
                    "Your GPA is now 0.0",
                    "No college will accept you",
                    "Your parents are so disappointed",
                    "You'll be repeating this year"
            };

            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(new Color(200, 0, 0));
            String message = messages[(frame / 12) % messages.length];
            g2d.drawString(message, (WIDTH - g2d.getFontMetrics().stringWidth(message)) / 2, HEIGHT - 10);

            g2d.dispose();
            return image;
        }

        @Override
        public String getDescription() {
            return "Academic Failure";
        }
    }

    /**
     * Relationship Breakup punishment animation
     */
    private static class RelationshipBreakup implements PunishmentAnimation {
        @Override
        public BufferedImage renderFrame(int frame) {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Romantic background gradient
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 0, 50), // Dark purple
                    WIDTH, HEIGHT, new Color(80, 0, 80) // Lighter purple
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw title
            g2d.setColor(new Color(200, 0, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "RELATIONSHIP OVER";
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
                    "You'll die alone with cats",
                    "They're already dating someone else",
                    "They blocked your number",
                    "They're telling everyone your secrets"
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
}