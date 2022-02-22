package io.rebble.libpebblecommon.util

actual fun runBlocking(block: suspend () -> Unit) = kotlinx.coroutines.runBlocking{block()}