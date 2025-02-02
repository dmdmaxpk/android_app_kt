package com.dmdmax.goonj.firebase

class EventName {
    companion object {
        val EVENT_SPLASH_SCREEN = "Splash_Screen"
        val EVENT_AGREE = "Agree"
        val EVENT_DISAGREE = "Disagree"
        val EVENT_NEWS_TAB_VIEW = "News_Tab_View"
        val EVENT_VIDEO_CLICK = "Video_Click"
        val EVENT_CLICK = "_Click"
        val CATEGORY_VIEW = "_Category_View"
        val EVENT_PAKISTAN_ANCHOR_CAROUSEL = "Pakistan_Anchor_Carousel"
        val EVENT_PROGRAM_CAROUSEL = "Program_Carousel"
        val EVENT_PLAY = "_Play"

        val EVENT_LANDING_PLAY = "Play"
        fun EVENT_CATEGORY_PLAY(category: String): String? {
            var cat = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase()
            return cat.replace(" ".toRegex(), "_") + EVENT_PLAY
        }

        val EVENT_TELESCHOOL_PLAY = "Teleschool_Play"
        val EVENT_LIVE_PLUS_VOD_PLAY = "Live_and_Vod_Play"
        val EVENT_LANDING_PAGE_VIEW = "Landing_Page_View"
        val EVENT_LANDING_PAGE_LIKE = "Landing_Page_Like"
        val EVENT_LANDING_PAGE_INFO_EXPAND = "Landing_Page_Info_Expand"
        val EVENT_LANDING_PAGE_SHARE = "Landing_Page_Share"
        val EVENT_LANDING_PAGE_BACK = "Landing_Page_Back"
        val EVENT_LANDING_PAGE_FULLSCREEN = "Landing_Page_Fullscreen"
        val EVENT_LANDING_PAGE_RELATED_VIDEO_CLICK = "Landing_Page_Related_Video_Click"
        val EVENT_LANDING_PAGE_PAUSE = "Landing_Page_Pause"
        val EVENT_LANDING_PAGE_PREVIOUS_VIDEO = "Landing_Page_Previous_Video"
        val EVENT_lANDING_PAGE_NEXT_VIDEO = "Landing_Page_Next_Video"
        val EVENT_LANDING_PAGE_VIDEO_TOPIC_CLICK = "Landing_Page_Video_Topic_Click"
        val EVENT_LANDING_PAGE_CHANNEL_CLICK = "Landing_Page_Channel_Click"
        val EVENT_LANDING_VIDEO_QUALITY_CHANGED = "Video_Quality_Changed"

        val EVENT_LIVE_TAB_VIEW = "Live_Tab_View"
        val EVENT_LIVE_TAB_VIDEO_PLAY = "Live_Tab_Video_Play"
        val EVENT_LIVE_TAB = "Live_Tab_"
        val EVENT_LIVE_PAUSE = "Live_Pause"
        val EVENT_LIVE_tAB_FULLSCREEN = "Live_Tab_Fullscreen"
        val EVENT_LIVE_TAB_CHANNEL_SELECT = "Live_Tab_Channel_Select"
        val EVENT_LIVE_QUALITY_CHANGED = "Live_Quality_Changed"
        val EVENT_CHANNEL_LOADED = "Channel_Loaded"
        val EVENT_SETTING_TAB_VIEW = "Settings_Tab_View"
        val EVENT_VIDEO_QUALITY_SETTING_CHANGED = "Video_Quality_Setting_Changed"
        val EVENT_VIEW_TERMS_AND_CONDITIONS = "View_Term_And_Conditions"
        val EVENT_VIEW_PRIVACY = "View_Privacy"
        val EVENT_FOLLOW_TAB_VIEW = "Follow_Tab_View"

        val EVENT_ANCHOR_FOLLOW_VIEW = "Anchors_Follow_View"
        val EVENT_TOPIC_FOLLOW_VIEW = "Topics_Follow_View"
        val EVENT_PROGRAMS_FOLLOW_VIEW = "Programs_Follow_View"
        val EVENT_ANCHOR_FOLLOW_SELECT = "Anchor_Follow_Select"
        val EVENT_TOPIC_FOLLOW_SELECT = "Topic_Follow_Select"
        val EVENT_PROGRAMS_FOLLOW_SELECT = "Programs_Follow_Select"
        val EVENT_ACNHORS_UN_FOLLOW_SELECT = "Anchors_Unfollow_Select"
        val EVENT_PROGRAMS_UN_FOLLOW_SELECT = "Programs_Unfollow_Select"
        val EVENT_TOPICS_UN_FOLLOW_SELECT = "Topic_Unfollow_Select"
        val EVENT_MORE_VIDEOS_FOLLOW = "More_Videos_Follow"
        val EVENT_MORE_VIDEOS_UN_FOLLOW = "More_Videos_Unfollow"

        val EVENT_SEARCH_SCREEN_VIEW = "Search_Screen_View"
        val EVENT_SEARCH_SUGGESTED_ANCHOR_SCROLL = "Search_Suggested_Anchors_Scroll"
        val EVENT_SEARCH_SUGGESTED_ANCHOR = "Search_Suggested_Anchors"
        val EVENT_SEARCH_SUGGESTED_TOPICS_CLICK = "Search_Suggested_Topics_Click"
        val EVENT_TEXTUAL_SEARCH_RESULTS = "Textual_Search_Results"
        val EVENT_SEARCH_RESULT_ANCHOR = "Search_Results_Anchors"
        val EVENT_SEARCH_RESULT_VIDEO_CLICK = "Search_Results_Video_Click"
        val EVENT_LOCATION_AGREE = "Location_Agree"
        val EVENT_LOCATION_DISAGREE = "Location_Disagree"
        val EVENT_MYFEED_FOLLOW_MORE = "Myfeed_Follow_More"

        val PAYWALL_VIEW = "Paywall_View"
        val PAYWALL_HE_ENABLED = "Paywall_He"
        val PAYWALL_NO_HE_ENABLED = "Paywall_No_He"
        val PAYWALL_OTP_SENT = "Paywall_Otp_Sent"
        val PAYWALL_OTP_VERIFIED = "Paywall_Otp_Verified"
        val PAYWALL_PAY_CLICK = "Paywall_Pay_Click"
        val PAYWALL_LOGIN_CLICK = "Paywall_Login_Click"
        val PAYWALL_SUBSCRIBED = "Paywall_Subscribed"
        val TRIAL_ACTIVATED = "Paywall_Trial_Activated"
        val PAYWALL_ALREADY_SUBSCRIBED = "Paywall_Already_Subscribed"
        val PREMIUM_UNSUB_CLICK = "Paywall_Unsubscribe_Click"
        val PREMIUM_UNSUB = "Paywall_Unsubscribed"
        val PREMIUM_SUB_AFTER_UNSUB = "Paywall_Subscribed_After_Unsub"

        val COMEDY_PAYWALL_VIEW = "Comedy_Paywall_View"
        val COMEDY_PAYWALL_HE_ENABLED = "Comedy_Paywall_He"
        val COMEDY_PAYWALL_NO_HE_ENABLED = "Comedy_Paywall_No_He"
        val COMEDY_PAYWALL_OTP_SENT = "Comedy_Paywall_Otp_Sent"
        val COMEDY_PAYWALL_OTP_VERIFIED = "Comedy_Paywall_Otp_Verified"
        val COMEDY_PAYWALL_PAY_CLICK = "Comedy_Paywall_Pay_Click"
        val COMEDY_PAYWALL_LOGIN_CLICK = "Comedy_Paywall_Login_Click"
        val COMEDY_PAYWALL_SUBSCRIBED = "Comedy_Paywall_Subscribed"
        val COMEDY_PAYWALL_BILLING_FAILED = "Comedy_Paywall_Billing_Failed"
        val COMEDY_PAYWALL_ALREADY_SUBSCRIBED = "Comedy_Paywall_Already_Subscribed"
        val COMEDY_PREMIUM_UNSUB_CLICK = "Comedy_Paywall_Unsubscribe_Click"
        val COMEDY_PREMIUM_UNSUB = "Comedy_Paywall_Unsubscribed"
        val COMEDY_PREMIUM_SUB_AFTER_UNSUB = "Comedy_Paywall_Subscribed_After_Unsub"
    }
}