package nl.flotsam.monkeyman

/**
 * The interface to be implemented to create a sink.
 */
trait SinkFactory {

  def create(location: String): Option[Sink]

}
