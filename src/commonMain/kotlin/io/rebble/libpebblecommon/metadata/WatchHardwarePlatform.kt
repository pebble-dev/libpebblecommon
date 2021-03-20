package io.rebble.libpebblecommon.metadata

enum class WatchHardwarePlatform(val protocolNumber: UByte, val watchType: WatchType) {
    PEBBLE_ONE_EV_1(1u, WatchType.APLITE),
    PEBBLE_ONE_EV_2(2u, WatchType.APLITE),
    PEBBLE_ONE_EV_2_3(3u, WatchType.APLITE),
    PEBBLE_ONE_EV_2_4(4u, WatchType.APLITE),
    PEBBLE_ONE_POINT_FIVE(5u, WatchType.APLITE),
    PEBBLE_ONE_POINT_ZERO(6u, WatchType.APLITE),
    PEBBLE_SNOWY_EVT_2(7u, WatchType.BASALT),
    PEBBLE_SNOWY_DVT(8u, WatchType.BASALT),
    PEBBLE_BOBBY_SMILES(10u, WatchType.BASALT),
    PEBBLE_ONE_BIGBOARD_2(254u, WatchType.APLITE),
    PEBBLE_ONE_BIGBOARD(255u, WatchType.APLITE),
    PEBBLE_SNOWY_BIGBOARD(253u, WatchType.BASALT),
    PEBBLE_SNOWY_BIGBOARD_2(252u, WatchType.BASALT),
    PEBBLE_SPALDING_EVT(9u, WatchType.CHALK),
    PEBBLE_SPALDING_PVT(11u, WatchType.CHALK),
    PEBBLE_SPALDING_BIGBOARD(251u, WatchType.CHALK),
    PEBBLE_SILK_EVT(12u, WatchType.DIORITE),
    PEBBLE_SILK(14u, WatchType.DIORITE),
    PEBBLE_SILK_BIGBOARD(250u, WatchType.DIORITE),
    PEBBLE_SILK_BIGBOARD_2_PLUS(248u, WatchType.DIORITE),
    PEBBLE_ROBERT_EVT(13u, WatchType.EMERY),
    PEBBLE_ROBERT_BIGBOARD(249u, WatchType.EMERY),
    PEBBLE_ROBERT_BIGBOARD_2(247u, WatchType.EMERY);

    companion object {
        fun fromProtocolNumber(number: UByte): WatchHardwarePlatform? {
            return values().firstOrNull { it.protocolNumber == number }
        }
    }
}