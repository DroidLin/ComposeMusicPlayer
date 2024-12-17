package com.music.android.lin.application.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.music.android.lin.application.MainActivity
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.mainActivity

/**
 * @author liuzhongao
 * @since 2024/12/17 14:04
 */
class RedirectIntentActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let { takeIntentAndGo(this, it) }
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        takeIntentAndGo(this, intent)
        finish()
    }
}

private fun takeIntentAndGo(activity: Activity, intent: Intent) {
    val newIntent = Intent(activity, MainActivity::class.java)
    newIntent.data = intent.data
    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    activity.startActivity(newIntent)
}
