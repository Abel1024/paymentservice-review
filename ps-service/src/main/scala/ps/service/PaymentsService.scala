package ps.service

import payment.Protocol.Commands.Command
import payment.Protocol.DomainErrors.PaymentError
import payment.Protocol.Queries.Query
import payment.Protocol.Responses.Response
import payment.{Configuration, Payments, VectorPayments}
import ps.config.ConfigLoader
import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

class PaymentsService(config: Configuration.Config) extends Payments {

  private var payments: Payments = VectorPayments(config.api.payment)
  private val lock: ReadWriteLock = new ReentrantReadWriteLock()

  override def command(command: Command): Either[PaymentError, Payments] = {
    lock.writeLock().lock()
    try {
      payments.command(command) match {
        case r@Right(p) => payments = p; r
        case l@Left(_) => l
      }
    }
    finally {lock.writeLock().unlock()}
  }

  override def query[T <: Response](query: Query[T]): Either[PaymentError, T] = {
    lock.readLock().lock()
    try {
      payments.query[T](query)
    }
    finally {lock.readLock().unlock()}
  }
}

object PaymentsService {
  def apply() = new PaymentsService(ConfigLoader())
}
