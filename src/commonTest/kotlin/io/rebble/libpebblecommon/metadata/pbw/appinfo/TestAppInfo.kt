package io.rebble.libpebblecommon.metadata.pbw.appinfo

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import io.rebble.libpebblecommon.metadata.StringOrBoolean

class TestAppInfo {
    companion object {
        const val APP_INFO_JSON_SIMPLICITY = "{\"versionLabel\": \"3.2\", \"companyName\": \"Pebble Technology\", \"targetPlatforms\": [\"aplite\", \"basalt\", \"chalk\"], \"resources\": {\"media\": [{\"name\": \"IMAGE_MENU_ICON\", \"type\": \"png\", \"file\": \"images/menu_icon_simplicity.png\", \"menuIcon\": true}]}, \"sdkVersion\": \"3\", \"longName\": \"Simplicity\", \"watchapp\": {\"watchface\": true}, \"projectType\": \"native\", \"capabilities\": [\"\"], \"uuid\": \"54eada19-67fd-4947-a7c5-256f24e3a7d7\", \"shortName\": \"Simplicity\", \"appKeys\": {}}"
        const val APP_INFO_JSON_DIALER_FOR_PEBBLE = "{\"targetPlatforms\":[\"aplite\",\"basalt\",\"chalk\"],\"projectType\":\"native\",\"messageKeys\":{},\"companyName\":\"matejdro\",\"enableMultiJS\":false,\"watchapp\":{\"onlyShownOnCommunication\":false,\"hiddenApp\":false,\"watchface\":false},\"versionLabel\":\"3.3\",\"longName\":\"Dialer\",\"shortName\":\"Dialer\",\"name\":\"Dialer\",\"sdkVersion\":\"3\",\"displayName\":\"Dialer\",\"uuid\":\"158a074d-85ce-43d2-ab7d-14416ddc1058\",\"appKeys\":{},\"capabilities\":[],\"resources\":{\"media\":[{\"menuIcon\":\"true\",\"type\":\"bitmap\",\"name\":\"ICON\",\"file\":\"icon.png\"},{\"type\":\"bitmap\",\"name\":\"ANSWER\",\"file\":\"answer.png\"},{\"type\":\"bitmap\",\"name\":\"ENDCALL\",\"file\":\"endcall.png\"},{\"type\":\"bitmap\",\"name\":\"MIC_OFF\",\"file\":\"mic_off.png\"},{\"type\":\"bitmap\",\"name\":\"MIC_ON\",\"file\":\"micon.png\"},{\"type\":\"bitmap\",\"name\":\"SPEAKER_ON\",\"file\":\"speakeron.png\"},{\"type\":\"bitmap\",\"name\":\"SPEAKER_OFF\",\"file\":\"speakeroff.png\"},{\"type\":\"bitmap\",\"name\":\"VOLUME_UP\",\"file\":\"volumeup.png\"},{\"type\":\"bitmap\",\"name\":\"VOLUME_DOWN\",\"file\":\"volumedown.png\"},{\"type\":\"bitmap\",\"name\":\"CALL_HISTORY\",\"file\":\"callhistory.png\"},{\"type\":\"bitmap\",\"name\":\"CONTACTS\",\"file\":\"contacts.png\"},{\"type\":\"bitmap\",\"name\":\"CONTACT_GROUP\",\"file\":\"contactgroup.png\"},{\"type\":\"bitmap\",\"name\":\"INCOMING_CALL\",\"file\":\"incomingcall.png\"},{\"type\":\"bitmap\",\"name\":\"OUTGOING_CALL\",\"file\":\"outgoingcall.png\"},{\"type\":\"bitmap\",\"name\":\"MISSED_CALL\",\"file\":\"missedcall.png\"},{\"type\":\"bitmap\",\"name\":\"MESSAGE\",\"file\":\"message.png\"},{\"type\":\"bitmap\",\"name\":\"CALL\",\"file\":\"call.png\"}]}}"

        val APP_INFO_OBJ_SIMPLICITY = PbwAppInfo(
            uuid = "54eada19-67fd-4947-a7c5-256f24e3a7d7",
            shortName = "Simplicity",
            longName = "Simplicity",
            companyName = "Pebble Technology",
            versionLabel = "3.2",
            capabilities = listOf(""),
            resources = Resources(
                media = listOf(
                    Media(
                        resourceFile = "images/menu_icon_simplicity.png",
                        menuIcon = StringOrBoolean(true),
                        name = "IMAGE_MENU_ICON",
                        type = "png"
                    )
                )
            ),
            sdkVersion = "3",
            targetPlatforms = listOf("aplite", "basalt", "chalk"),
            watchapp = Watchapp(
                watchface = true
            )
        )

        val APP_INFO_OBJ_DIALER_FOR_PEBBLE = PbwAppInfo(
            uuid = "158a074d-85ce-43d2-ab7d-14416ddc1058",
            shortName = "Dialer",
            longName = "Dialer",
            companyName = "matejdro",
            versionLabel = "3.3",
            resources = Resources(
                media = listOf(
                    Media(
                        resourceFile = "icon.png",
                        menuIcon = StringOrBoolean(true),
                        name = "ICON",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "answer.png",
                        name = "ANSWER",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "endcall.png",
                        name = "ENDCALL",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "mic_off.png",
                        name = "MIC_OFF",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "micon.png",
                        name = "MIC_ON",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "speakeron.png",
                        name = "SPEAKER_ON",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "speakeroff.png",
                        name = "SPEAKER_OFF",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "volumeup.png",
                        name = "VOLUME_UP",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "volumedown.png",
                        name = "VOLUME_DOWN",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "callhistory.png",
                        name = "CALL_HISTORY",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "contacts.png",
                        name = "CONTACTS",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "contactgroup.png",
                        name = "CONTACT_GROUP",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "incomingcall.png",
                        name = "INCOMING_CALL",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "outgoingcall.png",
                        name = "OUTGOING_CALL",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "missedcall.png",
                        name = "MISSED_CALL",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "message.png",
                        name = "MESSAGE",
                        type = "bitmap"
                    ),
                    Media(
                        resourceFile = "call.png",
                        name = "CALL",
                        type = "bitmap"
                    )
                )
            ),
            sdkVersion = "3",
            targetPlatforms = listOf("aplite", "basalt", "chalk"),
            watchapp = Watchapp()
        )
    }
    @Test
    fun deserialization() {
        val json = Json{ ignoreUnknownKeys = true }
        val simplicity: PbwAppInfo = json.decodeFromString(APP_INFO_JSON_SIMPLICITY)
        assertEquals(APP_INFO_OBJ_SIMPLICITY, simplicity)

        val dialerForPebble: PbwAppInfo = json.decodeFromString(APP_INFO_JSON_DIALER_FOR_PEBBLE)
        assertEquals(APP_INFO_OBJ_DIALER_FOR_PEBBLE, dialerForPebble)
    }
}