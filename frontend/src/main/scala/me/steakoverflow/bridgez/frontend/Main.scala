package me.steakoverflow.bridgez.frontend

import colibri.Subject
import outwatch.*
import outwatch.dsl.*
import zio.{App, ExitCode, Task, ZIO}
import zio.interop.catz.*

//import cats.effect.{ExitCode, IO, IOApp, SyncIO}

object Seed extends App {
  def run(args: List[String]): Task[ExitCode] = {

    val counter = Task {
      val number = Subject.behavior(0)
      div(
        button("+", onClick(number.map(_ + 1)) --> number),
        number,
      )
    }

    val app = div(
      h1("Hello World!"),
      counter,
    )

    OutWatch.renderInto[Task]("#app", app).as(ExitCode.Success)
  }
}
