package com.example.wea

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import jsonresponce.Response2
import kotlinx.android.synthetic.main.activity_information.*

class Moreinformation : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        getSupportActionBar()!!.hide()
        val intent = getIntent()
        val position = intent.getIntExtra("position", 0)
        val result = intent.getStringExtra("result")

        val gson = Gson()
        val data2 = gson.fromJson(result, Response2::class.java)
        textView1.text = data2.daily!![position]?.weather!![0]!!.description!!.toString()
        textView2.text = "%.1f".format(data2.daily!![position]?.temp!!.max!!.toFloat()) + "°C"
        textView3.text = "%.1f".format(data2.daily!![position]?.temp!!.min!!.toFloat()) + "°C"
        textView4.text = "%.1f".format(data2.daily!![position]?.feelsLike!!.day!!.toFloat()) + "°C"
        textView5.text = "%.1f".format(data2.daily!![position]?.feelsLike!!.night!!.toFloat()) + "°C"
        textView6.text = "%.1f".format(data2.daily!![position]?.windSpeed!!.toFloat()) + "°C"

        println(result)
    }
}