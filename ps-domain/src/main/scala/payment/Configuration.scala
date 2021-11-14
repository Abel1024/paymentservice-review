package payment

import scala.concurrent.duration.Duration

object Configuration {
  case class Payment(expiration: Duration, minEurAmount: Int, maxEurAmount: Int)
  case class Api(payment: Payment)
  case class Config(api: Api)
}
