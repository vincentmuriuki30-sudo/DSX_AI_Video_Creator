package com.dsx.aivideo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var etPrompt: EditText
    private lateinit var btnGenerate: Button
    private lateinit var tvStatus: TextView
    private lateinit var videoView: VideoView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etPrompt = findViewById(R.id.etPrompt)
        btnGenerate = findViewById(R.id.btnGenerate)
        tvStatus = findViewById(R.id.tvStatus)
        videoView = findViewById(R.id.videoView)

        val prefs = getSharedPreferences("dsx_user_session", Context.MODE_PRIVATE)
        val user = prefs.getString("account_name", "Creator")
        tvStatus.text = "Account: $user | Status: Ready"

        btnGenerate.setOnClickListener {
            val prompt = etPrompt.text.toString().trim()
            if (prompt.isNotEmpty()) {
                generateVideo(prompt)
            } else {
                tvStatus.text = "Status: Please enter a prompt first!"
            }
        }
    }

    private fun generateVideo(prompt: String) {
        tvStatus.text = "Status: Rendering video on free cloud server..."
        btnGenerate.isEnabled = false

        val encodedPrompt = Uri.encode(prompt)
        val videoUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=720&height=1280&model=flux"

        val request = Request.Builder().url(videoUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tvStatus.text = "Status: Connection failed. Check internet."
                    btnGenerate.isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes()
                    if (bytes != null) {
                        val projectFile = File(filesDir, "project_${System.currentTimeMillis()}.mp4")
                        val fos = FileOutputStream(projectFile)
                        fos.write(bytes)
                        fos.close()

                        runOnUiThread {
                            tvStatus.text = "Status: Saved to My Projects!"
                            btnGenerate.isEnabled = true
                            videoView.visibility = View.VISIBLE
                            videoView.setVideoPath(projectFile.absolutePath)
                            videoView.start()
                        }
                    }
                } else {
                    runOnUiThread {
                        tvStatus.text = "Status: Server error (${response.code})"
                        btnGenerate.isEnabled = true
                    }
                }
            }
        })
    }
}
