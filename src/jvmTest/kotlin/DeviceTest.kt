import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.HttpMethod
import io.ktor.util.KtorExperimentalAPI
import io.rebble.libpebblecommon.PingPong
import io.rebble.libpebblecommon.blobdb.BlobResponse
import io.rebble.libpebblecommon.services.NotificationSource
import io.rebble.libpebblecommon.services.PushNotification
import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.test.Test
import java.util.*
import kotlin.test.assertTrue

@ExperimentalUnsignedTypes
class DeviceTests {
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
    val phoneHost = "change-me"
    val phonePort = 9000

    @KtorExperimentalAPI
    val client = HttpClient {
        install(WebSockets)
    }

    init {
        if (phoneHost == "change-me") throw Exception("Need to set phone dev connection host, note that while this is a test it's intended for manually testing packets on a real device")
        PacketRegistry.setup()
    }

    @KtorExperimentalAPI
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    @Test
    fun sendNotification() {
        var block = true
        GlobalScope.launch {
            client.ws(
                method = HttpMethod.Get,
                host = phoneHost,
                port = phonePort, path = "/"
            ) {
                Timer("Unblock", false).schedule(object : TimerTask() {
                    override fun run() {
                        block = false
                    }
                }, 5000)
                val sendpacket = PushNotification(
                    sender = "A Notification",
                    subject = "Hello world!",
                    source = NotificationSource.SMS,
                    backgroundColor = 0b11100111u
                )
                val spacket = sendpacket.serialize().toByteArray()
                println(bytesToHex(spacket.toUByteArray()))
                send(Frame.Binary(true, byteArrayOf(0x01) + spacket))
                flush()
                while(true) {
                    val frame = incoming.receive()
                    if (frame is Frame.Binary) {
                        try{
                            val packet = PebblePacket.deserialize(frame.data.slice(1 until frame.data.size).toByteArray().toUByteArray())
                            print(packet::class.simpleName)
                            if (packet is BlobResponse && packet.token.get() == sendpacket.token.get()) {
                                print(" | TOKEN MATCH")
                            }
                            println()
                        }catch (e: PacketDecodeException) {
                            println(e.toString())
                        }
                    }
                }
            }
        }
        while (block) {Thread.sleep(50)}
    }

    @ExperimentalUnsignedTypes
    @Test
    fun sendPing() {
        var block = true
        var gotPong = false
        GlobalScope.launch {
            client.ws(
                method = HttpMethod.Get,
                host = phoneHost,
                port = phonePort, path = "/"
            ) {
                Timer("Unblock", false).schedule(object : TimerTask() {
                    override fun run() {
                        block = false
                    }
                }, 5000)
                send(Frame.Binary(true, byteArrayOf(0x01) + PingPong.Ping(1337u).serialize().toByteArray()))
                flush()
                // Receive frame.
                while(true) {
                    val frame = incoming.receive()
                    println(frame.data)
                    if (frame is Frame.Binary) {
                        val packet = PebblePacket.deserialize(frame.data.slice(1 until frame.data.size).toByteArray().toUByteArray())
                        if (packet is PingPong.Ping && packet.cookie.get() == 1337u) gotPong = true; break
                    }
                }
            }
        }
        while (block) {Thread.sleep(50)}
        assertTrue(gotPong, "Pong not received within sane amount of time")
    }
}