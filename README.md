# Game System Card

# Introduction
The project illustrates card game, where there are 10 cards: red, white, blue each 3 cards and 1 black card. The player who gets black card will lose, however the player can keep playing or terminate the game by putting black card, it is up to player. Moreover for each player 10 seconds has given, if the player won't do anything during this time player will lose and game will end. The game lasts for 5 rounds and there are 3 players who play the game. For this game GUI interface I used Swing Java built in library to make user friendly interface. Player experience, personal information, probability of each player is also included for statistical purposes.

Welcome message class and Override method were added in addition to player panel.
The code follows hierarchical architecture. The main class is GameLauncherGUI, every class is connected and coordinated with this class.


# OOP Concepts in more Detail with Code Snippets
In this below I mentioned some OOP concepts with some examples. Inside the project is used way more OOP concepts then here in the report.

# Inheritance:
Inheritance is a mechanism where a new class (subclass or derived class) is created by inheriting properties and behaviours from an existing class (superclass or base class). This allows for code reusability and the creation of hierarchies of classes. For example, "Game_Rule120" class inherits from "game_rule"

abstract class game_rule {}
public class Game_Rule120 extends game_rule {}
abstract class experience {}
public class player_experience extends experience {}
interface Beginning {}
public class Welcome_to implements Beginning {}


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

These codes below that I've provided shows this concept with Game Rules(game_rule) and the  gen_rule, rule_one methods: The getInformation method shows the information of the specific class that is overridden inside, and the celestialBodyAtmosphere will implement information about the atmosphere and temperature of those classes. Finally, the calculateTimeDilation method represent the Einstein's theory of general relativity which is due to gravity of every specific celestial body types.

## 1. Base Class and Abstract Methods:

game_rule is the base class with abstract methods gen_rule, rule_one.

abstract class game_rule {
    abstract String gen_rule();
    abstract String rule_one();
