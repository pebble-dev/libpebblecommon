package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.CompletableDeferred

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

    suspend fun openProtocol()
    suspend fun closeProtocol()

    /**
     *  Wait for the next packet in the sending queue. After you are done handling this packet, you must call
     *  [PendingPacket.notifyPacketStatus].
     *
     *  This is a lower level method. Before getting any packets, you MUST call [openProtocol] and
     *  after you are done, you MUST call [closeProtocol] to trigger any cleanup operations.
     */
    suspend fun waitForNextPacket(): PendingPacket

    /**
     *  Get the next packet in the sending queue or *null* if there are no waiting packets.
     *  After you are done handling this packet, you must call [PendingPacket.notifyPacketStatus].
     *
     *  This is a lower level method. Before getting any packets, you MUST call [openProtocol] and
     *  after you are done, you MUST call [closeProtocol] to trigger any cleanup operations.
     */
    suspend fun getNextPacketOrNull(): PendingPacket?

    class PendingPacket(
        val data: UByteArray,
        private val callback: CompletableDeferred<Boolean>
    ) {
        /**
         * @param success *true* if sending was successful or
         * *false* if packet sending failed due to unrecoverable circumstances
         * (such as watch disconnecting completely)
         */
        fun notifyPacketStatus(success: Boolean) {
            callback.complete(success)
        }
    }

}