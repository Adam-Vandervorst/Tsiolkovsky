
package example


import be.adamv.momentum.concrete.{Relay, Var}
import be.adamv.momentum.{Descend, Sink, adaptNow, updatePresent, value, zipLeft, Source, given}
import be.adamv.impuls.delta.{BitRelayVar, TreeMapDelta, TreeMapRelayVar, given}
import be.adamv.tsiolkovsky.tdom.{N, a, button, cls, div, footer, h1, html, input, label, li, set, ul, span}
import be.adamv.tsiolkovsky.frp.{ChildNodeDelta, child, children, childrenDelta, clsToggle, display, onclick, ondblclick, onkeyup, defaultValue, onmount, onblur, checked, oninput}

import language.implicitConversions
import collection.mutable
import org.scalajs.dom

extension [A](s: Sink[A, Unit])
  def <-- (d: Descend[Unit, A, Unit]) = d.adaptNow(s)
  def <-| (d: Relay[A]) =
    d.adaptNow(s)
    s.set(d.value.get)

object HelloCounterApp:
  enum Command:
    case Increment
    case Reset

  // --- State ---
  val counterState = Relay[Int]()
  counterState.set(0)


  val commandHandler: Sink[Command, Unit] = {
    case Command.Increment =>
      counterState.set(counterState.value.get + 1)
    case Command.Reset =>
      counterState.set(0)
  }
  val commandObserver = commandHandler.eachTapped((c: Command) => println(s"Command received: $c"))

  // --- Views ---
  val node: html.Element ?=> html.Div =
    div {
      cls("hellocounterapp")
      div {
        h1 {
          N"Hello Counter"
        }
        renderCounter
      }
    }
  
  val renderCounter: html.Element ?=> html.Div =
    div {
      // N"Count: ${counterState.value.get}"
      child.contramap(c => N"Count: $c") <-| counterState
    }

  
  def init =
      dom.window.setTimeout(() => {
        commandObserver.set(Command.Increment)
      }, 1000)

end HelloCounterApp


@main def m =
  given html.Div = dom.document.querySelector("#main").asInstanceOf
  HelloCounterApp.node
  HelloCounterApp.init