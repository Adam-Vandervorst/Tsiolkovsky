package hellocounter


import be.adamv.momentum.concrete.Relay
import be.adamv.momentum.dsl.*
import be.adamv.momentum.{Descend, Sink, Source, adaptNow, updatePresent, value, given}
import be.adamv.impuls.delta.{CountDelta, CountRelayVar, given}
import be.adamv.tsiolkovsky.tdom.{N, button, cls, div, h1, html}
import be.adamv.tsiolkovsky.frp.{child, onclick, timeout}

import org.scalajs.dom


object HelloCounterApp:
  enum Command: // User actions
    case Increment
    case Reset

  private val counterState = Relay[Int]()
  counterState.set(0)

  private val commandHandler: Sink[Command, Unit] = {
    case Command.Increment =>
      counterState.updatePresent(_ + 1)
    case Command.Reset =>
      counterState.set(0)
  }
  private val commandSink: Sink[Command, Unit] = commandHandler.eachTapped(c =>
    println(s"Command received: $c"))

  val node: html.Element ?=> html.Div =
    div {
      cls("hellocounterapp")
      div {
        h1 {
          N"Hello Counter"
        }
        counter
        button {
          cls("increment")
          commandSink.contramapTo(Command.Increment) <-- onclick
          N"Increment"
        }
        button {
          cls("reset")
          commandSink.contramapTo(Command.Reset) <-- onclick
          N"Reset"
        }
      }
    }

  val counter: html.Element ?=> html.Div =
    div {
      child.contramap(c => N"Count: $c") <-| counterState
    }

  def init(): Unit =
    // Simulate user button press after 1 second
    commandSink.contramapTo(Command.Increment) <-- timeout(1000)
end HelloCounterApp


@main def m =
  HelloCounterApp.node(using dom.document.querySelector("#main").asInstanceOf)
  HelloCounterApp.init()
