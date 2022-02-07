import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetTheory.ArithExp.*

class SetTheoryTests extends AnyFlatSpec with Matchers {

  behavior of "my first language for set theory operations"

  it should "create a set and insert objects into it" in {
    Assign(Identifier("someSetName"), Insert(Identifier("var"), Variable(1))).eval()
    Check(Identifier("someSetName"), Value(1)).eval() shouldBe true
  }
}
