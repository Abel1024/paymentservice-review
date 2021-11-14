package payment

import payment.Protocol.Responses.{PaymentResponse, PaymentsResponse}
import java.time.LocalDateTime
import java.util.UUID

object Protocol {

  object Commands {
    sealed trait Command
    case class NewPaymentCommand(amount: BigDecimal, fiatCurrency: String, cryptoCurrency: String) extends Command
  }

  object Queries {
    sealed trait Query[T]
    case class PaymentById(id: UUID) extends Query[PaymentResponse]
    case class PaymentsByFiatCurrency(currency: String) extends Query[PaymentsResponse]
    case object AllPayments extends Query[PaymentsResponse]
  }

  object Responses {
    sealed trait Response
    case class PaymentResponse(
      id: UUID,
      fiatAmount: BigDecimal,
      fiatCurrency: String,
      coinAmount: BigDecimal,
      coinCurrency: String,
      exchangeRate: BigDecimal,
      createdAt: LocalDateTime,
      expirationTime: LocalDateTime
    ) extends Response
    case class PaymentsResponse(payments: Seq[PaymentResponse]) extends Response
  }

  object Errors {
    sealed trait PaymentError
    case class UnknownFiatCurrency(currency: String) extends PaymentError
    case class UnknownCryptoCurrency(crypto: String) extends PaymentError
    case class BelowMinEuroAmount(amount: BigDecimal, min: BigDecimal) extends PaymentError
    case class AboveMaxEuroAmount(amount: BigDecimal, max: BigDecimal) extends PaymentError
    case class PaymentWithIdNotFound(uuid: UUID) extends PaymentError
  }

}
