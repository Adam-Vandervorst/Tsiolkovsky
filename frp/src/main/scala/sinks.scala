package be.adamv.tsiolkovsky.frp

import org.scalajs.dom
import scala.scalajs.js

import be.adamv.momentum.Sink

def _set(name: String, value: js.Any)(using e: dom.html.Element): Unit =
  e.asInstanceOf[js.Dynamic].updateDynamic(name)(value)

def clsToggle(using e: dom.html.Element): Sink[(String, Boolean), Unit] =
  (s: String, b: Boolean) => e.classList.toggle(s, b)

def innerText(using e: dom.html.Element): Sink[String, Unit] =
  (s: String) => e.innerText = s

def display(using e: dom.html.Element): Sink[String, Unit] =
  (s: String) => e.style.display = s

def children(using e: dom.html.Element): Sink[Seq[dom.html.Element], Unit] =
  (s: Seq[dom.html.Element]) => e.replaceChildren(s*)

def child(using e: dom.Node): Sink[dom.Node, Unit] =
  (s: dom.Node) => if e.hasChildNodes() then e.replaceChild(s, e.firstChild) else e.appendChild(s)

def value(using e: dom.html.Select | dom.html.Option): Sink[String, Unit] =
  (s: String) => e match
    case e: dom.html.Select => e.value = s
    case e: dom.html.Option => e.value = s

def defaultValue(using e: dom.html.Input): Sink[String, Unit] =
  (s: String) => e.defaultValue = s

def checked(using e: dom.html.Input): Sink[Boolean, Unit] =
  (b: Boolean) => e.checked = b

def autofocus(using e: dom.html.Element): Sink[Boolean, Unit] =
  (b: Boolean) => _set("autofocus", b)
