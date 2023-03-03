package be.adamv.tsiolkovsky.frp

import org.scalajs.dom
import scala.scalajs.js

import be.adamv.momentum.Descend


def onkeyup(using e: dom.html.Element): Descend[Unit, dom.KeyboardEvent, Unit] = s =>
  _ => e.addEventListener[dom.KeyboardEvent]("keyup", s.set)

def onclick(using e: dom.html.Element): Descend[Unit, dom.MouseEvent, Unit] = s =>
  _ => e.addEventListener[dom.MouseEvent]("click", s.set)

//def onclick(using e: dom.html.Element): Descend[Boolean, dom.MouseEvent, Unit] = s =>
//  b =>
//    if b then
//      e.addEventListener[dom.MouseEvent]("click", s.set)
//    else
//      e.removeEventListener("click", s.set)


def onchange(using e: dom.html.Element): Descend[Unit, dom.Event, Unit] = s =>
  _ => e.addEventListener[dom.Event]("change", s.set)
