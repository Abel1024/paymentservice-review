package payment

import market.MarketData.{exchangeRatesOfBTC, exchangeRatesToEUR}
import payment.Protocol.Commands._
import payment.Protocol.DomainErrors._
import payment.Protocol.Queries._
import payment.Protocol.Responses._
import payment.Types.{LocalDateTimeGen, UUIDGen}
import payment.VectorPayments.EUR
import ps.time.TimeImplicits._
import java.time.LocalDateTime
import java.util.UUID

object Types {
  type UUIDGen = () => UUID
  type LocalDateTimeGen = () => LocalDateTime
}

case class VectorPayments(
  config: Configuration.Payment,
  uuidGen: UUIDGen = () => UUID.randomUUID(),
  localDateTimeGen: LocalDateTimeGen = () => LocalDateTime.now(),
  payments: Vector[Payment] = Vector()) extends Payments {

  override def command(command: Command): Either[PaymentError, Payments] = {
    command match {
      case NewPaymentCommand(amount, fiatCurrency, cryptoCurrency) =>
        if (!DB.fiatCurrencies.contains(fiatCurrency))
          Left(UnknownFiatCurrency(fiatCurrency))
        else if (!DB.cryptoCurrencies.contains(cryptoCurrency))
          Left(UnknownCryptoCurrency(cryptoCurrency))
        else if (EUR == fiatCurrency && amount < config.minEurAmount)
          Left(BelowMinEuroAmount(amount, config.minEurAmount))
        else if (EUR == fiatCurrency && amount > config.maxEurAmount)
          Left(AboveMaxEuroAmount(amount, config.maxEurAmount))
        else {
          val now = localDateTimeGen()
          Right(this.copy(payments = payments ++ Vector(payment.Payment(
            id = uuidGen(),
            fiatAmount = amount,
            fiatCurrency = fiatCurrency,
            coinAmount = amount / exchangeRatesOfBTC(fiatCurrency),
            coinCurrency = cryptoCurrency,
            exchangeRate = exchangeRatesOfBTC(fiatCurrency),
            eurExchangeRate = exchangeRatesToEUR(fiatCurrency),
            createdAt = now,
            expirationTime = now + config.expiration
          ))))
        }
    }
  }

  override def query[T <: Response](query: Query[T]): Either[PaymentError, T] = {
    query match {
      case PaymentById(id) =>
        payments
          .find(_.id == id)
          .map(p => Right(p.toResponse.asInstanceOf[T]))
          .getOrElse(Left(PaymentWithIdNotFound(id)))
      case PaymentsByFiatCurrency(currency) =>
        Right(
          PaymentsResponse(
            payments
              .filter(_.fiatCurrency == currency)
              .map(_.toResponse)
          ).asInstanceOf[T])
      case AllPayments =>
        Right(
          PaymentsResponse(
            payments
              .map(_.toResponse)
          ).asInstanceOf[T])
    }
  }
}

object VectorPayments {
  private val EUR = "EUR"
}