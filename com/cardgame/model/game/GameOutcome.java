package com.cardgame.model.game;

import com.cardgame.model.game.punishments.*;
import java.awt.image.BufferedImage;
import java.util.List;


public class GameOutcome extends AbstractGameOutcome {
    
    private static final GameOutcome instance = new GameOutcome();
    
    
    private GameOutcome() {
    }
    
    
    public static GameOutcome getInstance() {
        return instance;
    }
    
    
    @Override
    public void loadAnimations() {

        List<PunishmentAnimation> actualAnimationsList = AbstractGameOutcome.getAnimationsList();

        if (!actualAnimationsList.isEmpty()) {
            return; 
        }

        actualAnimationsList.add(new PhysicalLabor());
        actualAnimationsList.add(new PublicHumiliation());
        actualAnimationsList.add(new FinancialRuin());
        actualAnimationsList.add(new SocialRejection());
        actualAnimationsList.add(new NightmareSequence());
        actualAnimationsList.add(new JobLoss());
        actualAnimationsList.add(new AcademicFailure());
        actualAnimationsList.add(new RelationshipBreakup());
    }
    
    public static BufferedImage getRandomOutcomeImageStatic() {
        return instance.getRandomOutcomeImage();
    }
    
    
    public static void resetAnimationStatic() {
        instance.resetAnimation();
    }
    
    
    public static void loadOutcomeImages() {
        instance.loadAnimations();
    }
    
    
    @Override
    public BufferedImage getRandomOutcomeImage() {
        return super.getRandomOutcomeImage();
    }
    
    
    @Override
    public void resetAnimation() {
        super.resetAnimation();
    }
}