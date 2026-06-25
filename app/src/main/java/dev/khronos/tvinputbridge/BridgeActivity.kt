package dev.khronos.tvinputbridge

import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

class BridgeActivity : AppCompatActivity() {

    companion object {
        var isTvMode = false
        private var instance: java.lang.ref.WeakReference<BridgeActivity>? = null

        fun finishInstance() {
            instance?.get()?.finish()
        }
    }

    private var pausedByScreenOff = false
    private var finishingFromSleep = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)
        instance = java.lang.ref.WeakReference(this)
        isTvMode = true
    }

    override fun onResume() {
        super.onResume()
        if (pausedByScreenOff && isTvMode) {
            // スリープ復帰: ホーム画面を見せるだけ。isTvModeは維持してお気に入り/ホームに委ねる
            pausedByScreenOff = false
            finishingFromSleep = true
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        val pm = getSystemService(PowerManager::class.java)
        pausedByScreenOff = !pm.isInteractive
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!finishingFromSleep) {
            isTvMode = false
        }
        instance = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Thread {
                if (BridgeApi.postInputHdmi()) {
                    runOnUiThread {
                        isTvMode = false
                        finish()
                    }
                } else {
                    Log.w("BridgeActivity", "input_hdmi request failed")
                }
            }.start()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
