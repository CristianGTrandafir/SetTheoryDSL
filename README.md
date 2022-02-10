# CS474Homework1

////////////////*How the commands work*///////////////

Assign has 2 parameters.
The first is the Identifier command for the set name.
The second is the Insert command for the object name and object.

Identifier has 1 parameter.
It is a string that can be used to identify a set or object depending on the context.

Insert has 2 parameters.
The first is the object name as a string.
The second is the object itself (can be anything).
Insert displays a success or failure message based on whether that set already has an object with the same name.

Delete has 2 parameters.
The first is an Identifier command for the set name you want to search in for the deletion.
The second is an Identifier command with the object's name that you want to remove from the set.
Delete will return a success or failure message based on whether the object name or set name exist.

Check has 2 parameters.
The first is an Identifier command for the set name you want to check.
The second is an Identifier command for the object's name you want to check.
Check will return a success or failure message based on the object or set's (non)existence.

The Set operations are Union, Intersection, Difference, Symmetric (Difference), and (Cartesian) Product.
Each take 2 Identifiers as their parameters. 
The first identifier is the name of the first set and the second identifier is the name of the second set.

////////////////*How the Set commands are implemented*///////////////

There is a "MapOfMaps" data structure that is a 2D Mutable Map.
In the first dimension, there is a mapping between set names and sets (These are implemented as maps).
In the second dimension, there is a mapping between object names and objects within the parent "set" (really a map).

The Set commands create a new map.
The new map is copied with the contents of the first set.
A second new map is created.
The second new map is copied with the contents of the second set.
Then 2 sets are instantiated with their respective elements.
Then Scala's in-built set commands are used to get the desired result.
Then the set is converted back to a mutable map and the map is returned.

////////////////*Limitations*///////////////

