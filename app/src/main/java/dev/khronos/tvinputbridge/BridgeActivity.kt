package dev.khronos.tvinputbridge

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class BridgeActivity : AppCompatActivity() {

    companion object {
        var isTvMode = false
    }

    private val inputHdmiUrl = "http://192.168.1.130:8765/input_hdmi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)
        isTvMode = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isTvMode = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isTvMode = false
            postInputHdmi()
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun postInputHdmi() {
        val thread = Thread {
            runCatching {
                val conn = URL(inputHdmiUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.outputStream.write("{}".toByteArray())
                conn.responseCode
                conn.disconnect()
            }
        }
        thread.start()
        thread.join(2000)
    }
}
