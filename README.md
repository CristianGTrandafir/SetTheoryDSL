# SetTheoryDSL

This is a set theoretic domain-specific language that I developed for the course CS 474 Object-Oriented Languages and Environments at UIC. 
It is written in Scala, which is a functional and object-oriented programming language that is interoperable with Java and runs on the JVM.

The overarching goal of this project was to develop familiarity with the implementation of common programming language features like scopes, exceptions, classes, and more. 
This project also explored many functional language concepts like lazy evaluation, call-by-name, referential transparency of functions, immutable variables, monads, and more. 
The project was divided into 5 homeworks:
* Homework 1 
  * Implemented the Assign command which creates a new Set with specified elements
  * Implemented primitive set operations like Union and Cartesian Product
  * User can define new scopes and nested scopes by using the Scope command
  * The Macro command can be used to create a macro for any of the commands in the language
* Homework 2
  * ClassDef command allows the user to define a new class
  * NewObject command allows the user to instantiate an object of a class
  * InvokeMethod command allows the user to call an object's method
  * The infix command Extends allows the user to inherit from a superclass
* Homework 3
  * InterfaceDecl command allows the user to define a new interface
  * AbstractClassDef command allows the user to definea new abstract class
  * The infix command Implements allows the user to implement an interface
  * The Extends command was updated to allow interfaces to inherit from superinterfaces
* Homework 4
  * A call-by-name IF command was implemented to gain familiarity with lazy evaluation
  * ExceptionClassDef command allows the user to define an exception class
  * CatchException command functions as a try/catch block
  * ThrowException command throws an exception
  * Scope command was updated to allow parent scopes to catch thrown exceptions inside nested scopes
* Homework 5
  * Partial evaluation was implemented to allow partial definition of command parameters
    * For example, Union(Identifier("DefinedSet1"), Identifier("UndefinedSet2")).eval would normally fail because the second parameter does not reference a variable stored by the program.
    * Instead of crashing, partial evaluation returns a partially evaluated statement, pEvalUnion(Identifier("DefinedSet1"), "UndefinedSet2").
    * In this case the second Identifier was partially evaluated, and the new partially evaluated Union command, pEvalUnion, is returned to the user.
  * The monadic function map was implemented to apply a predefined set of transformer functions to partially evaluated commands
  * UnionTransformer, DifferenceTransformer, and SymmetricTransformer were implemented to further simplify partially evaluated commands
    * For example, UnionTransformer reduces a Union of two sets with the same name to the same set, because the Union of 2 identical sets is an idempotent operation.

There are 50 commented tests that demonstrate how to use the language.
The history of the project can be explored by switching to earlier branches. 
Each branch contains a README that explains the new features that were implemented at that point in the project.
