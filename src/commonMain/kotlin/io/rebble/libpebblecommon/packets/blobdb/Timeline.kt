package io.rebble.libpebblecommon.packets.blobdb

import com.benasher44.uuid.Uuid
import io.rebble.libpebblecommon.packets.blobdb.TimelineItem.Attribute
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.structmapper.*
import io.rebble.libpebblecommon.util.DataBuffer

class TimelineItem(
    itemId: Uuid,
    parentId: Uuid, timestamp: UInt,
    duration: UShort, type: Type,
    flags: UShort, layout: UByte,
    attributes: List<Attribute>,
    actions: List<Action>
) : StructMappable() {
    enum class Type(val value: UByte) {
        Notification(1u),
        Pin(2u),
        Reminder(3u);

        companion object {
            fun fromValue(value: UByte): Type {
                return values().firstOrNull { it.value == value }
                    ?: error("Unknown timeline item type: $value")
            }
        }
    }

    val itemId = SUUID(m, itemId)
    val parentId = SUUID(m, parentId)
    val timestamp = SUInt(m, timestamp, endianness = '<')
    val duration = SUShort(m, duration, endianness = '<')
    val type = SUByte(m, type.value)
    val flags = SUShort(m, flags, endianness = '<')
    val layout = SUByte(m, layout)
    val dataLength = SUShort(m, endianness = '<')
    val attrCount = SUByte(m, attributes.size.toUByte())
    val actionCount = SUByte(m, actions.size.toUByte())
    val attributes =
        SFixedList(m, attrCount.get().toInt(), attributes) { Attribute(0u, ubyteArrayOf()) }
    val actions = SFixedList(m, actionCount.get().toInt(), actions) {
        Action(
            0u,
            Action.Type.Empty,
            emptyList()
        )
    }

    init {
        dataLength.set((this.attributes.toBytes().size + this.actions.toBytes().size).toUShort())
    }

    class Action(actionID: UByte, type: Type, attributes: List<Attribute>) : Mappable {
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

        val actionID = SUByte(m, actionID)
        val type = SUByte(m, type.value)
        val attributeCount = SUByte(m, attributes.size.toUByte())
        val attributes = SFixedList(m, attributeCount.get().toInt(), attributes) {
            Attribute(
                0u,
                ubyteArrayOf()
            )
        }

        override fun toBytes(): UByteArray = m.toBytes()

        override fun fromBytes(bytes: DataBuffer) = m.fromBytes(bytes)
    }

    class Attribute() : StructMappable() {
        enum class Timeline(val id: UByte) {
            Sender(0x01u),
            Subject(0x02u),
            Message(0x03u),
            Icon(0x04u),
            BackgroundCol(0x1Cu)
        }

        val attributeId = SUByte(m)
        val length = SUShort(m, endianness = '<')
        val content = SBytes(m, 0)

        constructor(
            attributeId: UByte,
            content: UByteArray,
            contentEndianness: Char = '|'
        ) : this() {
            this.attributeId.set(attributeId)

            this.length.set(content.size.toUShort())

            this.content.set(content)
            this.content.setEndiannes(contentEndianness)
        }

        init {
            content.linkWithSize(length)
        }
    }

    enum class Flag(val value: Int) {
        IS_VISIBLE(0),
        IS_FLOATING(1),
        IS_ALL_DAY(2),
        FROM_WATCH(3),
        FROM_ANCS(4),
        PERSIST_QUICK_VIEW(5);

        companion object {
            fun makeFlags(flags: List<Flag>): UShort {
                var short: UShort = 0u

                for (flag in flags) {
                    short = (1u shl flag.value).toUShort() or short
                }

                return short
            }
        }
    }
}

open class TimelineAction(message: Message) : PebblePacket(endpoint) {
    val command = SUByte(m, message.value)

    enum class Message(val value: UByte) {
        InvokeAction(0x02u),
        ActionResponse(0x11u)
    }

    class InvokeAction(
        itemID: Uuid = Uuid(0, 0),
        actionID: UByte = 0u,
        attributes: List<Attribute> = listOf()
    ) : TimelineAction(
        Message.InvokeAction
    ) {
        val itemID = SUUID(m, itemID)
        val actionID = SUByte(m, actionID)
        val numAttributes = SUByte(m, attributes.size.toUByte())
        val attributes = SFixedList(m, attributes.size, attributes, ::Attribute)
    }

    class ActionResponse(
        itemID: Uuid = Uuid(0, 0),
        response: UByte = 0u,
        attributes: List<Attribute> = listOf()
    ) : TimelineAction(
        Message.ActionResponse
    ) {
        val itemID = SUUID(m, itemID)
        val response = SUByte(m, response)
        val numAttributes = SUByte(m, attributes.size.toUByte())
        val attributes = SFixedList(m, attributes.size, attributes, ::Attribute)
    }

    companion object {
        val endpoint = ProtocolEndpoint.TIMELINE_ACTIONS
    }
}

fun timelinePacketsRegister() {
    PacketRegistry.register(
        TimelineAction.endpoint,
        TimelineAction.Message.InvokeAction.value
    ) { TimelineAction.InvokeAction() }
    PacketRegistry.register(
        TimelineAction.endpoint,
        TimelineAction.Message.ActionResponse.value
    ) { TimelineAction.ActionResponse() }
}