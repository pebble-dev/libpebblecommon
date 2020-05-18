package io.rebble.libpebblecommon

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.darwin.NSUInteger
import kotlin.collections.get

@ExperimentalUnsignedTypes
actual class DataBuffer {
    private val actualBuf: NSMutableData
    private var littleEndian = false

    actual constructor(size: Int) {
        actualBuf = NSMutableData.dataWithLength(size.toULong())!!
        actualBuf.setLength(size.toULong())
    }
    actual constructor(bytes: UByteArray) {
        actualBuf = NSMutableData()
        actualBuf.setData(NSString.create(string = bytes.toString())
                .dataUsingEncoding(NSUTF8StringEncoding, false)!!)
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
            return pShort.value
        }
    }

    actual fun putByte(byte: UByte) {
        memScoped {
            val pByte = alloc<UByteVar>()
            pByte.value = byte
            actualBuf.appendBytes(pByte.ptr, UByte.SIZE_BYTES.toULong())
        }
    }
    actual fun getByte(): UByte {
        memScoped {
            val pByte = alloc<UByteVar>()
            actualBuf.appendBytes(pByte.ptr, UByte.SIZE_BYTES.toULong())
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
            return pUInt.value
        }
    }

    actual fun putULong(ulong: ULong) {
        memScoped {
            val pULong = alloc<ULongVar>()
            pULong.value = ulong
            actualBuf.appendBytes(pULong.ptr, UByte.SIZE_BYTES.toULong())
        }
    }
    actual fun getULong(): ULong {
        memScoped {
            val pULong = alloc<ULongVar>()
            actualBuf.getBytes(pULong.ptr, ULong.SIZE_BYTES.toULong())
            return pULong.value
        }
    }
}