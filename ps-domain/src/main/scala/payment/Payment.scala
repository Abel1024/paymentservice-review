package payment

import payment.Protocol.Responses.PaymentResponse
import java.time.LocalDateTime
import java.util.UUID

case class Payment(
  id: UUID,
  fiatAmount: BigDecimal,
  fiatCurrency: String,
  coinAmount: BigDecimal,
  coinCurrency: String,
  exchangeRate: BigDecimal,
  eurExchangeRate: BigDecimal,
  createdAt: LocalDateTime,
  expirationTime: LocalDateTime
) {

  def toResponse: PaymentResponse =
    PaymentResponse(
      id = id,
      fiatAmount = fiatAmount,
      fiatCurrency = fiatCurrency,
      coinAmount = coinAmount,
      coinCurrency = coinCurrency,
      exchangeRate = exchangeRate,
      createdAt = createdAt,
      expirationTime = expirationTime
    )

  override def toString: String =
    s"""
       |Payment {
       |  id=$id
       |  fiatAmount=$fiatAmount,
       |  fiatCurrency=$fiatCurrency,
       |  coinAmount=$coinAmount,
       |  coinCurrency=$coinCurrency,
       |  exchangeRate=$exchangeRate,
       |  eurExchangeRate=$eurExchangeRate,
       |  createdAt=$createdAt,
       |  expirationTime=$expirationTime
       |}""".stripMargin
}
