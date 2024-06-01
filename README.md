# Game System Card

# Introduction
The project illustrates card game, where there are 10 cards: red, white, blue each 3 cards and 1 black card. The player who gets black card will lose, however the player can keep playing or terminate the game by putting black card, it is up to player. Moreover for each player 10 seconds has given, if the player won't do anything during this time player will lose and game will end. The game lasts for 5 rounds and there are 3 players who play the game. For this game GUI interface I used Swing Java built in library to make user friendly interface. Player experience, personal information, probability of each player is also included for statistical purposes.

Welcome message class and Override method were added in addition to player panel.
The code follows hierarchical architecture. The main class is GameLauncherGUI, every class is connected and coordinated with this class.


# OOP Concepts in more Detail with Code Snippets
In this below I mentioned some OOP concepts with some examples. Inside the project is used way more OOP concepts then here in the report.

# Inheritance:
Inheritance is a mechanism where a new class (subclass or derived class) is created by inheriting properties and behaviours from an existing class (superclass or base class). This allows for code reusability and the creation of hierarchies of classes. For example, "Game_Rule120" class inherits from "game_rule"

abstract class game_rule {
        ...
}

public class Game_Rule120 extends game_rule {
abstract class experience {...}
public class player_experience extends experience {...}
interface Beginning {...}
public class Welcome_to implements Beginning {...}
}


# Information Hiding and Encapsulation:
Encapsulation and information hiding are two related but distinct concepts in object-oriented programming. Encapsulation is the bundling of data (attributes) and the methods (functions) that operate on that data into a single unit (a class), while information hiding is the principle of restricting access to certain details of an object.

Here in the code below the attributes of the class are declared as protected and private access modifier which is the information hiding in action and restrict the access of other classes.

And we encapsulated these data (attributes) by the getter and setter method. it means that the other classes can access to these data via these methods.

public class Players {
    private String name, name2, name3;   //Information hiding.
    private int age, age2, age3;

    //Constructor...

public String getName() {return name;}
public void setName(String name1) {this.name = name;}
public int getAge() {return age;}
public void setAge(int age) {this.age = age;} 

# Polymorphism:
Polymorphism allows objects of different classes to be treated as objects of a common superclass. It enables flexibility and extensibility in your code. some common forms of polymorphism are:

## Method Overloading:

This type of polymorphism is resolved during compile time. In method overloading, multiple methods have the same name but different parameter lists.

Here below, there are some methods, with the same name but different parameters, which are the constructors of the Players. When we create an object of the these classes, we can choose which constructor to call based on the number of arguments we provide. This is compile-time polymorphism because the decision about which constructor to call is made at compile time based on the arguments we pass during object creation.

public void setName(String name1) {this.name = name;}
public void setAge(int age) {this.age = age;}

# Polymorphism by Inclusion:

The Inclusion polymorphism allows to point derived classes using base class pointers and references. This is runtime polymorphism and we do not know type of the actual object until it is executing.

In this section I provided three different example of usage of polymorphism by inclusion.

These codes below that I've provided shows this concept with Beginning interface and the  usage, rule, welcome methods: The methods shows the information of the specific classes that is overridden inside, and the information will be implemented.

## 1. Base Class and Abstract Methods:

Beginning is the base interface with public methods usage, rule, welcome.

interface Beginning {
    public String usage();
    public String  rule();
    public String welcome();
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

## 3. The class that determines winning chance
The prob_win_player1 method calculates player's chance of winning for each player. The winner's probability calculated inside showProb method in main class(GameLauncherGUI).

The prob_win_player1 method is responsible for calculating and returning random winning chance probability, wether player1 or 2 or 3, based on the random distribution logic.

public class prob_win_player1 {
    public String prob_win_player1(List<String> player1Hand) {
    //code...
    StringBuilder result = new StringBuilder();
        result.append("Probability for Player 1: \n\n");

        boolean hasBlackCard = player1Hand.contains("Black");
        if (hasBlackCard) {
            result.append("Winning chance is low\n");
        } else {
            int high = 0;
            int avg = 0;
            int low = 0;
            ...
            }
}
}

## Abstraction: 
The class gameSystem abstracts away the details of the game implementation from the user. Users only need to call the start method to begin the game, while the internal workings, such as dealing hands, player turns, and determining the winner/loser, are abstracted away within the class.

public class gameSystem {
    private List<String> player1Hand;
    private List<String> player2Hand;
    private List<String> player3Hand;
    private Timer playerTimer;
    private final long TIME_LIMIT = 10000;

    public void start(){...};

## Composition:
In Java, the concept of composition is a way of designing and structuring classes so that they can be composed of one another, where one class contains an instance of another class as a member variable.

In the GameLauncherGUI class:

It has instance variables game, PlayerExperience, and player_ok of types gameSystem, player_experience, and Players, respectively.
These instance variables represent parts of the GameLauncherGUI class, and they are used to provide specific functionalities to the GUI.
For example, game is used to start the game, PlayerExperience is used to manage player experiences, and player_ok is used to handle player names and ages.
The GameLauncherGUI class utilizes these instances to compose its functionality.

GameLauncherGUI class exhibits composition by composing its functionality using other classes.

public class GameLauncherGUI extends JFrame {
    private gameSystem game; // Composition: GameLauncherGUI has a gameSystem
    private player_experience PlayerExperience; // Composition: GameLauncherGUI has a player_experience
    private Players player_ok; // Composition: GameLauncherGUI has a Players

    // Other code...
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
    protected String general() {...}

    protected void experience_of() {
        // Implementation
    }
}

## Interface Implementation:
An interface defines a contract of behaviours that a class must implement. In simpler terms, it specifies a set of methods (and constants) that a class must provide, but it doesn't provide the implementation details. Interfaces are often used for creating common, shareable contracts that can be used by multiple classes.

interface Beginning{
    public String usage();
public class Welcome_to implements Beginning {

    public String usage() {...}

## Exception handling:
Exception handling in Java is a mechanism for dealing with unexpected or erroneous situations that may occur during program execution.
Exception handling is typically done using try and catch blocks. You enclose the code that might throw an exception within the try block and catch and handle the exception within the catch block.

protected void experience_of() {
        try {
        //code...
        if (gaming_experience < 5) {
                JOptionPane.showMessageDialog(null, "Unfortunately your experience is insufficient");
                System.exit(0);
            } else if (gaming_experience == 5 || gaming_experience > 5) {

            }
            //Player 2

            if (gaming_experience2 < 5) {
                JOptionPane.showMessageDialog(null, "Unfortunately your experience is insufficient");
                System.exit(0);

            } else if (gaming_experience2 == 5 || gaming_experience2 > 5) {

            }
            //player 3

            if (gaming_experience3 < 5) {
                JOptionPane.showMessageDialog(null, "Unfortunately your experience is insufficient");
                System.exit(0);
            } else if (gaming_experience3 == 5 || gaming_experience3 > 5) {

            }
        } catch (NumberFormatException e) {
        //code...
        }
    }

    
