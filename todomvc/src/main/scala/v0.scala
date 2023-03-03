package example


import be.adamv.momentum.concrete.{Relay, Var}
import be.adamv.momentum.{Descend, Sink, updatePresent, value, adaptNow, given}
import be.adamv.impuls.delta.{TreeMapRelayVar, TreeMapDelta, given}
import be.adamv.tsiolkovsky.tdom.{N, a, cls, div, footer, h1, html, input, li, set, ul}
import be.adamv.tsiolkovsky.frp.{onkeyup, display, children, childrenDelta, ChildNodeDelta}

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

  val filters: List[Filter] = ShowAll :: ShowActive :: ShowCompleted :: Nil

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
      if (filterVar.value == ShowCompleted)
        filterVar.set(ShowAll)
//      itemsVar.updatePresent(_ :+ TodoItem(id = lastId, text = itemText, completed = false))
      itemsVar.insert(lastId, TodoItem(id = lastId, text = itemText, completed = false))
    case UpdateText(itemId, text) =>
      itemsVar.insert(itemId, itemsVar.value(itemId).copy(text = text))
    case UpdateCompleted(itemId, completed) =>
      itemsVar.insert(itemId, itemsVar.value(itemId).copy(completed = completed))
    case Delete(itemId) =>
      itemsVar.delete(itemId)
    case DeleteCompleted => // filterValues
      itemsVar.deleteAll((k, v) => v.completed)
  }


  // --- Views ---
//  import be.adamv.tsiolkovsky.frp
  lazy val node: html.Element ?=> html.Div =
//    val $todoItems = itemsVar
//      .signal
//      .combineWith(filterVar.signal)
//      .mapN(_ filter _.passes)
    div {
      cls("todoapp")
      div {
        cls("header")
        h1("todos")
        renderNewTodoInput
      }
      div {
        hideIfNoItems
        cls("main")
        ul {
          cls("todo-list")
          // custom delta with appendChild/removeChild
          chil
//          itemsVar.map(_.map(item => renderTodoItem(item.id, item))).adapt(children)
          itemsVar.splitKeys(renderTodoItem).ddescend.map{
            case TreeMapDelta.Insert(k, v) => ChildNodeDelta.Replace(itemsVar )
            case TreeMapDelta.Insert(k, v) if k == lastId => ChildNodeDelta.Append(???)
            case TreeMapDelta.Delete(k) => ChildNodeDelta.Remove()
          }.adaptNow(childrenDelta.dsink)
//          children <-- $todoItems.split(_.id)(renderTodoItem)
        }
      }
      renderStatusBar
    }

  private def renderNewTodoInput: html.Element ?=> html.Input =
    input {
      cls("new-todo")
      set("placeholder", "What needs to be done?")
      set("autoFocus", true)
//      inContext { thisNode =>
//        // Note: mapTo below accepts parameter by-name, evaluating it on every enter key press
//        onEnterUp.mapTo(thisNode.ref.value).filter(_.nonEmpty) -->
//          commandObserver.contramap[String] { text =>
//            thisNode.ref.value = "" // clear input
//            Create(itemText = text)
//          }
//      }
    }

  // Render a single item. Note that the result is a single element: not a stream, not some virtual DOM representation.
  private def renderTodoItem(itemId: Int, $item: Descend[Unit, TodoItem, Unit]): html.UList ?=> html.LI =


    val isEditingVar = Var(false) // Example of local state
    val updateTextObserver = commandObserver.contramap[UpdateText] { updateCommand =>
      isEditingVar.set(false)
      updateCommand
    }
    li {
//      cls <-- $item.map(item => Map("completed" -> item.completed)),
//      onDblClick.filter(_ => !isEditingVar.now()).mapTo(true) --> isEditingVar.writer,
      isEditingVar
//      children <-- isEditingVar.signal.map[List[HtmlElement]] {
//        case true =>
//          renderTextUpdateInput(itemId, $item, updateTextObserver) :: Nil
//        case false =>
//          List(
//            renderCheckboxInput(itemId, $item),
//            label(child.text <-- $item.map(_.text)),
//            button(
//              cls("destroy"),
//              onClick.mapTo(Delete(itemId)) --> commandObserver
//            )
//          )
//      }
    }

  // Note that we pass reactive variables: `$item` for reading, `updateTextObserver` for writing
  private def renderTextUpdateInput(itemId: Int,
                                    $item: Descend[Unit, TodoItem, Unit],
                                    updateTextObserver: Sink[UpdateText, Unit]): html.Element ?=> html.Input  =

    input {
      cls("edit")
//      defaultValue <-- $item.map(_.text),
//      onMountFocus
//      inContext { thisNode =>
//        List(
//          onEnterUp.mapTo(UpdateText(itemId, thisNode.ref.value).specific) --> updateTextObserver,
//          onBlur.mapTo(UpdateText(itemId, thisNode.ref.value).specific) --> updateTextObserver
//        )
//      }
    }

  private def renderCheckboxInput(itemId: Int, $item: Descend[Unit, TodoItem, Unit]): html.Element ?=> html.Input  =

    input {
      cls("toggle")
      set("type", "checkbox")
//      checked <-- $item.map(_.completed),
//      inContext { thisNode =>
//        onInput.mapTo(
//          UpdateCompleted(itemId, completed = thisNode.ref.checked)
//        ) --> commandObserver
//      }
    }

  private def renderStatusBar: html.Element ?=> html.Element =


    footer {
      hideIfNoItems
      cls("footer")
//      span(
//        cls("todo-count"),
//        child.text <-- itemsVar.signal
//          .map(_.count(!_.completed))
//          .map(pluralize(_, "item left", "items left")),
//      ),
      ul {
        cls("filters")
//        filters.map(filter => li(renderFilterButton(filter)))
      }
//      child.maybe <-- itemsVar.signal.map { items =>
//        if (items.exists(ShowCompleted.passes)) Some(
//          button(
//            cls("clear-completed"),
//            "Clear completed",
//            onClick.map(_ => DeleteCompleted) --> commandObserver
//          )
//        ) else None
//      }
    }

  private def renderFilterButton(filter: Filter): html.Element ?=> html.Element =


    a {
//      cls.toggle("selected") <-- filterVar.signal.map(_ == filter),
//      onClick.preventDefault.mapTo(filter) --> filterVar.writer,
      N"${filter.name}"
    }

  // Every little thing in Laminar can be abstracted away
  private def hideIfNoItems(using html.Element): Unit =
    itemsVar.map { items =>
      if (items.nonEmpty) "" else "none"
    }.adaptNow(display)


  // --- Generic helpers ---
  private def pluralize(num: Int, singular: String, plural: String): String =
    s"$num ${if (num == 1) singular else plural}"

  private val onEnterUp: html.Element ?=> Descend[Unit, dom.KeyboardEvent, Unit] =
    onkeyup.filter(_.keyCode == dom.KeyCode.Enter)

  def init =
    itemsVar.set(List[TodoItem]())
    filterVar.set(ShowAll)
end TodoMvcApp


@main def m =
  given html.Div = dom.document.querySelector("#board").asInstanceOf
  TodoMvcApp.node
  TodoMvcApp.init
