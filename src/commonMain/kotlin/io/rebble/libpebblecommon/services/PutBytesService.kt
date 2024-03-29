package io.rebble.libpebblecommon.services

import co.touchlab.kermit.Logger
import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.metadata.WatchType
import io.rebble.libpebblecommon.metadata.pbw.manifest.PbwBlob
import io.rebble.libpebblecommon.packets.*
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.util.Crc32Calculator
import io.rebble.libpebblecommon.util.DataBuffer
import io.rebble.libpebblecommon.util.getPutBytesMaximumDataSize
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import kotlin.math.log

class PutBytesService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<PutBytesResponse>(Channel.BUFFERED)
    val progressUpdates = Channel<PutBytesProgress>(Channel.BUFFERED)

    private val logger = Logger.withTag("PutBytesService")

    data class PutBytesProgress(
        val count: Int,
        val total: Int,
        val delta: Int,
        val cookie: UInt
    )

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.PUT_BYTES, this::receive)
    }

    suspend fun send(packet: PutBytesOutgoingPacket) {
        if (packet is PutBytesAbort) {
            lastCookie = null
        }

        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is PutBytesResponse) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.trySend(packet)
    }

    var lastCookie: UInt? = null

    class PutBytesException(val cookie: UInt?, message: String, cause: Throwable? = null) : Error(message, cause);

    /**
     * Inits a PutBytes session on the device and sends an app, leaves aborting to the caller
     */
    @Throws(PutBytesException::class, IllegalStateException::class)
    suspend fun sendAppPart(
        appId: UInt,
        blob: ByteArray,
        watchType: WatchType,
        watchVersion:  WatchVersion.WatchVersionResponse,
        manifestEntry: PbwBlob,
        type: ObjectType
    ) {
        logger.i { "Send app part $watchType $appId $manifestEntry $type ${type.value}" }
        send(
            PutBytesAppInit(manifestEntry.size.toUInt(), type, appId)
        )

        val cookie = awaitCookieAndPutByteArray(
            blob,
            manifestEntry.crc,
            watchVersion
        )

        logger.d { "Sending install" }

        send(
            PutBytesInstall(cookie)
        )
        awaitAck()

        logger.i { "Install complete" }
    }

    suspend fun sendFirmwarePart(
        blob: ByteArray,
        watchVersion: WatchVersion.WatchVersionResponse,
        crc: Long,
        size: UInt,
        bank: UByte,
        type: ObjectType
    ) {
        logger.i { "Send FW part $type ${type.value}" }
        send(
            PutBytesInit(size, type, bank, "")
        )

        logger.d { "Putting byte array" }
        val cookie = awaitCookieAndPutByteArray(
            blob,
            crc,
            watchVersion
        )

        logger.d { "Sending install" }

        send(
            PutBytesInstall(cookie)
        )
        awaitAck()

        logger.i { "Install complete" }
    }

    suspend fun awaitCookieAndPutByteArray(
        byteArray: ByteArray,
        expectedCrc: Long?,
        watchVersion: WatchVersion.WatchVersionResponse
    ): UInt {
        try {
            val totalToPut = byteArray.size
            val cookie = awaitAck().cookie.get()
            lastCookie = cookie
            progressUpdates.trySend(
                PutBytesProgress(0, totalToPut, 0, cookie)
            )

            val maxDataSize =  if (watchVersion.running.isRecovery.get()) 2000 else getPutBytesMaximumDataSize(watchVersion)
            val buffer = DataBuffer(byteArray.asUByteArray())
            val crcCalculator = Crc32Calculator()

            var totalBytes = 0
            while (true) {
                val dataToRead = maxDataSize.coerceAtMost(buffer.remaining)
                if (dataToRead <= 0) {
                    break
                }
                val payload = buffer.getBytes(dataToRead)

                crcCalculator.addBytes(payload)

                send(PutBytesPut(cookie, payload))
                awaitAck()
                totalBytes += dataToRead
                progressUpdates.trySend(
                    PutBytesProgress(totalBytes, totalToPut, dataToRead, cookie)
                )
            }
            val calculatedCrc = crcCalculator.finalize()
            if (expectedCrc != null && calculatedCrc != expectedCrc.toUInt()) {
                throw IllegalStateException(
                    "Sending fail: Crc mismatch ($calculatedCrc != $expectedCrc)"
                )
            }

            logger.d { "Sending commit" }
            send(
                PutBytesCommit(cookie, calculatedCrc)
            )
            awaitAck()
            return cookie
        } catch (e: Error) {
            throw  PutBytesException(lastCookie, "awaitCookieAndPutByteArray failed: ${e.message}", e)
        }
    }

    private suspend fun getResponse(): PutBytesResponse {
        return withTimeout(20_000) {
            val iterator = receivedMessages.iterator()
            if (!iterator.hasNext()) {
                throw IllegalStateException("Received messages channel is closed")
            }

            iterator.next()
        }
    }

    private suspend fun awaitAck(): PutBytesResponse {
        val response = getResponse()

        val result = response.result.get()
        if (result != PutBytesResult.ACK.value) {
            throw PutBytesException(lastCookie, "Watch responded with NACK ($result). Aborting transfer")
        }

        return response
    }

}