package io.rebble.libpebblecommon.packets.blobdb

enum class TimelineIcon(val id: UInt) {
    NotificationReminder(3u),
    HockeyGame(30u),
    PayBill(38u),
    NotificationLinkedIn(115u),
    NotificationGoogleInbox(61u),
    GenericQuestion(63u),
    NotificationFlag(4u),
    GenericSms(45u),
    WatchDisconnected(48u),
    TvShow(73u),
    Basketball(74u),
    GenericWarning(28u),
    LightRain(32u),
    NotificationFacebook(11u),
    IncomingPhoneCall(78u),
    NotificationGoogleMessenger(76u),
    NotificationTelegram(7u),
    NotificationFacetime(110u),
    ArrowDown(99u),
    NotificationOutlook(64u),
    NoEvents(57u),
    AudioCassette(12u),
    Sunset(85u),
    NotificationTwitter(6u),
    Sunrise(84u),
    HeavyRain(52u),
    NotificationMailbox(60u),
    AmericanFootball(20u),
    CarRental(24u),
    CricketGame(26u),
    NotificationWeChat(71u),
    NotificationGeneric(1u),
    NotificationSkype(68u),
    CloudyDay(25u),
    DuringPhoneCallCentered(95u),
    NotificationLine(67u),
    HotelReservation(31u),
    NotificationFacebookMessenger(10u),
    NotificationLighthouse(81u),
    TimelineEmptyCalendar(96u),
    NotificationIosPhotos(114u),
    ResultDeleted(43u),
    NotificationGmail(9u),
    TimelineMissedCall(2u),
    Sleep(101u),
    ResultMute(46u),
    NotificationAmazon(111u),
    ThumbsUp(97u),
    ScheduledFlight(54u),
    Settings(83u),
    PartlyCloudy(37u),
    StocksEvent(42u),
    NotificationGoogleMaps(112u),
    RewardGood(103u),
    NotificationYahooMail(72u),
    BirthdayEvent(23u),
    GenericEmail(19u),
    ResultDismissed(51u),
    NotificationGooglePhotos(113u),
    TideIsHigh(50u),
    NotificationViber(70u),
    LightSnow(33u),
    NewsEvent(36u),
    GenericConfirmation(55u),
    TimelineSports(17u),
    NotificationSlack(116u),
    CheckInternetConnection(44u),
    Activity(100u),
    NotificationHipChat(77u),
    NotificationInstagram(59u),
    TimelineBaseball(22u),
    RewardBad(102u),
    ReachedFitnessGoal(66u),
    DaySeparator(56u),
    TimelineCalendar(21u),
    RainingAndSnowing(65u),
    RadioShow(39u),
    DismissedPhoneCall(75u),
    ArrowUp(98u),
    RewardAverage(104u),
    MusicEvent(35u),
    NotificationSnapchat(69u),
    NotificationBlackberryMessenger(58u),
    NotificationWhatsapp(5u),
    Location(82u),
    SoccerGame(41u),
    ResultFailed(62u),
    ResultUnmute(86u),
    ScheduledEvent(40u),
    TimelineWeather(14u),
    TimelineSun(16u),
    NotificationGoogleHangouts(8u),
    DuringPhoneCall(49u),
    NotificationKik(80u),
    ResultUnmuteAlt(94u),
    MovieEvent(34u),
    GlucoseMonitor(29u),
    ResultSent(47u),
    AlarmClock(13u),
    HeavySnow(53u),
    DinnerReservation(27u),
    NotificationKakaoTalk(79u);

    companion object {
        fun fromId(id: UInt): TimelineIcon {
            return entries.firstOrNull { it.id == id }
                ?: error("Unknown timeline icon id: $id")
        }
    }
}