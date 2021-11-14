package ps.time

import java.time.LocalDateTime
import java.time.{Duration => JDuration}
import scala.concurrent.duration._
import scala.language.postfixOps

object TimeImplicits {
  implicit class RichDateTime(val localDateTime: LocalDateTime) extends AnyVal {
    def +(duration: Duration): LocalDateTime = {
      localDateTime.plus(JDuration.ofMillis(duration.toMillis))
    }
  }
}
