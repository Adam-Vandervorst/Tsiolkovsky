package be.adamv.tsiolkovsky.frp

import org.scalajs.dom
import scala.scalajs.js

import be.adamv.momentum.Sink


def clsToggle(using e: dom.html.Element): Sink[(String, Boolean), Unit] =
  (s: String, b: Boolean) => e.classList.toggle(s, b)

def innerText(using e: dom.html.Element): Sink[String, Unit] =
  (s: String) => e.innerText = s

def display(using e: dom.html.Element): Sink[String, Unit] =
  (s: String) => e.style.display = s

def children(using e: dom.html.Element): Sink[Seq[dom.html.Element], Unit] =
  (s: Seq[dom.html.Element]) => e.childNodes.replaceChildren(s*)

def child(using e: dom.html.Element): Sink[dom.html.Element, Unit] =
  (s: dom.html.Element) => e.replaceChildren(s)

def value(using e: dom.html.Select | dom.html.Option): Sink[String, Unit] =
  (s: String) => e match
    case e: dom.html.Select => e.value = s
    case e: dom.html.Option => e.value = s