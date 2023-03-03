package be.adamv.tsiolkovsky.tdom

export org.scalajs.dom.html


def spawn(tag: String) =
  (init: TagOf[tag.type] ?=> Unit) => (p: ParentsOf[TagOf[tag.type]]) ?=>
    val e = createElement(tag)
    init(using e)
    p.appendChild(e)
    e

def createElement(tag: String): TagOf[tag.type] =
  org.scalajs.dom.document.createElement(tag).asInstanceOf[TagOf[tag.type]]


def h1 = spawn("h1")
def h2 = spawn("h2")
def h3 = spawn("h3")
def h4 = spawn("h4")
def h5 = spawn("h5")
def h6 = spawn("h6")

def a = spawn("a")

def b = spawn("b")

def nav = spawn("nav")
def footer = spawn("footer")
def main = spawn("main")
def section = spawn("section")

def ul = spawn("ul")
def ol = spawn("ol")
def li = spawn("li")

def select = spawn("select")
def option = spawn("option")

def div = spawn("div")
def p = spawn("p")

def td = spawn("td")
def th = spawn("th")
def tr = spawn("tr")
def table = spawn("table")

def button = spawn("button")
def input = spawn("input")


extension (sc: StringContext)(using p: html.Element)
  def N(args: Any*): org.scalajs.dom.Text =
    val tn = org.scalajs.dom.document.createTextNode(sc.s(args*))
    p.appendChild(tn)
    tn