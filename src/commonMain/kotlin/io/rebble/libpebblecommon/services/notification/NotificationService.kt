package io.rebble.libpebblecommon.services.notification

import io.rebble.libpebblecommon.packets.blobdb.BlobResponse
import io.rebble.libpebblecommon.packets.blobdb.PushNotification
import io.rebble.libpebblecommon.services.ProtocolService
import io.rebble.libpebblecommon.services.blobdb.BlobDBService
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Singleton to handle sending notifications cleanly, as well as TODO: receiving/acting on action events
 */
class NotificationService(private val blobDbService: BlobDBService) : ProtocolService {

    /**
     * Send a PushNotification command
     * @param notif the notification to send
     * @see PushNotification
     *
     * @return notification [BlobResponse] from the watch or *null* if sending failed
     */
    suspend fun send(notif: PushNotification): BlobResponse? {
        while (true) {
            val res = blobDbService.send(notif)
            if (res is BlobResponse.TryLater) {
                // Device pushed it back, let's change the token and do an async delayed send
                notif.token.set(Random.nextInt(0, UShort.MAX_VALUE.toInt()).toUShort())
                delay(100)
            } else {
                return res
            }
        }
    }
}