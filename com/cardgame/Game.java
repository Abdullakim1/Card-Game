package com.cardgame;

import com.cardgame.controller.states.GameState;
import com.cardgame.controller.states.MenuState;
import com.cardgame.view.animations.CardAnimation;
import com.cardgame.controller.states.SinglePlayerNameState;
import com.cardgame.controller.states.PlayerSelectionState;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game extends JFrame implements Runnable, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "Card Game";

    private boolean running;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics2D g2d;

    private GameState currentState;
    private CardAnimation cardAnimation;

    public Game() {
        setTitle(TITLE);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        cardAnimation = new CardAnimation();

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {  // Have no clue why all the methods are the same
                if (currentState != null) {
                    currentState.handleMouseEvent(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState != null) {
                    currentState.handleMouseEvent(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentState != null) {
                    currentState.handleMouseEvent(e);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentState != null) {
                    currentState.handleMouseEvent(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentState != null) {
                    currentState.handleMouseEvent(e);
                }
            }
        });
        
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        setState(new MenuState(this));
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        
        setVisible(true);
        
        createBufferStrategy(3);
        bs = getBufferStrategy();
        
        gameThread = new Thread(this);
        gameThread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) {
                render();
            }
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
        stop();
    }

    private void tick() {
        if (currentState != null) {
            currentState.tick();
        }
        cardAnimation.update();
    }

    private void render() {
        if (bs == null) {
            createBufferStrategy(3);
            bs = getBufferStrategy();
            if (bs == null) {
                return;
            }
        }

        g2d = (Graphics2D) bs.getDrawGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (currentState != null) {
            currentState.render(g2d);
        }

        cardAnimation.render(g2d);

        g2d.dispose();
        bs.show();
    }

    public void setState(GameState state) {
        if (currentState != null) {
            currentState.onExit();
        }
        currentState = state;
        if (currentState != null) {
            currentState.onEnter();
        }
    }


    public CardAnimation getCardAnimation() {
        return cardAnimation;
    }

    public void runLater(Runnable action) {
        SwingUtilities.invokeLater(action);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (currentState instanceof SinglePlayerNameState) {
            ((SinglePlayerNameState) currentState).processKeyEvent(e);
        } else if (currentState instanceof PlayerSelectionState) {
            ((PlayerSelectionState) currentState).processKeyEvent(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.start();
        });
    }
}