package io.rebble.libpebblecommon.packets.blobdb

import io.rebble.libpebblecommon.structmapper.*
import io.rebble.libpebblecommon.util.Endian

/**
 * Data of the APP BlobDB Entry
 */
class AppMetadata() : StructMappable() {
    /**
     * UUID of the app
     */
    val uuid: SUUID = SUUID(m)

    /**
     * App install flags.
     */
    val flags: SUInt = SUInt(m, endianness = Endian.Little)

    /**
     * Resource ID of the primary icon.
     */
    val icon: SUInt = SUInt(m, endianness = Endian.Little)

    /**
     * Major app version.
     */
    val appVersionMajor: SUByte = SUByte(m)

    /**
     * Minor app version.
     */
    val appVersionMinor: SUByte = SUByte(m)

    /**
     * Major sdk version.
     */
    val sdkVersionMajor: SUByte = SUByte(m)

    /**
     * Minor sdk version.
     */
    val sdkVersionMinor: SUByte = SUByte(m)

    /**
     * ??? (Always sent as 0 in the Pebble app)
     */
    val appFaceBgColor: SUByte = SUByte(m, 0u)

    /**
     * ??? (Always sent as 0 in the Pebble app)
     */
    val appFaceTemplateId: SUByte = SUByte(m, 0u)

    /**
     * Name of the app
     */
    val appName: SFixedString = SFixedString(m, 96)
}

