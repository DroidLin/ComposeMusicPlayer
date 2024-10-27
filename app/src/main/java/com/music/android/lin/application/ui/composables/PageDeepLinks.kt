package com.music.android.lin.application.ui.composables

/**
 * @author: liuzhongao
 * @since: 2024/10/25 21:57
 */
object PageDeepLinks {

    private const val SCHEME = "coco"
    private const val DEEPLINK_DOMAIN = "app-deeplink.cn"

    const val PATH_GUIDE_WELCOME = "${SCHEME}://${DEEPLINK_DOMAIN}/welcome"
    const val PATH_GUIDE_PERMISSION_AND_ACQUIRE = "${SCHEME}://${DEEPLINK_DOMAIN}/permissions-acquire"

    const val PATH_APP_MAIN = "${SCHEME}://${DEEPLINK_DOMAIN}/app-main"
    const val PATH_PERSONAL_INFORMATION = "${SCHEME}://${DEEPLINK_DOMAIN}/personal-information"
    const val PATH_SINGLE_MUSIC = "${SCHEME}://${DEEPLINK_DOMAIN}/single-music"
    const val PATH_ALBUM = "${SCHEME}://${DEEPLINK_DOMAIN}/album"
}