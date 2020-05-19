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

    enum class OSType(val value: UInt) {
        Unknown(0u),
        IOS(1u),
        Android(2u),
        MacOS(3u),
        Linux(4u),
        Windows(5u)
    }

    enum class SessionCapsFlag(val value: UByte) {
        Geolocation(1u),
        GammaRay(Int.MIN_VALUE.toUByte())
    }

    enum class ProtocolCapsFlag(val value: UByte) {
        SupportsAppRunStateProtocol(0u),
        SupportsInfiniteLogDump(1u),
        SupportsExtendedMusicProtocol(2u),
        SupportsTwoWayDismissal(3u),
        SupportsLocalization(4u),
        Supports8kAppMessage(5u),
        SupportsHealthInsights(6u),
        SupportsSendTextApp(8u),
        SupportsUnreadCoreDump(10u),
        SupportsWeatherApp(11u),
        SupportsRemindersApp(12u),
        SupportsWorkoutApp(13u),
        SupportsFwUpdateAcrossDisconnection(21u),
        SupportsSmoothFwInstallProgress(14u);

        companion object {
            fun makeFlags(vararg flags: ProtocolCapsFlag): UInt {
                var ret: UInt = 0u
                flags.forEach {flag ->
                    ret or flag.value.toUInt()
                }
                return ret
            }
        }
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