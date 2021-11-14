package ps.config


import org.scalatest.EitherValues
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import payment.Configuration
import payment.Configuration.{Api, Config}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class ConfigLoaderTest extends AnyFunSpec with Matchers with EitherValues {

  describe("Configuration") {
    describe("loads valid Config") {
      it("values should match application.config") {
        ConfigLoader() shouldBe Config(Api(Configuration.Payment(30 minutes, 20, 200000)))
      }
    }
    describe("loads invalid Config") {
      describe("fails fast with technical error") {
        it("configuration file not found") {
          intercept[IllegalStateException] {
            ConfigLoader("file not found")
          }.getMessage should startWith("Cannot start server with bad config!")
        }
        it("configuration file is malformed") {
          intercept[IllegalStateException] {
            ConfigLoader("bad-application.conf")
          }.getMessage should startWith("Cannot start server with bad config!")
        }
      }
    }
  }
}