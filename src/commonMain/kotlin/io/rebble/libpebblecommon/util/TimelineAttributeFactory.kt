package io.rebble.libpebblecommon.util

import io.rebble.libpebblecommon.packets.blobdb.TimelineAttribute
import io.rebble.libpebblecommon.packets.blobdb.TimelineIcon
import io.rebble.libpebblecommon.packets.blobdb.TimelineItem
import io.rebble.libpebblecommon.structmapper.SFixedList
import io.rebble.libpebblecommon.structmapper.SUInt
import io.rebble.libpebblecommon.structmapper.StructMapper
import kotlin.math.round

object TimelineAttributeFactory {
    private fun createAttribute(attributeId: UByte, content: UByteArray, contentEndianness: Endian = Endian.Unspecified): TimelineItem.Attribute {
        return TimelineItem.Attribute(attributeId, content, contentEndianness)
    }

    private fun createTextAttribute(type: TimelineAttribute, text: String): TimelineItem.Attribute {
        require(type.maxLength != -1) { "Attribute type $type is not a text attribute" }
        return createAttribute(type.id, text.encodeToByteArrayTrimmed(type.maxLength).toUByteArray())
    }

    private fun createStringListAttribute(type: TimelineAttribute, list: List<String>): TimelineItem.Attribute {
        val content = list.joinToString("\u0000")
            .encodeToByteArrayTrimmed(type.maxLength).toUByteArray()
        return createAttribute(type.id, content)
    }

    private fun createUByteAttribute(type: TimelineAttribute, value: UByte): TimelineItem.Attribute {
        val content = byteArrayOf(value.toByte()).toUByteArray()
        return createAttribute(type.id, content)
    }

    private fun createUIntAttribute(type: TimelineAttribute, value: UInt): TimelineItem.Attribute {
        val content = SUInt(StructMapper(), value, Endian.Little).toBytes()
        return createAttribute(type.id, content)
    }

    fun title(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.Title, text)
    }

    fun subtitle(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.Subtitle, text)
    }

    fun body(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.Body, text)
    }

    fun tinyIcon(icon: TimelineIcon, offsetId: Boolean = true): TimelineItem.Attribute {
        val id = if (offsetId) {
            icon.id or 0x80000000u
        } else {
            icon.id
        }
        return createUIntAttribute(TimelineAttribute.TinyIcon, id)
    }

    fun smallIcon(icon: TimelineIcon, offsetId: Boolean = true): TimelineItem.Attribute {
        val id = if (offsetId) {
            icon.id or 0x80000000u
        } else {
            icon.id
        }
        return createUIntAttribute(TimelineAttribute.SmallIcon, id)
    }

    fun largeIcon(icon: TimelineIcon, offsetId: Boolean = true): TimelineItem.Attribute {
        val id = if (offsetId) {
            icon.id or 0x80000000u
        } else {
            icon.id
        }
        return createUIntAttribute(TimelineAttribute.LargeIcon, id)
    }

    fun ancsAction(action: UInt): TimelineItem.Attribute {
        return createUIntAttribute(TimelineAttribute.ANCSAction, action)
    }

    fun cannedResponse(responses: List<String>): TimelineItem.Attribute {
        return createStringListAttribute(TimelineAttribute.CannedResponse, responses)
    }

    fun shortTitle(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.ShortTitle, text)
    }

    fun locationName(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.LocationName, text)
    }

    fun sender(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.Sender, text)
    }

    fun launchCode(code: UInt): TimelineItem.Attribute {
        return createUIntAttribute(TimelineAttribute.LaunchCode, code)
    }

    fun lastUpdated(msSinceEpoch: Int): TimelineItem.Attribute {
        return createUIntAttribute(TimelineAttribute.LastUpdated, round(msSinceEpoch / 1000f).toUInt())
    }

    fun rankAway(rank: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.RankAway, rank)
    }

    fun rankHome(rank: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.RankHome, rank)
    }

    fun nameAway(name: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.NameAway, name)
    }

    fun nameHome(name: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.NameHome, name)
    }

    fun recordAway(record: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.RecordAway, record)
    }

    fun recordHome(record: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.RecordHome, record)
    }

    fun scoreAway(score: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.ScoreAway, score)
    }

    fun scoreHome(score: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.ScoreHome, score)
    }

    fun sportsGameState(state: UByte): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.SportsGameState, state)
    }

    fun broadcaster(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.Broadcaster, text)
    }

    fun headings(headings: List<String>): TimelineItem.Attribute {
        return createStringListAttribute(TimelineAttribute.Headings, headings)
    }

    fun paragraphs(paragraphs: List<String>): TimelineItem.Attribute {
        return createStringListAttribute(TimelineAttribute.Body, paragraphs)
    }

    fun foregroundColor(color: PebbleColor): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.ForegroundColor, color.toProtocolNumber())
    }

    fun foregroundColor(rawPebbleColor: UByte): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.ForegroundColor, rawPebbleColor)
    }

    fun primaryColor(color: PebbleColor): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.PrimaryColor, color.toProtocolNumber())
    }

    fun primaryColor(rawPebbleColor: UByte): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.PrimaryColor, rawPebbleColor)
    }

    fun secondaryColor(color: PebbleColor): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.SecondaryColor, color.toProtocolNumber())
    }

    fun secondaryColor(rawPebbleColor: UInt): TimelineItem.Attribute {
        return createUIntAttribute(TimelineAttribute.SecondaryColor, rawPebbleColor)
    }

    fun displayRecurring(recurring: Boolean): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.DisplayRecurring, if (recurring) 1u else 0u)
    }

    fun shortSubtitle(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.ShortSubtitle, text)
    }

    fun timestamp(timestamp: UInt): TimelineItem.Attribute {
        return createUIntAttribute(TimelineAttribute.LastUpdated, timestamp / 1000u)
    }

    fun displayTime(displayTime: Boolean): TimelineItem.Attribute {
        return createUByteAttribute(TimelineAttribute.DisplayRecurring, if (displayTime) 1u else 0u)
    }

    fun subtitleTemplateString(text: String): TimelineItem.Attribute {
        return createTextAttribute(TimelineAttribute.SubtitleTemplateString, text)
    }

    fun icon(icon: TimelineIcon): TimelineItem.Attribute {
        return createUIntAttribute(TimelineAttribute.Icon, icon.id)
    }
}