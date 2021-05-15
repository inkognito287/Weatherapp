package com.example.wea

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycleritem.*
import java.text.SimpleDateFormat
import java.util.*

var position=0
var result=""
class WeeklyForecast : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sec)
        getSupportActionBar()!!.hide()
        val intent = getIntent()
        val nightTemperature = intent.getStringArrayExtra("nightTemperature")
        val images = intent.getIntArrayExtra("images")
        result=intent.getStringExtra("result")
        val dayTemperature = intent.getStringArrayExtra("dayTemperature")
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView1)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomRecyclerAdapter(nightTemperature, dayTemperature, images)


    }

   inner class CustomRecyclerAdapter(
        private val names: Array<String>,
        private val names2: Array<String>,
        private val names3: IntArray?
    ) :
        RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

        val ogo = Date()
        val calendarik = Calendar.getInstance()
        val format1 = SimpleDateFormat("EEEE")
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var den: TextView? = null
            var noch: TextView? = null
            var Imageweather: ImageView? = null
            var dayofnedelia: TextView? = null
            var okey: View?=null
            init {
                den = itemView.findViewById(R.id.textViewDen)
                noch = itemView.findViewById(R.id.textViewNoch)
                Imageweather = itemView.findViewById(R.id.imageView2)
                dayofnedelia = itemView.findViewById(R.id.dayofnedelia)
                okey=itemView.findViewById(R.id.view3)
                itemView.setOnClickListener {
                    _: View ->
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycleritem, parent, false)
            return MyViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return names.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position2: Int) {
            try {

                holder.den?.text = "%.1f".format(names2.get(position2).toFloat()) + "°c"
                holder.noch?.text = "%.1f".format(names.get(position2).toFloat()) + "°c"
                holder.Imageweather?.setImageResource(names3?.get(position2)!!)

                calendarik.setTime(ogo)
                calendarik.add(Calendar.DAY_OF_WEEK, position2 + 1)
                holder.dayofnedelia?.text = format1.format(calendarik.getTime()).toString()
            } catch (e: Exception) { }
            holder.okey!!.setOnClickListener(View.OnClickListener {
                print(position2)
                position=position2
                sendMessage(dayofnedelia,position2)
                // action on click
            })

            // TODO("Not yet implemented")
        }
    }
    public fun sendMessage(view:View,position2: Int) {
        val start: Intent = Intent(this,Moreinformation::class.java)
        start.putExtra("position", position2)
        start.putExtra("result",result)
        startActivity(start)
    }
}
