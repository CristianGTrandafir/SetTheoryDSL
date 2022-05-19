# CS474Homework5
Cristian Trandafir

## How to set up:

Include the imports SetTheory.AccessModifier.\*, SetTheory.ArithExp.\*, and SetTheory.ArithExp at the top of your program.
The first one, SetTheory.AccessModifiers.\*, is an enum that you need to specify the access modifiers for fields and methods.
The second one, SetTheory.ArithExp.\*, is needed so that you can use any of the ArithExp commands like Scope() or ClassDef().
The third one, SetTheory.ArithExp, is needed so you can specify the element types of the Arrays you pass in as methods (ArithExp types).

## How the new commands work:

PartialEvalCheck has 1 parameter.
This command is not called by the user, but it takes each ArithExp command that .eval is called on as a parameter.
The ArithExp command is then attempted to be partially evaluated.
True is returned if the partial evaluation was successful, false if it was unsuccessful.

Map has 1 parameter.
The parameter is supposed to take one of the predefined transformer functions and apply it to a partially evaluated ArithExp statement.
An optimized partially evaluated function is returned.

UnionTransformer has no parameters.
It reduces a Union of 2 sets with the same name to 1 set.
Variable(setName) is returned.

DifferenceTransformer has no parameters.
It reduces a Difference of 2 sets with the same name to the empty set.
mutable.Map(setName -> None) is returned.

SymmetricTransformer has no parameters.
It reduces a Symmetric of 2 sets with the same name 1 set.
Variable(setName)

Here is a well=formed map statement with an optimization transformer:

    Difference(Identifier("RandomUndefinedSet3"), Identifier("RandomUndefinedSet3")).eval
    .asInstanceOf[pEvalDifference]
    .map(e=>e.asInstanceOf[ArithExp].DifferenceTransformer)

The first line is the Difference ArithExp command. 
It will get partially evaluated to pEvalDifference("RandomUndefinedSet3", "RandomUndefinedSet3").
The second line is a cast to pEvalDifference because my eval method returns Any because of how I implemented the HW1 and HW2 solutions.
The third line is calling map and passing in an optimizing transformer function, DifferenceTransformer.
Again, a cast is necessary because my type inference for eval is too vague.

## How Partial Evaluation is implemented:

At the start of eval, there is a new check that checks if the ArithExp command can be partially evaluated.
The check is done by the method PartialEvalCheck.
True is returned if the ArithExp command can be partially evaluated, and the partially evaluated ArithExp statement is returned from eval.
If false is returned, regular evaluation occurs.

Partially evaluated statements are ArithExp statements prefixed with "pEval".
These are newly defined data types for partial evaluation.
A command of the form

    Union(Identifier("DefinedSet1"), Identifier("UndefinedSet2")).eval

will evaluate to:

    pEvalUnion(Identifier("DefinedSet1"), "UndefinedSet2")

Most ArithExp commands will be partially evaluated, but it will be simpler to list the ones that are not and the reason why not:

Variable and Identifier - these are wrappers that are only ever called as parameters to other commands.
Normal evaluation of them always returns their stored content, so there is no room for partial evaluation. 

Insert, Assign, Macro - Insert is only ever called as a parameter of Assign.
They both work to assign a definite variable. 
Macro also creates a definite Macro variable. 
So there is no room for partial evaluation in these commands.

ClassDef, AbstractClassDef, InterfaceDecl - these all define new classes/interfaces.
There is no room for partial evaluation in these commands.

ExceptionClassDef - this defines an exception.
Again, no room for partial evaluation.

Method, Constructor, Field - these are only ever used as parameters when defining a class or interface.
Since they define methods, constructors, and fields, there is no room for partial evaluation.

Every other command will partially evaluate to pEval + the command name, with the defined variable's structure preserved and the undefined variable reduced to its String name.
Each pEval + command name data type has parameters of type Any, so multiple parameters can be partially evaluated and in any order to the same pEval + command name.
When eval is called on a partially evaluated command, it simply returns itself.

## Limitations

I did not use case classes in my project so I could not copy the parameterized type map from the WrapperContainer.scala sample code that was designed in class.
The command still works, but I followed the definition from the HW5 pdf instead.

My eval statement returns Any, not Set[Any] because of previous implementation decisions I made in HW1 and HW2.
This results in ugly casting needed to make use of map.

Note to the grader: I made various data structures that should be private public so that I could easily reference them in the Scalatests. 
They are: classMap, objectMap, setMap, interfaceMap, exceptionMap, and scopeWithExceptionMap.

Each branch on my Github has my past readmes corresponding to that homework assignment. 
I removed the past command definitions from this one to make it more concise.
Everything else about my language can be found in my HW1/2/3/4 readmes.
