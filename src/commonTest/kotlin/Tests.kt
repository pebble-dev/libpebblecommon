import io.rebble.libpebblecommon.packets.PingPong
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
@OptIn(ExperimentalUnsignedTypes::class)
class Tests {
    @ExperimentalStdlibApi
    private fun bytesToHex(bytes: UByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = (bytes[j] and 0xFFu).toInt()

            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    @Test
    fun serializeSimplePacket() {
        val packet: PebblePacket = PingPong.Ping(51966u)

        assertEquals(bytesToHex(ubyteArrayOf(0x00u,0x05u,0x07u,0xD1u,0x00u,0x00u,0x00u,0xCAu,0xFEu)), bytesToHex(packet.serialize()),
            "Serialized big-endian packet invalid") // [short1,short2],[short1,short2],[byte],[uint1,uint2,uint3,uint4]
    }

    @Test
    fun deserializeSimplePacket() {
        PacketRegistry.setup()
        val expect = ubyteArrayOf(0x00u,0x05u,0x07u,0xD1u,0x00u,0x00u,0x00u,0xCAu,0xFEu)

        val bytes = byteArrayOf(0x00,0x05,0x07, 0xD1.toByte(),0x00,0x00,0x00, 0xCA.toByte(), 0xFE.toByte())
        val packet = PebblePacket.deserialize(bytes.toUByteArray())

        assertEquals(bytesToHex(expect), bytesToHex(packet.serialize()))
    }
}