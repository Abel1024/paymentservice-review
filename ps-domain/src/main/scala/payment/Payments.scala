package payment

import payment.Protocol.Commands._
import payment.Protocol.DomainErrors._
import payment.Protocol.Queries._
import payment.Protocol.Responses._
import scala.annotation.tailrec

trait Payments {

  /**
   * Generates new payments state as a result of processing the command.
   *
   * @param command - contains all information needed to execute specific business command.
   * @return - either a PaymentError as an indication of an invalid input, or an updated payments
   */
  def command(command: Command): Either[PaymentError, Payments]

  /**
   * Returns a view of the current state of the payments, without changing it.
   *
   * @param query - specifies which information is requested.
   * @tparam T - determines the type of object that will be returned.
   * @return - eiter a PaymentError as an indication of invalid inquiry, or one of the Responses.
   */
  def query[T <: Response](query: Query[T]): Either[PaymentError, T]

  /**
   * Same as command method above, but can accept multiple commands at once. Handy for test.
   *
   * @param commands - one, or multiple commands.
   * @return - same as the commands method above.
   */
  def command(commands: Command*): Either[PaymentError, Payments] = {
    @tailrec
    def go(commands: Seq[Command], payments: Payments): Either[PaymentError, Payments] = {
      commands match {
        case Nil => Right(payments)
        case h :: t => payments.command(h) match {
          case Right(p) => go(t, p)
          case l@Left(_) => l
        }
      }
    }
    go(commands.toList, payments = this)
  }
}

