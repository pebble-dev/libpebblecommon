package io.rebble.libpebblecommon.blobdb

import com.benasher44.uuid.uuid4
import com.benasher44.uuid.uuidFrom
import com.benasher44.uuid.uuidOf
import com.soywiz.klock.DateTime
import io.rebble.libpebblecommon.structmapper.SUInt
import io.rebble.libpebblecommon.structmapper.StructMapper
import kotlin.random.Random

@ExperimentalUnsignedTypes
private val notifsUUID = uuidFrom("B2CAE818-10F8-46DF-AD2B-98AD2254A3C1")

@ExperimentalUnsignedTypes
enum class NotificationSource(val id: UInt) { //TODO: There's likely more... (probably fw >3)
    Generic(1u),
    Twitter(6u),
    Facebook(11u),
    Email(19u),
    SMS(45u),
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
/**
 * Helper class to generate a BlobDB command that inserts a notification
 */
open class PushNotification(subject: String, sender: String? = null, message: String? = null, source: NotificationSource = NotificationSource.Generic, backgroundColor: UByte? = null): BlobCommand.InsertCommand(Random.nextInt(0, UShort.MAX_VALUE.toInt()).toUShort(),
    BlobDatabase.Notification, ubyteArrayOf(), ubyteArrayOf()) {
    init {
        val iconID = SUInt(StructMapper(), source.id or 0x80000000u).toBytes() //TODO: Work out why GB masks this, and why that makes it work
        val itemID = uuid4()

        //TODO: Replies, open on phone, detect dismiss
        val attributes = mutableListOf(
            TimelineItem.Attribute(
                TimelineItem.Attribute.Timeline.Sender.id,
                sender?.encodeToByteArray()?.toUByteArray() ?: ubyteArrayOf()
            ),
            TimelineItem.Attribute(
                TimelineItem.Attribute.Timeline.Icon.id,
                iconID,
                contentEndianness = '<'
            )
        )
        if (message != null) attributes += TimelineItem.Attribute(
            TimelineItem.Attribute.Timeline.Message.id,
            message.encodeToByteArray().toUByteArray()
        )
        attributes += TimelineItem.Attribute(
            TimelineItem.Attribute.Timeline.Subject.id,
            subject.encodeToByteArray().toUByteArray()
        )

        if (backgroundColor != null) attributes += TimelineItem.Attribute(
            TimelineItem.Attribute.Timeline.BackgroundCol.id,
            ubyteArrayOf(backgroundColor)
        )

        val actions = mutableListOf(
            TimelineItem.Action(
                0u, TimelineItem.Action.Type.Dismiss, mutableListOf(
                    TimelineItem.Attribute(
                        0x01u,
                        "Dismiss".encodeToByteArray().toUByteArray()
                    )
                )
            )
        )

        val timestamp = DateTime.nowUnixLong() / 1000

        val notification = TimelineItem(
            itemID,
            uuidOf(ByteArray(2 * Long.SIZE_BYTES) { 0 }),
            timestamp.toUInt(),
            0u,
            TimelineItem.Type.Notification,
            0u,
            0x01u,
            attributes,
            actions
        )
        super.targetKey.set(notification.itemId.toBytes(), notification.itemId.size)
        super.keySize.set(super.targetKey.size.toUByte())
        val nbytes = notification.toBytes()
        super.targetValue.set(nbytes, nbytes.size)
        super.valSize.set(super.targetValue.size.toUShort())
    }
}