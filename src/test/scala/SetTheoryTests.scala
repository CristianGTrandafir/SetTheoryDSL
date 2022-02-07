import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetTheory.ArithExp.*


class SetTheoryTests extends AnyFlatSpec with Matchers {

  //Test 1
  behavior of "my first language for set theory operations"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval2
    Check(Identifier("someSetName"), Identifier("var")).eval2 shouldBe "Set " + "someSetName" + " does contain " + "var" + "."
  }
  //Test 2
  behavior of "my first language for set theory operations2"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval2
    Check(Identifier("someSetName"), Identifier("var")).eval2 shouldBe "Set " + "someSetName" + " does contain " + "var" + "."
  }
  //Test 3
  behavior of "my first language for set theory operations3"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval2
    Check(Identifier("someSetName"), Identifier("var")).eval2 shouldBe "Set " + "someSetName" + " does contain " + "var" + "."
  }
  //Test 4
  behavior of "my first language for set theory operations4"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval2
    Check(Identifier("someSetName"), Identifier("var")).eval2 shouldBe "Set " + "someSetName" + " does contain " + "var" + "."
  }
  //Test 5
  behavior of "my first language for set theory operations5"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval2
    Check(Identifier("someSetName"), Identifier("var")).eval2 shouldBe "Set " + "someSetName" + " does contain " + "var" + "."
  }
}
