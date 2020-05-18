package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.protocol.PacketRegistry
import io.rebble.libpebblecommon.protocol.PebblePacket
import io.rebble.libpebblecommon.protocol.ProtocolEndpoint

@ExperimentalUnsignedTypes
class ProtocolHandler(private val send: (ByteArray) -> Unit) {
    init {
        PacketRegistry.setup()
    }

    private fun _send(bytes: ByteArray, packet: PebblePacket) {
        println("Sending on EP ${packet.endpoint}: ${packet.type}")
        send(bytes)
    }
    fun handle(bytes: ByteArray) {
        val packet = PebblePacket.deserialize(bytes.toUByteArray())

        when (packet.endpoint) {
            ProtocolEndpoint.PING -> {
                when (packet.type) {
                    PingPong.Message.Ping.value -> PingPong.Pong((packet as PingPong.Ping).cookie.get())
                    PingPong.Message.Pong.value -> println("Pong! ${(packet as PingPong.Pong).cookie.get()}")
                }
            }

            ProtocolEndpoint.PHONE_VERSION -> {
                val res = PhoneAppVersion.AppVersionResponse()
                res.protocolVersion.set(1u)
                res.majorVersion.   set(2u)
                res.minorVersion.   set(2u)
                res.bugfixVersion.  set(0u)
                //TODO: Platform flags + CAPS
                _send(res.serialize().toByteArray(), packet)
            }

            else -> println("TODO: ${packet.endpoint}")
        }
    }
}