package ps.config

import payment.Configuration.Config
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object ConfigLoader {
  def apply(configFile: String = "application.conf"): Config =
    ConfigSource.resources(configFile).load[Config] match {
      case Right(config) => config
      case Left(error) => throw new IllegalStateException(
        s"Cannot start server with bad config! $error")
    }
}
