package nl.flotsam.frontman

import java.io.InputStream
import org.joda.time.LocalDate


trait Resource {

  /**
   * A human readable String to refer to this file. Can contain spaces.
   */
  def label: Option[String]

  /**
   * The date from which on this file should be considered published. (Note: that doesn't mean it will stay there forever, it just allows you to hold off publication if you don't want to and have a publication date if that's what you want to display.
   */
  def pubDate: Option[LocalDate]

  /**
   * The type of resource.
   */
  def contentType: String

  /**
   * The bytes.
   */
  def open: InputStream

  /**
   * The path to this resource.
   */
  def path: String

  /**
   * A number of tags associated to this resource. Eases lookups. Can be used for whatever you need.
   */
  def tags: Set[String]

}
