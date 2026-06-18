package dev.khronos.tvinputbridge

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import java.net.HttpURLConnection
import java.net.URL

class BridgeAccessibilityService : AccessibilityService() {

    private val tvOffUrl = "http://192.168.1.130:8765/tv_off"

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("BridgeAS", "Service connected")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d("BridgeAS", "keyCode=${event.keyCode} action=${event.action}")
        if (event.keyCode == 313 /* KEYCODE_MACRO_2: favorites */ && event.action == KeyEvent.ACTION_DOWN && BridgeActivity.isTvMode) {
            val thread = Thread {
                runCatching {
                    val conn = URL(tvOffUrl).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.write("{}".toByteArray())
                    conn.responseCode
                    conn.disconnect()
                }
            }
            thread.start()
            thread.join(3000)
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                BridgeActivity.finishInstance()
            }, 1000)
            return true
        }
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}
}
