package dev.khronos.tvinputbridge

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class BridgeAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("BridgeAS", "Service connected")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d("BridgeAS", "keyCode=${event.keyCode} action=${event.action} isTvMode=${BridgeActivity.isTvMode}")
        if (event.keyCode == KeyEvent.KEYCODE_HOME && event.action == KeyEvent.ACTION_DOWN && BridgeActivity.isTvMode) {
            Thread {
                if (BridgeApi.postInputHdmi()) {
                    BridgeActivity.isTvMode = false
                }
            }.start()
            return false  // ホーム画面遷移はシステムに任せる
        }
        if (event.keyCode == 313 /* KEYCODE_MACRO_2: favorites */ && event.action == KeyEvent.ACTION_DOWN && BridgeActivity.isTvMode) {
            Thread {
                if (BridgeApi.postTvOff()) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        BridgeActivity.isTvMode = false
                        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
                        mainHandler.postDelayed({
                            BridgeActivity.finishInstance()
                        }, 1000)
                    }
                } else {
                    Log.w("BridgeAS", "tv_off request failed")
                }
            }.start()
            return true
        }
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}
}
