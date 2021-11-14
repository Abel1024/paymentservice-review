package testability

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TestableUUIDGenTest extends AnyFunSpec with Matchers {
  describe("for testing") {
    it("should generate known UUID values") {
      val uuidGen = TestableUUIDGen()
      uuidGen() shouldBe uuidGen.UUID1
      uuidGen() shouldBe uuidGen.UUID2
      uuidGen() shouldBe uuidGen.UUID3
      intercept[ArrayIndexOutOfBoundsException] {
        uuidGen()
      }
    }
  }
}