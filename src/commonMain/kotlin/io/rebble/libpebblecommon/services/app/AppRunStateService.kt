package io.rebble.libpebblecommon.services.app

import com.benasher44.uuid.Uuid
import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppRunStateMessage
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.services.ProtocolService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow

class AppRunStateService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<AppRunStateMessage>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_RUN_STATE, this::receive)
    }

    suspend fun send(packet: AppRunStateMessage) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is AppRunStateMessage) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.trySend(packet)
    }

    suspend fun startApp(uuid: Uuid) {
        send(AppRunStateMessage.AppRunStateStart(uuid))
    }

    suspend fun stopApp(uuid: Uuid) {
        send(AppRunStateMessage.AppRunStateStop(uuid))
    }

}