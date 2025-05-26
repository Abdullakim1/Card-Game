# Card Game Project Report (Revised)

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
     - [Subtype Polymorphism](#subtype-polymorphism)
     - [Parametric Polymorphism](#parametric-polymorphism)
     - [Coercion Polymorphism](#coercion-polymorphism)
   - [Composition](#composition)
   - [Reuse](#reuse)
   - [Abstraction](#abstraction)
   - [Inheritance](#inheritance)
   - [Subtyping](#subtyping)
   - [Exception Handling](#exception-handling)
   - [Extensibility](#extensibility)
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
Encapsulation is the bundling of data and methods that operate on that data within a single unit (class). It's about creating cohesive units where the data and the operations on that data are packaged together. Encapsulation focuses on the **structural organization** of code.

Example from Card.java:
```java
public class Card {
    private CardColor color;
    private int value;
    private boolean faceUp;
    
    public CardColor getColor() {
        return color;
    }
    
    public int getValue() {
        return value;
    }
    
    public boolean matches(Card other) {
        return color == other.color || value == other.value;
    }
}
```

This demonstrates encapsulation by:
- **Bundling related concepts**: The card properties (color, value, faceUp) and their associated behaviors are grouped together in a single class
- **Cohesive responsibility**: The Card class has a single, well-defined responsibility - to represent a playing card and its behaviors
- **Logical grouping**: Methods that operate on card data are placed within the Card class, creating a self-contained unit

Encapsulation is fundamentally about **organization and structure** - putting related things together in a meaningful way. While it often works together with information hiding, they are distinct concepts with different purposes.

### Information Hiding
Information hiding is a principle that restricts access to implementation details, making only certain parts of the class visible to the outside world. While encapsulation focuses on **organization**, information hiding focuses on **access control and visibility**. It's about protecting the internal state and implementation details from external interference.

Example from CardContainer.java:
```java
public class CardContainer<T extends Card> {
    private final List<T> cards;
    
    public List<T> getCards() {
        // Return an unmodifiable view to maintain information hiding
        return Collections.unmodifiableList(cards);
    }
    
    public T get(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return cards.get(index);
    }
}
```

#### The 3-Step Information Hiding Engineering Process

We strictly follow a 3-step process for all attributes to ensure proper information hiding:

1. **Always private fields**: All attributes are declared as private to prevent direct access from outside the class
   ```java
   private final List<T> cards;
   ```
   
   This is a deliberate design choice that prevents external code from directly manipulating our internal state. Even in inheritance hierarchies, we use private fields with protected getters rather than protected fields:
   
   ```java
   // In GameState.java - CORRECT approach
   private Game game;  // Private field
   
   protected Game getGame() {  // Protected accessor
       return game;
   }
   
   // Instead of - INCORRECT approach
   protected Game game;  // Directly exposing the field
   ```

2. **Class or instance determination**: For each field, we consciously decide whether it should be a class-level (static) or instance-level variable
   ```java
   // Instance variable - each container has its own list of cards
   private final List<T> cards;
   
   // vs. Class variable (static) - shared across all instances
   private static final int MAX_CARDS = 52;
   ```
   
   This decision impacts the memory model and behavior of our classes. Instance variables maintain separate state for each object, while static variables share state across all instances of a class.

3. **Constant or variable determination**: For each field, we decide whether it should be constant (final) or variable
   ```java
   // Constant - cannot be reassigned after initialization
   private final List<T> cards;  // The reference is constant, though contents can change
   
   // vs. Variable - can be changed
   private boolean isEmpty;  // Can be modified throughout the object's lifecycle
   ```
   
   This decision affects mutability and thread safety. Final fields provide guarantees about the stability of references, while variable fields allow for state changes.

#### Advanced Information Hiding Techniques

Beyond the basic 3-step process, we employ several advanced information hiding techniques:

- **Returning defensive copies or unmodifiable views**: Prevents external code from modifying internal collections
  ```java
  // Return an unmodifiable view instead of the actual list
  return Collections.unmodifiableList(cards);
  ```

- **Input validation**: Guards internal invariants by validating parameters before they affect internal state
  ```java
  if (index < 0 || index >= cards.size()) {
      throw new IndexOutOfBoundsException("Index out of bounds: " + index);
  }
  ```

- **Private helper methods**: Keeps implementation details hidden while exposing only the necessary public API
  ```java
  // Public method with simple interface
  public void shuffle() {
      shuffleImplementation(0, cards.size() - 1);
  }
  
  // Private implementation details
  private void shuffleImplementation(int start, int end) {
      // Complex algorithm hidden from clients
  }
  ```

Information hiding is distinct from encapsulation - while encapsulation bundles related concepts together, information hiding controls access to those concepts and protects implementation details.

### Polymorphism

#### Subtype Polymorphism
Subtype polymorphism allows objects of different classes to be treated as objects of a common superclass or interface.

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

public class PlayState extends GameState {
    @Override
    public void render(Graphics g) {
        // Play-specific rendering
    }
}
```

This allows different GameState subclasses to be used interchangeably:
```java
GameState currentState = new MenuState(game);
// Later...
currentState = new PlayState(game);
// The render method will behave differently based on the actual type
currentState.render(graphics);
```

#### Parametric Polymorphism
Parametric polymorphism (generics) allows a method or class to operate on objects of various types while providing compile-time type safety.

Example from CardContainer.java:
```java
public class CardContainer<T extends Card> {
    private final List<T> cards;
    
    public void add(T card) {
        cards.add(card);
    }
    
    public T get(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return cards.get(index);
    }
    
    // Demonstrates functional programming with generics
    public List<T> filter(Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T card : cards) {
            if (condition.test(card)) {
                result.add(card);
            }
        }
        return result;
    }
}
```

Usage example:
```java
// Container for regular cards
CardContainer<Card> deck = new CardContainer<>();
deck.add(new Card(CardColor.RED, 5));

// Container for special cards
CardContainer<SpecialCard> specialDeck = new CardContainer<>();
specialDeck.add(new SpecialCard(CardColor.GOLD, -1, SpecialEffect.WILD));

// Filter cards by condition
List<Card> redCards = deck.filter(card -> card.getColor() == CardColor.RED);
```

#### Coercion Polymorphism
Coercion polymorphism involves automatic type conversion during method calls or operations. This is a distinct form of polymorphism where the compiler automatically converts one type to another to ensure type compatibility.

##### Numeric Coercion in Card Scoring

We deliberately implement coercion polymorphism in our scoring system to demonstrate this important OOP concept:

```java
public class ScoreCalculator {
    public static double calculateScore(Card card) {
        // COERCION POLYMORPHISM: Implicit conversion from int to double
        // card.getValue() returns an int, which is automatically converted to double
        double baseScore = card.getValue();
        
        // Apply multiplier based on card color with further coercion
        switch (card.getColor()) {
            case RED:
                // COERCION POLYMORPHISM: int literal 5 is coerced to double 5.0 for multiplication
                return baseScore * 1.5; 
            case BLUE:
                return baseScore * 2.0;
            case GREEN:
                return baseScore * 1.25;
            case GOLD:
                return baseScore * 3.0;
            default:
                return baseScore;
        }
    }
    
    public static int getTotalScore(List<Card> cards) {
        // COERCION POLYMORPHISM: Explicit conversion from double to int (narrowing conversion)
        // The cast (int) forces the double result to be converted to an integer
        return (int) cards.stream()
                .mapToDouble(ScoreCalculator::calculateScore)
                .sum(); // double to int coercion
    }
}
```

##### String Coercion in UI Components

We also use string coercion extensively in our UI components:

```java
public class Card {
    // ...
    
    @Override
    public String toString() {
        // COERCION POLYMORPHISM: Multiple types coerced to String
        // - color (enum) is coerced to String via its toString() method
        // - value (int) is coerced to String representation
        return "Card[color=" + color + ", value=" + value + "]";
    }
    
    public void displayCardInfo() {
        // COERCION POLYMORPHISM: Automatic string concatenation with coercion
        System.out.println("Card: " + color + " " + value);
        // Integers and enums are automatically coerced to String
    }
}
```

##### Boolean Coercion in Game Logic

We use boolean coercion in our game logic for conditional checks:

```java
public class PlayState extends GameState {
    // ...
    
    public void handleCardPlay(Card card) {
        // COERCION POLYMORPHISM: Integer to boolean coercion
        // playerHand.size() returns an int, which is coerced to boolean in the if condition
        // Any non-zero value is coerced to true
        if (playerHand.size()) { // This is equivalent to playerHand.size() > 0
            // Handle card play
        }
        
        // COERCION POLYMORPHISM: Object reference to boolean coercion
        // card is an object reference, which is coerced to boolean in the if condition
        // Non-null references are coerced to true
        if (card) { // This is equivalent to card != null
            // Process card
        }
    }
}
```

##### Primitive Widening in Animation Calculations

We use primitive widening conversions in our animation system:

```java
public class CardAnimation {
    // ...
    
    public void updatePosition(Card card, int targetX, int targetY) {
        // Current position
        int currentX = card.getX();
        int currentY = card.getY();
        
        // COERCION POLYMORPHISM: int to float coercion for precise calculations
        float dx = targetX - currentX;
        float dy = targetY - currentY;
        
        // COERCION POLYMORPHISM: Multiple coercions in a single expression
        // - int to float coercion for speed (which is a float)
        // - float result is coerced back to int for setting the position
        card.setX(currentX + (int)(dx * speed));
        card.setY(currentY + (int)(dy * speed));
    }
}
```

These examples demonstrate our deliberate implementation of coercion polymorphism throughout the codebase, showing how different types are automatically converted to ensure type compatibility in various contexts.

### Composition
Composition is a design principle where a class is composed of one or more objects of other classes to create a more complex functionality.

Example from Game.java:
```java
public class Game extends JFrame {
    private GameState currentState;
    private CardAnimation cardAnimation;
    private AudioManager audioManager;
    private List<GameObserver> observers;
    private ResourceManager resourceManager;
    private InputHandler inputHandler;
    
    public Game() {
        // Initialize composed objects
        cardAnimation = new CardAnimation();
        audioManager = new AudioManager();
        observers = new ArrayList<>();
        resourceManager = new ResourceManager();
        inputHandler = new InputHandler(this);
    }
}
```

This demonstrates composition by:
- Building the Game class from multiple component objects
- Each component handles a specific responsibility
- The Game class delegates to its components rather than implementing all functionality itself

### Reuse
Code reuse is achieved through various mechanisms such as inheritance, composition, and creating reusable components.

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

This component can be reused across different screens:
```java
// In MenuState
modernButton.render(g, 100, 200, 200, 50);

// In SettingsState
modernButton.render(g, 150, 300, 180, 40);
```

### Abstraction
Abstraction is the process of hiding complex implementation details and showing only the necessary features of an object.

Example from GameCommand interface:
```java
public interface GameCommand {
    void execute();
    void undo();
}
```

Implementation example:
```java
public class DrawCardCommand implements GameCommand {
    private final Player player;
    private final Deck deck;
    private Card drawnCard;
    
    public DrawCardCommand(Player player, Deck deck) {
        this.player = player;
        this.deck = deck;
    }
    
    @Override
    public void execute() {
        drawnCard = deck.drawCard();
        player.addCard(drawnCard);
    }
    
    @Override
    public void undo() {
        player.removeCard(drawnCard);
        deck.returnCard(drawnCard);
    }
}
```

This demonstrates abstraction by:
- Hiding the complex details of drawing/returning cards
- Providing a simple interface (execute/undo) for clients to use
- Allowing different command implementations to be used interchangeably

### Inheritance
Inheritance establishes an "is-a" relationship between classes, allowing a subclass to inherit properties and methods from a superclass.

Example of class hierarchy:
```java
public abstract class GameState {
    protected Game game;
    
    public GameState(Game game) {
        this.game = game;
    }
    
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract void onEnter();
    public abstract void onExit();
}

public class MenuState extends GameState {
    public MenuState(Game game) {
        super(game);
    }
    
    @Override
    public void tick() {
        // Menu-specific update logic
    }
    
    @Override
    public void render(Graphics g) {
        // Menu-specific rendering
    }
    
    @Override
    public void onEnter() {
        // Setup when entering menu state
    }
    
    @Override
    public void onExit() {
        // Cleanup when exiting menu state
    }
}
```

This demonstrates inheritance by:
- MenuState inheriting common functionality from GameState
- Providing specialized implementations of abstract methods
- Reusing code from the parent class

### Subtyping
Subtyping establishes an "is-substitutable-for" relationship, where objects of a subtype can be used wherever objects of the supertype are expected. This is a fundamental concept in object-oriented programming that enables polymorphic behavior.

#### Basic Subtyping with Interfaces

Our project uses interface-based subtyping extensively. For example, with the GameStateFactory interface:

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

This allows different factory implementations to be used interchangeably wherever a GameStateFactory is expected:

```java
// Client code doesn't need to know the concrete type
GameStateFactory factory = getAppropriateFactory();
GameState state = factory.createState(game);
```

#### Multityping: Objects with Multiple Types

Multityping is a more advanced form of subtyping where an object simultaneously belongs to multiple types (implements multiple interfaces). This is a powerful concept that allows objects to fulfill different roles in different contexts.

Our project demonstrates multityping through the InteractiveCard class, which implements both the Drawable and Interactive interfaces:

```java
// The interfaces define different aspects of behavior
public interface Drawable {
    void render(Graphics g, int x, int y, int width, int height);
}

public interface Interactive {
    boolean handleMouseEvent(MouseEvent e);
    boolean contains(int x, int y);
}

// InteractiveCard implements BOTH interfaces - this is multityping
public class InteractiveCard implements Drawable, Interactive {
    private final Card card;
    private final Rectangle bounds;
    private boolean selected;
    private boolean hovered;
    
    // Implementation for Drawable interface
    @Override
    public void render(Graphics g, int x, int y, int width, int height) {
        // Update bounds if position changed
        bounds.setLocation(x, y);
        bounds.setSize(width, height);
        
        // Set card highlight based on selection/hover state
        card.setHighlighted(selected || hovered);
        
        // Render the card
        card.render(g, x, y, width, height);
    }
    
    // Implementation for Interactive interface
    @Override
    public boolean handleMouseEvent(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            boolean wasHovered = hovered;
            hovered = contains(x, y);
            return wasHovered != hovered;
        }
        else if (e.getID() == MouseEvent.MOUSE_CLICKED && contains(x, y)) {
            selected = !selected;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }
}
```

#### Benefits of Multityping

Multityping provides several key advantages:

1. **Role Separation**: An object can fulfill different roles in different contexts
2. **Interface Segregation**: Clients only depend on the interfaces they actually use
3. **Flexible Type Hierarchies**: Objects can be part of multiple type hierarchies simultaneously

Here's how our InteractiveCard can be used in different contexts due to multityping:

```java
// Create an InteractiveCard instance
InteractiveCard card = new InteractiveCard(new Card(CardColor.RED, 5), 100, 200, 80, 120);

// CONTEXT 1: Used as a Drawable in the rendering system
List<Drawable> drawables = new ArrayList<>();
drawables.add(card);  // Card treated as a Drawable

// CONTEXT 2: Used as an Interactive element in the input system
List<Interactive> interactives = new ArrayList<>();
interactives.add(card);  // Same card object treated as Interactive

// CONTEXT 3: Used directly as an InteractiveCard when both aspects are needed
if (card.isSelected()) {
    // Do something with the selected card
}

// Process all drawables in the rendering loop
for (Drawable d : drawables) {
    d.render(g, x, y, width, height);  // Polymorphic call
}

// Process all interactive elements in the input handling loop
for (Interactive i : interactives) {
    if (i.contains(mouseX, mouseY)) {
        i.handleMouseEvent(mouseEvent);  // Polymorphic call
    }
}
```

#### Multiple Interface Implementation vs. Multiple Inheritance

Java supports multityping through multiple interface implementation, but not through multiple inheritance of classes. This is a deliberate design choice to avoid the "diamond problem" and other complexities associated with multiple inheritance.

Our design takes advantage of this by defining behavior through interfaces and implementing those interfaces in concrete classes:

```java
// Multiple interface implementation (SUPPORTED in Java)
public class GameButton implements Drawable, Interactive, Animatable {
    // Implements methods from all three interfaces
}

// Multiple inheritance (NOT supported in Java)
// public class GameButton extends JComponent, Animation { ... } // INVALID
```

By using multityping extensively in our design, we create a flexible system where objects can participate in multiple subsystems without tight coupling between those subsystems.

### Exception Handling
Exception handling is used to manage errors and exceptional conditions in a controlled way.

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
            System.err.println("Invalid card: " + e.getMessage());
        }
    }
    
    private void validateCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        // Additional validation
    }
}
```

This demonstrates exception handling by:
- Using exceptions to signal error conditions
- Providing meaningful error messages
- Catching and handling exceptions at appropriate levels

### Extensibility
Extensibility is the ability to extend a system with new functionality without modifying existing code. This is a critical aspect of our design that allows the game to evolve over time without requiring changes to existing, tested code.

#### Comprehensive State Management System

We've implemented a robust state management system that demonstrates extensibility through the StateManager class:

```java
public class StateManager {
    private final Game game;
    private final Map<String, GameStateFactory> stateFactories;
    private GameState currentState;
    
    public StateManager(Game game) {
        this.game = game;
        this.stateFactories = new HashMap<>();
        
        // Register default state factories
        registerStateFactory("menu", new MenuStateFactory());
        registerStateFactory("play", new PlayStateFactory());
        registerStateFactory("rules", new RulesStateFactory());
    }
    
    /**
     * Registers a new state factory, allowing for extensibility
     * New game states can be added without modifying this class
     */
    public void registerStateFactory(String stateId, GameStateFactory factory) {
        stateFactories.put(stateId, factory);
    }
    
    /**
     * Changes to a different game state using the appropriate factory
     */
    public boolean changeState(String stateId) {
        GameStateFactory factory = stateFactories.get(stateId);
        if (factory == null) {
            return false;
        }
        
        // Create the new state using the factory
        GameState newState = factory.createState(game);
        
        // Exit the current state if it exists
        if (currentState != null) {
            currentState.onExit();
        }
        
        // Set and enter the new state
        currentState = newState;
        currentState.onEnter();
        
        return true;
    }
}
```

This design allows new game states to be added without modifying existing code:

```java
// Adding a new tutorial state to the game
public class TutorialState extends GameState { /* implementation */ }

public class TutorialStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new TutorialState(game);
    }
}

// Client code to register the new state
stateManager.registerStateFactory("tutorial", new TutorialStateFactory());

// Later, transitioning to the new state
stateManager.changeState("tutorial");
```

#### Card Effect Strategy Pattern

We've implemented a comprehensive card effect system using the Strategy pattern to allow new card effects to be added without modifying existing code:

```java
// The CardEffect interface defines the contract for all card effects
public interface CardEffect {
    void apply(PlayState state, Player player);
    String getDescription();
}

// Concrete implementations for different effects
public class DrawCardEffect implements CardEffect {
    private final int cardsToDraw;
    
    public DrawCardEffect(int cardsToDraw) {
        this.cardsToDraw = cardsToDraw;
    }
    
    @Override
    public void apply(PlayState state, Player player) {
        for (int i = 0; i < cardsToDraw; i++) {
            player.drawCard(state.getDeck());
        }
        state.skipNextTurn();
    }
    
    @Override
    public String getDescription() {
        return "Draw " + cardsToDraw + " cards and skip a turn";
    }
}

public class SkipTurnEffect implements CardEffect {
    @Override
    public void apply(PlayState state, Player player) {
        state.skipNextTurn();
    }
    
    @Override
    public String getDescription() {
        return "Skip a turn";
    }
}

public class ReverseDirectionEffect implements CardEffect {
    @Override
    public void apply(PlayState state, Player player) {
        state.reverseDirection();
    }
    
    @Override
    public String getDescription() {
        return "Reverse direction of play";
    }
}
```

#### Card Effect Factory for Runtime Extension

We've implemented a factory for card effects that allows new effects to be registered at runtime:

```java
public class CardEffectFactory {
    private static final Map<CardColor, Class<? extends CardEffect>> colorEffectMap = new HashMap<>();
    
    // Static initializer to set up the default mappings
    static {
        colorEffectMap.put(CardColor.RED, SkipTurnEffect.class);
        colorEffectMap.put(CardColor.BLUE, DrawCardEffect.class);
        colorEffectMap.put(CardColor.GREEN, ReverseDirectionEffect.class);
        colorEffectMap.put(CardColor.GOLD, WildCardEffect.class);
    }
    
    /**
     * Registers a new effect for a card color
     * This demonstrates extensibility by allowing new effects to be added
     * without modifying existing code
     */
    public static void registerEffect(CardColor color, Class<? extends CardEffect> effectClass) {
        colorEffectMap.put(color, effectClass);
    }
    
    /**
     * Creates an effect for a special card
     */
    public static CardEffect createEffect(Card card) {
        // Implementation details
    }
}
```

This allows new card effects to be added without modifying existing code:

```java
// Define a new card effect
public class SwapHandsEffect implements CardEffect {
    @Override
    public void apply(PlayState state, Player player) {
        Player opponent = state.getNextPlayer();
        List<Card> tempHand = new ArrayList<>(player.getHand());
        player.setHand(opponent.getHand());
        opponent.setHand(tempHand);
    }
    
    @Override
    public String getDescription() {
        return "Swap hands with the next player";
    }
}

// Register the new effect for a new card color or replace an existing one
CardEffectFactory.registerEffect(CardColor.PURPLE, SwapHandsEffect.class);
```

#### Open/Closed Principle Implementation

Our design follows the Open/Closed Principle throughout the codebase. For example, our generic CardContainer class is open for extension but closed for modification:

```java
// The base container is complete and doesn't need modification
public class CardContainer<T extends Card> {
    private final List<T> cards;
    
    // Implementation details
}

// Extended for specific use cases without modifying the original
public class DeckContainer extends CardContainer<Card> {
    public void dealCards(Player player, int count) {
        for (int i = 0; i < count; i++) {
            if (!isEmpty()) {
                player.addCard(remove(0));
            }
        }
    }
}

public class SpecialCardContainer<T extends SpecialCard> extends CardContainer<T> {
    public List<T> getActiveEffects() {
        return filter(card -> card.isEffectActive());
    }
}
```

#### Extensibility Through Parametric Polymorphism

Our use of generics provides another dimension of extensibility:

```java
// The filter method allows for extensible querying of cards
public List<T> filter(Predicate<T> condition) {
    List<T> result = new ArrayList<>();
    for (T card : cards) {
        if (condition.test(card)) {
            result.add(card);
        }
    }
    return result;
}

// Usage with different predicates without modifying the container
List<Card> redCards = deck.filter(card -> card.getColor() == CardColor.RED);
List<Card> highValueCards = deck.filter(card -> card.getValue() > 5);
List<Card> specialCards = deck.filter(Card::isSpecial);
```

By designing our system with extensibility as a core principle, we ensure that the game can evolve over time with minimal changes to existing code. This reduces the risk of introducing bugs when adding new features and makes the codebase more maintainable in the long term.

## Design Patterns

### MVC Pattern
The Model-View-Controller pattern separates the application into three main components:
- **Model**: Represents the data and business logic
- **View**: Displays the data to the user
- **Controller**: Handles user input and updates the model/view

Implementation in the card game:
- **Model**: Card, Deck, Player classes
- **View**: Rendering methods, UI components
- **Controller**: GameState classes, event handlers

### State Pattern
The game uses the State pattern for managing different game states:
- MenuState
- PlayState
- RulesState

This allows the game to change its behavior when its internal state changes.

### Observer Pattern
Used for game event handling and UI updates through the GameObserver interface.

```java
public interface GameObserver {
    void onCardPlayed(Card card, Player player);
    void onTurnChanged(Player currentPlayer);
    void onGameOver(Player winner);
}
```

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

### Command Pattern
Used for implementing game actions and undo functionality:
```java
public interface GameCommand {
    void execute();
    void undo();
}
```

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

## Conclusion
The project successfully implements a card game while demonstrating strong OOP principles and design patterns. The modular design allows for easy extensions and maintenance, while the use of design patterns ensures robust and flexible architecture.