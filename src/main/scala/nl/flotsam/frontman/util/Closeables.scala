package nl.flotsam.frontman.util

object Closeables {

  def using[A, B <: {def close() : Unit}](opener: => B)(f: B => A): A = {
    val closeable = opener
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
  }

}
