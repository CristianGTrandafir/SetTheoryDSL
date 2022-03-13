//Cristian Trandafir

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetTheory.ArithExp.*
import SetTheory.ArithExp
import SetTheory.AccessModifier.*
import scala.collection.mutable
import SetTheory.classMap
import SetTheory.objectMap
import SetTheory.setMap
//I made these 3 data structures public() so I could complete the testing cases.

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
    val hash = Map[String, Any]("var" -> 1, "var2" ->1)
    testUnion shouldBe hash
  }

  //Test 5
  behavior of "my first language for set theory operations 5"

  it should "perform cartesian product on 2 sets" in {
    Assign(Identifier("set1"), Insert(Identifier("var1"), Variable(1))).eval
    Assign(Identifier("set2"), Insert(Identifier("var2"), Variable(1))).eval
    Assign(Identifier("set1"), Insert(Identifier("var3"), Variable(1))).eval
    Assign(Identifier("set2"), Insert(Identifier("var4"), Variable(1))).eval
    val p = Product(Identifier("set1"),Identifier("set2")).eval
    val hash = Map[String, Any]("var3var2" -> (1,1), "var1var2" -> (1,1), "var3var4" -> (1,1), "var1var4" -> (1,1))
    p shouldBe hash
  }

  //Test 6
  behavior of "my first language for set theory operations 6"

  it should "perform arithexp assignments with scope" in {
    Assign(Identifier("set10"), Insert(Identifier("var1"), Variable(1))).eval
    Assign(Identifier("set20"), Insert(Identifier("var2"), Variable(1))).eval
    Assign(Identifier("set10"), Insert(Identifier("var3"), Variable(1))).eval
    Assign(Identifier("set20"), Insert(Identifier("var4"), Variable(1))).eval
    Scope(Identifier("newScope3"),Identifier("main"), Assign(Identifier("set30"), Insert(Identifier("var4"), Variable(1)))).eval
    //set10 is in parent scope of set30. This should throw an error if it fails - success if program checks parent scopes.
    Scope(Identifier("newScope3"),Identifier("main"), Union(Identifier("set10"), Identifier("set30"))).eval
    Scope(Identifier("newScope3"),Identifier("main"), Check(Identifier("newScope3"), Identifier("set30"))).eval shouldBe "Set " + "newScope3" + " does contain " + "set30" + "."
    Scope(Identifier("newScope3"),Identifier("main"), Delete(Identifier("newScope3"), Identifier("set30"))).eval shouldBe "Successful deletion of " + "set30" + " from " + "newScope3" + "."
    //Anonymous scope
    Scope(Identifier(""),Identifier("main"), Assign(Identifier("set30"), Insert(Identifier("var4"), Variable(1)))).eval
    Scope(Identifier(""),Identifier("main"), Check(Identifier(""), Identifier("set30"))).eval shouldBe "Set " + "" + " does contain " + "set30" + "."
  }

  //Test 7
  behavior of "my first language for set theory operations 7"

  it should "check if a nested scope contains a set" in {
    Assign(Identifier("set200"), Insert(Identifier("var2"), Variable(1))).eval
    Scope(Identifier("newScope30"),Identifier("main"), Assign(Identifier("set200"), Insert(Identifier("var3"), Variable(1)))).eval
    Scope(Identifier("newScope40"),Identifier("main.newScope30"), Assign(Identifier("set300"), Insert(Identifier("var4"), Variable(1)))).eval
    Scope(Identifier("newScope40"),Identifier("main.newScope30"), Check(Identifier("newScope40"), Identifier("set300"))).eval shouldBe "Set " + "newScope40" + " does contain " + "set300" + "."
    //newScope30 is in the parent scope of newScope40
    Scope(Identifier("newScope40"),Identifier("main.newScope30"), Check(Identifier("newScope30"), Identifier("set200"))).eval shouldBe "Set " + "newScope30" + " does contain " + "set200" + "."
  }

  ///////////////////////////////////////////////////////////////////////////////HW2 Tests

  //Test 8
  behavior of "Extends"

  it should "define a new class, define a class that extends the first class, then instantiate an object" in {

    ClassDef("TestClass",
      Array[Field](Field("a",Private()), Field("b", Public()), Field("c", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("c", 5), FieldAssign("b", 10), FieldAssign("a", 0))),
      Array[Method](Method("method1", Public(), Array[ArithExp](Assign(Identifier("set15"), Insert(Identifier("var15"), Variable(1))), Macro(Identifier("testMacro"), Assign(Identifier("someSetName2"), Insert(Identifier("var2"), Variable(1)))))),
        Method("method3", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0).eval

    ClassDef("TestClass2",
      Array[Field](Field("d",Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("c", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0) Extends "TestClass"

    NewObject("TestClass2", "randomName2").eval

    objectMap.contains("randomName2") shouldBe true
    classMap.contains("TestClass") shouldBe true
    classMap.contains("TestClass2") shouldBe true
  }

  //Test 9
  behavior of "invoking public() parent method"

  it should "invoke TestClass inherited method on TestClass2 object" in {
    InvokeMethod("randomName2","method1").eval
    Check(Identifier("set15"), Identifier("var15")).eval shouldBe "Set set15 does contain var15."
  }

  //Test 10
  behavior of "invoking private() parent method"

  it should "throw a RuntimeException" in {
    a [RuntimeException] should be thrownBy InvokeMethod("randomName2","method3").eval
  }

  //Test 11
  behavior of "invoking private() parent field"

  it should "throw a RuntimeException" in {
    ClassDef("TestClass3",
      Array[Field](Field("d",Private()), Field("e", Public()), Field("f", Protected())),
      //Accesses private() field "a" in TestClass
      Constructor(Array[FieldAssign](FieldAssign("a", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0) Extends "TestClass"
    a [RuntimeException] should be thrownBy NewObject("TestClass3", "randomName3").eval
  }

  //Test 12
  behavior of "accessing public() parent field"

  it should "set field b to 5000 in parent class" in {
    ClassDef("TestClass6",
      Array[Field](Field("d",Private()), Field("e", Public()), Field("f", Protected())),
      //Accesses public() field "b" in TestClass
      Constructor(Array[FieldAssign](FieldAssign("b", 5000))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0) Extends "TestClass"
    NewObject("TestClass6", "object6").eval
    //object6's field b should be 5000
    objectMap("object6").asInstanceOf[mutable.Map[String, mutable.Map[String,mutable.Map[String,Int]]]]("fields")("public")("b") shouldBe 5000
  }

  //Test 13
  behavior of "Nested classes"

  it should "create a nested class" in {
    ClassDef("TestClass4",
      Array[Field](Field("d",Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("d", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      ClassDef("TestClass5",
        Array[Field](Field("d",Private()), Field("e", Public()), Field("f", Protected())),
        Constructor(Array[FieldAssign](FieldAssign("a", 5))),
        Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
        0)).eval
    //Check if TestClass4 has reference to TestClass5 inside of it
    classMap("TestClass4").asInstanceOf[mutable.Map[String,Any]]("nested").asInstanceOf[(String,Any)]._1 shouldBe "TestClass5"
  }
  ///////////////////////////////////////////////////////////////////////////////HW3 Tests

  //Test 14
  behavior of "Abstract Class"

  it should "create an abstract class" in {
    AbstractClassDef("TestClassA",
      Array[Field](Field("d",Private()), Field("e", Public()), Field("f", Protected())),
      Constructor(Array[FieldAssign](FieldAssign("d", 5))),
      Array[Method](Method("method2", Private(), Array[ArithExp](Assign(Identifier("set20"), Insert(Identifier("var20"), Variable(1)))))),
      0).eval

    classMap.contains("TestClassA") shouldBe true
  }
}
