package io.rebble.libpebblecommon.util

import io.rebble.libpebblecommon.metadata.pbw.appinfo.PbwAppInfo
import io.rebble.libpebblecommon.metadata.pbw.manifest.PbwManifest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SerializationUtil {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeAppInfo(appInfo: PbwAppInfo): String = json.encodeToString(appInfo)
    fun deserializeAppInfo(jsonString: String): PbwAppInfo = json.decodeFromString(jsonString)

    fun serializeManifest(manifest: PbwManifest): String = json.encodeToString(manifest)
    fun deserializeManifest(jsonString: String): PbwManifest = json.decodeFromString(jsonString)
}