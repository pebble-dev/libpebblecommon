package io.rebble.libpebblecommon.services.blobdb

import io.rebble.libpebblecommon.blobdb.BlobCommand
import io.rebble.libpebblecommon.blobdb.BlobResponse
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket

@ExperimentalUnsignedTypes
/**
 * Singleton to handle sending BlobDB commands cleanly, by allowing registered callbacks to be triggered when the sending packet receives a BlobResponse
 * @see BlobResponse
 */
object BlobDBService {
    private var send: ((PebblePacket) -> Unit)? = null
    private val pending: MutableMap<UShort, (BlobResponse) -> Unit> = mutableMapOf()

    /**
     * Setup the singleton so it is able to send packets
     * @param send the call used to send packets
     */
    fun init(send: (packet: PebblePacket) -> Unit) {
        this.send = send
    }

    /**
     * Send a BlobCommand, with an optional callback to be triggered when a matching BlobResponse is received
     * @see BlobCommand
     * @see BlobResponse
     * @param packet the packet to send
     * @param callback the callback to trigger on BlobResponse, NOTE: not guaranteed to trigger
     */
    fun send(packet: BlobCommand, callback: (BlobResponse) -> Unit) {
        pending[packet.token.get()] = callback
        (send!!)(packet)
    }

    /**
     * Intended to be called via a protocol handler, handles BlobResponse packets
     * @return true if the packet was handled, false if it wasn't (e.g. not sent via send())
     * @see send
     * @see BlobResponse
     */
    fun receive(packet: BlobResponse): Boolean {
        val cb = pending[packet.token.get()] ?: return false
        cb(packet)
        pending.remove(packet.token.get())
        return true
    }
}