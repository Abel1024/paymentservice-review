package ps.time

import java.time.LocalDateTime

object TimeParsers {
  def fromLong(date: String): LocalDateTime =
    LocalDateTime.parse(date, TimeFormats.long)

}
