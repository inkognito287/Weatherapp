package com.example.wea


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.Period
import java.util.*


class razbivkapodniam : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sec)
        val intent = getIntent()
        val temperaturanochy = intent.getStringArrayExtra("temperaturanochy")

        val images = intent.getIntArrayExtra("images")
        val temperaturadnem = intent.getStringArrayExtra("temperaturadnem")
        var k = 4
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView1)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomRecyclerAdapter(temperaturanochy, temperaturadnem, images)
    }

    private fun fillList(): List<String> {
        val data = mutableListOf<String>()
        (0..30).forEach { i -> data.add("$i element") }
        return data
    }

    class CustomRecyclerAdapter(
            private val names: Array<String>,
            private val names2: Array<String>,
            private val names3: IntArray?
    ) :
            RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
        val ogo = Date()
        val calendarik = Calendar.getInstance()
        val format1 = SimpleDateFormat("EEEE")

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var den: TextView? = null
            var noch: TextView? = null
            var Imageweather: ImageView? = null
            var dayofnedelia: TextView? = null

            init {
                den = itemView.findViewById(R.id.textViewDen)
                noch = itemView.findViewById(R.id.textViewNoch)
                Imageweather = itemView.findViewById(R.id.imageView2)
                dayofnedelia = itemView.findViewById(R.id.dayofnedelia)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val itemView =
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.recycleritem, parent, false)
            return MyViewHolder(itemView)
            // TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            return names.size
            //TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//"%.1f".format(names.get(position).toFloat()) + "°c"
            try {
                holder.den?.text = "%.1f".format(names2.get(position).toFloat()) + "°c"
                holder.noch?.text = "%.1f".format(names.get(position).toFloat()) + "°c"
                holder.Imageweather?.setImageResource(names3?.get(position)!!)
                calendarik.setTime(ogo)
                calendarik.add(Calendar.DAY_OF_WEEK, position + 1)
                holder.dayofnedelia?.text = format1.format(calendarik.getTime()).toString()
            } catch (e: Exception) {

            }
            // TODO("Not yet implemented")
        }
    }
}
