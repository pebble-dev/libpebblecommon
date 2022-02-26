package io.rebble.libpebblecommon.metadata.pbw.manifest

import kotlinx.serialization.Serializable

@Serializable
data class SdkVersion(
        val major: Int?,
        val minor: Int?
)