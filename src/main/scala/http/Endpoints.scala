package http

import payment.Protocol.Responses.{PaymentResponse, PaymentsResponse}
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.{PublicEndpoint, endpoint, path, plainBody, query}
import transfer.PaymentRequest
import java.util.UUID

object Endpoints {

  import Marshalling._
  private val ep = endpoint.errorOut(jsonBody[ErrorInfo])

  val newPayment: PublicEndpoint[PaymentRequest, ErrorInfo, Boolean, Any] =
    ep
      .post
      .in("payment")
      .in("new")
      .in(jsonBody[PaymentRequest])
      .out(plainBody[Boolean])

  val paymentById: PublicEndpoint[UUID, ErrorInfo, PaymentResponse, Any] =
    ep
      .get
      .in("payment")
      .in(path[UUID])
      .out(jsonBody[PaymentResponse])

  val paymentsByFiat: PublicEndpoint[String, ErrorInfo, PaymentsResponse, Any] =
    ep
      .get
      .in("payments")
      .in(query[String](name = "currency"))
      .out(jsonBody[PaymentsResponse])

  val allPayments: PublicEndpoint[Unit, ErrorInfo, PaymentsResponse, Any] =
    ep
      .get
      .in("payment")
      .in("all")
      .out(jsonBody[PaymentsResponse])

}
