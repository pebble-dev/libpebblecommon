package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.protocol.PacketRegistry
import io.rebble.libpebblecommon.protocol.PebblePacket
import io.rebble.libpebblecommon.protocol.ProtocolEndpoint
import io.rebble.libpebblecommon.PhoneAppVersion.ProtocolCapsFlag
@ExperimentalUnsignedTypes
class ProtocolHandler(private val send: (ByteArray) -> Unit) {
    var protocolCaps: UInt = ProtocolCapsFlag.makeFlags(ProtocolCapsFlag.SupportsSendTextApp)

    init {
        PacketRegistry.setup()
    }

    private fun _send(bytes: ByteArray, packet: PebblePacket) {
        println("Sending on EP ${packet.endpoint}: ${packet.type}")
        send(bytes)
    }
    fun handle(bytes: ByteArray) {
        try {
            val packet = PebblePacket.deserialize(bytes.toUByteArray())

            when (packet.endpoint) {
                ProtocolEndpoint.PING -> {
                    when (packet.type) {
                        PingPong.Message.Ping.value -> send(PingPong.Pong((packet as PingPong.Ping).cookie.get()).serialize().toByteArray())
                        PingPong.Message.Pong.value -> println("Pong! ${(packet as PingPong.Pong).cookie.get()}")
                    }
                }

                ProtocolEndpoint.PHONE_VERSION -> {
                    val res = PhoneAppVersion.AppVersionResponse()
                    res.protocolVersion.set(0xffffffffu)
                    res.sessionCaps.    set(0u)
                    res.platformFlags.  set(0u)
                    res.majorVersion.   set(2u)
                    res.minorVersion.   set(2u)
                    res.bugfixVersion.  set(0u)
                    res.platformFlags.  set(protocolCaps)
                    _send(res.serialize().toByteArray(), res)
                }

                else -> println("TODO: ${packet.endpoint}")
            }
        }catch (e: PacketDecodeException){
            println("Warning: failed to decode a packet: '${e.message}'")
        }
    }
}