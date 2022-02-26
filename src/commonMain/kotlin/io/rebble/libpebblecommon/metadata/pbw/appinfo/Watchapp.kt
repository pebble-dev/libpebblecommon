package io.rebble.libpebblecommon.metadata.pbw.appinfo

import kotlinx.serialization.Serializable

@Serializable
data class Watchapp(
    val watchface: Boolean = false,
    val hiddenApp: Boolean = false,
    val onlyShownOnCommunication: Boolean = false
)