//Cristian Trandafir

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetTheory.ArithExp.*
import SetTheory.ArithExp
import SetTheory.AccessModifier.*
import SetTheory.IF
import scala.collection.mutable
import SetTheory.classMap
import SetTheory.objectMap
import SetTheory.setMap
import SetTheory.interfaceMap
import SetTheory.exceptionMap
import SetTheory.scopeWithExceptionMap
//I made these 4 data structures public so I could complete the testing cases.

class SetTheoryTests extends AnyFlatSpec with Matchers {

  //////////////////////////////////////////////////////////////////////////////////HW1 Tests

  //Test 1
  behavior of "my first language for set theory operations 1"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval
    Check(Identifier("someSetName"), Identifier("var")).eval shouldBe "Set " + "someSetName" + " does contain " + "var" + "."
  }

  //Test 2
  behavior of "my first language for set theory operations 2"

  it should "create a set and remove and object from it" in {
    Delete(Identifier("someSetName"), Identifier("var")).eval shouldBe "Successful deletion of var from someSetName."
  }

  //Test 3
  behavior of "my first language for set theory operations 3"

  it should "create a macro and use it" in {
    Macro(Identifier("testMacro"), Assign(Identifier("someSetName2"), Insert(Identifier("var2"), Variable(1)))).eval
    UseMacro(Identifier("testMacro")).eval
    Check(Identifier("someSetName2"), Identifier("var2")).eval shouldBe "Set " + "someSetName2" + " does contain " + "var2" + "."
  }

  //Test 4
  behavior of "my first language for set theory operations 4"

  it should "use Union on 2 sets" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval
    val testUnion = Union(Identifier("someSetName"), Identifier("someSetName2")).eval
    val hash = Map[String, Any]("var" -> 1, "var2" -> 1)
    testUnion shouldBe hash
  }

  //Test 5
  behavior of "my first language for set theory operations 5"

  it should "perform cartesian product on 2 sets" in {
    Assign(Identifier("set1"), Insert(Identifier("var1"), Variable(1))).eval
    Assign(Identifier("set2"), Insert(Identifier("var2"), Variable(1))).eval
    Assign(Identifier("set1"), Insert(Identifier("var3"), Variable(1))).eval
    Assign(Identifier("set2"), Insert(Identifier("var4"), Variable(1))).eval
    val p = Product(Identifier("set1"), Identifier("set2")).eval
    val hash = Map[String, Any]("var3var2" -> (1, 1), "var1var2" -> (1, 1), "var3var4" -> (1, 1), "var1var4" -> (1, 1))
    p shouldBe hash
  }

  //Test 6
  behavior of "my first language for set theory operations 6"

  it should "perform arithexp assignments with scope" in {
    Assign(Identifier("set10"), Insert(Identifier("var1"), Variable(1))).eval
    Assign(Identifier("set20"), Insert(Identifier("var2"), Variable(1))).eval
    Assign(Identifier("set10"), Insert(Identifier("var3"), Variable(1))).eval
    Assign(Identifier("set20"), Insert(Identifier("var4"), Variable(1))).eval
    Scope(Identifier("newScope3"), Identifier("main"), Assign(Identifier("set30"), Insert(Identifier("var4"), Variable(1))), 0).eval
    //set10 is in parent scope of set30. This should throw an error if it fails - success if program checks parent scopes.
    Scope(Identifier("newScope3"), Identifier("main"), Union(Identifier("set10"), Identifier("set30")), 0).eval
    Scope(Identifier("newScope3"), Identifier("main"), Check(Identifier("newScope3"), Identifier("set30")), 0).eval shouldBe "Set " + "newScope3" + " does contain " + "set30" + "."
    Scope(Identifier("newScope3"), Identifier("main"), Delete(Identifier("newScope3"), Identifier("set30")), 0).eval shouldBe "Successful deletion of " + "set30" + " from " + "newScope3" + "."
    //Anonymous scope
    Scope(Identifier(""), Identifier("main"), Assign(Identifier("set30"), Insert(Identifier("var4"), Variable(1))), 0).eval
    Scope(Identifier(""), Identifier("main"), Check(Identifier(""), Identifier("set30")), 0).eval shouldBe "Set " + "" + " does contain " + "set30" + "."
  }

  //Test 7
  behavior of "my first language for set theory operations 7"

  it should "check if a nested scope contains a set" in {
    Assign(Identifier("set200"), Insert(Identifier("var2"), Variable(1))).eval
    Scope(Identifier("newScope30"), Identifier("main"), Assign(Identifier("set200"), Insert(Identifier("var3"), Variable(1))), 0).eval
    Scope(Identifier("newScope40"), Identifier("main.newScope30"), Assign(Identifier("set300"), Insert(Identifier("var4"), Variable(1))), 0).eval
    Scope(Identifier("newScope40"), Identifier("main.newScope30"), Check(Identifier("newScope40"), Identifier("set300")), 0).eval shouldBe "Set " + "newScope40" + " does contain " + "set300" + "."
    //newScope30 is in the parent scope of newScope40
    Scope(Identifier("newScope40"), Identifier("main.newScope30"), Check(Identifier("newScope30"), Identifier("set200")), 0).eval shouldBe "Set " + "newScope30" + " does contain " + "set200" + "."
  }

  ///////////////////////////////////////////////////////////////////////////////HW2 Tests

  //Test 8
  behavior of "Extends"

  it should "define a new class, define a class that extends the first class, then instantiate an object" in {

    ClassDef("TestClass",
      Array[Field](Field("a", Private()), Field("b", Public()), Field("c", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("c", 5), FieldAssign("b", 10), FieldAssign("a", 0))),
      Array[Method](Method("method1", Public(), Array[ArithExp](Assign(Identifier("set15"), Insert(Identifier("var15"), Variable(1))), Macro(Identifier("testMacro"), Assign(Identifier("someSetName2"), Insert(Identifier("var2"), Variable(1)))))),
        Method("method3", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0).eval

    ClassDef("TestClass2",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("c", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Extends "TestClass"

    NewObject("TestClass2", "randomName2").eval

    objectMap.contains("randomName2") shouldBe true
    classMap.contains("TestClass") shouldBe true
    classMap.contains("TestClass2") shouldBe true
  }

  //Test 9
  behavior of "invoking public() parent method"

  it should "invoke TestClass inherited method on TestClass2 object" in {
    InvokeMethod("randomName2", "method1").eval
    Check(Identifier("set15"), Identifier("var15")).eval shouldBe "Set set15 does contain var15."
  }

  //Test 10
  behavior of "invoking private() parent method"

  it should "throw a RuntimeException" in {
    a[RuntimeException] should be thrownBy InvokeMethod("randomName2", "method3").eval
  }

  //Test 11
  behavior of "invoking private() parent field"

  it should "throw a RuntimeException" in {
    ClassDef("TestClass3",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      //Accesses private field "a" in TestClass
      Constructor(Array[FieldAssign](FieldAssign("a", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Extends "TestClass"
    a[RuntimeException] should be thrownBy NewObject("TestClass3", "randomName3").eval
  }

  //Test 12
  behavior of "accessing public() parent field"

  it should "set field b to 5000 in parent class" in {
    ClassDef("TestClass6",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      //Accesses public() field "b" in TestClass
      Constructor(Array[FieldAssign](FieldAssign("b", 5000))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Extends "TestClass"
    NewObject("TestClass6", "object6").eval
    //object6's field b should be 5000
    objectMap("object6").asInstanceOf[mutable.Map[String, mutable.Map[String, mutable.Map[String, Int]]]]("fields")("public")("b") shouldBe 5000
  }

  //Test 13
  behavior of "Nested classes"

  it should "create a nested class" in {
    ClassDef("TestClass4",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("d", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      ClassDef("TestClass5",
        Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
        Constructor(Array[FieldAssign](FieldAssign("a", 5))),
        Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
        0, 0)
      , 0).eval
    //Check if TestClass4 has reference to TestClass5 inside of it
    classMap("TestClass4").asInstanceOf[mutable.Map[String, Any]]("nestedC").asInstanceOf[(String, Any)]._1 shouldBe "TestClass5"
  }
  ///////////////////////////////////////////////////////////////////////////////HW3 Tests

  //Test 14
  behavior of "Abstract Class"

  it should "define an abstract class normally" in {
    AbstractClassDef("TestClassA",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("d", 5))),
      //Abstract method with no body
      Array[Method](Method("method2", Abstract(), Array[ArithExp]())),
      0, 0).eval
    classMap.contains("TestClassA") shouldBe true
  }

  //Test 15
  behavior of "Abstract Class"

  it should "throw an error when attempted to be instantiated" in {
    AbstractClassDef("TestClassB",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("d", 5))),
      //Abstract method with no body
      Array[Method](Method("method2", Abstract(), Array[ArithExp]())),
      0, 0).eval
    a[RuntimeException] should be thrownBy NewObject("TestClassB", "abstractInstantiation").eval
  }

  //Test 16
  behavior of "Concrete Class Defining Abstract Method With No Body"

  it should "throw an error when attempted to be defined" in {

    a[RuntimeException] should be thrownBy ClassDef("TestClassD",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("d", 5))),
      //Abstract method with no body
      Array[Method](Method("method2", Abstract(), Array[ArithExp]())),
      0, 0).eval
  }

  //Test 17
  behavior of "Concrete Class overriding abstract method"

  it should "allow class that implements abstract method to be instantiated" in {
    AbstractClassDef("TestClassC",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("d", 5))),
      //Abstract method with no body
      Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp]())),
      0, 0).eval

    ClassDef("ConcreteImplementer",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("e", 5))),
      //Overridden abstract method
      Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp](Assign(Identifier("setC"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Extends "TestClassC"

    NewObject("ConcreteImplementer", "ConcreteVar").eval
    InvokeMethod("ConcreteVar", "abstractMethod").eval

    Check(Identifier("setC"), Identifier("var20")).eval shouldBe "Set setC does contain var20."
  }

  //Test 18
  behavior of "Interface"

  it should "be declared normally" in {
    InterfaceDecl("TestInterface",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      //Default method
      Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
      0, 0).eval

    interfaceMap.contains("TestInterface") shouldBe true
  }

  //Test 19
  behavior of "Interface"

  it should "throw an error for defining non-abstract method" in {
    a[RuntimeException] should be thrownBy InterfaceDecl("TestInterface1",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      //Public method throws error
      Array[Method](Method("abstractMethod", Public(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
      0, 0).eval
  }

  //Test 20
  behavior of "Interface implementing another interface"

  it should "throw an error for not using extends" in {
    a[RuntimeException] should be thrownBy (InterfaceDecl("TestInterface1",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
      0, 0) Implements "TestInterface")
  }

  //Test 21
  behavior of "Class implementing an interface"

  it should "gain access to its methods" in {
    ClassDef("ConcreteImplementer2",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("e", 5))),
      //Implements abstract method
      Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp](Assign(Identifier("setIm"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Implements "TestInterface"

    classMap("ConcreteImplementer2").asInstanceOf[mutable.Map[String, Any]]("parents").asInstanceOf[Array[String]](0) shouldBe "TestInterface"
  }

  //Test 22
  behavior of "Class implementing an interface"

  it should "throw an error due to not implementing the interface's abstract method" in {
    ClassDef("ConcreteImplementer3",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("e", 5))),
      //Doesn't implement abstract method
      Array[Method](Method("concreteMethod", Public(), Array[ArithExp](Assign(Identifier("setIm"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Implements "TestInterface"

    a[RuntimeException] should be thrownBy NewObject("ConcreteImplementer3", "randomName").eval
  }

  //Test 23
  behavior of "Class Nesting"

  it should "nest multiple combinations of classes and interfaces" in {
    ClassDef("Nesting",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("e", 5))),
      Array[Method](Method("concreteMethod", Public(), Array[ArithExp](Assign(Identifier("setIm"), Insert(Identifier("var20"), Variable(1)))))),
      //Nested Class
      ClassDef("NestingClass1",
        Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
        Constructor(Array[FieldAssign](FieldAssign("e", 5))),
        Array[Method](Method("concreteMethod", Public(), Array[ArithExp](Assign(Identifier("setIm"), Insert(Identifier("var20"), Variable(1)))))),
        0,
        //Nested Interface within a Nested Class
        InterfaceDecl("TestInterface2",
          Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
          Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
          0,
          0)
      ),
      //Nested Interface
      InterfaceDecl("TestInterface1",
        Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
        Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
        //Nested Class within a Nested Interface
        ClassDef("NestingClass2",
          Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
          Constructor(Array[FieldAssign](FieldAssign("e", 5))),
          Array[Method](Method("concreteMethod", Public(), Array[ArithExp](Assign(Identifier("setIm"), Insert(Identifier("var20"), Variable(1)))))),
          0,
          0),
        //Nested interface within a Nested Interface
        InterfaceDecl("TestInterface3",
          Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
          Array[Method](Method("abstractMethod", Abstract(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
          0,
          0)
      )
    ).eval

    classMap("Nesting").asInstanceOf[mutable.Map[String, Any]]("nestedC").asInstanceOf[(String, Any)]._1 shouldBe "NestingClass1"
    classMap("Nesting").asInstanceOf[mutable.Map[String, Any]]("nestedC").asInstanceOf[(String, mutable.Map[String, Any])]._2("nestedI").asInstanceOf[(String, Any)]._1 shouldBe "TestInterface2"
    classMap("Nesting").asInstanceOf[mutable.Map[String, Any]]("nestedI").asInstanceOf[(String, Any)]._1 shouldBe "TestInterface1"
    classMap("Nesting").asInstanceOf[mutable.Map[String, Any]]("nestedI").asInstanceOf[(String, mutable.Map[String, Any])]._2("nestedC").asInstanceOf[(String, Any)]._1 shouldBe "NestingClass2"
    classMap("Nesting").asInstanceOf[mutable.Map[String, Any]]("nestedI").asInstanceOf[(String, mutable.Map[String, Any])]._2("nestedI").asInstanceOf[(String, Any)]._1 shouldBe "TestInterface3"
  }

  //Test 24
  behavior of "Interface Extending another Interface"

  it should "work normally" in {
    InterfaceDecl("TestInterface5",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Array[Method](Method("random", Abstract(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
      0, 0) Extends "TestInterface"

    interfaceMap("TestInterface5").asInstanceOf[mutable.Map[String, Array[String]]]("parents")(0) shouldBe "TestInterface"
    //Checks if the interface has the "abstractMethod" that it should inherit from TestInterface
    interfaceMap("TestInterface5").asInstanceOf[mutable.Map[String, mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]]("methods")("abstract").contains("abstractMethod") shouldBe true
  }

  //Test 25
  behavior of "Interface Extending a Class"

  it should "throw an error" in {
    a[RuntimeException] should be thrownBy (InterfaceDecl("TestInterface7",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Array[Method](Method("random", Abstract(), Array[ArithExp](Assign(Identifier("setI"), Insert(Identifier("varI"), Variable(1)))))),
      0, 0) Extends "ConcreteImplementer")


  }

  //Test 26
  behavior of "Class Implementing a Class"

  it should "throw an error" in {
    a[RuntimeException] should be thrownBy (ClassDef("TestClass10",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("c", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Implements "ConcreteImplementer")
  }

  //Test 27
  behavior of "Class Extending Itself"

  it should "throw an error" in {
    a[RuntimeException] should be thrownBy (ClassDef("TestClass100",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("c", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Extends "TestClass100")
  }

  //Test 28
  behavior of "Abstract Class implementing an interface"

  it should "work normally" in {
    AbstractClassDef("AbstractClassImpl",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("e", 5))),
      //Doesn't implement abstract method
      Array[Method](Method("concreteMethod", Public(), Array[ArithExp](Assign(Identifier("setIm"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Implements "TestInterface"

    classMap("AbstractClassImpl").asInstanceOf[mutable.Map[String, Any]]("parents").asInstanceOf[Array[String]](0) shouldBe "TestInterface"
  }

  //Test 29
  behavior of "Abstract Class Inheriting from Concrete Class"

  it should "work normally" in {
    AbstractClassDef("NormalDef1",
      Array[Field](Field("d", Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("e", 5))),
      //Doesn't implement abstract method
      Array[Method](Method("concreteMethod", Public(), Array[ArithExp](Assign(Identifier("setIm"), Insert(Identifier("var20"), Variable(1)))))),
      0, 0) Extends "ConcreteImplementer2"

    classMap("NormalDef1").asInstanceOf[mutable.Map[String, Any]]("parents").asInstanceOf[Array[String]](0) shouldBe "ConcreteImplementer2"
  }

  ///////////////////////////////////////////////////////////////////////////////HW4 Tests

  //Test 30
  behavior of "If statement with true condition"

  it should "evaluate first clause" in {

    Assign(Identifier("sampleSet"), Insert(Identifier("sampleVar"), Variable("Anything"))).eval

    //Condition is true
    IF(Check(Identifier("sampleSet"), Identifier("sampleVar")).eval == "Set " + "sampleSet" + " does contain " + "sampleVar" + ".",
      Assign(Identifier("trueSet"), Insert(Identifier("trueVar"), Variable("Anything"))).eval,
      Assign(Identifier("falseSet"), Insert(Identifier("falseVar"), Variable("Anything"))).eval,
    )

    Check(Identifier("trueSet"), Identifier("trueVar")).eval shouldBe "Set " + "trueSet" + " does contain " + "trueVar" + "."
    Check(Identifier("falseSet"), Identifier("falseSet")).eval shouldBe "Set " + "falseSet" + " does not exist."
  }

  //Test 31
  behavior of "If statement with false condition"

  it should "evaluate second clause" in {

    IF(false,
      Assign(Identifier("trueSet2"), Insert(Identifier("trueVar"), Variable("Anything"))).eval,
      Assign(Identifier("falseSet2"), Insert(Identifier("falseVar"), Variable("Anything"))).eval,
    )

    Check(Identifier("falseSet2"), Identifier("falseVar")).eval shouldBe "Set " + "falseSet2" + " does contain falseVar."
    Check(Identifier("trueSet2"), Identifier("trueVar")).eval shouldBe "Set " + "trueSet2" + " does not exist" + "."
  }

  //Test 32
  behavior of "ExceptionClassDef"

  it should "define exception in map" in {
    ExceptionClassDef("testException", "field").eval
    exceptionMap("testException") shouldBe "field"
  }

  //Test 33
  behavior of "ExceptionClassDef"

  it should "throw an error because of duplicate definition" in {
    a[RuntimeException] should be thrownBy ExceptionClassDef("testException", "field").eval
  }

  //This test fails because now ThrowException gets partially evaluated
  //Test 34
  behavior of "ThrowException"

  it should "throw an error because of undefined exception" in {
    val exception = the[RuntimeException] thrownBy ThrowException("testException2").eval
    exception.getMessage should equal("Can't throw exception that is undefined.")
  }

  //Test 35
  behavior of "ThrowException"

  it should "throw an error because catch isn't defined in any scope" in {
    val exception = the[RuntimeException] thrownBy ThrowException("testException").eval
    exception.getMessage should equal("No catch block found.")
  }

  //Test 36
  behavior of "CatchException"

  it should "define a try/catch in the main scope" in {
    Scope(Identifier("main"), Identifier("main"), Assign(Identifier("set000"), Insert(Identifier("var4"), Variable(1))),
      CatchException("testException",
        Array[ArithExp](Assign(Identifier("try"), Insert(Identifier("tryVar"), Variable(1)))),
        Array[ArithExp](Assign(Identifier("catch"), Insert(Identifier("catchVar"), Variable(1))))
      )
    ).eval
    //What's stored in the main scope for the testException catch block should be the catch block Above (array of ArithExp commands)
    scopeWithExceptionMap("main")("testException") shouldBe Array[ArithExp](Assign(Identifier("catch"), Insert(Identifier("catchVar"), Variable(1))))
  }

  //Test 37
  behavior of "CatchException"

  it should "throw an exception and catch it" in {
    Scope(Identifier("main"), Identifier("main"), Assign(Identifier("setter"), Insert(Identifier("var4"), Variable(1))),
      CatchException("testException",
        Array[ArithExp](Assign(Identifier("try2"), Insert(Identifier("tryVar2"), Variable(1))),
          ThrowException("testException")),
        Array[ArithExp](Assign(Identifier("catch"), Insert(Identifier("catchVar"), Variable(1))))
      )
    ).eval
    //Command in the catch block should have instantiated set "catch"
    Check(Identifier("main"), Identifier("catch")).eval shouldBe "Set " + "main" + " does contain " + "catch" + "."
  }

  //Test 38
  behavior of "CatchException in parent scope"

  it should "throw an exception in child scope and catch it in parent scope" in {
    Scope(Identifier("main"), Identifier("main"), Assign(Identifier("se"), Insert(Identifier("var4"), Variable(1))),
      CatchException("testException",
        Array[ArithExp](Assign(Identifier("try23"), Insert(Identifier("tryVar23"), Variable(1)))),
        Array[ArithExp](Assign(Identifier("catchParent"), Insert(Identifier("catchVar"), Variable(1))))
      )
    ).eval

    Scope(Identifier("test"), Identifier("main"), ThrowException("testException"), 0).eval

    //Parent scope catch block should have instantiated set "catchParent"
    Check(Identifier("main"), Identifier("catchParent")).eval shouldBe "Set main does contain catchParent."
  }

  //Test 39
  behavior of "CatchException"

  it should "not execute the command after ThrowException" in {
    Scope(Identifier("main"), Identifier("main"), Assign(Identifier("s"), Insert(Identifier("var4"), Variable(1))),
      CatchException("testException",
        Array[ArithExp](
          Assign(Identifier("t"), Insert(Identifier("t2"), Variable(1))),
          ThrowException("testException"),
          Assign(Identifier("afterThrow"), Insert(Identifier("tryVar2"), Variable(1)))
        ),
        Array[ArithExp](Assign(Identifier("catch2"), Insert(Identifier("catchVar"), Variable(1))))
      )
    ).eval
    //Check that command before throws was executed
    Check(Identifier("main"), Identifier("t")).eval shouldBe "Set " + "main" + " does contain " + "t" + "."
    //Check that command after throws was not executed
    Check(Identifier("main"), Identifier("afterThrow")).eval shouldBe "Set " + "main" + " does not contain " + "afterThrow" + "."
  }

  //Test 40
  behavior of "CatchException calling ThrowException in catch block"

  it should "propagate to parent scopes normally" in {
    ExceptionClassDef("childException", "test").eval
    ExceptionClassDef("parentException", "test").eval

    Scope(Identifier("main"), Identifier("main"), Assign(Identifier("Not important"), Insert(Identifier("NA"), Variable(1))),
      CatchException("parentException",
        Array[ArithExp](),
        Array[ArithExp](Assign(Identifier("caughtFromChildCatch"), Insert(Identifier("catchVar"), Variable(1))))
      )
    ).eval

    Scope(Identifier("nestedScope1"), Identifier("main"), Assign(Identifier("Not important"), Insert(Identifier("NA"), Variable(1))),
      CatchException("childException",
        //This try block will throw an exception that will be caught in current scope
        Array[ArithExp](ThrowException("childException")),
        //This catch block will catch the above exception then throw an exception that only the parent scope has registered
        Array[ArithExp](ThrowException("parentException"))
      )
    ).eval

    //Parent scope catch block should have instantiated set "caughtFromChildCatch"
    Check(Identifier("main"), Identifier("caughtFromChildCatch")).eval shouldBe "Set main does contain caughtFromChildCatch."
  }

  //Test 41
  behavior of "If statement with int instead of ArithExp in catch block"

  it should "not compile" in {

    //Condition is true
    "IF(Check(Identifier('sampleSet''), Identifier('sampleVar')).eval,Assign(Identifier('trueSet''), Insert(Identifier('trueVar''), Variable('Anything''))).eval,0)" shouldNot compile

  }

  ///////////////////////////////////////////////////////////////////////////////HW5 Tests

  //Test 42
  behavior of "Partial evaluation of Delete"

  it should "be equal to pEvalDelete" in {
    Delete(Identifier("RandomUndefinedVariable"), Identifier("RandomUndefinedSet")).eval shouldBe pEvalDelete("RandomUndefinedVariable", "RandomUndefinedSet").eval
  }

  //Test 43
  behavior of "Partial evaluation of Union first parameter"

  it should "be equal to pEvalUnion" in {
    Assign(Identifier("realSet"), Insert(Identifier("var"), Variable(1))).eval
    Union(Identifier("RandomUndefinedSet1"), Identifier("realSet")).eval shouldBe pEvalUnion("RandomUndefinedSet1", Identifier("realSet")).eval
  }

  //Test 44
  behavior of "Partial evaluation of Union second parameter"

  it should "be equal to pEvalUnion" in {
    Union(Identifier("realSet"), Identifier("RandomUndefinedSet2")).eval shouldBe pEvalUnion(Identifier("realSet"), "RandomUndefinedSet2").eval
  }

  //Test 45
  behavior of "Partial evaluation of Union both parameters"

  it should "be equal to pEvalUnion" in {
    Union(Identifier("RandomUndefinedSet3"), Identifier("RandomUndefinedSet4")).eval shouldBe pEvalUnion("RandomUndefinedSet3", "RandomUndefinedSet4").eval
  }

  //My eval returns Any, not ArithExp because of HW1 and HW2 commits. Hence the casting.
  //Test 46
  behavior of "Map with UnionTransformer function"

  it should "simplify a partially evaluated Union that has 2 sets with the same name" in {
    Union(Identifier("RandomUndefinedSet3"), Identifier("RandomUndefinedSet3")).eval.asInstanceOf[pEvalUnion]
      .map(e=>e.asInstanceOf[ArithExp].UnionTransformer) shouldBe Variable("RandomUndefinedSet3")
  }

  //Test 47
  behavior of "Map with IntersectionTransform function"

  it should "simplify a partially evaluated Intersection that has 2 sets with the same name" in {
    Intersection(Identifier("RandomUndefinedSet3"), Identifier("RandomUndefinedSet3")).eval.asInstanceOf[pEvalIntersection]
      .map(e=>e.asInstanceOf[ArithExp].IntersectionTransformer) shouldBe Variable("RandomUndefinedSet3")
  }

  //Test 48
  behavior of "Map with DifferenceTransformer function"

  it should "simplify a partially evaluated Difference that has 2 sets with the same name" in {
    Difference(Identifier("RandomUndefinedSet3"), Identifier("RandomUndefinedSet3")).eval.asInstanceOf[pEvalDifference]
      .map(e=>e.asInstanceOf[ArithExp].DifferenceTransformer) shouldBe Variable(mutable.Map[String, Any]("RandomUndefinedSet3" -> None))
  }

  //Test 49
  behavior of "Map with DifferenceTransformer function"

  it should "throw an error due to using map on Difference with 2 different set names" in {

    val exception = the[RuntimeException] thrownBy Difference(Identifier("RandomUndefinedSet3"), Identifier("RandomUndefinedSet4")).eval.asInstanceOf[pEvalDifference]
                                                      .map(e=>e.asInstanceOf[ArithExp].DifferenceTransformer)
    exception.getMessage should equal("Cannot transform pDifferenceIntersection with unequal parameters")
  }

  //Test 50
  behavior of "Map with DifferenceTransformer function"

  it should "throw an error due to using map on wrong type of ArithExp command" in {
    val exception = the[RuntimeException] thrownBy Union(Identifier("RandomUndefinedSet3"), Identifier("RandomUndefinedSet4")).eval.asInstanceOf[pEvalUnion]
      .map(e=>e.asInstanceOf[ArithExp].DifferenceTransformer)
    exception.getMessage should equal("Need to use differenceTransformer on pDifferenceIntersection")
  }

}
