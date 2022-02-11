# CS474Homework1
Cristian Trandafir

////////////////*How to set up*///////////////

Write import SetTheory.ArithExp.* at the top of your program.
This will give you access to all the commands.

////////////////*How the commands work*///////////////

Identifier has 1 parameter.
It is a string that can be used to identify a set or object depending on the context.

Assign has 2 parameters.
The first is the Identifier command for the set name.
The second is the Insert command for the object name and object.

Variable has 1 parameter.
It can be any object.

Insert has 2 parameters.
The first is an Identifier command for the object name.
The second is the object itself (can be anything).
Insert does not insert a duplicate object into the same scope.

Delete has 2 parameters.
The first is an Identifier command for the set name you want to search in for the deletion.
The second is an Identifier command with the object's name that you want to remove from the set.
Delete will throw an error is the set name or object name do not exist.

Check has 2 parameters.
The first is an Identifier command for the set name you want to check.
The second is an Identifier command for the object's name you want to check.
Check will return a success or failure message based on the object or set's (non)existence.

Macro has 2 parameters.
The first is an Identifier command for the macro name you want to create.
The second is an ArithExp command for the set of steps you want to bind the name to.

UseMacro has 1 parameter.
The first is an Identifier command for the macro name you want to use.

The Set operations are Union, Intersection, Difference, Symmetric (Difference), and (Cartesian) Product.
Each take 2 Identifiers as their parameters.
The first identifier is the name of the first set and the second identifier is the name of the second set.
Each Set operation returns a Map representation of the set contents (strings mapped to objects).

////////////////*How the Set commands are implemented*///////////////

There is a "setMap" data structure that I use as a 2D Mutable Map.
In the first dimension, there is a mapping between set names and sets (These are implemented as strings and maps respectively).
In the second dimension, there is a mapping between object names and objects within the parent set/map.
The object names and objects are mapped as (String->Any).

The Set commands create a new map.
The new map is copied with the contents of the first map.
A second new map is created.
The second new map is copied with the contents of the second map.
Then 2 sets are instantiated.
Scala’s in-built conversion commands are used to convert the 2 maps to 2 sets.
Then Scala's in-built set commands are used to get the desired result (for example, set1 union set2).
Then the resulting set is converted back to a mutable map and the map is returned.

Macro is implemented in the same way with a macroMap of (String->ArithExp) mappings.

////////////////*Limitations*///////////////

The conversion from maps to sets back to maps will be very costly when the amount of items stored in each map is large.

I did not implement Scope.