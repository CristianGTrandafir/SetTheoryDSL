//Cristian Trandafir

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetTheory.ArithExp.*
import SetTheory.ArithExp


class SetTheoryTests extends AnyFlatSpec with Matchers {

///////////////////////////////////////////////////////////////////////////////HW2 Tests

//Note that I omitted the "shouldBe" check for most of these tests because I didn't know what to compare.
//You can do: println(setMap), println(objectMap), println(classMap) in main for better diagnostic information.
//The tests will still fail if an error is thrown - I modeled the test blocks to show core functionality this way without "shouldBe."

  //Test 8
  behavior of "my first language for set theory operations 8"

  it should "define a new class, define a class that extends the first class, then instantiate an object" in {
    ClassDef("TestClass",
      Array[Field](
        Field("a","private"), Field("b", "private"), Field("c", "private")
      ),
      Constructor(Array[FieldAssign](
        FieldAssign("c", 5), FieldAssign("b", 10), FieldAssign("a", 0))
      ),
      Array[Method](
        Method(
          "method1", "public", Array[ArithExp](
            Assign(Identifier("set10"), Insert(Identifier("var1"), Variable(1))), Macro(Identifier("testMacro"), Assign(Identifier("someSetName2"), Insert(Identifier("var2"), Variable(1)))))
          )
        )
    ).eval

    NewObject("TestClass", "randomName1").eval
  }
  //Test 9
  behavior of "my first language for set theory operations 9"

  it should "invoke method1 on object randomName1" in {
    InvokeMethod("randomName1","method1").eval
  }
  
  //Test 10
  behavior of "my first language for set theory operations 10"

  it should "extend a class" in {
    ClassDef("TestClass2",
      Array[Field](Field("a","private"), Field("b", "private"), Field("c", "private")),
      Constructor(Array[FieldAssign](FieldAssign("c", 5))),
      Array[Method](Method("method2", "private", Array[ArithExp](Assign(Identifier("set10"), Insert(Identifier("var2"), Variable(1))))))
    ) Extends "TestClass"
    NewObject("TestClass2", "randomName2").eval
  }
  //Test 11
  behavior of "my first language for set theory operations 11"

  it should "invoke a method in a parent class" in {
    InvokeMethod("randomName2","method1").eval
  }
  //Test 12
  behavior of "my first language for set theory operations 12"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval
    Check(Identifier("someSetName"), Identifier("var")).eval shouldBe "Set " + "someSetName" + " does contain " + "var" + "."
  }

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
}
