package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.structmapper.*

@OptIn(ExperimentalUnsignedTypes::class)
open class TimeMessage(private val message: Message) : PebblePacket(
    ProtocolEndpoint.TIME) {
    enum class Message(val value: UByte) {
        GetTimeRequest(0x00u),
        GetTimeResponse(0x01u),
        SetLocalTime(0x02u),
        SetUTC(0x03u)
    }
    val command = SByte(m, message.value)
    init {
        type = command.get()
    }

    class GetTimeRequest : TimeMessage(Message.GetTimeRequest)
    class GetTimeResponse(time: UInt = 0u) : TimeMessage(Message.GetTimeResponse) {
        val time = SUInt(m, time)
    }

    class SetLocalTime(time: UInt = 0u) : TimeMessage(Message.SetLocalTime) {
        val time = SUInt(m, time)
    }
    class SetUTC(unixTime: UInt = 0u, utcOffset: Short = 0, timeZoneName: String) : TimeMessage(Message.SetUTC) {
        val unixTime = SUInt(m, unixTime)
        val utcOffset = SShort(m, utcOffset)
        @ExperimentalStdlibApi
        val timeZoneName = SString(m, timeZoneName)
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
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

    enum class ProtocolCapsFlag(val value: UInt) {
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
                    ret or flag.value
                }
                return ret
            }
        }
    }

    enum class PlatformFlag(val value: UInt) {
        Telephony(16u),
        SMS(32u),
        GPS(64u),
        BTLE(128u),
        CameraFront(240u),
        CameraRear(256u),
        Accelerometer(512u),
        Gyroscope(1024u),
        Compass(2048u);
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

@OptIn(ExperimentalUnsignedTypes::class)
open class SystemMessage(message: Message) : PebblePacket(endpoint) {
    enum class Message(val value: UByte) {
        NewFirmwareAvailable(0x00u),
        FirmwareUpdateStart(0x01u),
        FirmwareUpdateComplete(0x02u),
        FirmwareUpdateFailed(0x03u),
        FirmwareUpToDate(0x04u),
        StopReconnecting(0x06u),
        StartReconnecting(0x07u),
        MAPDisabled(0x08u),
        MAPEnabled(0x09u),
        FirmwareUpdateStartResponse(0x0au)
    }
    val command = SByte(m, message.value)
    val messageType = SByte(m)
    init {
        type = command.get()
        TODO("Incomplete packet declaration")
    }

    companion object {
        val endpoint = ProtocolEndpoint.SYSTEM_MESSAGE
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class BLEControl(opcode: UByte = 0x4u, discoverable: Boolean, duration: UShort) : PebblePacket(endpoint) {
    val command = SByte(m, opcode)
    //val discoverable = SBool(m, discoverable)
    val duration = SUShort(m, duration)
    init {
        type = command.get()
        TODO("Incomplete packet declaration")
    }
    companion object {
        val endpoint = ProtocolEndpoint.BLE_CONTROL
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
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

@OptIn(ExperimentalUnsignedTypes::class)
fun systemPacketsRegister() {
    PacketRegistry.register(PhoneAppVersion.endpoint, PhoneAppVersion.Message.AppVersionRequest.value) { PhoneAppVersion.AppVersionRequest() }
    PacketRegistry.register(PhoneAppVersion.endpoint, PhoneAppVersion.Message.AppVersionResponse.value) { PhoneAppVersion.AppVersionResponse() }

    PacketRegistry.register(PingPong.endpoint, PingPong.Message.Ping.value) { PingPong.Ping() }
    PacketRegistry.register(PingPong.endpoint, PingPong.Message.Pong.value) { PingPong.Pong() }
}