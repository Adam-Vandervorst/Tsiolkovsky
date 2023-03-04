package be.adamv.tsiolkovsky.tdom

import org.scalajs.dom


class UnspecifiedElem extends dom.html.Element

type ParentsOf[X <: dom.html.Element] <: dom.html.Element = X match
  case dom.html.LI => dom.html.UList | dom.html.OList | dom.html.Menu
  case dom.html.TableCell => dom.html.TableRow
  case dom.html.TableSection => dom.html.TableRow
  case dom.html.TableRow => dom.html.Table
  case dom.html.Heading => dom.html.Element
  case UnspecifiedElem => dom.html.Element
  case _ => dom.html.Element


type TagOf[X <: String] <: dom.html.Element = X match
  case "a" => dom.html.Anchor
  case "b" => UnspecifiedElem
  case "nav" => UnspecifiedElem
  case "footer" => UnspecifiedElem
  case "main" => UnspecifiedElem
  case "section" => UnspecifiedElem
  case "h1" | "h2" | "h3" | "h4" | "h5" | "h6" => dom.html.Heading
  case "ul" => dom.html.UList
  case "ol" => dom.html.OList
  case "li" => dom.html.LI
  case "select" => dom.html.Select
  case "option" => dom.html.Option
  case "button" => dom.html.Button
  case "input" => dom.html.Input
  case "div" => dom.html.Div
  case "p" => dom.html.Paragraph
  case "td" => dom.html.TableCell
  case "th" => dom.html.TableSection
  case "tr" => dom.html.TableRow
  case "table" => dom.html.Table
  case "style" => dom.html.Style
  case "script" => dom.html.Script
  case "link" => dom.html.Link
  case "label" => dom.html.Label
  case "span" => dom.html.Span
