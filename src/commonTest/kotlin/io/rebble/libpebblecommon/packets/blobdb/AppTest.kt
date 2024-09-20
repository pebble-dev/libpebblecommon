package io.rebble.libpebblecommon.packets.blobdb

import assertUByteArrayEquals
import io.rebble.libpebblecommon.util.DataBuffer
import kotlin.test.Test

internal class AppTest {
    @Test
    fun `AppMetadata flags should be little endian`() {
        val appMetadata = AppMetadata()
        appMetadata.flags.set(0x00FFu)
        val serialized = appMetadata.toBytes()
        val bytes = ubyteArrayOf(serialized[16], serialized[17])
        assertUByteArrayEquals(ubyteArrayOf(0xFFu, 0x00u), bytes)
    }
}