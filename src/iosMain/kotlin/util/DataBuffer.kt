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
        actualBuf = NSMutableData.dataWithLength(size.toULong())!!
        actualBuf.setLength(size.toULong())
    }

    actual constructor(bytes: UByteArray) {
        actualBuf = NSMutableData()
        actualBuf.setData(
            NSString.create(string = bytes.toString())
                .dataUsingEncoding(NSUTF8StringEncoding, false)!!
        )
    }

    actual fun putUShort(short: UShort) {
        memScoped {
            val pShort = alloc<UShortVar>()
            pShort.value = short
            actualBuf.appendBytes(pShort.ptr, UShort.SIZE_BYTES.toULong())
        }
    }
    actual fun getUShort(): UShort {
        memScoped {
            val pShort = alloc<UShortVar>()
            actualBuf.getBytes(pShort.ptr, UShort.SIZE_BYTES.toULong())
            _readPosition += UShort.SIZE_BYTES
            return pShort.value
        }
    }

    actual fun putShort(short: Short) {
        memScoped {
            val pShort = alloc<ShortVar>()
            pShort.value = short
            actualBuf.appendBytes(pShort.ptr, Short.SIZE_BYTES.toULong())
        }
    }
    actual fun getShort(): Short {
        memScoped {
            val pShort = alloc<ShortVar>()
            actualBuf.getBytes(pShort.ptr, Short.SIZE_BYTES.toULong())
            _readPosition += Short.SIZE_BYTES
            return pShort.value
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
            actualBuf.appendBytes(pByte.ptr, UByte.SIZE_BYTES.toULong())
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
            actualBuf.appendBytes(pByte.ptr, Byte.SIZE_BYTES.toULong())
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
            actualBuf.getBytes(pBytes.getPointer(this), length = count.toULong())
            _readPosition += count
            return pBytes.readBytes(count).toUByteArray()
        }
    }

    actual fun array(): UByteArray = getBytes(actualBuf.length.toInt())

    actual fun setEndian(endian: Char) {
        littleEndian = endian == '<'
        if (littleEndian) TODO("iOS little endian")
    }

    actual fun putUInt(uint: UInt) {
        memScoped {
            val pUInt = alloc<UIntVar>()
            pUInt.value = uint
            actualBuf.appendBytes(pUInt.ptr, UInt.SIZE_BYTES.toULong())
        }
    }
    actual fun getUInt(): UInt {
        memScoped {
            val pUInt = alloc<UIntVar>()
            actualBuf.getBytes(pUInt.ptr, UInt.SIZE_BYTES.toULong())
            _readPosition += UInt.SIZE_BYTES
            return pUInt.value
        }
    }

    actual fun putInt(int: Int) {
        memScoped {
            val pInt = alloc<IntVar>()
            pInt.value = int
            actualBuf.appendBytes(pInt.ptr, Int.SIZE_BYTES.toULong())
        }
    }
    actual fun getInt(): Int {
        memScoped {
            val pInt = alloc<IntVar>()
            actualBuf.getBytes(pInt.ptr, Int.SIZE_BYTES.toULong())
            _readPosition += Int.SIZE_BYTES
            return pInt.value
        }
    }

    actual fun putULong(ulong: ULong) {
        memScoped {
            val pULong = alloc<ULongVar>()
            pULong.value = ulong
            actualBuf.appendBytes(pULong.ptr, ULong.SIZE_BYTES.toULong())
        }
    }
    actual fun getULong(): ULong {
        memScoped {
            val pULong = alloc<ULongVar>()
            actualBuf.getBytes(pULong.ptr, ULong.SIZE_BYTES.toULong())
            _readPosition += ULong.SIZE_BYTES
            return pULong.value
        }
    }
}