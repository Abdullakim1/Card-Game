package com.cardgame.model.game;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public abstract class AbstractGameOutcome {
    private static final Random random = new Random();
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    private static int animationFrame = 0;
    private static final int MAX_FRAMES = 60;
    private static final List<PunishmentAnimation> animations = new ArrayList<>();
    private static boolean inRouletteMode = false;
    private static int rouletteFrame = 0;
    private static final int ROULETTE_DURATION = 120; 
    private static int selectedAnimationIndex = -1;
    private static boolean animationFinalized = false;


    public static List<PunishmentAnimation> getAnimationsList() {
        return animations;
    }

    
    public abstract void loadAnimations();

    
    public void resetAnimation() {
        animationFrame = 0;
        inRouletteMode = true;
        rouletteFrame = 0;
        selectedAnimationIndex = -1;
        animationFinalized = false;
    }

    
    public BufferedImage getRandomOutcomeImage() {
        if (animations.isEmpty()) {
            loadAnimations();
        }

        if (animationFinalized && selectedAnimationIndex >= 0) {
            animationFrame = (animationFrame + 1) % MAX_FRAMES;
            return animations.get(selectedAnimationIndex).renderFrame(animationFrame);
        }

        if (animationFrame == 0 && !inRouletteMode) {
            inRouletteMode = true;
            rouletteFrame = 0;
            selectedAnimationIndex = -1;
        }

        if (inRouletteMode) {
            rouletteFrame++;

            if (rouletteFrame >= ROULETTE_DURATION) {
                inRouletteMode = false;
                selectedAnimationIndex = random.nextInt(animations.size());
                animationFrame = 0; 
                animationFinalized = true; 
                System.out.println("Selected punishment: " + animations.get(selectedAnimationIndex).getDescription());
                return animations.get(selectedAnimationIndex).renderFrame(animationFrame);
            }

            int cycleSpeed;
            if (rouletteFrame < ROULETTE_DURATION / 3) {
                cycleSpeed = 2; 
            } else if (rouletteFrame < ROULETTE_DURATION * 2 / 3) {
                cycleSpeed = 4; 
            } else {
                cycleSpeed = 8; 
            }

            int currentIndex = (rouletteFrame / cycleSpeed) % animations.size();
            return renderRouletteFrame(currentIndex, rouletteFrame);
        }

        if (selectedAnimationIndex < 0) {
            selectedAnimationIndex = random.nextInt(animations.size());
            animationFinalized = true;
        }
        animationFrame = (animationFrame + 1) % MAX_FRAMES;
        return animations.get(selectedAnimationIndex).renderFrame(animationFrame);
    }

    
    public BufferedImage renderRouletteFrame(int currentIndex, int frame) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(20, 0, 30),
                WIDTH, HEIGHT, new Color(60, 0, 60)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        GradientPaint frameGradient = new GradientPaint(
                0, 0, new Color(180, 140, 20),
                WIDTH, HEIGHT, new Color(220, 180, 40)
        );
        g2d.setPaint(frameGradient);
        g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawRoundRect(10, 10, WIDTH - 20, HEIGHT - 20, 20, 20);

        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(30, 50, WIDTH - 60, HEIGHT - 100, 10, 10);

        int visibleAnimations = 3; 
        int animHeight = (HEIGHT - 100) / visibleAnimations;

        double speedFactor;
        if (frame < ROULETTE_DURATION / 3) {
            speedFactor = 1.0; 
        } else if (frame < ROULETTE_DURATION * 2 / 3) {
            speedFactor = 0.5; 
        } else {
            speedFactor = 0.2; 
        }

        double scrollOffset = (frame * speedFactor * 10) % animations.size();

        for (int i = 0; i < visibleAnimations + 1; i++) {
            int animIndex = (currentIndex + i) % animations.size();
            BufferedImage animFrame = animations.get(animIndex).renderFrame(frame % MAX_FRAMES);

            int targetWidth = WIDTH - 80;
            int targetHeight = animHeight - 10;

            int y = 55 + (i * animHeight) - (int) (scrollOffset * animHeight / animations.size());

            if (y < HEIGHT - 50 && y + targetHeight > 50) {
                g2d.drawImage(animFrame, 40, y, targetWidth, targetHeight, null);

                g2d.setColor(new Color(255, 255, 255));
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String description = animations.get(animIndex).getDescription();
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(description, (WIDTH - fm.stringWidth(description)) / 2, y + targetHeight - 5);
            }
        }

        g2d.setColor(new Color(200, 50, 50));
        g2d.fillRoundRect(WIDTH - 25, 50, 15, 40, 5, 5);
        g2d.setColor(new Color(150, 150, 150));
        g2d.fillOval(WIDTH - 25, 85, 15, 15);

        int numLights = 20;
        for (int i = 0; i < numLights; i++) {
            double angle = (Math.PI * 2 * i) / numLights;
            int x = WIDTH / 2 + (int) (Math.cos(angle) * (WIDTH / 2 - 15));
            int y = HEIGHT / 2 + (int) (Math.sin(angle) * (HEIGHT / 2 - 15));

            boolean isLit = (i + frame) % 5 == 0;
            g2d.setColor(isLit ? new Color(255, 255, 0) : new Color(100, 100, 0));
            g2d.fillOval(x - 5, y - 5, 10, 10);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(new Color(255, 220, 50));
        String title = "PUNISHMENT ROULETTE";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 35);

        g2d.dispose();
        return image;
    }
}
