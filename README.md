# CS474Homework4
Cristian Trandafir

## How to set up:

Include the imports SetTheory.AccessModifier.\*, SetTheory.ArithExp.\*, and SetTheory.ArithExp at the top of your program.
The first one, SetTheory.AccessModifiers.\*, is an enum that you need to specify the access modifiers for fields and methods.
The second one, SetTheory.ArithExp.\*, is needed so that you can use any of the ArithExp commands like Scope() or ClassDef().
The third one, SetTheory.ArithExp, is needed so you can specify the element types of the Arrays you pass in as methods (ArithExp types).

## How the commands work:

### Updated old commands:

Scope has 4 parameters.
The first is an Identifier* command for the scope name you want to create.
The second is an Identifier* command for the scope hierarchy you want to create your new scope in.
The third is an ArithExp command.
The new fourth is an optional parameter for CatchException, which sets up the equivalent of a try/catch block and registers the scope to catch a thrown exception.

\*Identifier has 1 parameter.
It is simply a string that is used to name something.

### New commands:

IF has 3 parameters. 
The first is a boolean condition that is analogous to if(condition) in Java.
The second is an ArithExp expression that will be lazily evaluated if the condition evaluates to true.
The third is an ArithExp expression that will be lazily evaluated if the condition evaluates to fasle.

ExceptionClassDef has 2 parameters.
The first is a string to name the exception class.
The second is a string for the field of the exception class.

ThrowException has 1 parameter.
It is a string for the type of exception to throw.

CatchException has 3 parameters.
The first is a string for the exception to catch in the catch block.
The second is an Array of ArithExp commands that will be lazily evaluated analogously to sequential execution of a try block.
The third is an Array of ArithExp commands that will be lazily evaluated analogously to sequential execution of a catch block (provided that it catches a thrown exception).

Information on other commands can be found in previous homeworks' readmes.

## How IF and the Exceptions are implemented:

All of the parameters to IF are call by name.
IF functions like in any other language - it evaluates a condition to true or false, then executes the true block or false block of code depending on the condition value.
Here is a sample usage of IF:

    IF (
 
        1+1 == 2,     //Boolean expression

        Assign(Identifier("trueSet"), Insert(Identifier("trueVar"), Variable(True))).eval, //True branch

        Assign(Identifier("falseSet"), Insert(Identifier("falseVar"), Variable(False))).eval   //False branch

    )

Because the expressions are lazily evaluated, calling .eval does nothing until the boolean expression is evaluated inside the function call and either the true or false branch runs.

exceptionMap is a new variable that maps exceptions defined by ExceptionClassDef to their fields. 
Calling ExceptionClassDef creates a new addition to this map, unless the exception name is already defined, in which case a RuntimeException is thrown to the user to use a different name.

scopeWithExceptionMap is a new variable that maps scopes to the exceptions they catch.

    "scopeName" -> Map("exceptionName1" -> Array[ArithExpCommands]),
                      ("exceptionName2" -> Array[ArithExpCommands])
    "scopeName2" -> Map("exceptionName4" -> Array[ArithExpCommands])

Each scope is mapped to another map.
This second map contains the exception names as keys and the catch blocks within the scope as values.

Upon CatchException.eval, only the try block executes right away.
Each CatchException definition "registers" a scope with an exceptionName and a catch block (each instance of "Array[ArithExpCommands]").
The same scope can be registered with multiple exception types.
Each exception type has its own catch block.

ThrowException throws an exception from the exceptionMap (test 34 shows the RuntimeException thrown if exception is not defined in exceptionMap).
Once the exception is thrown in a scope, the helper method recurseCheckForException is called.
The helper method checks the current scope for a catch block of the specified exception, then recursively checks each parent scope until it reaches the main scope (final scope level).
If the exception is still not handled after main is checked, a RuntimeException is thrown to the user (test 35).
Test 38 showcase throwing an exception from a nested scope.

There is also another helper method, executeCommands that is called whenever a try or catch block executes.
It recursively calls itself and evals each command, until a ThrowException is matched.
If ThrowException is identified, then the helper method executes the ThrowException and returns (test 39).
This means that commands after any ThrowException don't get executed.
This means that both try and catch blocks can throw exceptions (test 40) and operate like in Java.

Here is a sample of all 3 exception enums:

    1 ExceptionClassDef("testException", "field")
    2 Scope(Identifier("main"), Identifier("main"), Assign(Identifier("se"), Insert(Identifier("var4"), Variable(1))),
    3     CatchException("testException",
    4     Array[ArithExp](Assign(Identifier("try23"), Insert(Identifier("tryVar23"), Variable(1)))),
    5     Array[ArithExp](Assign(Identifier("catchParent"), Insert(Identifier("catchVar"), Variable(1))))
    6     )
    7 ).eval
    8 Scope(Identifier("test"), Identifier("main"), ThrowException("testException"), 0).eval

The first line is a definition of testException.
The second line is the HW1 Scope definition.
The third line is the start of the CatchException parameter to Scope, containing the exception name to catch.
The fourth line is the try block.
The fifth line is the catch block.
The eighth line is throwing an exception from a parent scope ("main.test").

The program will throw the exception, check the current scope ("main.test") to see if it's registered to an exception and catch block, fail, then check the parent scope ("main") for the exception and catch block, succeed, then execute main's testException catch block.

## Limitations

I designed the IF statement to only take in 1 ArithExp expression for the true branch and for the false branch.
This would be inconvenient to users of the language who want to include multiple statements in the branches.
I did this not for any design considerations, but to get myself familiar with the => operator in function calls.
I researched and found that you can't mix varargs with call by name in Scala.

Note to the grader: I made various data structures that should be private public so that I could easily reference them in the Scalatests. 
They are: classMap, objectMap, setMap, interfaceMap, exceptionMap, and scopeWithExceptionMap.
