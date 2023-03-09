
package example


import be.adamv.momentum.concrete.{Relay, Var}
import be.adamv.momentum.{Descend, Sink, Source, adaptNow, updatePresent, value, zipLeft, given}
import be.adamv.impuls.delta.{BitRelayVar, TreeMapDelta, TreeMapRelayVar, given}
import be.adamv.tsiolkovsky.tdom.{N, a, button, cls, div, footer, h1, html, input, label, li, set, ul, span}
import be.adamv.tsiolkovsky.frp.{ChildNodeDelta, child, children, childrenDelta, clsToggle, display, onclick, ondblclick, onkeyup, defaultValue, onmount, onblur, checked, oninput}

import language.implicitConversions
import collection.mutable
import org.scalajs.dom

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
  enum Command:
    case Increment
    case Reset

  // --- State ---
  private val counterState = Relay[Int]()
  counterState.set(0)


  private val commandHandler: Sink[Command, Unit] = {
    case Command.Increment =>
      counterState.set(counterState.value.get + 1)
    case Command.Reset =>
      counterState.set(0)
  }
  private val commandObserver = commandHandler.eachTapped((c: Command) => println(s"Command received: $c"))

  // --- Views ---
  lazy val node: html.Element ?=> html.Div =
    div {
      cls("hellocounterapp")
      div {
        h1 {
          N"Hello Counter"
        }
        counter
      }
    }

  lazy val counter: html.Element ?=> html.Div =
    div {
      child.contramap(c => N"Count: $c") <-| counterState
    }

  def init(): Unit =
    dom.window.setTimeout(() => {
      commandObserver.set(Command.Increment)
    }, 1000)

end HelloCounterApp


@main def m =
  given html.Div = dom.document.querySelector("#main").asInstanceOf
  HelloCounterApp.node
  HelloCounterApp.init()