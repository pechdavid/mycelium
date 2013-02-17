package cz.pechdavid.mycelium.extension.amqp

import akka.actor.ExtendedActorSystem
import akka.serialization.Serializer

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
