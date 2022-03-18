# CS474Homework3
Cristian Trandafir

### New for Homework 3:

I removed the readme text from HW1 and HW2 - they can be found under the HW1 and HW2 branches.
I tried to include all the relevant details from the past readmes in this one.

##How to set up:

Include the imports SetTheory.AccessModifier.\*, SetTheory.ArithExp.\*, and SetTheory.ArithExp at the top of your program.
The first one, SetTheory.AccessModifiers.\*, is a new enum that you need to specify the access modifiers for fields and methods.
The second one, SetTheory.ArithExp.\*, is needed so that you can use any of the ArithExp commands like Scope() or ClassDef().
The third one, SetTheory.ArithExp, is needed so you can specify the element types of the Arrays you pass in as methods (ArithExp types).

##How the new commands work:

####Old commands for reference:

Field has 2 parameters. The first is a String for the name of the class field you want to define. The second is a String for the access modifier of the field you want to define - "public"/"private"/"protected".

Method has 3 parameters. The first is a String for the name of the method you want to define. The second is a String for the access modifier of the method you want to define - "public"/"private"/"protected". The third is an Array of ArithExp - you can input an arbitrary amount of commands from HW1 into the array to define the method body.

FieldAssign has 2 parameters. The first is a String that specifies the field name that you want to find. The second is any object you want to assign to that field.

Constructor has 1 parameter. It is an Array of FieldAssigns - you can input an arbitrary amount of FieldAssigns into the constructor body that will execute when NewObject is called.

####New commands:

Public, Private, Protected, Abstract.
These are new elements of the AccessModifier enum.
These should be specified in place of strings like "public", "private", "protected", and "abstract".

InterfaceDecl has 5 parameters.
The first is a string to name the interface.
The second is an Array of Fields.
The third is an Array of Methods.
The fourth is a nested ClassDef.
The fifth is a nested InterfaceDecl.

AbstractClassDef has 6 parameters.
The first is a string to name the abstract class.
The second is an Array of Fields.
The third is a Constructor - it contains an Array of FieldAssigns.
The fourth is an Array of Methods.
The fifth is a nested ClassDef.
The sixth is a nested InterfaceDecl.

Extends is an infix method. 
It has been updated for to meet the new specs.
An InterfaceDecl can Extend an already existing interface by specifying its name as a String.
A ClassDef or AbstractClassDef can Extend an already existing ClassDef or AbstractClassDef by specifying its name as a String.

Implements is a new infix method.
It is used by ClassDefs or AbstractClassDefs to Implement an already existing interface by specifying its name as a String.

Here is an example of a well-formed AbstractClassDef statement:

    AbstractClassDef("TestClass",
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
        0, //Nested ClassDef can go here
        1  //Nested InterfaceDecl can go here
    ).eval

This statement can include nested ClassDefs in place of the 0, or nested InterfaceDecls in place of the 1.
It can be postfixed with "Extends "ClassName"" or with "Implements "InterfaceName"".

##How the Classes and Interfaces are implemented:

    "className" -> Map("name" -> "className"

                   "parents" -> Array[parentClassNames] immediate parent first

                   "constructor" -> Array[FieldAssign("fieldName",Any])

                   "fields" -> Map("private" -> Map("fieldName" -> Any)

                                   "public" -> Map("fieldName" -> Any)

                                   "protected" -> Map("fieldName" -> Any),

                   "methods" -> Map("private" -> Map("methodName" -> Array[ArithExp])

                                    "public" -> Map("methodName" -> Array[ArithExp])

                                    "protected" -> Map("methodName" -> Array[ArithExp])
                                    
                                    "abstract" -> Map("methodName" -> Array[ArithExp])

                   "nestedC" -> Map("className" -> classDef)

                   "nestedI" -> Map("interfaceName" -> interfaceDecl) 

This is the ClassDef data structure.
One new addition is that support for nested Interfaces has been added.
The AbstractClassDef data structure is identical to this.
The InterfaceDecl data structure lacks the constructor field, and it can only have abstract methods, but it is otherwise identical.

###How abstract methods work:

Abstract is an element of the AccessModifier enum. 
It works just like the other access modifiers.
But there are additional checks within the code. 

In case 1, an abstract method is defined with no body. 
This means that the current class or interface doesn't provide a default implementation for the method.
This means that if the current class has an abstract method with no body, the class cannot be instantiated.
In order to be instantiated, the current class must provide their own (nonempty) definition for the abstract method with the AccessModifier type Abstract and the same method name - 
this is effectively "overriding" the parent's abstract method.

In case 2, an abstract method is defined with a body.
This means that there is a default implementation for it.
Any class that extends or implements this method does not need to provide their own definition for the method.
Hence, they can be instantiated normally.

##Questions:

####Can a class/interface inherit from itself?

No.
There is a check in Extends and Implements that prevents this from happening. 
See Test 27.
This would not break anything in my implementation, 
but I disallowed it because it's more likely that the user of my language doesn't want to extend the exact same class - after all, it's pointless.

####Can an interface inherit from an abstract class with all pure methods?

No.
I did not allow interfaces to Extends classes, only other interfaces.
See Test 25.
This is because my abstract classes include constructors, so having an interface Extends it would be awkward.
I would have had to ignore the constructor and include a check that all of the abstract class's methods are pure.
I don't think many users will want to do this so I disallowed it.

####Can an interface implement another interface?

No.
The documentation said this should throw an error.
See Test 20.
Although the documentation said it should throw an error, Extends and Implements are functionally the same in my code.
I could have let interfaces Implements other interfaces instead of Extends them because NewObject and MethodInvocation handle the instantiation and method logic.
Extends and Implements mainly update the parent array (my version of a vtable) to hold the parent class's name.

####Can a class implement two or more different interfaces that declare methods with exactly the same signatures?

No.
I only allowed classes to Implements a single interface.
If a parent and grandparent interface contain the same method with the same signature, then the parent's method shadows the grandparent's method.
This is how I implemented methods generally in HW2.
A class cannot Extends 2 classes or Implements 2 interfaces so no problems with diamond inheritance arise.
This is a serious limitation since most languages have some version of multiple inheritance.
But I didn't want to implement multiple inheritance because of all the extra bugs that come with it.

####Can an abstract class inherit from another abstract class and implement interfaces where all interfaces and the abstract class have methods with the same signatures?

Yes. 
The most recently defined method will simply shadow the parent methods.
This was the easiest resolution to implement.

####Can an abstract class implement interfaces?

Yes.
See Test 28.
I didn't foresee any complications by doing this so I allowed it.
Neither abstract class nor interface can be instantiated.

####Can a class implement two or more interfaces that have methods whose signatures differ only in return types?

Classes can only implement 1 interface.
I did not include the return type as part of the method signature, so can method in a child class with the same name and AccessModifier will shadow the method in the parent class.

####Can an abstract class inherit from a concrete class?

Yes.
There is no restriction on this because I allowed abstract classes to have public, private, and protected methods, as well as constructors.
See Test 29.

####Can an abstract class/interface be instantiated as anonymous concrete classes?

No.
I am inexperienced with anonymous classes so I'm not sure how they work or how I would implement them.

##Limitations

The biggest limitation is that classes cannot Implements multiple interfaces. 
Neither can classes Extends multiple classes.
So there is no mechanism for multiple inheritance in my language.
This will likely be a huge downside for my users.

Another limitation is that classes and interfaces can't have multiple nested classes on the same level.
Classes and interfaces can indefinitely nest within other classes and interfaces, but there cannot be 2 classes or 2 interfaces at the same level.
This could be fixed by creating an Array of nested classes and interfaces, but it would have complicated the code for not much more functionality.