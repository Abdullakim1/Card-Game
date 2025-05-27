package com.cardgame;

import com.cardgame.controller.states.GameState;
import com.cardgame.controller.states.MenuState;
import com.cardgame.controller.states.PlayerSelectionState;
import com.cardgame.controller.states.SinglePlayerNameState;
import com.cardgame.view.animations.CardAnimation;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game extends JFrame implements Runnable {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "Card Game";
    
    private boolean isFullScreen = true;
    private Rectangle windowedBounds;
    private int screenWidth;
    private int screenHeight;
    
    // Scaling factors for fullscreen mode
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    
    private boolean running;
    private Thread gameThread;
    private BufferStrategy bs;
    private Graphics2D g2d;

    private GameState currentState;
    private CardAnimation cardAnimation;

    public Game() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Get screen dimensions
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();
        
        // Calculate scaling factors
        updateScalingFactors();
        
        // Store windowed mode bounds for toggling
        windowedBounds = new Rectangle((screenWidth - WIDTH) / 2, (screenHeight - HEIGHT) / 2, WIDTH, HEIGHT);
        
        // Set fullscreen by default
        if (isFullScreen) {
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setSize(WIDTH, HEIGHT);
            setResizable(false);
            setLocationRelativeTo(null);
        }
        
        cardAnimation = new CardAnimation();

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState != null) {
                    // Convert coordinates if in fullscreen mode
                    if (isFullScreen) {
                        MouseEvent scaledEvent = createScaledMouseEvent(e);
                        currentState.handleMouseEvent(scaledEvent);
                    } else {
                        currentState.handleMouseEvent(e);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState != null) {
                    // Convert coordinates if in fullscreen mode
                    if (isFullScreen) {
                        MouseEvent scaledEvent = createScaledMouseEvent(e);
                        currentState.handleMouseEvent(scaledEvent);
                    } else {
                        currentState.handleMouseEvent(e);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentState != null) {
                    // Convert coordinates if in fullscreen mode
                    if (isFullScreen) {
                        MouseEvent scaledEvent = createScaledMouseEvent(e);
                        currentState.handleMouseEvent(scaledEvent);
                    } else {
                        currentState.handleMouseEvent(e);
                    }
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
                    // Convert coordinates if in fullscreen mode
                    if (isFullScreen) {
                        MouseEvent scaledEvent = createScaledMouseEvent(e);
                        currentState.handleMouseEvent(scaledEvent);
                    } else {
                        currentState.handleMouseEvent(e);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentState != null) {
                    // Convert coordinates if in fullscreen mode
                    if (isFullScreen) {
                        MouseEvent scaledEvent = createScaledMouseEvent(e);
                        currentState.handleMouseEvent(scaledEvent);
                    } else {
                        currentState.handleMouseEvent(e);
                    }
                }
            }
        });
        
        // Add keyboard listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (currentState != null) {
                    if (currentState instanceof PlayerSelectionState) {
                        // Only handle regular character input in keyTyped
                        if (Character.isLetterOrDigit(e.getKeyChar()) || Character.isSpaceChar(e.getKeyChar())) {
                            ((PlayerSelectionState) currentState).handleKeyEvent(e.getKeyChar(), e.getKeyCode());
                        }
                    } else if (currentState instanceof SinglePlayerNameState) {
                        ((SinglePlayerNameState) currentState).processKeyEvent(e);
                    }
                }
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                // Handle special keys like backspace, enter, and escape
                if (currentState != null && currentState instanceof PlayerSelectionState) {
                    // Only handle special keys in keyPressed (backspace, enter, escape)
                    int keyCode = e.getKeyCode();
                    if (keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE) {
                        ((PlayerSelectionState) currentState).handleKeyEvent(e.getKeyChar(), keyCode);
                    }
                }
            }
        });
        
        // Make sure the frame can receive keyboard events
        setFocusable(true);
        requestFocus();
        
        // Add key listener for toggling fullscreen with F11
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    toggleFullScreen();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isFullScreen) {
                    toggleFullScreen();
                }
            }
        });

        // Start with menu state
        setState(new MenuState(this));
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        
        // Make the window visible first
        setVisible(true);
        
        // Create buffer strategy after window is visible
        createBufferStrategy(3);
        bs = getBufferStrategy();
        
        // Start game thread
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

        // Clear screen
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        if (isFullScreen) {
            // Apply scaling transformation in fullscreen mode
            g2d.translate((getWidth() - WIDTH * scaleX) / 2, (getHeight() - HEIGHT * scaleY) / 2);
            g2d.scale(scaleX, scaleY);
        }

        // Render current state
        if (currentState != null) {
            currentState.render(g2d);
        }

        // Render card animations
        cardAnimation.render(g2d);

        // Clean up
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
    
    /**
     * Toggles between fullscreen and windowed mode
     */
    public void toggleFullScreen() {
        isFullScreen = !isFullScreen;
        
        dispose(); // Dispose the current frame
        
        if (isFullScreen) {
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setUndecorated(false);
            setBounds(windowedBounds);
            setResizable(false);
        }
        
        // Update scaling factors
        updateScalingFactors();
        
        setVisible(true);
        createBufferStrategy(3);
        bs = getBufferStrategy();
    }
    
    /**
     * Returns the current screen width
     */
    public int getScreenWidth() {
        return isFullScreen ? screenWidth : WIDTH;
    }
    
    /**
     * Returns the current screen height
     */
    public int getScreenHeight() {
        return isFullScreen ? screenHeight : HEIGHT;
    }
    
    /**
     * Checks if the game is in fullscreen mode
     */
    public boolean isFullScreen() {
        return isFullScreen;
    }
    
    /**
     * Updates scaling factors based on current screen size
     */
    private void updateScalingFactors() {
        if (isFullScreen) {
            scaleX = (float) screenWidth / WIDTH;
            scaleY = (float) screenHeight / HEIGHT;
            
            // Use the smaller scaling factor to maintain aspect ratio
            float scale = Math.min(scaleX, scaleY);
            scaleX = scale;
            scaleY = scale;
        } else {
            scaleX = 1.0f;
            scaleY = 1.0f;
        }
    }
    
    /**
     * Converts screen X coordinate to game coordinate
     */
    public int screenToGameX(int screenX) {
        if (isFullScreen) {
            int offsetX = (int)((getWidth() - WIDTH * scaleX) / 2);
            return (int)((screenX - offsetX) / scaleX);
        }
        return screenX;
    }
    
    /**
     * Converts screen Y coordinate to game coordinate
     */
    public int screenToGameY(int screenY) {
        if (isFullScreen) {
            int offsetY = (int)((getHeight() - HEIGHT * scaleY) / 2);
            return (int)((screenY - offsetY) / scaleY);
        }
        return screenY;
    }
    
    /**
     * Creates a scaled mouse event with converted coordinates for fullscreen mode
     */
    private MouseEvent createScaledMouseEvent(MouseEvent original) {
        int scaledX = screenToGameX(original.getX());
        int scaledY = screenToGameY(original.getY());
        
        return new MouseEvent(
            original.getComponent(),
            original.getID(),
            original.getWhen(),
            original.getModifiersEx(),
            scaledX,
            scaledY,
            original.getClickCount(),
            original.isPopupTrigger(),
            original.getButton()
        );
    }

    public void runLater(Runnable action) {
        SwingUtilities.invokeLater(action);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.start();
        });
    }
}