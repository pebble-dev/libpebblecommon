package io.rebble.libpebblecommon.util

import kotlinx.cinterop.*

actual fun runBlocking(block: suspend () -> Unit) = kotlinx.coroutines.runBlocking{block()}

internal fun isPlatformBigEndian(): Boolean {
    memScoped {
        val i = alloc<IntVar>()
        i.value = 1
        val bytes = i.reinterpret<ByteVar>()
        return bytes.value == 0.toByte()
    }
}

internal fun reverseOrd(varr: UShort): UShort = (((varr.toInt() and 0xff) shl 8) or ((varr.toInt() and 0xffff) ushr 8)).toUShort()

internal fun reverseOrd(varr: UInt): UInt = ((reverseOrd((varr and 0xffffu).toUShort()).toInt() shl 16) or (reverseOrd((varr shr 16).toUShort()).toInt() and 0xffff)).toUInt()

internal fun reverseOrd(varr: ULong): ULong = ((reverseOrd((varr and 0xffffffffu).toUInt()).toLong() shl 32) or (reverseOrd((varr shr 32).toUInt()).toLong() and 0xffffffff)).toULong()