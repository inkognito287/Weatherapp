package com.example.wea


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycleritem.*
import kotlinx.android.synthetic.main.recycleritem.view.*
import java.text.SimpleDateFormat
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
    class CustomRecyclerAdapter(
            private val names: Array<String>,
            private val names2: Array<String>,
            private val names3: IntArray?
    ) :
            RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
        fun kykareky(position2:Int){
            storage.mesto=position2.toString()

        }
        val ogo = Date()
        val calendarik = Calendar.getInstance()
        val format1 = SimpleDateFormat("EEEE")

       inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var den: TextView? = null
            var noch: TextView? = null
            var Imageweather: ImageView? = null
            var dayofnedelia: TextView? = null

            init{

                den = itemView.findViewById(R.id.textViewDen)
                noch = itemView.findViewById(R.id.textViewNoch)
                Imageweather = itemView.findViewById(R.id.imageView2)
                dayofnedelia = itemView.findViewById(R.id.dayofnedelia)
                itemView.setOnClickListener{v:View->
                    val position2:Int=adapterPosition



            }


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

        override fun onBindViewHolder(holder: MyViewHolder, position2: Int) {


            try {

                holder.den?.text = "%.1f".format(names2.get(position2).toFloat()) + "°c"
                holder.noch?.text = "%.1f".format(names.get(position2).toFloat()) + "°c"
                holder.Imageweather?.setImageResource(names3?.get(position2)!!)

                calendarik.setTime(ogo)
                calendarik.add(Calendar.DAY_OF_WEEK, position2 + 1)
                holder.dayofnedelia?.text = format1.format(calendarik.getTime()).toString()
            } catch (e: Exception) {

            }

            // TODO("Not yet implemented")
        }


    }


}
