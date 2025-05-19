# Card Game Project Report

## Table of Contents
1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Libraries and Frameworks](#libraries-and-frameworks)
4. [Object-Oriented Design](#object-oriented-design)
   - [Class Diagram](#class-diagram)
5. [OOP Concepts Implementation](#oop-concepts-implementation)
   - [Encapsulation](#encapsulation)
   - [Information Hiding](#information-hiding)
   - [Method Overloading](#method-overloading)
   - [Polymorphism](#polymorphism)
   - [Composition](#composition)
   - [Reuse](#reuse)
   - [Abstraction](#abstraction)
   - [Inheritance](#inheritance)
   - [Subtyping](#subtyping)
   - [Exception Handling](#exception-handling)
6. [Design Patterns](#design-patterns)
   - [MVC Pattern](#mvc-pattern)
   - [State Pattern](#state-pattern)
   - [Observer Pattern](#observer-pattern)
   - [Factory Pattern](#factory-pattern)
   - [Command Pattern](#command-pattern)
   - [Singleton Pattern](#singleton-pattern)
7. [Conclusion](#conclusion)

## Introduction
This project implements a card game inspired by UNO, focusing on creating an engaging and interactive gaming experience while adhering to Object-Oriented Programming principles. The game features a graphical user interface, card animations, and a state-based game flow management system.

## Project Overview
The project is structured using the Model-View-Controller (MVC) architectural pattern to ensure separation of concerns and maintainable code. Key motivations for the design choices include:

1. **Modularity**: Using packages to organize related functionality
2. **Extensibility**: Implementing design patterns for easy feature additions
3. **Maintainability**: Following SOLID principles and clean code practices
4. **User Experience**: Incorporating animations and modern UI components

## Libraries and Frameworks
The project utilizes the following libraries and frameworks:

1. **Java Swing (javax.swing)**: For creating the graphical user interface
   - JFrame for the main window
   - Graphics2D for custom rendering
   - MouseListener for user input

2. **Java AWT (java.awt)**: For graphics and event handling
   - Graphics2D for advanced rendering
   - Event handling (MouseEvent, etc.)
   - Layout management

3. **Java Sound API (javax.sound)**: For audio playback
   - Background music management
   - Sound effects for game events
   - Audio resource handling

## Object-Oriented Design

### Package Structure
```
com.cardgame
├── model          # Game data and business logic
│   ├── card      # Card-related classes
│   ├── player    # Player management
│   └── game      # Core game mechanics
├── view          # UI components and rendering
│   ├── components # Reusable UI elements
│   └── animations # Visual effects and transitions
└── controller    # Game flow and user input
```

## OOP Concepts Implementation

### Method Overloading
Example from Card.java showing constructor and method overloading:
```java
public class Card {
    // Constructor overloading
    public Card(CardColor color, int value) {
        this(color, value, false);
    }

    public Card(CardColor color, int value, boolean faceUp) {
        this.color = color;
        this.value = value;
        this.faceUp = faceUp;
    }

    // Method overloading
    public boolean matches(Card other) {
        return color == other.color || value == other.value;
    }

    public boolean matches(CardColor color) {
        return this.color == color;
    }

    public boolean matches(int value) {
        return this.value == value;
    }
}
```

### Encapsulation
Example from Card.java:
```java
public class Card {
    private CardColor color;
    private int value;
    private boolean faceUp;
    
    public CardColor getColor() {
        return color;
    }
    
    public boolean matches(Card other) {
        return color == other.color || value == other.value;
    }
}
```
This demonstrates encapsulation by:
- Private fields for card properties
- Public methods for controlled access
- Internal state protection

### Information Hiding
Example from PlayState.java:
```java
public class PlayState extends GameState {
    private List<Card> playerHand;
    private List<Card> computerHand;
    
    private void handleCardPlay(Card card) {
        if (isValidPlay(card)) {
            executePlay(card);
        }
    }
}
```
Information hiding is achieved through:
- Private implementation details
- Public interface for game actions
- Internal helper methods

### Polymorphism
Example from GameState hierarchy:
```java
public abstract class GameState {
    public abstract void render(Graphics g);
    public abstract void handleMouseEvent(MouseEvent e);
}

public class MenuState extends GameState {
    @Override
    public void render(Graphics g) {
        // Menu-specific rendering
    }
}
```

### Composition
Example from Game.java:
```java
public class Game extends JFrame {
    private GameState currentState;
    private CardAnimation cardAnimation;
    private AudioManager audioManager;
    private List<GameObserver> observers;
    private ResourceManager resourceManager;
    private InputHandler inputHandler;
}
```

### Reuse
Example of code reuse through ModernButton:
```java
public class ModernButton {
    private boolean hovered;
    private boolean pressed;
    
    public void render(Graphics g, int x, int y, int width, int height) {
        // Reusable button rendering logic
    }
}
```

### Abstraction
Example from GameCommand interface:
```java
public interface GameCommand {
    void execute();
    void undo();
}
```

### Inheritance
Example of class hierarchy:
```java
public abstract class GameState {
    protected Game game;
    
    public GameState(Game game) {
        this.game = game;
    }
}

public class MenuState extends GameState {
    public MenuState(Game game) {
        super(game);
    }
}
```

### Subtyping
Example with GameStateFactory interface:
```java
public interface GameStateFactory {
    GameState createState(Game game);
}

class MenuStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new MenuState(game);
    }
}
```

### Exception Handling
Example from deck management:
```java
public class Deck {
    public Card drawCard() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot draw from empty deck");
        }
        return cards.remove(0);
    }
    
    public void addCard(Card card) {
        try {
            validateCard(card);
            cards.add(card);
        } catch (IllegalArgumentException e) {
            // Handle invalid card
        }
    }
}
```

## Design Patterns

### Singleton Pattern
Implementation in GameManager.java:
```java
public class GameManager {
    private static GameManager instance;
    
    private GameManager() {
        // Private constructor
    }
    
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
}
```
Used for managing global game state and settings.

### Factory Pattern
Implementation in GameStateFactory.java:
```java
public interface GameStateFactory {
    GameState createState(Game game);
}

class PlayStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new PlayState(game);
    }
}
```
Used for creating different game states in a decoupled way.

### State Pattern
The game uses the State pattern for managing different game states:
- MenuState
- PlayState
- RulesState

### Observer Pattern
Used for game event handling and UI updates through the GameObserver interface.

## Conclusion
The project successfully implements a card game while demonstrating strong OOP principles and design patterns. The modular design allows for easy extensions and maintenance, while the use of design patterns ensures robust and flexible architecture.
