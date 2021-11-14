package testability

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TestableLocalDateTimeGenTest extends AnyFunSpec with Matchers {
  describe("for testing") {
    it("should generate known LocalDateTime values") {
      val ldtGen = TestableLocalDateTimeGen()
      ldtGen() shouldBe ldtGen.LDT1
      ldtGen() shouldBe ldtGen.LDT2
      ldtGen() shouldBe ldtGen.LDT3
      intercept[ArrayIndexOutOfBoundsException] {
        ldtGen()
      }
    }
  }
}