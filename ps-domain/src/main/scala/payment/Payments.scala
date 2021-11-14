package payment

import payment.Protocol.Commands._
import payment.Protocol.Errors._
import payment.Protocol.Queries._
import payment.Protocol.Responses._
import scala.annotation.tailrec

trait Payments {

  def command(command: Command): Either[PaymentError, Payments]
  def query[T <: Response](query: Query[T]): Either[PaymentError, T]

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

