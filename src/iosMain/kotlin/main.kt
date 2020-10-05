package io.rebble.libpebblecommon

@OptIn(ExperimentalUnsignedTypes::class)
actual fun getPlatform(): PhoneAppVersion.OSType = PhoneAppVersion.OSType.IOS