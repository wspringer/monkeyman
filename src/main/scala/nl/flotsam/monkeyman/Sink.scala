package nl.flotsam.monkeyman

/**
 * The interface implemented by objects storing resources.
 */
trait Sink {

  def receive(resource: Resource)

}
