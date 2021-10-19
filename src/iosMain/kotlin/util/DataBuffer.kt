package io.rebble.libpebblecommon.util

import kotlinx.cinterop.*
import platform.Foundation.*

actual class DataBuffer {
    private val actualBuf: NSMutableData
    private var littleEndian = false

    /**
     * Total length of the buffer
     */
    actual val length: Int
        get() = actualBuf.length.toInt()

    private var _readPosition: Int = 0

    /**
     * Current position in the buffer
     */
    actual val readPosition: Int
        get() = _readPosition

    actual constructor(size: Int) {
        actualBuf = NSMutableData.dataWithCapacity(size.toULong())!!
    }

    actual constructor(bytes: UByteArray) {
        actualBuf = NSMutableData()
        memScoped {
            actualBuf.setData(
                NSData.create(bytes = allocArrayOf(bytes.toByteArray()), length = bytes.size.toULong())
            )
        }
    }

    private fun shouldReverse(): Boolean {
        return if (isPlatformBigEndian() && !littleEndian) {
            false
        }else if (isPlatformBigEndian() && littleEndian) {
            true
        }else !isPlatformBigEndian() && !littleEndian
    }

    actual fun putUShort(short: UShort) {
        memScoped {
            val pShort = alloc<UShortVar>()
            pShort.value = if (shouldReverse())  reverseOrd(short) else short
            actualBuf.appendBytes(pShort.ptr, UShort.SIZE_BYTES.toULong())
        }
    }
    actual fun getUShort(): UShort {
        memScoped {
            val pShort = alloc<UShortVar>()
            actualBuf.getBytes(pShort.ptr, NSMakeRange(_readPosition.toULong(), UShort.SIZE_BYTES.toULong()))
            _readPosition += UShort.SIZE_BYTES
            return if (shouldReverse()) reverseOrd(pShort.value) else pShort.value
        }
    }

    actual fun putShort(short: Short) {
        memScoped {
            val pShort = alloc<ShortVar>()
            pShort.value = if (shouldReverse()) reverseOrd(short.toUShort()).toShort() else short
            actualBuf.appendBytes(pShort.ptr, Short.SIZE_BYTES.toULong())
        }
    }
    actual fun getShort(): Short {
        memScoped {
            val pShort = alloc<ShortVar>()
            actualBuf.getBytes(pShort.ptr, NSMakeRange(_readPosition.toULong(), Short.SIZE_BYTES.toULong()))
            _readPosition += Short.SIZE_BYTES
            return if (shouldReverse()) reverseOrd(pShort.value.toUShort()).toShort() else pShort.value
        }
    }

    actual fun putUByte(byte: UByte) {
        memScoped {
            val pByte = alloc<UByteVar>()
            pByte.value = byte
            actualBuf.appendBytes(pByte.ptr, UByte.SIZE_BYTES.toULong())
        }
    }
    actual fun getUByte(): UByte {
        memScoped {
            val pByte = alloc<UByteVar>()
            actualBuf.getBytes(pByte.ptr, NSMakeRange(_readPosition.toULong(), UByte.SIZE_BYTES.toULong()))
            _readPosition += UByte.SIZE_BYTES
            return pByte.value
        }
    }

    actual fun putByte(byte: Byte) {
        memScoped {
            val pByte = alloc<ByteVar>()
            pByte.value = byte
            actualBuf.appendBytes(pByte.ptr, Byte.SIZE_BYTES.toULong())
        }
    }
    actual fun getByte(): Byte {
        memScoped {
            val pByte = alloc<ByteVar>()
            actualBuf.getBytes(pByte.ptr, NSMakeRange(_readPosition.toULong(), Byte.SIZE_BYTES.toULong()))
            _readPosition += Byte.SIZE_BYTES
            return pByte.value
        }
    }

    actual fun putBytes(bytes: UByteArray) {
        memScoped {
            val pBytes = allocArrayOf(bytes.toByteArray())
            actualBuf.appendBytes(pBytes, bytes.size.toULong())
        }
    }
    actual fun getBytes(count: Int): UByteArray {
        memScoped {
            val pBytes = allocArray<UByteVar>(count)
            actualBuf.getBytes(pBytes.getPointer(this), NSMakeRange(_readPosition.toULong(), count.toULong()))
            _readPosition += count
            return pBytes.readBytes(count).toUByteArray()
        }
    }

    actual fun array(): UByteArray = getBytes(actualBuf.length.toInt())

    actual fun setEndian(endian: Char) {
        littleEndian = endian == '<'
    }

    actual fun putUInt(uint: UInt) {
        memScoped {
            val pUInt = alloc<UIntVar>()
            pUInt.value = if (shouldReverse()) reverseOrd(uint) else uint
            actualBuf.appendBytes(pUInt.ptr, UInt.SIZE_BYTES.toULong())
        }
    }
    actual fun getUInt(): UInt {
        memScoped {
            val pUInt = alloc<UIntVar>()
            actualBuf.getBytes(pUInt.ptr, NSMakeRange(_readPosition.toULong(), UInt.SIZE_BYTES.toULong()))
            _readPosition += UInt.SIZE_BYTES
            return if (shouldReverse()) reverseOrd(pUInt.value) else pUInt.value
        }
    }

    actual fun putInt(int: Int) {
        memScoped {
            val pInt = alloc<IntVar>()
            pInt.value = if (shouldReverse()) reverseOrd(int.toUInt()).toInt() else int
            actualBuf.appendBytes(pInt.ptr, Int.SIZE_BYTES.toULong())
        }
    }
    actual fun getInt(): Int {
        memScoped {
            val pInt = alloc<IntVar>()
            actualBuf.getBytes(pInt.ptr, NSMakeRange(_readPosition.toULong(), Int.SIZE_BYTES.toULong()))
            _readPosition += Int.SIZE_BYTES
            return if (shouldReverse()) reverseOrd(pInt.value.toUInt()).toInt() else pInt.value
        }
    }

    actual fun putULong(ulong: ULong) {
        memScoped {
            val pULong = alloc<ULongVar>()
            pULong.value = if (shouldReverse()) reverseOrd(ulong) else ulong
            actualBuf.appendBytes(pULong.ptr, ULong.SIZE_BYTES.toULong())
        }
    }
    actual fun getULong(): ULong {
        memScoped {
            val pULong = alloc<ULongVar>()
            actualBuf.getBytes(pULong.ptr, NSMakeRange(_readPosition.toULong(), ULong.SIZE_BYTES.toULong()))
            _readPosition += ULong.SIZE_BYTES
            return if (shouldReverse()) reverseOrd(pULong.value) else pULong.value
        }
    }

    actual fun rewind() {
        TODO("iOS rewind buffer")
    }
}