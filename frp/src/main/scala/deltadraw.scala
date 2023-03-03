package be.adamv.tsiolkovsky.frp

import org.scalajs.dom

import be.adamv.momentum.{*, given}
import be.adamv.impuls.*




enum ChildNodeDelta extends Delta[dom.Node]:
  case Replace(n: dom.Node, old: dom.Node)
  case Insert(n: dom.Node, before: dom.Node)
  case Remove(n: dom.Node)
  case Append(n: dom.Node)
import ChildNodeDelta.*


trait ChildNodeHandle extends UDeltaSink[dom.Node, ChildNodeDelta]:
  self =>
  lazy val replaceHandle: Sink[(dom.Node, dom.Node), Unit] = self.dsink.contramap(Replace)
  lazy val insertHandle: Sink[(dom.Node, dom.Node), Unit] = self.dsink.contramap(Insert)
  lazy val removeHandle: Sink[dom.Node, Unit] = self.dsink.contramap(Remove)
  lazy val appendHandle: Sink[dom.Node, Unit] = self.dsink.contramap(Append)


trait ChildNodeView extends UDeltaDescend[dom.Node, ChildNodeDelta]:
  self =>
  lazy val replaceHandle: Descend[Unit, (dom.Node, dom.Node), Unit] = self.ddescend.collect { case Replace(n, old) => (n, old) }
  lazy val insertHandle: Descend[Unit, (dom.Node, dom.Node), Unit] = self.ddescend.collect { case Insert(n, before) => (n, before) }
  lazy val removeHandle: Descend[Unit, dom.Node, Unit] = self.ddescend.collect { case Remove(n) => n }
  lazy val appendHandle: Descend[Unit, dom.Node, Unit] = self.ddescend.collect { case Append(n) => n }


class ChildNodeRelayVar(initial: dom.Node) extends UDeltaRelayVar[dom.Node, ChildNodeDelta](initial):
  override def integration(dt: ChildNodeDelta): dom.Node = dt match
    case Replace(n, old) => value.replaceChild(n, old); value
    case Insert(n, before) => value.insertBefore(n, before); value
    case Remove(n) => value.removeChild(n); value
    case Append(n) => value.appendChild(n); value

  def forEachNode(f: dom.Node => Unit): Unit =
    var n = value.firstChild
    while n != null do
      f(n)
      n = n.nextSibling

  def replace(n: dom.Node, old: dom.Node): Unit =
    integrate(Replace(n, old))

  def insert(n: dom.Node, before: dom.Node): Unit =
    integrate(Insert(n, before))

  def remove(n: dom.Node): Unit =
    integrate(Remove(n))

  def append(n: dom.Node): Unit =
    integrate(Append(n))

  def removeAll(p: dom.Node => Boolean): Int =
    val todo = scalajs.js.Array[dom.Node]()
    forEachNode { n =>
      if p(n) then todo.push(n)
    }
    todo.foreach(remove)
    todo.length

def childrenDelta(using e: dom.html.Element): ChildNodeRelayVar =
    ChildNodeRelayVar(e)
