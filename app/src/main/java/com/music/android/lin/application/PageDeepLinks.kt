package com.music.android.lin.application

/**
 * @author: liuzhongao
 * @since: 2024/10/25 21:57
 */
object PageDeepLinks {

    private const val SCHEME = "coco"
    private const val DEEPLINK_DOMAIN = "app-deeplink.cn"

    const val PATH_GUIDE_WELCOME = "$SCHEME://$DEEPLINK_DOMAIN/welcome"
    const val PATH_GUIDE_PERMISSION_AND_ACQUIRE = "$SCHEME://$DEEPLINK_DOMAIN/permissions-acquire"
    const val PATH_GUIDE_MEDIA_INFORMATION_SCANNER = "$SCHEME://$DEEPLINK_DOMAIN/media-information-scanner"

    const val PATH_APP_MAIN = "$SCHEME://$DEEPLINK_DOMAIN/app-main"
    const val PATH_PERSONAL_INFORMATION = "$SCHEME://$DEEPLINK_DOMAIN/personal-information"
    const val PATH_SINGLE_MUSIC = "$SCHEME://$DEEPLINK_DOMAIN/single-music"
    const val PATH_ALBUM = "$SCHEME://$DEEPLINK_DOMAIN/album"
    const val PATH_SETTINGS = "$SCHEME://$DEEPLINK_DOMAIN/settings"
    const val PATH_ABOUT = "$SCHEME://$DEEPLINK_DOMAIN/about"
    const val PATH_PLAYER = "$SCHEME://$DEEPLINK_DOMAIN/player"
    const val PATH_SEARCH = "$SCHEME://$DEEPLINK_DOMAIN/search"
    const val PATH_HOME_PAGE = "$SCHEME://$DEEPLINK_DOMAIN/home"
}