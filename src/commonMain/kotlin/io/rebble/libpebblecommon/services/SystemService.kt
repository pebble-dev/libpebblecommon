package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.PacketPriority
import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.getPlatform
import io.rebble.libpebblecommon.packets.PhoneAppVersion
import io.rebble.libpebblecommon.packets.ProtocolCapsFlag
import io.rebble.libpebblecommon.packets.SystemPacket
import io.rebble.libpebblecommon.packets.WatchVersion
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

/**
 * Singleton to handle sending notifications cleanly, as well as TODO: receiving/acting on action events
 */
class SystemService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<SystemPacket>(Channel.BUFFERED)

    private var watchVersionCallback: CompletableDeferred<WatchVersion.WatchVersionResponse>? = null

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.PHONE_VERSION, this::receive)
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.WATCH_VERSION, this::receive)
    }

    /**
     * Send an AppMessage
     */
    suspend fun send(packet: SystemPacket, priority: PacketPriority = PacketPriority.NORMAL) {
        protocolHandler.send(packet, priority)
    }

    suspend fun requestWatchVersion(): WatchVersion.WatchVersionResponse {
        val callback = CompletableDeferred<WatchVersion.WatchVersionResponse>()
        watchVersionCallback = callback

        send(WatchVersion.WatchVersionRequest())

        return callback.await()
    }

    suspend fun receive(packet: PebblePacket) {
        if (packet !is SystemPacket) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        when (packet) {
            is WatchVersion.WatchVersionResponse -> {
                watchVersionCallback?.complete(packet)
                watchVersionCallback = null
            }
            is PhoneAppVersion -> {
                val responsePacket = PhoneAppVersion.AppVersionResponse(
                    UInt.MAX_VALUE,

                    0u,
                    PhoneAppVersion.PlatformFlag.makeFlags(
                        getPlatform(), emptyList()
                    ),
                    2u,
                    4u,
                    4u,
                    2u,
                    ProtocolCapsFlag.makeFlags(
                        listOf(
                            ProtocolCapsFlag.Supports8kAppMessage
                        )
                    )

                )

                protocolHandler.send(responsePacket)
            }
            else -> receivedMessages.offer(packet)
        }


    }

}