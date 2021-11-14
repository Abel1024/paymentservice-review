package payment

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, OptionValues}
import payment.PaymentsTest.withPayments
import payment.Protocol.Commands._
import payment.Protocol.Errors._
import payment.Protocol.Queries._
import payment.Protocol.Responses.PaymentResponse
import ps.time.TimeImplicits._
import testability.{TestableLocalDateTimeGen, TestableUUIDGen}
import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class PaymentsTest extends AnyFunSpec with Matchers with OptionValues with EitherValues {

  val config: Configuration.Payment = Configuration.Payment(1 hour, 100, 1000000)

  describe("Payments Immutable Domain") {
    describe("receives a commands") {
      describe("NewPayment") {
        describe("success path") {
          it("add new payment") {
            //given
            val uuid = UUID.randomUUID()
            val now = LocalDateTime.now()
            //when
            val payments: Payments = VectorPayments(config, () => uuid, () => now)
              .command(
                NewPaymentCommand(84000.00, "EUR", "BTC")
              ).value
            //then
            payments.query(AllPayments).value.payments shouldBe
              Vector(
                PaymentResponse(
                  id = uuid,
                  fiatAmount = 84000.00,
                  fiatCurrency = "EUR",
                  coinAmount = 2,
                  coinCurrency = "BTC",
                  exchangeRate = 42000.00,
                  createdAt = now,
                  expirationTime = now + config.expiration
                ))
          }

          it("add a different payment") {
            //given
            val uuid = UUID.randomUUID()
            val now = LocalDateTime.now()
            //when
            val payments: Payments = VectorPayments(config, () => uuid, () => now)
              .command(
                NewPaymentCommand(150000.00, "USD", "BTC")
              ).value
            //then
            payments.query(AllPayments).value.payments shouldBe
              Vector(
                PaymentResponse(
                  id = uuid,
                  fiatAmount = 150000.00,
                  fiatCurrency = "USD",
                  coinAmount = 3,
                  coinCurrency = "BTC",
                  exchangeRate = 50000.00,
                  createdAt = now,
                  expirationTime = now + config.expiration
                ))
          }

          it("add two payments") {
            //given
            val uuidGen = TestableUUIDGen()
            val localDateTimeGen = TestableLocalDateTimeGen()
            //when
            val payments: Payments = VectorPayments(config, uuidGen, localDateTimeGen).command(
              NewPaymentCommand(300000, "USD", "BTC"),
              NewPaymentCommand(84000, "EUR", "BTC")).value
            //then
            payments.query(AllPayments).value.payments shouldBe
              Vector(
                PaymentResponse(
                  id = uuidGen.UUID1,
                  fiatAmount = 300000,
                  fiatCurrency = "USD",
                  coinAmount = 6,
                  coinCurrency = "BTC",
                  exchangeRate = 50000.0,
                  createdAt = localDateTimeGen.LDT1,
                  expirationTime = localDateTimeGen.LDT1 + config.expiration
                ),
                PaymentResponse(
                  id = uuidGen.UUID2,
                  fiatAmount = 84000,
                  fiatCurrency = "EUR",
                  coinAmount = 2,
                  coinCurrency = "BTC",
                  exchangeRate = 42000.0,
                  createdAt = localDateTimeGen.LDT2,
                  expirationTime = localDateTimeGen.LDT2 + config.expiration
                ),
              )
          }

          describe("USD has no amount limits") {
            it("no min limit") {
              withPayments { payments =>
                payments.command(NewPaymentCommand(99.99, "USD", "BTC")).isRight shouldBe true
              }
            }
            it("no max limit") {
              withPayments { payments =>
                payments.command(NewPaymentCommand(1000.01, "USD", "BTC")).isRight shouldBe true
              }
            }
          }
        }

        describe("failure path") {
          it("unknown fiat currency") {
            withPayments { payments =>
              payments.command(NewPaymentCommand(1, "XXX", "BTC")).left.value shouldBe
                UnknownFiatCurrency("XXX")
            }
          }
          it("unknown crypto currency") {
            withPayments { payments =>
              payments.command(NewPaymentCommand(1, "EUR", "YYY")).left.value shouldBe
                UnknownCryptoCurrency("YYY")
            }
          }
          it("below min euro amount") {
            withPayments { payments =>
              payments.command(NewPaymentCommand(99.99, "EUR", "BTC")).left.value shouldBe
                BelowMinEuroAmount(amount = 99.99, min = 100)
            }
          }
          it("above max euro amount") {
            withPayments { payments =>
              payments.command(NewPaymentCommand(1000.01, "EUR", "BTC")).left.value shouldBe
                AboveMaxEuroAmount(amount = 1000.01, max = 1000)
            }
          }
        }
      }
    }

    describe("receives a query") {
      it("payment by id") {
        //given
        val uuid = UUID.randomUUID()
        val now = LocalDateTime.now()
        val payments: Payments = VectorPayments(config, () => uuid, () => now)
          .command(
            NewPaymentCommand(84000.00, "EUR", "BTC")
          ).value
        //then
        payments.query(PaymentById(uuid)).value shouldBe
          PaymentResponse(
            id = uuid,
            fiatAmount = 84000.00,
            fiatCurrency = "EUR",
            coinAmount = 2,
            coinCurrency = "BTC",
            exchangeRate = 42000.00,
            createdAt = now,
            expirationTime = now + config.expiration
          )
      }
      it("payments by fiat currency") {
        withPayments { payments =>
          //given
          val updatedPayments: Payments = payments.command(
            NewPaymentCommand(1, "USD", "BTC"),
            NewPaymentCommand(200, "EUR", "BTC"),
            NewPaymentCommand(300, "EUR", "BTC")
          ).value
          //then
          updatedPayments.query(PaymentsByFiatCurrency("EUR")).value.payments
            .map(p => (p.fiatCurrency, p.fiatAmount)) shouldBe List(("EUR", 200), ("EUR", 300))
          updatedPayments.query(PaymentsByFiatCurrency("USD")).value.payments
            .map(p => (p.fiatCurrency, p.fiatAmount)) shouldBe List(("USD", 1))
        }
      }
    }
  }
}

object PaymentsTest {
  def withPayments(block: Payments => Unit): Unit = {
    block(VectorPayments(Configuration.Payment(1 hour, 100, 1000), TestableUUIDGen(), TestableLocalDateTimeGen()))
  }
}



