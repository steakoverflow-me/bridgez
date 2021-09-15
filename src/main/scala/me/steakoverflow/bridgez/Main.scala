package me.steakoverflow.bridgez

import zio.{App, RIO, Runtime, Task, ZEnv, ZIO, ExitCode as ZExitCode}
import korolev.Context
import korolev.server.{KorolevServiceConfig, StateLoader}
import korolev.zio.ZioEffect
import korolev.state.javaSerialization.*
import korolev.zio.http.ZioHttpKorolev
import zhttp.http.HttpApp
import zhttp.service.Server

import javax.swing.text.html.parser.TagElement
import scala.concurrent.ExecutionContext

object Main extends App :

  type AppTask[A] = RIO[ZEnv, A]

  private class Service()(implicit runtime: Runtime[ZEnv]) :

    import levsha.dsl.*
    import levsha.dsl.html.*
    import levsha.Document.Empty
    import scala.concurrent.duration.*

    implicit val ec: ExecutionContext = runtime.platform.executor.asEC
    implicit val effect: ZioEffect[ZEnv, Throwable] = new ZioEffect[ZEnv, Throwable](runtime, identity, identity)

    val ctx = Context[AppTask, Int, Any]
    import ctx.*

    def config = KorolevServiceConfig[AppTask, Int, Any](
//      stateLoader = StateLoader.default(Option.empty[Int]),
      stateLoader = StateLoader.default(0),
      rootPath = "/",
      document = { s =>
        optimize {
          Html(
            head(
              link(href := "https://unpkg.com/tailwindcss@^2/dist/tailwind.min.css", rel := "stylesheet")
            ),
            body(
              if s > 0 then delay(3.seconds) { access =>
                access.transition {
                  case _ => 0
                }
              } else Empty,
              button(
                clazz := "bg-red-400",
                "Push the button",
                if s > 0 then s" $s" else Empty,
                event("click") { access =>
                  access.transition {
                    case s => s + 1
                  }
                }
              ),
              if s > 0 then "Wait 3 seconds!!!" else Empty
            )
          )
        }
//        case None => optimize {
//          Html(
//            body(
//              button(
//                event("click") { access =>
//                  access.transition { _ => Some(1) }
//                },
//                "Push the button"
//              )
//            )
//          )
//        }
      }
    )

    def route(): HttpApp[ZEnv, Throwable] =
      new ZioHttpKorolev[ZEnv].service(config)
  end Service

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ZExitCode] =
    val prog = for {
      httpApp <- ZIO.runtime[ZEnv].map { implicit rts => new Service().route() }
      _ <- Server.start(8088, httpApp)
    } yield ZExitCode.success

    prog.orDie

end Main

