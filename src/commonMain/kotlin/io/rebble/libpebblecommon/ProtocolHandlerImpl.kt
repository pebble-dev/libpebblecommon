package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.packets.PingPong
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
import kotlin.coroutines.coroutineContext
import kotlin.jvm.Volatile

/**
 * Default pebble protocol handler
 */
class ProtocolHandlerImpl() : ProtocolHandler {
    private val receiveRegistry = HashMap<ProtocolEndpoint, suspend (PebblePacket) -> Unit>()

    private val normalPriorityPackets = Channel<PendingPacket>(Channel.BUFFERED)
    private val lowPriorityPackets = Channel<PendingPacket>(Channel.BUFFERED)

    @Volatile
    private var idlePacketLoop: Job? = null

    init {
        startIdlePacketLoop()
    }

    override suspend fun send(packetData: UByteArray, priority: PacketPriority): Boolean {
        val targetChannel = when (priority) {
            PacketPriority.NORMAL -> normalPriorityPackets
            PacketPriority.LOW -> lowPriorityPackets
        }

        val callback = CompletableDeferred<Boolean>()
        targetChannel.send(PendingPacket(packetData, callback))

        return callback.await()
    }

    override suspend fun send(packet: PebblePacket, priority: PacketPriority): Boolean {
        return send(packet.serialize(), priority)
    }

    /**
     * Start a loop that will wait for any packets to be sent through [send] method and then
     * call provided lambda with byte array to send to the watch.
     *
     * Lambda should return *true* if sending was successful or
     * *false* if packet sending failed due to unrecoverable circumstances
     * (such as watch disconnecting completely)
     *
     * When lambda returns false, this method terminates
     */
    override suspend fun startPacketSendingLoop(rawSend: suspend (UByteArray) -> Boolean) {
        idlePacketLoop?.cancelAndJoin()

        try {
            while (coroutineContext.isActive) {
                // Receive packet first from normalPriorityPackets or from
                // lowPriorityPackets if there is no normal packet
                val packet = select<PendingPacket> {
                    normalPriorityPackets.onReceive { it }
                    lowPriorityPackets.onReceive { it }
                }

                val success = rawSend(packet.data)

                if (success) {
                    packet.callback.complete(true)
                } else {
                    packet.callback.complete(false)
                    break
                }
            }
        } finally {
            startIdlePacketLoop()
        }
    }

    /**
     * Start idle loop when there is no packet sending loop active. This loop will just
     * reject all packets with false
     */
    private fun startIdlePacketLoop() {
        idlePacketLoop = GlobalScope.launch {
            while (isActive) {
                val packet = select<PendingPacket> {
                    normalPriorityPackets.onReceive { it }
                    lowPriorityPackets.onReceive { it }
                }

                packet.callback.complete(false)
            }
        }
    }


    override fun registerReceiveCallback(
        endpoint: ProtocolEndpoint,
        callback: suspend (PebblePacket) -> Unit
    ) {
        val existingCallback = receiveRegistry.put(endpoint, callback)
        if (existingCallback != null) {
            throw IllegalStateException(
                "Duplicate callback registered for $endpoint: $callback, $existingCallback"
            )
        }
    }

    /**
     * Handle a raw pebble packet
     * @param bytes the raw pebble packet (including framing)
     * @return true if packet was handled, otherwise false
     */
    override suspend fun receivePacket(bytes: UByteArray): Boolean {
        try {
            val packet = PebblePacket.deserialize(bytes)

            when (packet) {
                //TODO move this to separate service (PingPong service?)
                is PingPong.Ping -> send(PingPong.Pong(packet.cookie.get()))
                is PingPong.Pong -> println("Pong! ${packet.cookie.get()}")
            }

            val receiveCallback = receiveRegistry[packet.endpoint]
            if (receiveCallback == null) {
                //TODO better logging
                println("Warning, ${packet.endpoint} does not have receive callback")
            } else {
                receiveCallback.invoke(packet)
            }

        } catch (e: PacketDecodeException) {
            println("Warning: failed to decode a packet: '${e.message}'")
            return false
        }
        return true
    }

    private class PendingPacket(
        val data: UByteArray,
        val callback: CompletableDeferred<Boolean>
    )
}