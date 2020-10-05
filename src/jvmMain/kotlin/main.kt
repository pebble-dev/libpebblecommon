package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.packets.PhoneAppVersion

@OptIn(ExperimentalUnsignedTypes::class)
actual fun getPlatform(): PhoneAppVersion.OSType = PhoneAppVersion.OSType.Unknown