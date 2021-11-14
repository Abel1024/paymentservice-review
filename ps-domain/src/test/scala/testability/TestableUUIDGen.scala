package testability

import payment.Types.UUIDGen
import java.util.UUID

class TestableUUIDGen extends UUIDGen {
  val UUID1: UUID = UUID.randomUUID()
  val UUID2: UUID = UUID.randomUUID()
  val UUID3: UUID = UUID.randomUUID()
  private val UUIDs = Array(UUID1, UUID2, UUID3)
  var current: Int = 0
  override def apply(): UUID = {
    UUIDs({
      val c = current;
      current += 1;
      c
    })
  }
}

object TestableUUIDGen {
  def apply(): TestableUUIDGen = new TestableUUIDGen()
}
