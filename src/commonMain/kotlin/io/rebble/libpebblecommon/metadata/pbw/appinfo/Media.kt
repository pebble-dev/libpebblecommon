package io.rebble.libpebblecommon.metadata.pbw.appinfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Media(
    @SerialName("file")
    val resourceFile: String,
    val menuIcon: Boolean = false,
    val name: String,
    val type: String
)