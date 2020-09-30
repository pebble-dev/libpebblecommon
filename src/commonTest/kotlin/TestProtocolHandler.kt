import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.ProtocolHandler

/**
 * Test double of the protocol handler.
 *
 * If you specify [sender] parameter, it will be called whenever packet is supposed to be sent.
 * Otherwise outgoing packets collect in the [sentPackets] list.
 *
 * You can manually receive packets by calling [receivePacket].
 */
@OptIn(ExperimentalUnsignedTypes::class)
class TestProtocolHandler(private val sender: (suspend TestProtocolHandler.(PebblePacket) -> Unit)? = null) :
    ProtocolHandler {
    val sentPackets = ArrayList<PebblePacket>()

    private val receiveRegistry = HashMap<ProtocolEndpoint, suspend (PebblePacket) -> Unit>()


    /**
     * Send data to the watch. MUST be called within [withWatchContext]
     */
    override suspend fun send(packet: PebblePacket) {
        if (sender != null) {
            sender.invoke(this, packet)
        } else {
            sentPackets += packet
        }
    }

    /**
     * Calls the specified block within watch sending context. Only one block within watch context
     * can be active at the same time, ensuring atomic bluetooth sending.
     */
    override suspend fun <T> withWatchContext(block: suspend () -> T): T {
        // Assume tests are run as single thread, no need for mutexes
        return block()
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
     * Handle pebble packet
     */
    suspend fun receivePacket(packet: PebblePacket): Boolean {
        val receiveCallback = receiveRegistry[packet.endpoint]
        if (receiveCallback == null) {
            throw IllegalStateException("${packet.endpoint} does not have receive callback")
        } else {
            receiveCallback.invoke(packet)
        }

        return true
    }
}