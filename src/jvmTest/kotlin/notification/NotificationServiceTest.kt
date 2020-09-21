package notification

import TestProtocolHandler
import assertIs
import io.rebble.libpebblecommon.blobdb.BlobCommand
import io.rebble.libpebblecommon.blobdb.BlobResponse
import io.rebble.libpebblecommon.blobdb.NotificationSource
import io.rebble.libpebblecommon.blobdb.PushNotification
import io.rebble.libpebblecommon.services.blobdb.BlobDBService
import io.rebble.libpebblecommon.services.notification.NotificationService
import runBlockingWithTimeout
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class NotificationServiceTest {
    @Test
    fun `Forward notification success`() = runBlockingWithTimeout {
        val protocolHandler = TestProtocolHandler { receivedPacket ->
            if (receivedPacket is BlobCommand) {
                this.receivePacket(BlobResponse.Success().also {
                    it.token.set(receivedPacket.token.get())
                })
            }
        }

        val notificationService = NotificationService(BlobDBService((protocolHandler)))
        val result = notificationService.send(TEST_NOTIFICATION)

        assertIs<BlobResponse.Success>(
            result,
            "Reply wasn't success from BlobDB when sending notif"
        )
    }

    @Test
    fun `Forward notification fail`() = runBlockingWithTimeout {
        val protocolHandler = TestProtocolHandler { receivedPacket ->
            if (receivedPacket is BlobCommand) {
                this.receivePacket(BlobResponse.GeneralFailure().also {
                    it.token.set(receivedPacket.token.get())
                })
            }
        }

        val notificationService = NotificationService(BlobDBService((protocolHandler)))
        val result = notificationService.send(TEST_NOTIFICATION)

        assertIs<BlobResponse.GeneralFailure>(
            result,
            "Reply wasn't fail from BlobDB when sending notif"
        )
    }

    @Test
    fun `Resend notification`() = runBlockingWithTimeout {
        val receivedTokens = ArrayList<UShort>()
        val protocolHandler = TestProtocolHandler { receivedPacket ->
            if (receivedPacket is BlobCommand) {
                val nextPacket = if (receivedTokens.size == 0) {
                    BlobResponse.TryLater()
                } else {
                    BlobResponse.Success()
                }

                this.receivePacket(nextPacket.also {
                    it.token.set(receivedPacket.token.get())
                })

                receivedTokens.add(receivedPacket.token.get())

            }
        }

        val notificationService = NotificationService(BlobDBService((protocolHandler)))
        val result = notificationService.send(TEST_NOTIFICATION)

        assertIs<BlobResponse.Success>(
            result,
            "Reply wasn't success from BlobDB when sending notif"
        )

        assertEquals(2, receivedTokens.size)

        val uniqueTokens = receivedTokens.distinct()
        assertEquals(
            2,
            uniqueTokens.size,
            "NotificationService should re-generate token every time."
        )
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
private val TEST_NOTIFICATION = PushNotification(
    sender = "Test Notif",
    subject = "This is a test notification!",
    message = "This is the notification body",
    backgroundColor = 0b11110011u,
    source = NotificationSource.Email
)
