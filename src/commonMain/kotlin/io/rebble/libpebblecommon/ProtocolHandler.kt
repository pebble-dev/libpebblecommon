package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint

interface ProtocolHandler {
    /**
     * Send data to the watch. MUST be called within [withWatchContext]
     */
    suspend fun send(packet: PebblePacket)

    /**
     * Calls the specified block within watch sending context. Only one block within watch context
     * can be active at the same time, ensuring atomic bluetooth sending.
     */
    suspend fun <T> withWatchContext(block: suspend () -> T): T
    fun registerReceiveCallback(endpoint: ProtocolEndpoint, callback: suspend (PebblePacket) -> Unit)
}