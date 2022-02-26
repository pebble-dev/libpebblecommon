package io.rebble.libpebblecommon.metadata.pbw.manifest


import kotlinx.serialization.Serializable

@Serializable
data class PbwManifest(
    val application: PbwBlob,
    val resources: PbwBlob?,
    val worker: PbwBlob? = null,
    val debug: Debug?,
    val generatedAt: Int?,
    val generatedBy: String?,
    val manifestVersion: Int?,
    val type: String?
)