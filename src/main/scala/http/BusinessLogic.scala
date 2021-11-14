package http

import payment.Protocol.Queries.{AllPayments, PaymentById, PaymentsByFiatCurrency}
import payment.Protocol.Responses.{PaymentResponse, PaymentsResponse}
import ps.service.PaymentsService
import transfer.PaymentRequest
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BusinessLogic(paymentService: PaymentsService) {

  def newPayment(request: PaymentRequest): Future[Either[ErrorInfo, Boolean]] = Future {
    for {
      newPaymentCommand <- NewPaymentValidator
        .validateNewPayment(request)
        .leftMap(errors => ErrorInfo(errors.toList.mkString(" ")))
        .toEither
      _ <- paymentService.command(newPaymentCommand) match {
        case Left(value) => Left(ErrorInfo(value.toString))
        case _ => Right(true)
      }
    } yield true
  }

  def allPayments: Future[Either[ErrorInfo, PaymentsResponse]] = Future {
    paymentService.query(AllPayments) match {
      case Left(l) => Left(ErrorInfo(l.toString))
      case Right(r: PaymentsResponse) => Right(r)
    }
  }

  def paymentById(uuid: UUID): Future[Either[ErrorInfo, PaymentResponse]] = Future {
    paymentService.query(PaymentById(uuid)) match {
      case Left(l) => Left(ErrorInfo(l.toString))
      case Right(r: PaymentResponse) => Right(r)
    }
  }

  def paymentsByFiat(currency: String): Future[Either[ErrorInfo, PaymentsResponse]] = Future {
    paymentService.query(PaymentsByFiatCurrency(currency)) match {
      case Left(l) => Left(ErrorInfo(l.toString))
      case Right(r: PaymentsResponse) => Right(r)
    }
  }

}
