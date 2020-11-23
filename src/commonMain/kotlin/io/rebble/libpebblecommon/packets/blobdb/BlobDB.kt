package io.rebble.libpebblecommon.packets.blobdb

import io.rebble.libpebblecommon.protocolhelpers.PacketRegistry
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.structmapper.SBytes
import io.rebble.libpebblecommon.structmapper.SUByte
import io.rebble.libpebblecommon.structmapper.SUShort

open class BlobCommand constructor(message: Message, token: UShort, database: BlobDatabase) : PebblePacket(
    endpoint
) {
    enum class Message(val value: UByte) {
        Insert(0x01u),
        Delete(0x04u),
        Clear(0x05u)
    }

    enum class BlobDatabase(val id: UByte) {
        Test(0u),
        Pin(1u),
        App(2u),
        Reminder(3u),
        Notification(4u),
        AppGlance(11u)
    }

    val command = SUByte(m, message.value)
    val token = SUShort(m, token)
    val database = SUByte(m, database.id)

    open class InsertCommand(token: UShort, database: BlobDatabase, key: UByteArray, value: UByteArray) : BlobCommand(
        Message.Insert, token, database
    ) {
        val keySize = SUByte(m, key.size.toUByte())
        val targetKey = SBytes(m, key.size, key)
        val valSize = SUShort(m, value.size.toUShort(), endianness = '<')
        val targetValue = SBytes(m, value.size, value)
    }

    class DeleteCommand(token: UShort, database: BlobDatabase, key: UByteArray) : BlobCommand(
        Message.Delete, token, database
    ) {
        val keySize = SUByte(m, key.size.toUByte())
        val targetKey = SBytes(m, key.size, key)
    }

    class ClearCommand(token: UShort, database: BlobDatabase) : BlobCommand(
        Message.Clear, token, database
    )

    companion object {
        val endpoint = ProtocolEndpoint.BLOBDB_V1
    }
}

open class BlobResponse(response: BlobStatus = BlobStatus.GeneralFailure) : PebblePacket(endpoint) {
    enum class BlobStatus(val value: UByte) {
        Success(0x01u),
        GeneralFailure(0x02u),
        InvalidOperation(0x03u),
        InvalidDatabaseID(0x04u),
        InvalidData(0x05u),
        KeyDoesNotExist(0x06u),
        DatabaseFull(0x07u),
        DataStale(0x08u),
        NotSupported(0x09u),
        Locked(0xAu),
        TryLater(0xBu)
    }

    class Success : BlobResponse(BlobStatus.Success)
    class GeneralFailure : BlobResponse(BlobStatus.GeneralFailure)
    class InvalidOperation : BlobResponse(BlobStatus.InvalidOperation)
    class InvalidDatabaseID : BlobResponse(BlobStatus.InvalidDatabaseID)
    class InvalidData : BlobResponse(BlobStatus.InvalidData)
    class KeyDoesNotExist : BlobResponse(BlobStatus.KeyDoesNotExist)
    class DatabaseFull : BlobResponse(BlobStatus.DatabaseFull)
    class DataStale : BlobResponse(BlobStatus.DataStale)
    class NotSupported : BlobResponse(BlobStatus.NotSupported)
    class Locked : BlobResponse(BlobStatus.Locked)
    class TryLater : BlobResponse(BlobStatus.TryLater)

    val token = SUShort(m)
    val response = SUByte(m, response.value)

    companion object {
        val endpoint = ProtocolEndpoint.BLOBDB_V1
    }
}

fun blobDBPacketsRegister() {
    PacketRegistry.registerCustomTypeOffset(BlobResponse.endpoint, 4 + UShort.SIZE_BYTES)
    PacketRegistry.register(BlobResponse.endpoint, BlobResponse.BlobStatus.Success.value) { BlobResponse.Success() }
    PacketRegistry.register(
        BlobResponse.endpoint,
        BlobResponse.BlobStatus.GeneralFailure.value
    ) { BlobResponse.GeneralFailure() }
    PacketRegistry.register(
        BlobResponse.endpoint,
        BlobResponse.BlobStatus.InvalidOperation.value
    ) { BlobResponse.InvalidOperation() }
    PacketRegistry.register(
        BlobResponse.endpoint,
        BlobResponse.BlobStatus.InvalidDatabaseID.value
    ) { BlobResponse.InvalidDatabaseID() }
    PacketRegistry.register(
        BlobResponse.endpoint,
        BlobResponse.BlobStatus.InvalidData.value
    ) { BlobResponse.InvalidData() }
    PacketRegistry.register(
        BlobResponse.endpoint,
        BlobResponse.BlobStatus.KeyDoesNotExist.value
    ) { BlobResponse.KeyDoesNotExist() }
    PacketRegistry.register(
        BlobResponse.endpoint,
        BlobResponse.BlobStatus.DatabaseFull.value
    ) { BlobResponse.DatabaseFull() }
    PacketRegistry.register(BlobResponse.endpoint, BlobResponse.BlobStatus.DataStale.value) { BlobResponse.DataStale() }
    PacketRegistry.register(
        BlobResponse.endpoint,
        BlobResponse.BlobStatus.NotSupported.value
    ) { BlobResponse.NotSupported() }
    PacketRegistry.register(BlobResponse.endpoint, BlobResponse.BlobStatus.Locked.value) { BlobResponse.Locked() }
    PacketRegistry.register(BlobResponse.endpoint, BlobResponse.BlobStatus.TryLater.value) { BlobResponse.TryLater() }
}