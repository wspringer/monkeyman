package nl.flotsam.frontman.decorator.permalink

import nl.flotsam.frontman.decorator.ResourceDecoration
import nl.flotsam.frontman.Resource
import com.ibm.icu.text.Transliterator
import org.apache.commons.io.FilenameUtils._
import annotation.tailrec

class PermalinkDecoration(resource: Resource) extends ResourceDecoration(resource) {

  override def path =
    resource.title.map {
      str =>
        getPath(resource.path) +
        permalinkName(getBaseName(str), 60) +
        "." + getExtension(resource.path)
    }.getOrElse(resource.path)

  /**
   * Turns the String into something that wouldn't suck being part of a URI.
   *
   * Reduces whitespace to dashes, turns everything to lowercase, removes non letters or digits.
   */
  private def permalinkName(str: String, maxchars: Int = Int.MaxValue): String = {
    val cleaned = str.replace("&", " and ").replace("/", " or ")
    val parts = for {
      word <- cleaned.split("[- :;,\t\n]+")
    } yield {
      withoutAccents(word).filter(_.isLetterOrDigit).toLowerCase
    }
    join(parts, "-", maxchars)
  }

  /**
   * Strips off the accents from characters in the String, etc.
   */
  private def withoutAccents(str: String) = {
    val transliterator = Transliterator.getInstance("NFD; [:Nonspacing Mark:] Remove; NFC")
    transliterator.transliterate(str)
  }

  /**
   * Joins the Strings in a [[scala.collection.immutable.Seq]] until a certain limit is reached.
   *
   * @param parts The Strings to be joined.
   * @param separator The separator to be used.
   * @param maxchars The maximum number of characters to be included.
   */
  private def join(parts: Seq[String], separator: String, maxchars: Int = Int.MaxValue) = {
    @tailrec
    def join(parts: Seq[String], separator: String, maxchars: Int, builder: StringBuilder): String =
      parts.headOption match {
        case Some(part) =>
          if (separator.size + part.size + builder.size > maxchars) builder.toString
          else {
            builder.append(separator).append(part)
            join(parts.tail, separator, maxchars, builder)
          }
        case None =>
          builder.toString
      }
    parts.headOption match {
      case Some(part) if (part.size <= maxchars) => join(parts.tail, separator, maxchars, new StringBuilder(part))
      case _ => ""
    }

  }




}


