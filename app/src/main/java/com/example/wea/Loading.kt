package com.example.wea

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

lateinit var topAnim: Animation
lateinit var bottom: Animation
lateinit var image: ImageView
lateinit var logo: TextView
class Loading : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_loading)
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        image = findViewById(R.id.imageView3)
        logo = findViewById(R.id.textView8)
        image.setAnimation(topAnim)
        logo.setAnimation(bottom)
        Handler().postDelayed(
            {
                run() {
                    val MainIntent = Intent(this@Loading, MainActivity::class.java)
                    startActivity(MainIntent)
                    this@Loading.finish()
                }
            },
            5000
        )
    }
}