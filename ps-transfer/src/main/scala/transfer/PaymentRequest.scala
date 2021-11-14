package transfer

case class PaymentRequest(
  fiatAmount: BigDecimal,
  fiatCurrency: String,
  coinCurrency: String
)
