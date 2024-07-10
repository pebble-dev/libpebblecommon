package io.rebble.libpebblecommon.util

data class PebbleColor(
    val alpha: UByte,
    val red: UByte,
    val green: UByte,
    val blue: UByte
)

fun PebbleColor.toProtocolNumber() =
    ((alpha / 85u) shl 6) or
    ((red / 85u) shl 4) or
    ((green / 85u) shl 2) or
    (blue / 85u)
