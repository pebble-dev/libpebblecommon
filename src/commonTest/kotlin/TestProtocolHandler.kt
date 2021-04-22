import io.rebble.libpebblecommon.PacketPriority
import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint

/**
 * Test double of the protocol handler.
 *
 * If you specify [sender] parameter, it will be called whenever packet is supposed to be sent.
 * Otherwise outgoing packets collect in the [sentPackets] list.
 *
 * You can manually receive packets by calling [receivePacket].
 */
class TestProtocolHandler(private val sender: (suspend TestProtocolHandler.(PebblePacket) -> Unit)? = null) :
    ProtocolHandler {
    val sentPackets = ArrayList<PebblePacket>()

    private val receiveRegistry = HashMap<ProtocolEndpoint, suspend (PebblePacket) -> Unit>()


    override suspend fun send(packet: PebblePacket, priority: PacketPriority): Boolean {
        if (sender != null) {
            sender.invoke(this, packet)
        } else {
            sentPackets += packet
        }

        return true
    }

    override suspend fun send(packetData: UByteArray, priority: PacketPriority): Boolean {
        throw UnsupportedOperationException("Not supported for TestProtocolHandler")
    }

    override suspend fun startPacketSendingLoop(rawSend: suspend (UByteArray) -> Boolean) {
        throw UnsupportedOperationException("Not supported for TestProtocolHandler")
    }

    override suspend fun openProtocol() {
        throw UnsupportedOperationException("Not supported for TestProtocolHandler")
    }

    override suspend fun closeProtocol() {
        throw UnsupportedOperationException("Not supported for TestProtocolHandler")
    }

    override suspend fun waitForNextPacket(): ProtocolHandler.PendingPacket {
        throw UnsupportedOperationException("Not supported for TestProtocolHandler")
    }

    override suspend fun getNextPacketOrNull(): ProtocolHandler.PendingPacket? {
        throw UnsupportedOperationException("Not supported for TestProtocolHandler")
    }

    override suspend fun receivePacket(bytes: UByteArray): Boolean {
        return receivePacket(PebblePacket.deserialize(bytes))
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