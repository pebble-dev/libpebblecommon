package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.getPlatform
import io.rebble.libpebblecommon.packets.PhoneAppVersion
import io.rebble.libpebblecommon.packets.SystemPacket
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

/**
 * Singleton to handle sending notifications cleanly, as well as TODO: receiving/acting on action events
 */
class SystemService(private val protocolHandler: ProtocolHandler) {
    val receivedMessages = Channel<SystemPacket>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.PHONE_VERSION, this::receive)
    }

    /**
     * Send an AppMessage
     */
    suspend fun send(packet: SystemPacket) {
        protocolHandler.withWatchContext {
            protocolHandler.send(packet)
        }
    }

    suspend fun receive(packet: PebblePacket) {
        if (packet !is SystemPacket) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        when (packet) {
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
                    PhoneAppVersion.ProtocolCapsFlag.makeFlags(
                        listOf(
                            PhoneAppVersion.ProtocolCapsFlag.Supports8kAppMessage
                        )
                    )

                )

                protocolHandler.withWatchContext {
                    protocolHandler.send(responsePacket)
                }
            }
            else -> receivedMessages.offer(packet)
        }


    }

}