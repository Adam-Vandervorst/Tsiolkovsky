package todomvc


import be.adamv.momentum.concrete.{Relay, Var}
import be.adamv.momentum.{Descend, Sink, adaptNow, updatePresent, value, zipLeft, given}
import be.adamv.impuls.delta.{BitRelayVar, BitDelta, TreeMapDelta, TreeMapRelayVar, given}
import be.adamv.tsiolkovsky.tdom.{N, a, button, cls, div, footer, h1, html, input, label, li, set, ul, span}
import be.adamv.tsiolkovsky.frp.{ChildNodeDelta, child, children, childrenDelta, clsToggle, display, onclick, ondblclick, onkeyup, defaultValue, onmount, onblur, checked, oninput}

import language.implicitConversions
import collection.mutable
import org.scalajs.dom


object TodoMvcApp:
  case class TodoItem(id: Int, text: String, completed: Boolean)

  enum Filter(val name: String, val passes: TodoItem => Boolean):
    case ShowAll extends Filter("All", _ => true)
    case ShowActive extends Filter("Active", !_.completed)
    case ShowCompleted extends Filter("Completed", _.completed)
  import Filter.*

  enum Command:
    case Create(itemText: String)
    case UpdateText(itemId: Int, text: String)
    case UpdateCompleted(itemId: Int, completed: Boolean)
    case Delete(itemId: Int)
    case DeleteCompleted
  import Command.*

  // --- State ---
  private var lastId = 1
//  private val itemsVar = Relay[List[TodoItem]]()
  private val itemsVar = TreeMapRelayVar[Int, TodoItem](mutable.TreeMap.empty)
  private val filterVar = Relay[Filter]()
  private val commandObserver: Sink[Command, Unit] = {
    case Create(itemText) =>
      lastId += 1
      if (filterVar.value == ShowCompleted) {
        filterVar.set(ShowAll)
      }
      println(s"create ${itemText} ${itemsVar.value}")
      itemsVar.insert(lastId, TodoItem(id = lastId, text = itemText, completed = false))
      println(s"inserted ${itemsVar.value}")
    case UpdateText(itemId, text) =>
      println("updated")
      itemsVar.insert(itemId, itemsVar.value(itemId).copy(text = text))
    case UpdateCompleted(itemId, completed) =>
      println(s"pre update completed $itemId $completed")
      val v = itemsVar.value(itemId)
      println("got value")
      itemsVar.insert(itemId, v.copy(completed = completed))
      println("post update completed")
    case Delete(itemId) =>
      println(s"delete $itemId")
      itemsVar.delete(itemId)
    case DeleteCompleted => // filterValues
      println(s"deleteCompleted")
      itemsVar.deleteAll((k, v) => v.completed)
  }


  // --- Views ---
  lazy val node: html.Element ?=> html.Div =
    div {
      cls("todoapp")
      div {
        cls("header")
        h1 { N"todos" }
        renderNewTodoInput
      }
      div {
        hideIfNoItems
        cls("main")
        ul {
          cls("todo-list")
          val rendered = itemsVar.splitKeys(renderTodoItem)
          rendered.ddescend.map{
            case TreeMapDelta.Insert(k, v) if k == lastId => ChildNodeDelta.Append(v)
            case TreeMapDelta.Insert(k, v) => ChildNodeDelta.Replace(v, rendered.value(k))
            case TreeMapDelta.Delete(k) => ChildNodeDelta.Remove(rendered.value(k))
          }.adaptNow(childrenDelta.dsink)
//          rendered.ddescend.map(_ => rendered.value.values.toSeq).adaptNow(children)
//          itemsVar.dsink.set(TreeMapDelta.Delete[Int, example.TodoMvcApp.TodoItem](0))
        }
      }
      renderStatusBar
    }

  private def renderNewTodoInput: html.Element ?=> html.Input =
    input {
      cls("new-todo")
      set("placeholder", "What needs to be done?")
      set("autoFocus", true)
      val thisNode = summon[html.Input]
      // Note: mapTo below accepts parameter by-name, evaluating it on every enter key press
      onEnterUp.map(_ => thisNode.value).filter(_.nonEmpty).adaptNow(
        commandObserver.contramap[String] { text =>
        thisNode.value = "" // clear input
        Create(itemText = text)
      })

    }

  // Render a single item. Note that the result is a single element: not a stream, not some virtual DOM representation.
  private def renderTodoItem(itemId: Int, $item: Descend[Unit, TodoItem, Unit]): html.UList ?=> html.LI =
    dom.console.log(s"rendering $itemId")
    $item.adaptNow(td => println(s"test $td"))
    val isEditingVar = BitRelayVar(true)
    val updateTextObserver = commandObserver.contramap[Command] { updateCommand =>
      isEditingVar.setLow()
      updateCommand
    }
    li {
//      filterVar.zipLeft($item).map((f: Filter, td: TodoItem) => if f.passes(td) then "" else "none").adaptNow(display)
      $item.map(item => "completed" -> item.completed).tapEach(i => println(s"compl $i")).adaptNow(clsToggle)
      ondblclick.filter(_ => !isEditingVar.value).map(_ => ()).adaptNow(isEditingVar.riseHandle)

      isEditingVar.ddescend.map {
        case BitDelta.Rise =>
          println("in true")
          renderTextUpdateInput(itemId, $item, updateTextObserver) :: Nil
        case BitDelta.Fall =>
          println("in false")
          List(
            renderCheckboxInput(itemId, $item),
            label {
              println("pre label text")
              $item.map(i => N"${i.text}").tapEach(i => println(s"text $i")).adaptNow(child)
              println("post label text")
            },
            button {
              cls("destroy")
              onclick.map(_ => Delete(itemId)).adaptNow(commandObserver)
            }
          )
      }.adaptNow(children)
      println("pre set false")
      isEditingVar.setLow()
      println("post set false")
    }

// Note that we pass reactive variables: `$item` for reading, `updateTextObserver` for writing
  private def renderTextUpdateInput(itemId: Int,
                                    $item: Descend[Unit, TodoItem, Unit],
                                    updateTextObserver: Sink[Command, Unit]): html.Element ?=> html.Input  =

    input {
      cls("edit")
      $item.map(_.text).adaptNow(defaultValue)
      val in = summon[html.Input]
      onmount.adaptNow(_ => in.focus())
      onEnterUp.map(_ => UpdateText(itemId, in.value)).adaptNow(updateTextObserver)
      onblur.map(_ => UpdateText(itemId, in.value)).adaptNow(updateTextObserver)
    }

  private def renderCheckboxInput(itemId: Int, $item: Descend[Unit, TodoItem, Unit]): html.Element ?=> html.Input  =

    input {
      cls("toggle")
      set("type", "checkbox")
      $item.map(_.completed).adaptNow(checked)
      val in = summon[html.Input]
      oninput.map(_ =>
        UpdateCompleted(itemId, completed = in.checked)
      ).adaptNow(commandObserver)
    }

  private def renderStatusBar: html.Element ?=> html.Element =

    footer {
      hideIfNoItems
      cls("footer")
      span {
        cls("todo-count")
        itemsVar.drelay.map(_ => itemsVar.value)
          .map((m: mutable.TreeMap[Int, example.TodoMvcApp.TodoItem]) => m.values.count(!_.completed))
          .map(pluralize(_, "item left", "items left"))
      }
      ul {
        cls("filters")
        Filter.values.foreach(filter => li { renderFilterButton(filter) })
      }

      itemsVar.drelay.map(_ => itemsVar.value)
        .map { (items: mutable.TreeMap[Int, example.TodoMvcApp.TodoItem]) =>
        if (items.values.exists(ShowCompleted.passes)) Seq(
          button(
            cls("clear-completed"),
            "Clear completed",
            onclick.map(_ => DeleteCompleted).adaptNow(commandObserver)
          )
        ) else Nil
      }.adaptNow(children)
    }

  private def renderFilterButton(filter: Filter): html.Element ?=> html.Element =
    a {
      filterVar.map(f => "selected" -> (f == filter)).adaptNow(clsToggle)
      onclick.map(e => { e.preventDefault(); filter }).adaptNow(filterVar)
      N"${filter.name}"
    }

  private def hideIfNoItems(using e: html.Element): Unit =
    itemsVar.ddescend.map(_ => if itemsVar.value.nonEmpty then "" else "none").adaptNow(display)


  // --- Generic helpers ---
  private def pluralize(num: Int, singular: String, plural: String)(using html.Element): dom.Node =
    N"$num ${if num == 1 then singular else plural}"

  private val onEnterUp: html.Element ?=> Descend[Unit, dom.KeyboardEvent, Unit] =
    onkeyup.filter(_.keyCode == dom.KeyCode.Enter)

  def init() =
//    lastId += 1
//    println(s"init create ${itemsVar.value}")
//    itemsVar.insert(lastId, TodoItem(id = lastId, text = "testing", completed = false))
//    println(s"init insert ${itemsVar.value}")
      filterVar.set(ShowAll)
end TodoMvcApp


@main def m =
  given html.Div = dom.document.querySelector("#board").asInstanceOf
  TodoMvcApp.init()
  TodoMvcApp.node
//  TodoMvcApp.init
