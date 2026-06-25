package dev.khronos.tvinputbridge

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

object BridgeApi {

    private const val BASE_URL = "http://192.168.1.130:8765"
    private const val CONNECT_TIMEOUT_MS = 2000
    private const val READ_TIMEOUT_MS = 2000

    fun postInputHdmi(): Boolean = post("/input_hdmi")
    fun postTvOff(): Boolean = post("/tv_off")

    private fun post(path: String): Boolean {
        return runCatching {
            val conn = URL("$BASE_URL$path").openConnection() as HttpURLConnection
            try {
                conn.requestMethod = "POST"
                conn.connectTimeout = CONNECT_TIMEOUT_MS
                conn.readTimeout = READ_TIMEOUT_MS
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.outputStream.use { it.write("{}".toByteArray(Charsets.UTF_8)) }
                val code = conn.responseCode
                Log.d(TAG, "POST $path -> $code")
                code in 200..299
            } finally {
                conn.disconnect()
            }
        }.getOrElse { e ->
            Log.e(TAG, "POST $path failed: ${e.message}")
            false
        }
    }

    private const val TAG = "BridgeApi"
}
