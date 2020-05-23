package io.rebble.libpebblecommon.blobdb

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.bytes
import io.rebble.libpebblecommon.exceptions.PacketEncodeException
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.structmapper.*
import io.rebble.libpebblecommon.util.DataBuffer

@ExperimentalUnsignedTypes
class TimelineItem(
    itemId: Uuid,
    parentId: Uuid, timestamp: UInt,
    duration: UShort, type: Type,
    flags: UShort, layout: UByte,
    attributes: List<Attribute>,
    actions: List<Action>
): Mappable {
    val m = StructMapper()
    enum class Type(val value: UByte) {
        Notification(1u),
        Pin(2u),
        Reminder(3u)
    }
    val itemId = SUUID(m, itemId)
    val parentId = SUUID(m, parentId)
    val timestamp = SUInt(m, timestamp, endianness = '<')
    val duration = SUShort(m, duration, endianness = '<')
    val type = SByte(m, type.value)
    val flags = SUShort(m, flags, endianness = '<')
    val layout = SByte(m, layout)
    val dataLength = SUShort(m, endianness = '<')
    val attrCount = SByte(m, attributes.size.toUByte())
    val actionCount = SByte(m, actions.size.toUByte())
    val attributes = SFixedList(m, attrCount.get().toInt(), attributes)
    val actions = SFixedList(m, actionCount.get().toInt(), actions)
    init {
        dataLength.set((this.attributes.toBytes().size + this.actions.toBytes().size).toUShort())
    }

    class Action(actionID: UByte, type: Type, attributes: List<Attribute>): Mappable {
        val m = StructMapper()
        enum class Type(val value: UByte) {
            AncsDismiss(0x01u),
            Generic(0x02u),
            Response(0x03u),
            Dismiss(0x04u),
            HTTP(0x05u),
            Snooze(0x06u),
            OpenWatchapp(0x07u),
            Empty(0x08u),
            Remove(0x09u),
            OpenPin(0x0au)
        }

        val actionID = SByte(m, actionID)
        val type = SByte(m, type.value)
        val attributeCount = SByte(m, attributes.size.toUByte())
        val attributes = SFixedList(m, attributeCount.get().toInt(), attributes)

        override fun toBytes(): UByteArray = m.toBytes()

        override fun fromBytes(bytes: DataBuffer) = m.fromBytes(bytes)
    }

    class Attribute(attributeId: UByte, content: UByteArray, contentEndianness: Char = '|'): Mappable {
        enum class Timeline(val id: UByte) {
            Sender(0x01u),
            Subject(0x02u),
            Message(0x03u),
            Icon(0x04u),
            BackgroundCol(0x1Cu)
        }

        val m = StructMapper()
        val attributeId = SByte(m, attributeId)
        val length = SUShort(m, content.size.toUShort(), endianness = '<')
        val content = SBytes(m, content.size, content, endianness = contentEndianness)

        override fun toBytes(): UByteArray = m.toBytes()

        override fun fromBytes(bytes: DataBuffer) = m.fromBytes(bytes)

    }

    override fun toBytes(): UByteArray {
        return m.toBytes()
    }

    override fun fromBytes(bytes: DataBuffer) {
        m.fromBytes(bytes)
    }
}

@ExperimentalUnsignedTypes
open class TimelineAction(message: Message) : PebblePacket(endpoint) {
    val command = SByte(m, message.value)

    enum class Message(val value: UByte) {
        InvokeAction(0x02u),
        ActionResponse(0x11u)
    }

    class InvokeAction(itemID: Uuid = Uuid(0,0), actionID: UByte = 0u, attributes: List<TimelineItem.Attribute> = listOf()): TimelineAction(Message.InvokeAction) {
        val itemID = SUUID(m, itemID)
        val actionID = SByte(m, actionID)
        val numAttributes = SByte(m, attributes.size.toUByte())
        val attributes = SFixedList(m, attributes.size, attributes)
    }

    class ActionResponse(itemID: Uuid = Uuid(0,0), response: UByte = 0u, attributes: List<TimelineItem.Attribute> = listOf()): TimelineAction(Message.ActionResponse) {
        val itemID = SUUID(m, itemID)
        val response = SByte(m, response)
        val numAttributes = SByte(m, attributes.size.toUByte())
        val attributes = SFixedList(m, attributes.size, attributes)
    }

    companion object {
        val endpoint = ProtocolEndpoint.TIMELINE_ACTIONS
    }
}

@ExperimentalUnsignedTypes
fun timelinePacketsRegister() {
    PacketRegistry.register(TimelineAction.endpoint, TimelineAction.Message.InvokeAction.value) {TimelineAction.InvokeAction()}
    PacketRegistry.register(TimelineAction.endpoint, TimelineAction.Message.ActionResponse.value) {TimelineAction.ActionResponse()}
}