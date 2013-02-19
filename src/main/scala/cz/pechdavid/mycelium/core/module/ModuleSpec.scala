package cz.pechdavid.mycelium.core.module

/**
 * Created: 2/15/13 5:54 PM
 */
case class ModuleSpec(name: String, requirements: Set[String]) {
  override def equals(obj: Any) = {
    obj.isInstanceOf[ModuleSpec] && obj.asInstanceOf[ModuleSpec].name == name
  }
}
