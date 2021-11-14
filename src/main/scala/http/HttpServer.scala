package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import http.Marshalling._
import ps.service.PaymentsService
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray._
import sttp.tapir.server.akkahttp.{AkkaHttpServerInterpreter, AkkaHttpServerOptions}
import sttp.tapir.server.interceptor.ValuedEndpointOutput
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object HttpServer extends App {

  private implicit val actorSystem: ActorSystem = ActorSystem()
  private val businessLogic = BusinessLogic(PaymentsService())

  val interpreter =
    AkkaHttpServerInterpreter(
      AkkaHttpServerOptions
        .customInterceptors
        .errorOutput(m => ValuedEndpointOutput(jsonBody[ErrorInfo], ErrorInfo(m)))
        .options
    )

  val routes: Route =
    Directives
      .concat(
        interpreter.toRoute(Endpoints.allPayments.serverLogic((_: Unit) => businessLogic.allPayments)),
        interpreter.toRoute(Endpoints.newPayment.serverLogic(businessLogic.newPayment)),
        interpreter.toRoute(Endpoints.paymentById.serverLogic(businessLogic.paymentById)),
        interpreter.toRoute(Endpoints.paymentsByFiat.serverLogic(businessLogic.paymentsByFiat))
      )

  val eventualBinding: Future[Http.ServerBinding] =
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(routes)

  Await.result(eventualBinding, 1.minute)
  println("Server started!")
}


