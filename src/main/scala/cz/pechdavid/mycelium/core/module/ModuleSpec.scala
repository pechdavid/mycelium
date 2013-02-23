package cz.pechdavid.mycelium.core.module

/**
 * Created: 2/15/13 5:54 PM
 */
case class ModuleSpec(name: String, requirements: Set[String] = Set.empty, args: Option[AnyRef] = None,
                       initialLaunchPattern: Option[String] = None) {
  override def equals(obj: Any) = {
    obj.isInstanceOf[ModuleSpec] && obj.asInstanceOf[ModuleSpec].name == name
  }

  def launchPattern = initialLaunchPattern match {
    case Some(pat) =>
      pat

    case None =>
      name
  }
}
