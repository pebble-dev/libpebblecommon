package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint

interface ProtocolHandler {
    /**
     * Send data to the watch.
     *
     * @param priority Priority of the packet. Higher priority items will be sent before
     * low priority ones. Use low priority for background messages like sync and higher priority
     * for user-initiated actions that should be transmitted faster
     *
     * @return *true* if sending was successful, *false* if packet sending failed due to
     * unrecoverable circumstances (such as watch disconnecting completely).
     */
    suspend fun send(
        packet: PebblePacket,
        priority: PacketPriority = PacketPriority.NORMAL
    ): Boolean

    /**
     * Send raw data to the watch.
     *
     * @param priority Priority of the packet. Higher priority items will be sent before
     * low priority ones. Use low priority for background messages like sync and higher priority
     * for user-initiated actions that should be transmitted faster
     *
     * @return *true* if sending was successful, *false* if packet sending failed due to
     * unrecoverable circumstances (such as watch disconnecting completely).
     */
    suspend fun send(
        packetData: UByteArray,
        priority: PacketPriority = PacketPriority.NORMAL
    ): Boolean

    suspend fun startPacketSendingLoop(rawSend: suspend (UByteArray) -> Boolean)

    fun registerReceiveCallback(
        endpoint: ProtocolEndpoint,
        callback: suspend (PebblePacket) -> Unit
    )

    suspend fun receivePacket(bytes: UByteArray): Boolean
}