# CS474Homework1
Cristian Trandafir

////////////////*How to set up*///////////////

Type "import SetTheory.ArithExp.*" at the top of your program.
This will give you access to all of the commands.

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