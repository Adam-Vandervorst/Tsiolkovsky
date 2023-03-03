package be.adamv.tsiolkovsky.tdom

import org.scalajs.dom
import scala.scalajs.js


def set(name: String, value: js.Any)(using e: dom.html.Element): Unit =
  e.asInstanceOf[js.Dynamic].updateDynamic(name)(value)

def cls(names: String*)(using e: dom.html.Element): Unit =
  names.foreach(name => e.classList.add(name))