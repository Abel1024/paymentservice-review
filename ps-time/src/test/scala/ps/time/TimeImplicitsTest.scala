package ps.time

import org.scalatest.OptionValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import ps.time.TimeImplicits._
import ps.time.TimeParsers.fromLong

import java.time.LocalDateTime
import scala.concurrent.duration._
import scala.language.postfixOps


class TimeImplicitsTest extends AnyFunSpec with Matchers with OptionValues {

  describe("TimeImplicits") {
    describe("Enrich LocalDateTime with") {
      it("+ duration") {
        //given
        val ldt: LocalDateTime = fromLong(date = "2021-11-12 21:06")
        val oneDay: FiniteDuration = 1 day
        //when
        val future = ldt + oneDay
        //then
        future shouldBe fromLong(date = "2021-11-13 21:06")
      }
    }
  }
}