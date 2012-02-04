package nl.flotsam.frontman

import org.fusesource.scalate.Template

trait LayoutResolver {

  def resolve(path: String): Option[Template]

}
