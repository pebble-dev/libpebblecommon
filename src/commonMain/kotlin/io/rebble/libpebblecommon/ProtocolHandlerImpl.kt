package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.PhoneAppVersion.ProtocolCapsFlag
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Default pebble protocol handler
 */
@OptIn(ExperimentalUnsignedTypes::class)
class ProtocolHandlerImpl(private val bluetoothConnection: BluetoothConnection) : ProtocolHandler {
    var protocolCaps: UInt = ProtocolCapsFlag.makeFlags(ProtocolCapsFlag.SupportsSendTextApp)

    private val receiveRegistry = HashMap<ProtocolEndpoint, suspend (PebblePacket) -> Unit>()
    private val protocolMutex = Mutex()

    init {
        PacketRegistry.setup()

        bluetoothConnection.setReceiveCallback(this::handle)
    }

    /**
     * Send data to the watch. MUST be called within [withWatchContext]
     */
    override suspend fun send(packet: PebblePacket) {
        println("Sending on EP ${packet.endpoint}: ${packet.type}")
        bluetoothConnection.sendPacket(packet.serialize().toByteArray())
    }

    /**
     * Calls the specified block within watch sending context. Only one block within watch context
     * can be active at the same time, ensuring atomic bluetooth sending.
     */
    override suspend fun <T> withWatchContext(block: suspend () -> T): T {
        return protocolMutex.withLock {
            block()
        }
    }

    override fun registerReceiveCallback(endpoint: ProtocolEndpoint, callback: suspend (PebblePacket) -> Unit) {
        val existingCallback = receiveRegistry.put(endpoint, callback)
        if (existingCallback != null) {
            throw IllegalStateException(
                "Duplicate callback registered for $endpoint: $callback, $existingCallback")
        }
    }

    /**
     * Handle a raw pebble packet
     * @param bytes the raw pebble packet (including framing)
     * @return true if packet was handled, otherwise false
     */
    private suspend fun handle(bytes: ByteArray): Boolean {
        try {
            val packet = PebblePacket.deserialize(bytes.toUByteArray())

            when (packet) {
                //TODO move this to separate service (PingPong service?)
                is PingPong.Ping -> send(PingPong.Pong(packet.cookie.get()))
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
                    send(res)
                }
            }

            val receiveCallback = receiveRegistry[packet.endpoint]
            if (receiveCallback == null) {
                //TODO better logging
                println("Warning, ${packet.endpoint} does not have receive callback")
            } else {
                receiveCallback.invoke(packet)
            }

        }catch (e: PacketDecodeException){
            println("Warning: failed to decode a packet: '${e.message}'")
            return false
        }
        return true
    }
}