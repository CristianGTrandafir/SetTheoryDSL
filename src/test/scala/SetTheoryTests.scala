//Cristian Trandafir

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetTheory.ArithExp.*


class SetTheoryTests extends AnyFlatSpec with Matchers {

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
    Macro (Identifier("testMacro"), Assign(Identifier("someSetName2"), Insert(Identifier("var2"), Variable(1)))).eval
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
}
