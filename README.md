# CS474Homework1
Cristian Trandafir

//////////////////////////////////////////////////////////////////////////Homework1

////////////////*How to set up*///////////////

Type "import SetTheory.ArithExp.*" at the top of your program.
This will give you access to all of the commands.
Simply type the commands listed in the following section with their appropriate parameters, and finish the statement off by calling .eval.
You can print out the result of .eval-ing the commands for helpful information. 
Here is an example of a well-formed statement:

Assign(Identifier("Set1"), Insert(Identifier("ObjectName"), Identifier(Object))).eval

This statement will create a set Set1 with the element Object titled ObjectName.

////////////////*How the commands work*///////////////

Identifier has 1 parameter.
It is a string that can be used to identify a set or object depending on the context.

Assign has 2 parameters.
The first is the Identifier command for the set name.
The second is the Insert command for the object name and object.
Assign is used to create new sets with named elements.

Variable has 1 parameter.
It can be any object.

Insert has 2 parameters.
The first is an Identifier command for the object name.
The second is the object itself (can be anything).
Insert returns a (String, Any) tuple of the parameters passed in, so calling it by itself is pointless. 
Call it in an Assign statement.

Delete has 2 parameters.
The first is an Identifier command for the set name you want to search in for the deletion.
The second is an Identifier command with the object's name that you want to remove from the set.
Delete will throw an error if the set name or object name do not exist.
Since the addition of Scope, it is also possible to delete sets and scopes with Delete, not just set items.
Delete(Identifier("scopeName"), Identifier("setOrScope")) will delete the "setOrScope" from the program.

Check has 2 parameters.
The first is an Identifier command for the set name you want to check.
The second is an Identifier command for the object's name you want to check.
Check will return a success or failure message based on the object or set's (non)existence.

Macro has 2 parameters.
The first is an Identifier command for the macro name you want to create.
The second is any of the other commands listed here for the set of steps you want to bind the name to.

UseMacro has 1 parameter.
The first is an Identifier command for the macro name you want to use.

Scope has 3 parameters.
The first is an Identifier command for the scope name you want to create.
The second is an Identifier command for the scope hierarchy you want to create your new scope in. 
Every scope starts with the string "main".
Make sure that when you specify a scope hierarchy you delimit each instance with a "."
For example, "main.scope1.scope2".
The third is any other command listed here.

The Set operations are Union, Intersection, Difference, Symmetric (Difference), and (Cartesian) Product.
Each take 2 Identifiers as their parameters.
The first identifier is the name of the first set and the second identifier is the name of the second set.
Each Set operation returns a Map representation of the set contents (strings mapped to objects).

////////////////*How the Set commands are implemented*///////////////

There is a setMap data structure that is a mapping of Strings to Anys.
The way sets are inserted into this data structure is straightforward.
Consider the following command: 
Assign(Identifier("set10"), Insert(Identifier("var1"), Variable(1)))
At the first layer, the set name "set10" is mapped to its contents.
Its contents are composed of tuples of variable names mapped to variables.
Note that scoping is covered in the "Scope Implementation" section.

The Set commands create a new map.
The new map is copied with the contents of the first map.
A second new map is created.
The second new map is copied with the contents of the second map.
Then 2 sets are instantiated.
Scala’s in-built conversion commands are used to convert the 2 maps to 2 sets.
Then Scala's in-built set commands are used to get the desired result (for example, set1 union set2).
Then the resulting set is converted back to a mutable map and the map is returned.

Macro is implemented in the same way with a macroMap of (String->ArithExp) mappings.

////////////////*Scope Implementation*///////////////

Scopes are treated like sets. 
The universal scope that every set is a part of is called "main".
It is the first element in the setMap. 
Every element added to setMap must be a submap of main.

scopeMap keeps a mapping of the scope hierarchy. main is mapped to main. main.scope1 would be mapped to main. main.scope1.scope2 would be mapped to main.scope1 and so on.
currentScope keeps a mapping of "current" to the last parent scope passed in by Scope.
currentMap is a map that holds a subscope of the setMap.

When Scope is called, scopeMap, currentScope, and currentMap are all updated.

The method recursiveResolveScope() is used to navigate from the main scope of the setMap to the specified subscope by the second parameter of Insert.
The method resolveScopeToMain() is used to start from a subscope, where it checks if a set or scope name exists.
If it fails, it calls recursiveResolveScope() to get to the next parent scope, then rescan the variables.
This repeats until no object is found, in which case a new object must be created.

There is support for nested scopes and multiple scopes at the same scope level and anonymous scopes.
Scopes are just like sets except with an extra level of indirection.
Note that you cannot overwrite already existing scopes or sets with scopes.
There is a check in Scope that will throw an error if this is attempted.

////////////////*Limitations*///////////////

The conversion from maps to sets back to maps will be very costly when the amount of items stored in each map is large.

The Identifier command is redundant in almost every case. 
It is good for forcing new users to learn the language parameters and syntax, but it gets repetitive typing it out when a simple String could be just as good.

Adding scopes makes the setMap grow very fast with multiple nested layers.
The search time for each variable also increases greatly with the more nested scope you pass in.

When deleting or checking a specific scope, only the scope or set name can be checked or deleted, not the contents.
This is an unintended consequence of treating scopes and names as equivalent.
This can be fixed with an overloaded Delete or Check method that takes in an extra parameter to access the sets.
I did not implement this because it would make the program crash or cause undesired behavior.
If a user uses Delete with 3 parameters on a set, then the program would crash.
If a user uses Delete with 2 parameters on a scope, then they would inadvertently delete the entire set rather than one of the elements.

One last issue I ran into with my code was that I could never figure out how to return a specific type from case statements.
This resulted in my code being bloated with .asInstanceOf[Type] statements.

////////////////////////////////////////////////////////////////////////Homework 2

////////////////*How to set up*///////////////

New for Homework 2:

Include the import "SetTheory.ArithExp" at the top of your program. 
The previous one was only "SetTheory.ArithExp.*" which only imported the case statements like Identifier().
Adding this import is necessary because you will need to create Arrays of ArithExp, which wasn't possible with only the previous import.


////////////////*How the commands work*///////////////

Field has 2 parameters.
The first is a String for the name of the class field you want to define.
The second is a String for the access modifier of the field you want to define - "public"/"private"/"protected".

Method has 3 parameters.
The first is a String for the name of the method you want to define.
The second is a String for the access modifier of the method you want to define - "public"/"private"/"protected".
The third is an Array of ArithExp - you can input an arbitrary amount of commands from HW1 into the array to define the method body.

FieldAssign has 2 parameters.
The first is a String that specifies the field name that you want to find.
The second is any object you want to assign to that field.

Constructor has 1 parameter.
It is an Array of FieldAssigns - you can input an arbitrary amount of FieldAssigns into the constructor body that will execute when NewObject is called.

NewObject has 2 parameters.
The first is a String for the class name that you want to instantiate.
The second is a String that functions as the object name.
Instantiated objects are stored in the objectMap variable.

InvokeMethod has 2 parameters.
The first is a String for the object whose method you want to invoke.
The second is a String for the method name that you want to invoke.

ClassDef has 5 parameters.
The first is a String for the name of the class you want to define.
Next is an Array of Fields that will define all the class fields.
Next is a Constructor that will contain an Array of FieldAssigns to execute when NewObject is called on the class.
Next is an Array of Methods that will define all the class methods.
Finally, there is ClassDef parameter for defining an arbitrary amount of inner nested classes.
Simply leave any other variable like a 0 or String in the final parameter to stop the nesting at any point.
Class definitions are stored in the classMap variable.

Here is an example of a well-formed ClassDef statement:

    ClassDef("TestClass",
        Array[Field](
            Field("a","private"), Field("b", "public"), Field("c", "protected")
            ),
        Constructor(Array[FieldAssign](
            FieldAssign("c", 5), FieldAssign("b", 10), FieldAssign("a", 0))
            ),
        Array[Method](
            Method("method1", "public", Array[ArithExp](
                Assign(Identifier("set15"), Insert(Identifier("var15"), Variable(1))), 
                Macro(Identifier("testMacro"), Assign(Identifier("someSetName2"), Insert(Identifier("var2"), Variable(1)))))
                ),
            Method("method3", "private", Array[ArithExp](
                Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1))))
                )
            ),
        0).eval

Extends is an infix method.
The first parameter it takes is a new ClassDef for the class you want to define.
The second parameter is a String for the class that you want to inherit from.

////////////////*How the Class commands are implemented*///////////////

    "className" -> Map("name" -> "className"

                   "parents" -> Array[parentClassNames] immediate parent first

                   "constructor" -> Array[FieldAssign("fieldName",Any])

                   "fields" -> Map("private" -> Map("fieldName" -> Any)

                                   "public" -> Map("fieldName" -> Any)

                                   "protected" -> Map("fieldName" -> Any),

                   "methods" -> Map("protected" -> Map("methodName" -> Array[ArithExp])

                                    "private" -> Map("methodName" -> Array[ArithExp])

                                    "private" -> Map("methodName" -> Array[ArithExp])

                   "nested" -> Map("className" -> class)) //(this data structure repeats in class)

This is the data structure that classMap stores. 
It is created inside of ClassDef. 
ClassDef calls the helper method recurseAddClassToMap that recursively creates this data structure.
It is necessary for evaluating nested ClassDef calls which add an arbitrary amount of nested classes.

Field, Method, and FieldAssign are used to structure the user's input data. 
They don't return anything by themselves; it was simply easier to carry out the calculations inside of ClassDef.
Constructor only returns the Array of FieldAssigns.

NewObject and InvokeMethod use the "parents" Array above as a stand-in for the vtable.
The "parents" array simply stores Strings that keep track of the current class's parents.
For example: ["parent", "grandparent","great-grandparent"].

NewObject uses a ReverseIterator to start at the oldest ancestor, copy the ancestor's fields into the current object, and then execute the ancestor's constructor.
After this happens, the second oldest ancestor repeats the same process and the process repeats for all of the current class's parents.
There is a check to make sure that no ancestor constructor accesses its own ancestor's private methods.

InvokeMethod uses the helper method recurseClassHierarchy to start from the current class and check for the method name upwards on the class hierarchy.
Once the method is found (private ancestor methods are ignored), its ArithExp commands are executed.

NewObject and InvokeMethod implement field and method overriding, respectively. 
This is done implicitly of course, the user gets no error message.
If Ancestor A defines Field B and Method C and Child D defines Field B and Method C, the ancestor field and method are shadowed in regards to a child object.

////////////////*Limitations*///////////////

While classes can be indefinitely nested, I did not implement multiple nested classes in one class.
This could be done by passing in an Array of ClassDefs and calling recurseAddClassToMap on each element.
I did not implement this because I don't think the specification required it.
I only implemented indefinite nesting.

I limited Constructor to only execute FieldAssigns. 
I also limited Method to only execute the HW1 commands. 
This limits the flexibility of my implementation compared to normal programming languages, but I think it also makes it easier to keep track of commands.

One difficulty I had in coding this was adding varargs to case statements - it kept giving me errors and I wasn't sure how to fix it.
This resulted in ClassDef having ugly Array[] syntax so I could still support arbitrary parameters.

One note for the grader - I made setMap, objectMap, and classMap public so I could reference them from the Scalatests.
I made them public so I could access the objects and classes and test their fields with the shouldBe command.

//////////////////////////////////////////////////////////////////////////Homework3

