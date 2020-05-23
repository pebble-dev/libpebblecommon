package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.PhoneAppVersion.ProtocolCapsFlag

/**
 * Default pebble protocol handler
 * @param send callback to handle replying to packets
 */
@ExperimentalUnsignedTypes
open class ProtocolHandler(private val send: (ByteArray) -> Unit) {
    var protocolCaps: UInt = ProtocolCapsFlag.makeFlags(ProtocolCapsFlag.SupportsSendTextApp)

    init {
        PacketRegistry.setup()
    }

    private fun _send(bytes: ByteArray, packet: PebblePacket) {
        println("Sending on EP ${packet.endpoint}: ${packet.type}")
        send(bytes)
    }

    /**
     * Handle a raw pebble packet
     * @param bytes the raw pebble packet (including framing)
     * @return true if packet was handled, otherwise false
     */
    open fun handle(bytes: ByteArray): Boolean {
        try {
            when (val packet = PebblePacket.deserialize(bytes.toUByteArray())) {
                is PingPong.Ping -> send(PingPong.Pong(packet.cookie.get()).serialize().toByteArray())
                is PingPong.Pong -> println("Pong! ${packet.cookie.get()}")

                is PhoneAppVersion.AppVersionRequest -> {
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

                else -> {
                    println("TODO: ${packet.endpoint}")
                    return false
                }
            }
        }catch (e: PacketDecodeException){
            println("Warning: failed to decode a packet: '${e.message}'")
            return false
        }
        return true
    }
}