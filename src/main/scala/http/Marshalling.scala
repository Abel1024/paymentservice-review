package http

import payment.Protocol.Responses.{PaymentResponse, PaymentsResponse}
import ps.time.{TimeFormats, TimeParsers}
import spray.json.{DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}
import transfer.PaymentRequest
import java.time.LocalDateTime
import java.util.UUID

object Marshalling {

  import spray.json.DefaultJsonProtocol._

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID): JsString = JsString(uuid.toString)
    def read(value: JsValue): UUID = value match {
      case JsString(uuid) => UUID.fromString(uuid)
      case _ => throw DeserializationException("Expected hexadecimal UUID string")
    }
  }
  implicit object DateJsonFormat extends RootJsonFormat[LocalDateTime] {
    override def write(ldt: LocalDateTime): JsString = JsString(TimeFormats.long.format(ldt))
    override def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) => TimeParsers.fromLong(s)
      case _ => throw DeserializationException(s"Invalid date $json")
    }
  }

  implicit val errorInfoFormat: RootJsonFormat[ErrorInfo] = jsonFormat1(ErrorInfo)
  implicit val paymentRequestFormat: RootJsonFormat[PaymentRequest] = jsonFormat3(PaymentRequest)
  implicit val paymentResponseFormat: RootJsonFormat[PaymentResponse] = jsonFormat8(PaymentResponse)
  implicit val allPaymentsResponseFormat: RootJsonFormat[PaymentsResponse] = jsonFormat1(PaymentsResponse)

}
