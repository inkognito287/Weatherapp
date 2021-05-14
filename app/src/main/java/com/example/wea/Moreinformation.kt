package com.example.wea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import jsonresponce.Response2
import kotlinx.android.synthetic.main.activity_information.*

class Moreinformation : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        val intent = getIntent()
        val position = intent.getIntExtra("position",0)
        val result=intent.getStringExtra("result")

        val gson=Gson()
        val data2 = gson.fromJson(result, Response2::class.java)
        textView1.text= data2.daily!![position]?.weather!![0]!!.description.toString()
        textView2.text= data2.daily!![position]?.temp!!.max.toString()
        textView3.text= data2.daily!![position]?.temp!!.min.toString()
        textView4.text= data2.daily!![position]?.feelsLike!!.day .toString()
        textView5.text= data2.daily!![position]?.feelsLike!!.night.toString()
        textView6.text= data2.daily!![position]?.windSpeed.toString()

        println(result)
    }

}