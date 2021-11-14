package ps.time

import java.time.format.DateTimeFormatter

object TimeFormats {
  val long: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
}
