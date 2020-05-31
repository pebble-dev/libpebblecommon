package io.rebble.libpebblecommon.services.notification

import io.rebble.libpebblecommon.blobdb.BlobResponse
import io.rebble.libpebblecommon.blobdb.PushNotification
import io.rebble.libpebblecommon.services.blobdb.BlobDBService
import io.rebble.libpebblecommon.util.LazyLock
import io.rebble.libpebblecommon.util.runBlocking
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.random.Random

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
/**
 * Singleton to handle sending notifications cleanly, as well as TODO: receiving/acting on action events
 */
object NotificationService {
    private val lockSend = LazyLock()

    /**
     * Send a PushNotification command
     * @param notif the notification to send
     * @param resultCallback optional callback to handle the BlobResponse
     * @see PushNotification
     */
    fun send(notif: PushNotification, resultCallback: ((BlobResponse) -> Unit)? = null) {
        GlobalScope.launch {
            lockSend.syncLock(1000) // If we're already sending a notif and it's just been pushed back by device or something, wait until it isn't / until we stop caring
            lockSend.lock()
            BlobDBService.send(notif) { res ->
                if (res is BlobResponse.TryLater) { // Device pushed it back, let's change the token and do an async delayed send
                    notif.token.set(Random.nextInt(0, UShort.MAX_VALUE.toInt()).toUShort())
                    GlobalScope.launch {
                        delay(100)
                        send(notif)
                    }
                } else {
                    lockSend.unlock()
                    if (resultCallback != null) resultCallback(res)
                }
            }
        }
    }
}