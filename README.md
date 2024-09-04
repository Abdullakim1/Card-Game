# Game System Card

# Introduction
The project illustrates card game, where there are cards: red, white, blue each 3 cards and 1 black card. The player who gets black card will lose, however the player can keep playing or terminate the game by putting black card, it is up to player. Player experience class makes sure if player doesn't have sufficient experience it won't allow to play the game for all players. Players receive 3 cards each, shuffled and distributed randomly.

The players can be any number from 1 to infinity, each player gets 3 cards. The game is implemented using Java's Swing library for a user-friendly graphical interface. The project also includes functionality for player experience, player name.

# Project Structure
The project uses a hierarchical architecture with the following key classes:

1. GameLauncherGUI: The main class that initiates the game and interacts with other components.
2. gameSystem: Superclass for game management.
3. PlayerExperience: Handles player experience validation.
4. Players: Manages player information and card distribution.

# OOP Concepts in more Detail with Code Snippets
In this below I mentioned some OOP concepts with some examples. Inside the project is used way more OOP concepts then here in the report.

# Inheritance:
Inheritance is a mechanism where a new class (subclass or derived class) is created by inheriting properties and behaviours from an existing class (superclass or base class). This allows for code reusability and the creation of hierarchies of classes. For example, "Game_Rule120" class inherits from "game_rule"

abstract class game_rule { ... }

public class Game_Rule120 extends game_rule { ... }


# Information Hiding and Encapsulation:
Encapsulation and information hiding are two related but distinct concepts in object-oriented programming. Encapsulation is the bundling of data (attributes) and the methods (functions) that operate on that data into a single unit (a class), while information hiding is the principle of restricting access to certain details of an object.

Here in the code below the attributes of the class are declared as protected and private access modifier which is the information hiding in action and restrict the access of other classes.

And we encapsulated these data (attributes) by the getter and setter method. it means that the other classes can access to these data via these methods.

public class Players {
    private String name;
    private int age;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}


# Polymorphism:
Polymorphism allows objects of different classes to be treated as objects of a common superclass. It enables flexibility and extensibility in your code. some common forms of polymorphism are:

## Method Overloading:

This type of polymorphism is resolved during compile time. In method overloading, multiple methods have the same name but different parameter lists.

Here below, there are some methods, with the same name but different parameters, which are the constructors of the Players. When we create an object of the these classes, we can choose which constructor to call based on the number of arguments we provide. This is compile-time polymorphism because the decision about which constructor to call is made at compile time based on the arguments we pass during object creation.

public void setName(String name1) {this.name = name;}
public void setAge(int age) {this.age = age;}

# Polymorphism by Inclusion:

The Inclusion polymorphism allows to point derived classes using base class pointers and references. This is runtime polymorphism and we do not know type of the actual object until it is executing. Polymorphism by inclusion, where objects of different classes can be treated uniformly if they implement the same interface, providing flexibility and modularity in the design of the system.

The code bellow depicts example of usage of polymorphism by inclusion.

interface Beginning {
    String welcome();
}

public class Welcome_to implements Beginning {
    public String welcome() { ... }
}

## 1. Base Class and Abstract Methods:

Beginning is the base interface with public method welcome.

interface Beginning{
    public String welcome();
}

public class Welcome_to implements Beginning {

    public String welcome() {
    }
}
class usage implements Beginning {

    public String welcome() {
    }
}
class usage2 implements Beginning{

    public String welcome() {
    }
}

## 2. Derived Classes: override1:
override1 is derived class that inherit from override. It will override the over method, providing its own specific implementations.

// Derived class override1
class override1 extends override{
    //code...
    @Override
    public String over(){
    //implementation of this method
}
}

## Method overloading

This is seen in having two methods with the same name calculateWinProbability, but different parameter lists. One takes a List<T> and the other takes a String[]. This allows the class to calculate win probabilities using either a list or an array of strings as input. Method overloading allows to define multiple methods with the same name but different parameter lists. This provides flexibility and readability of the code. Java determines which method to execute based on the number and types of arguments provided in the method call. Overloading is determined at compile time and is based on the method signature (name and parameter types).

public String calculateWinProbability(List<T> playerHand) {
    // Method implementation
}

public String calculateWinProbability(String[] playerHand) {
    // Overloaded method implementation
}

## Parametric polymorphism

Parametric polymorphism allows to write code that can operate on a wide range of data types. In the provided code, the class prob_win_player3 is using generics <T> to achieve parametric polymorphism.

The method calculateWinProbability() is declared with a parameter of type List<T>, where T is a placeholder for a generic type. This means that the method can accept a list of any type. Inside the method, it operates on elements of type T without knowing the specific type until runtime.

public class prob_win_player3<T> { // <---- Generics declared here

    public String calculateWinProbability(List<T> playerHand) { // <---- List<T> is a generic type
        StringBuilder result = new StringBuilder();
        result.append("Probability for Player3: \n\n");

        // Code operates on elements of type T without knowing the specific type until runtime

    }
}

## Abstraction: 
Abstraction in programming is a concept that involves hiding the complex implementation details and showing only the essential features of an object. In the provided code, abstraction is highlighted through the use of an abstract class and the abstract methods it defines. The experience class is declared as an abstract class. This means it cannot be instantiated directly and must be subclassed. It contains two abstract methods, experience_of() and general(), which must be implemented by any subclass. This design enforces a contract that any subclass must provide specific implementations for these methods, while the details of these methods are not exposed in the experience class itself.

abstract class experience {
    abstract void experience_of();
    abstract String general();
}

public class player_experience extends experience {
    protected String general() { ... }
    protected void experience_of() { ... }
}

## Composition:
In Java, the concept of composition is a way of designing and structuring classes so that they can be composed of one another, where one class contains an instance of another class as a member variable.

In the GameLauncherGUI class:

It has instance variables game, PlayerExperience, and player_ok of types gameSystem, player_experience, and Players, respectively.
These instance variables represent parts of the GameLauncherGUI class, and they are used to provide specific functionalities to the GUI.
For example, game is used to start the game, PlayerExperience is used to manage player experiences, and player_ok is used to handle player names and ages.
The GameLauncherGUI class utilizes these instances to compose its functionality.

GameLauncherGUI class exhibits composition by composing its functionality using other classes.

public class GameLauncherGUI extends JFrame {
    private gameSystem game;
    private player_experience PlayerExperience;
    private Players player_ok;

    // Constructor and methods
}


## Subtyping:

Subtyping in Java is a fundamental concept related to the inheritance hierarchy and type compatibility between classes and interfaces. Subtyping allows a subclass to be treated as an instance of its superclass or as an instance that implements an interface. This is crucial for polymorphism and method overriding.

Abstract Class: The experience class is an abstract class with abstract methods experience_of() and general(). Abstract classes cannot be instantiated directly; they provide a blueprint for subclasses to implement their own functionality.

Subclassing: The player_experience class extends the experience abstract class. By doing so, player_experience becomes a subtype of experience. It inherits the abstract methods experience_of() and general(), which it must implement.

Implementation of Abstract Methods: The player_experience class provides implementations for the abstract methods experience_of() and general(), fulfilling the contract specified by the experience abstract class.

Polymorphism: Through inheritance and method overriding, instances of player_experience can be treated as instances of experience. This allows for polymorphic behavior, where code written to operate on experience objects can work with player_experience objects as well.

Superclass: experience
Subclass: player_experience
Subtyping: player_experience is a subtype of experience, inheriting its methods and possibly extending its functionality.

abstract class experience {
    abstract void experience_of();
    abstract String general();
}

public class player_experience extends experience {
    protected String general() { ... }
    protected void experience_of() { ... }
}


## Interface Implementation:
An interface defines a contract of behaviours that a class must implement. In simpler terms, it specifies a set of methods (and constants) that a class must provide, but it doesn't provide the implementation details. Interfaces are often used for creating common, shareable contracts that can be used by multiple classes.

interface Beginning {
    String usage();
}

public class Welcome_to implements Beginning {
    public String usage() { ... }
}

## Exception handling:
Exception handling in Java is a mechanism for dealing with unexpected or erroneous situations that may occur during program execution.
Exception handling is typically done using try and catch blocks. You enclose the code that might throw an exception within the try block and catch and handle the exception within the catch block.

protected void experience_of() {
    try {
        // Code
    } catch (NumberFormatException e) {
        // Handle exception
    }
}

