package com.example.ponggame

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var pongView: PongView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pongView = findViewById(R.id.pong_view)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            pongView.updatePaddlePosition(it.x)
        }
        return super.onTouchEvent(event)
    }

    override fun onPause() {
        super.onPause()
        pongView.pause()
    }

    override fun onResume() {
        super.onResume()
        pongView.resume()
    }
}
