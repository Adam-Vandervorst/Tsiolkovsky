
package example


import be.adamv.momentum.concrete.{Relay, Var}
import be.adamv.momentum.{Descend, Sink, Source, adaptNow, updatePresent, value, zipLeft, given}
import be.adamv.impuls.delta.{BitRelayVar, TreeMapDelta, TreeMapRelayVar, given}
import be.adamv.tsiolkovsky.tdom.{N, a, button, cls, div, footer, h1, html, input, label, li, set, ul, span}
import be.adamv.tsiolkovsky.frp.{ChildNodeDelta, child, children, childrenDelta, clsToggle, display, onclick, ondblclick, onkeyup, defaultValue, onmount, onblur, checked, oninput}

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
  // user actions
  enum Command:
    case Increment
    case Reset

  private val counterState = Relay[Int]()
  counterState.set(0)

  private val commandHandler: Sink[Command, Unit] = {
    case Command.Increment =>
      counterState.set(counterState.value.get + 1)
    case Command.Reset =>
      counterState.set(0)
  }
  private val commandSink: Sink[Command, Unit] = commandHandler.eachTapped(c =>
    println(s"Command received: $c"))

  lazy val node: html.Element ?=> html.Div =
    div {
      cls("hellocounterapp")
      div {
        h1 {
          N"Hello Counter"
        }
        counter
        button {
          cls("increment")
          commandSink.contramap(e => Command.Increment) <-- onclick
          N"Increment"
        }
        button {
          cls("reset")
          commandSink.contramap(e => Command.Reset) <-- onclick
          N"Reset"
        }
      }
    }

  lazy val counter: html.Element ?=> html.Div =
    div {
      // the state (Int) needs to be converted to a child element (dom.Element)
      // this can be done by contramap'ing on the sink or map'ing on the descend
      child.contramap(c => N"Count: $c") <-| counterState
    }

  def init(): Unit =
    // Simulate user button press after 1 second
    // TODO make setTimout and setInterval descends
    dom.window.setTimeout(() =>
      commandSink.set(Command.Increment)
    , 1000)
end HelloCounterApp


@main def m =
  given html.Div = dom.document.querySelector("#main").asInstanceOf
  HelloCounterApp.node
  HelloCounterApp.init()
