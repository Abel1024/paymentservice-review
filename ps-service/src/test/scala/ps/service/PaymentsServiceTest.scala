package ps.service

import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import payment.Protocol.Commands.NewPaymentCommand
import payment.Protocol.Queries.AllPayments
import scala.language.postfixOps

class PaymentsServiceTest extends AnyFunSpec with Matchers with EitherValues {

  describe("forward commands and queries") {
    it("add a payment") {
      //given
      val service = PaymentsService()
      //when
      service.command(NewPaymentCommand(100, "EUR", "BTC"))
      //then
      service.query(AllPayments)
        .value
        .payments
        .head
        .fiatAmount shouldBe 100
    }
  }

}