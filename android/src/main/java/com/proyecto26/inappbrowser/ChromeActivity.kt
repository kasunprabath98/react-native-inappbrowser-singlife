package com.proyecto26.inappbrowser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.facebook.react.bridge.Arguments
import org.greenrobot.eventbus.EventBus

public class ChromeActivity: Activity() {
    var opened = false;
    var resultType = "Dismissed";
    companion object {
        const val KEY_BROWSER_INTENT = "browserIntent"
        const val  TAG = "IN_APP_BROWSER"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        // open the chrome tab manager Activity
        try {
            val url  = intent.extras?.getString("url")
            Log.d(TAG, "onCreate: URL"+ url);
            resultType = "Dismissed";
            val customTabIntent = CustomTabsIntent.Builder()
            customTabIntent.build().intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            customTabIntent.build().launchUrl(this , Uri.parse(url))
        }
        catch (err: Exception) {

        }
    }

    override fun onDestroy() {
        val result = Arguments.createMap()
        result.putString("type", resultType);
        result.putString("message", "cancel");
        Log.d(TAG, "onDestroy: " );
        if(resultType == "cancel") {
            EventBus.getDefault()
                .post(ChromeTabsDismissedEvent("chrome tabs activity closed", "cancel", false))
        }
        super.onDestroy();
    }

    override fun onResume() {
        super.onResume();

        Log.d(TAG, "onResume: $opened")

        if(!opened) {
            opened = true
        }else {
            // user has press the back button
            resultType = "cancel";
            finish();
        }
    }

    private fun registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}
