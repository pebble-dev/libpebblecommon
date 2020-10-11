package io.rebble.libpebblecommon.packets

import assertIs
import com.benasher44.uuid.uuidFrom
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import kotlin.test.Test
import kotlin.test.assertEquals


internal class AppRunStateMessageTest {
    @Test
    fun serializeDeserializeStartMessage() {
        val originalMessage = AppRunStateMessage.AppRunStateStart(
            uuidFrom("30880933-cead-49f6-ba94-3a6f8cd3218a")
        )

        val bytes = originalMessage.serialize()
        val newMessage = PebblePacket.deserialize(bytes)

        assertIs<AppRunStateMessage.AppRunStateStart>(newMessage)
        assertEquals(uuidFrom("30880933-cead-49f6-ba94-3a6f8cd3218a"), newMessage.uuid.get())
    }

    @Test
    fun serializeDeserializeStopMessage() {
        val originalMessage = AppRunStateMessage.AppRunStateStop(
            uuidFrom("30880933-cead-49f6-ba94-3a6f8cd3218a")
        )

        val bytes = originalMessage.serialize()
        val newMessage = PebblePacket.deserialize(bytes)

        assertIs<AppRunStateMessage.AppRunStateStop>(newMessage)
        assertEquals(uuidFrom("30880933-cead-49f6-ba94-3a6f8cd3218a"), newMessage.uuid.get())
    }

    @Test
    fun serializeDeserializeRequestMessage() {
        val originalMessage = AppRunStateMessage.AppRunStateRequest()

        val bytes = originalMessage.serialize()
        val newMessage = PebblePacket.deserialize(bytes)

        assertIs<AppRunStateMessage.AppRunStateRequest>(newMessage)
    }
}