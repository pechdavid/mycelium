/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.extension.amqp

import akka.actor.ExtendedActorSystem
import akka.serialization.Serializer

/**
 * Json serializer
 *
 * @param system
 */
class LiftJsonSerializer(system: ExtendedActorSystem) extends Serializer {

  def identifier = 27015

  def includeManifest = false

  def toBinary(o: AnyRef) = {
    import net.liftweb.json.{Serialization, NoTypeHints}
    implicit val formats = Serialization.formats(NoTypeHints)
    val json = Serialization.write(o)
    json.getBytes("utf-8")
  }

  def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]) = {
    bytes
  }
}
