package com.cardgame.model.game;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class that manages the animated punishments shown when a player loses the game
 */
public abstract class AbstractGameOutcome {
    protected static final Random random = new Random();
    protected static final int WIDTH = 300;
    protected static final int HEIGHT = 200;

    // Animation properties
    protected static int animationFrame = 0;
    protected static final int MAX_FRAMES = 60;
    protected static final List<PunishmentAnimation> animations = new ArrayList<>();
    protected static boolean inRouletteMode = false;
    protected static int rouletteFrame = 0;
    protected static final int ROULETTE_DURATION = 120; // frames for the roulette animation
    protected static int selectedAnimationIndex = -1;
    protected static boolean animationFinalized = false;

    /**
     * Loads all punishment animations
     */
    public abstract void loadAnimations();

    /**
     * Resets the animation state to start a new animation sequence
     */
    public void resetAnimation() {
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
    public BufferedImage getRandomOutcomeImage() {
        if (animations.isEmpty()) {
            loadAnimations();
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
    protected BufferedImage renderRouletteFrame(int currentIndex, int frame) {
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

        // Draw slot machine lever
        g2d.setColor(new Color(200, 50, 50));
        g2d.fillRoundRect(WIDTH - 25, 50, 15, 40, 5, 5);
        g2d.setColor(new Color(150, 150, 150));
        g2d.fillOval(WIDTH - 25, 85, 15, 15);

        // Draw flashing lights around the frame
        int numLights = 20;
        for (int i = 0; i < numLights; i++) {
            double angle = (Math.PI * 2 * i) / numLights;
            int x = WIDTH / 2 + (int) (Math.cos(angle) * (WIDTH / 2 - 15));
            int y = HEIGHT / 2 + (int) (Math.sin(angle) * (HEIGHT / 2 - 15));

            // Make the lights flash
            boolean isLit = (i + frame) % 5 == 0;
            g2d.setColor(isLit ? new Color(255, 255, 0) : new Color(100, 100, 0));
            g2d.fillOval(x - 5, y - 5, 10, 10);
        }

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(new Color(255, 220, 50));
        String title = "PUNISHMENT ROULETTE";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 35);

        g2d.dispose();
        return image;
    }
}
