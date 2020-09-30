package io.rebble.libpebblecommon

interface BluetoothConnection {
    suspend fun sendPacket(data: ByteArray)
    fun setReceiveCallback(callback: suspend (ByteArray) -> Unit)
}