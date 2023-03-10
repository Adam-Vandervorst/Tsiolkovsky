package be.adamv.tsiolkovsky.frp

import org.scalajs.dom
import scala.scalajs.js

import be.adamv.momentum.Descend
import be.adamv.momentum.tick


def onkeyup(using e: dom.html.Element): Descend[Unit, dom.KeyboardEvent, Unit] = s =>
  _ => e.addEventListener[dom.KeyboardEvent]("keyup", s.set)

def onclick(using e: dom.html.Element): Descend[Unit, dom.MouseEvent, Unit] = s =>
  _ => e.addEventListener[dom.MouseEvent]("click", s.set)

def ondblclick(using e: dom.html.Element): Descend[Unit, dom.MouseEvent, Unit] = s =>
  _ => e.addEventListener[dom.MouseEvent]("dblclick", s.set)

def onmount(using e: dom.html.Element): Descend[Unit, dom.Event, Unit] = s =>
  _ => e.addEventListener[dom.MouseEvent]("mount", s.set)

def onblur(using e: dom.html.Element): Descend[Unit, dom.FocusEvent, Unit] = s =>
  _ => e.addEventListener[dom.FocusEvent]("blur", s.set)

def onfocus(using e: dom.html.Element): Descend[Unit, dom.FocusEvent, Unit] = s =>
  _ => e.addEventListener[dom.FocusEvent]("focus", s.set)

//def onclick(using e: dom.html.Element): Descend[Boolean, dom.MouseEvent, Unit] = s =>
//  b =>
//    if b then
//      e.addEventListener[dom.MouseEvent]("click", s.set)
//    else
//      e.removeEventListener("click", s.set)

def oninput(using e: dom.html.Input): Descend[Unit, dom.Event, Unit] = s =>
  _ => e.addEventListener[dom.Event]("input", s.set)


def onchange(using e: dom.html.Element): Descend[Unit, dom.Event, Unit] = s =>
  _ => e.addEventListener[dom.Event]("change", s.set)

def ontimeout(interval: Int): Descend[Unit, Unit, Unit] = s =>
  _ => dom.window.setTimeout(() => s.tick(), interval) 