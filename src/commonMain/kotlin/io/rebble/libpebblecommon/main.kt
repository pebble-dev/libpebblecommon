package io.rebble.libpebblecommon

import io.rebble.libpebblecommon.packets.PhoneAppVersion

@OptIn(ExperimentalUnsignedTypes::class)
expect fun getPlatform(): PhoneAppVersion.OSType