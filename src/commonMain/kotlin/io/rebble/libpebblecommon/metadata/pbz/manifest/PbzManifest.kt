package io.rebble.libpebblecommon.metadata.pbz.manifest

import io.rebble.libpebblecommon.metadata.pbw.manifest.Debug
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PbzManifest(
    val manifestVersion: Int,
    val generatedAt: Long,
    val generatedBy: String? = null,
    val debug: Debug? = null,
    val firmware: PbzFirmware,
    val resources: SystemResources? = null,
    @SerialName("js_tooling")
    val jsTooling: JsTooling? = null,
    val type: String
)