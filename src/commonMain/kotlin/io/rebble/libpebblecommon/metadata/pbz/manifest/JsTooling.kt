package io.rebble.libpebblecommon.metadata.pbz.manifest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsTooling(
    @SerialName("bytecode_version")
    val bytecodeVersion: Int
)
