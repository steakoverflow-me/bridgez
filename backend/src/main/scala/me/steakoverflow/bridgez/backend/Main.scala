package me.steakoverflow.bridgez.backend

import zio.Console.printLine
import zio.{App, ExitCode, URIO}

object Main extends App :
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    printLine("Welcome to your first ZIO app!").exitCode
