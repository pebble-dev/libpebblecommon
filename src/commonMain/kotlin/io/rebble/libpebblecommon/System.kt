package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.protocol.PacketRegistry
import io.rebble.libpebblecommon.protocol.PebblePacket
import io.rebble.libpebblecommon.protocol.ProtocolEndpoint
import io.rebble.libpebblecommon.structmapper.SByte
import io.rebble.libpebblecommon.structmapper.SUInt
import io.rebble.libpebblecommon.structmapper.SULong
import io.rebble.libpebblecommon.structmapper.StructMapper

@ExperimentalUnsignedTypes
class TimeMessage(private val message: Message) : PebblePacket(ProtocolEndpoint.TIME) {
    enum class Message(val value: UByte) {
        GetTimeRequest(0x00u),
        GetTimeResponse(0x01u),
        SetLocalTime(0x02u),
        SetUTC(0x03u)
    }
}

@ExperimentalUnsignedTypes
open class PhoneAppVersion(message: Message) : PebblePacket(endpoint) {
    enum class Message(val value: UByte, val instance: () -> PebblePacket) {
        AppVersionRequest(0x00u, {AppVersionRequest()}),
        AppVersionResponse(0x01u, {AppVersionResponse()})
    }
    val command = SByte(m, message.value)
    init {
        type = command.get()
    }

    class AppVersionRequest : PhoneAppVersion(Message.AppVersionRequest)
    class AppVersionResponse : PhoneAppVersion(Message.AppVersionResponse) {
        val protocolVersion = SUInt(m) // Unused as of v3.0
        val sessionCaps = SUInt(m) // Unused as of v3.0
        val platformFlags = SUInt(m)
        val responseVersion = SByte(m, 2u)
        val majorVersion = SByte(m)
        val minorVersion = SByte(m)
        val bugfixVersion = SByte(m)
        val protocolCaps = SULong(m)
    }

    companion object {
        val endpoint = ProtocolEndpoint.PHONE_VERSION
    }
}

@ExperimentalUnsignedTypes
open class PingPong(message: Message, cookie: UInt): PebblePacket(endpoint) {
    enum class Message(val value: UByte) {
        Ping(0u),
        Pong(1u)
    }
    val command = SByte(m, message.value)
    val cookie = SUInt(m, cookie)
    init {
        type = command.get()
    }

    class Ping(cookie: UInt = 0u) : PingPong(Message.Ping, cookie) {
        init {
            PacketRegistry.register(endpoint, command.get(), {Ping()})
        }
    }
    class Pong(cookie: UInt = 0u) : PingPong(Message.Pong, cookie) {
        init {
            PacketRegistry.register(endpoint, command.get(), {Pong()})
        }
    }

    companion object {
        val endpoint = ProtocolEndpoint.PING
    }
}

@ExperimentalUnsignedTypes
fun systemPacketsRegister() {
    PacketRegistry.register(PhoneAppVersion.endpoint, PhoneAppVersion.Message.AppVersionRequest.value) { PhoneAppVersion.AppVersionRequest() }
    PacketRegistry.register(PhoneAppVersion.endpoint, PhoneAppVersion.Message.AppVersionResponse.value) { PhoneAppVersion.AppVersionResponse() }

    PacketRegistry.register(PingPong.endpoint, PingPong.Message.Ping.value) { PingPong.Ping() }
    PacketRegistry.register(PingPong.endpoint, PingPong.Message.Pong.value) { PingPong.Pong() }
}