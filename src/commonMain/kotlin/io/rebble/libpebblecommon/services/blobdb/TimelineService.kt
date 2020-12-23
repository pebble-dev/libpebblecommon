package io.rebble.libpebblecommon.services.blobdb

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.blobdb.TimelineAction
import io.rebble.libpebblecommon.packets.blobdb.TimelineItem
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.services.ProtocolService

/**
 * Singleton that handles receiving of timeline actions.
 *
 * Consumer must set [actionHandler] that will then be called whenever user triggers timeline pin
 * action.
 */
class TimelineService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    var actionHandler: (suspend (TimelineAction.InvokeAction) -> ActionResponse)? = null

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.TIMELINE_ACTIONS, this::receive)
    }


    suspend fun receive(packet: PebblePacket) {
        if (packet !is TimelineAction.InvokeAction) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        val result = actionHandler?.invoke(packet) ?: return

        val returnPacket = TimelineAction.ActionResponse().apply {
            itemID.set(packet.itemID.get())
            response.set(if (result.success) 0u else 1u)
            numAttributes.set(result.attributes.size.toUByte())
            attributes.list = result.attributes
        }

        protocolHandler.send(returnPacket)
    }

    class ActionResponse(
        val success: Boolean,
        val attributes: List<TimelineItem.Attribute> = emptyList()
    )
}