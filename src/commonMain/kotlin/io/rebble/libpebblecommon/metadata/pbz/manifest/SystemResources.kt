package io.rebble.libpebblecommon.metadata.pbz.manifest

import kotlinx.serialization.Serializable

@Serializable
data class SystemResources(
    val name: String,
    val timestamp: Long,
    val size: Long,
    val crc: Long
)
