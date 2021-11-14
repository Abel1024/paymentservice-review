package http

import payment.Protocol.Commands.NewPaymentCommand
import transfer.PaymentRequest

object NewPaymentValidator {

  import cats.data._
  import cats.implicits._

  private type ErrorMessage = String
  private type ValidationResult[A] = ValidatedNel[ErrorMessage, A]

  private def validateFiatAmount(fiatAmount: BigDecimal): ValidationResult[BigDecimal] =
    if (fiatAmount > 0) fiatAmount.validNel
    else s"Fiat amount must be greater than zero!".invalidNel

  private def validateFiatCurrency(fiatCurrency: String): ValidationResult[String] =
    if (fiatCurrency.length == 3) fiatCurrency.validNel
    else s"Invalid fiat currency: '$fiatCurrency', must have 3 characters!".invalidNel

  private def validateCryptoCurrency(cryptoCurrency: String): ValidationResult[String] =
    if (cryptoCurrency.length == 3) cryptoCurrency.validNel
    else s"Invalid crypto currency: '$cryptoCurrency', must have 3 characters!".invalidNel

  def validateNewPayment(request: PaymentRequest): ValidationResult[NewPaymentCommand] =
    (validateFiatAmount(request.fiatAmount),
      validateFiatCurrency(request.fiatCurrency),
      validateCryptoCurrency(request.coinCurrency)).mapN(NewPaymentCommand)

}
