package io.rebble.libpebblecommon.util

actual class Bitmap {
    actual val width: Int
        get() = throw UnsupportedOperationException("Not supported on generic JVM")
    actual val height: Int
        get() = throw UnsupportedOperationException("Not supported on generic JVM")

    /**
     * Return pixel at the specified position at in AARRGGBB format.
     */
    actual fun getPixel(x: Int, y: Int): Int {
        throw UnsupportedOperationException("Not supported on generic JVM")
    }
}