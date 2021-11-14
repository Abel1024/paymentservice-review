package testability

import payment.Types.LocalDateTimeGen
import ps.time.TimeParsers
import java.time.LocalDateTime

class TestableLocalDateTimeGen extends LocalDateTimeGen {
  val LDT1: LocalDateTime = TimeParsers.fromLong("2021-11-11 21:05")
  val LDT2: LocalDateTime = TimeParsers.fromLong("2021-11-12 21:06")
  val LDT3: LocalDateTime = TimeParsers.fromLong("2021-11-13 21:07")
  private val LDTs = Array(LDT1, LDT2, LDT3)
  var current: Int = 0
  override def apply(): LocalDateTime = {
    LDTs({
      val c = current;
      current += 1;
      c
    })
  }
}
object TestableLocalDateTimeGen {
  def apply(): TestableLocalDateTimeGen = new TestableLocalDateTimeGen()
}
