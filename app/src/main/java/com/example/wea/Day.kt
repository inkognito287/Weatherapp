package com.example.wea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.day.*

class Day: AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day)
        val intent3 = getIntent()
        val mesto= intent3.getStringExtra("k")
        textView.text=storage.mesto.toString()
    }



}