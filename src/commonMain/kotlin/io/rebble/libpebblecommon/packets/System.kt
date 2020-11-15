package io.rebble.libpebblecommon.packets

import io.rebble.libpebblecommon.packets.PhoneAppVersion.AppVersionRequest
import io.rebble.libpebblecommon.packets.PhoneAppVersion.AppVersionResponse
import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.structmapper.*

sealed class SystemPacket(endpoint: ProtocolEndpoint) : PebblePacket(endpoint)

@OptIn(ExperimentalUnsignedTypes::class)
open class TimeMessage(message: Message) : SystemPacket(
    ProtocolEndpoint.TIME
) {
    enum class Message(val value: UByte) {
        GetTimeRequest(0x00u),
        GetTimeResponse(0x01u),
        SetLocalTime(0x02u),
        SetUTC(0x03u)
    }

    val command = SUByte(m, message.value)

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

    class SetUTC(unixTime: UInt = 0u, utcOffset: Short = 0, timeZoneName: String) : TimeMessage(
        Message.SetUTC
    ) {
        val unixTime = SUInt(m, unixTime)
        val utcOffset = SShort(m, utcOffset)

        @ExperimentalStdlibApi
        val timeZoneName = SString(m, timeZoneName)
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
open class PhoneAppVersion(message: Message) : SystemPacket(endpoint) {
    enum class Message(val value: UByte, val instance: () -> PebblePacket) {
        AppVersionRequest(0x00u, { AppVersionRequest() }),
        AppVersionResponse(0x01u, { AppVersionResponse() })
    }

    val command = SUByte(m, message.value)

    init {
        type = command.get()
    }

    enum class OSType(val value: UInt) {
        Unknown(0u),
        IOS(1u),
        Android(2u),
        MacOS(3u),
        Linux(4u),
        Windows(5u);

        companion object {
            fun fromValue(value: UInt): OSType {
                return values().firstOrNull { it.value == value }
                    ?: error("Unknown OS type value: $value")
            }
        }
    }

    enum class SessionCapsFlag(val value: UByte) {
        Geolocation(1u),
        GammaRay(Int.MIN_VALUE.toUByte())
    }

    enum class ProtocolCapsFlag(val value: Int) {
        SupportsAppRunStateProtocol(0),
        SupportsInfiniteLogDump(1),
        SupportsExtendedMusicProtocol(2),
        SupportsTwoWayDismissal(3),
        SupportsLocalization(4),
        Supports8kAppMessage(5),
        SupportsHealthInsights(6),
        SupportsSendTextApp(8),
        SupportsUnreadCoreDump(10),
        SupportsWeatherApp(11),
        SupportsRemindersApp(12),
        SupportsWorkoutApp(13),
        SupportsFwUpdateAcrossDisconnection(21),
        SupportsSmoothFwInstallProgress(14);

        companion object {
            fun makeFlags(flags: List<ProtocolCapsFlag>): UByteArray {
                val bytes = UByteArray(8)

                for (flag in flags) {
                    val combinedPosition = flag.value
                    val byteIndex: Int = combinedPosition / 8
                    val positionInsideByte: Int = combinedPosition % 8
                    bytes[byteIndex] = (1u shl positionInsideByte).toUByte() or bytes[byteIndex]
                }

                return bytes
            }

            fun fromFlags(flags: UByteArray): List<ProtocolCapsFlag> {
                return values().filter {
                    val combinedPosition = it.value
                    val byteIndex: Int = combinedPosition / 8
                    val positionInsideByte: Int = combinedPosition % 8
                    ((1u shl positionInsideByte) and flags[byteIndex].toUInt()) != 0u
                }
            }
        }
    }

    enum class PlatformFlag(val value: UInt) {
        Telephony(16u),
        SMS(32u),
        GPS(64u),
        BTLE(128u),

        // This value is not multiple of 2 and cannot be uniquely (de)serialized as flags.
        // Bug in original Pebble app?
        // CameraFront(240u),
        CameraRear(256u),
        Accelerometer(512u),
        Gyroscope(1024u),
        Compass(2048u);

        companion object {
            fun makeFlags(osType: OSType, flags: List<PlatformFlag>): UInt {
                var ret: UInt = osType.value
                flags.forEach { flag ->
                    ret = ret or flag.value
                }
                return ret
            }

            fun fromFlags(combinedFlags: UInt): Pair<OSType, List<PlatformFlag>> {
                val osType = OSType.fromValue(combinedFlags and 15u)
                val flags = values().filter { (combinedFlags and it.value) != 0u }

                return osType to flags
            }
        }
    }

    class AppVersionRequest : PhoneAppVersion(Message.AppVersionRequest)
    class AppVersionResponse() : PhoneAppVersion(Message.AppVersionResponse) {
        /**
         * Protocol version. Pebble app always sends [UInt.MAX_VALUE] here.
         *
         * Unused as of v3.0
         */
        val protocolVersion = SUInt(m)

        /**
         * [SessionCapsFlag] flags. Purpose unknown.
         *
         * Note that Pebble app always sends 0 here (no flags)
         *
         * Unused as of v3.0
         */
        val sessionCaps = SUInt(m)

        /**
         * Combined flag for [PlatformFlag] (list of supported features of the phone) and
         * [OSType].
         *
         * Use [PlatformFlag.makeFlags] to serialize and [PlatformFlag.fromFlags] to
         * deserialize.
         *
         * Note [PlatformFlag] part seems to be left unused in the Pebble app.
         */
        val platformFlags = SUInt(m)

        /**
         * Unknown (Pebble app always sends 2 here)
         */
        val responseVersion = SUByte(m, 2u)

        /**
         * Major version of the phone app.
         *
         * Last Pebble app version was 4.4.2, so using "4" here would probably
         * result in a best compatibility with the original Pebble firmware
         */
        val majorVersion = SUByte(m)

        /**
         * Minor version of the phone app.
         *
         * Last Pebble app version was 4.4.2, so using "4" here would probably
         * result in a best compatibility with the original Pebble firmware
         */
        val minorVersion = SUByte(m)

        /**
         * Bugfix version of the phone app.
         *
         * Last Pebble app version was 4.4.2, so using "2" here would probably
         * result in a best compatibility with the original Pebble firmware
         */
        val bugfixVersion = SUByte(m)

        /**
         * Flags for supported protocol features.
         *
         * Use [ProtocolCapsFlag.makeFlags] to serialize and [ProtocolCapsFlag.fromFlags] to
         * deserialize.
         */
        val protocolCaps = SBytes(m, 8)

        constructor(
            protocolVersion: UInt,
            sessionCaps: UInt,
            platformFlags: UInt,
            responseVersion: UByte,
            majorVersion: UByte,
            minorVersion: UByte,
            bugfixVersion: UByte,
            protocolCaps: UByteArray
        ) : this() {
            this.protocolVersion.set(protocolVersion)
            this.sessionCaps.set(sessionCaps)
            this.platformFlags.set(platformFlags)
            this.responseVersion.set(responseVersion)
            this.majorVersion.set(majorVersion)
            this.minorVersion.set(minorVersion)
            this.bugfixVersion.set(bugfixVersion)
            this.protocolCaps.set(protocolCaps)
        }
    }

    companion object {
        val endpoint = ProtocolEndpoint.PHONE_VERSION
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
open class SystemMessage(message: Message) : SystemPacket(endpoint) {
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

    val command = SUByte(m, message.value)
    val messageType = SUByte(m)

    init {
        type = command.get()
        TODO("Incomplete packet declaration")
    }

    companion object {
        val endpoint = ProtocolEndpoint.SYSTEM_MESSAGE
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class BLEControl(opcode: UByte = 0x4u, discoverable: Boolean, duration: UShort) : SystemPacket(
    endpoint
) {
    val command = SUByte(m, opcode)

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
open class PingPong(message: Message, cookie: UInt) : SystemPacket(endpoint) {
    enum class Message(val value: UByte) {
        Ping(0u),
        Pong(1u)
    }

    val command = SUByte(m, message.value)
    val cookie = SUInt(m, cookie)

    init {
        type = command.get()
    }

    class Ping(cookie: UInt = 0u) : PingPong(Message.Ping, cookie) {
        init {
            PacketRegistry.register(endpoint, command.get(), { Ping() })
        }
    }

    class Pong(cookie: UInt = 0u) : PingPong(Message.Pong, cookie) {
        init {
            PacketRegistry.register(endpoint, command.get(), { Pong() })
        }
    }

    companion object {
        val endpoint = ProtocolEndpoint.PING
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun systemPacketsRegister() {
    PacketRegistry.register(
        PhoneAppVersion.endpoint,
        PhoneAppVersion.Message.AppVersionRequest.value
    ) { PhoneAppVersion.AppVersionRequest() }
    PacketRegistry.register(
        PhoneAppVersion.endpoint,
        PhoneAppVersion.Message.AppVersionResponse.value
    ) { PhoneAppVersion.AppVersionResponse() }

    PacketRegistry.register(PingPong.endpoint, PingPong.Message.Ping.value) { PingPong.Ping() }
    PacketRegistry.register(PingPong.endpoint, PingPong.Message.Pong.value) { PingPong.Pong() }
}