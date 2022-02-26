package io.rebble.libpebblecommon.metadata.pbw.manifest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PbwBlob(
        val crc: Long?,
        val name: String,
        @SerialName("sdk_version")
        val sdkVersion: SdkVersion? = null,
        val size: Int,
        val timestamp: Int?
)