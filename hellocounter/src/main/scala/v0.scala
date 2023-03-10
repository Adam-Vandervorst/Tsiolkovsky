package hellocounter


import be.adamv.momentum.concrete.{Relay, Var}
import be.adamv.momentum.{Descend, Sink, Source, adaptNow, updatePresent, value, given}
import be.adamv.impuls.delta.{CountDelta, CountRelayVar, given}
import be.adamv.tsiolkovsky.tdom.{N, button, cls, div, h1, html}
import be.adamv.tsiolkovsky.frp.{child, onclick, timeout}

import org.scalajs.dom


// TODO move to momentum.dsl
extension [A](s: Sink[A, Unit])
  def <-- (d: Descend[Unit, A, Unit]) =
    d.adaptNow(s)
  def -| (d: Source[Option[A], Unit]) = d.value match
    case Some(value) => s.set(value)
    case None => ()
  def <-|(d: Descend[Unit, A, Unit] & Source[Option[A], Unit]) =
    s <-- d
    s -| d

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
  val root = dom.document.querySelector("#main").asInstanceOf
  HelloCounterApp.node(using root)
  HelloCounterApp.init()
