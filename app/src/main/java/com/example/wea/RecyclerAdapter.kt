package com.example.wea
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
class weeklyforecast : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sec)
        val intent = getIntent()
        val nightTemperature = intent.getStringArrayExtra("nightTemperature")

        val images = intent.getIntArrayExtra("images")
        val dayTemperature = intent.getStringArrayExtra("dayTemperature")


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView1)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomRecyclerAdapter(nightTemperature, dayTemperature, images)
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
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var den: TextView? = null
            var noch: TextView? = null
            var Imageweather: ImageView? = null
            var dayofnedelia: TextView? = null
            init {
                den = itemView.findViewById(R.id.textViewDen)
                noch = itemView.findViewById(R.id.textViewNoch)
                Imageweather = itemView.findViewById(R.id.imageView2)
                dayofnedelia = itemView.findViewById(R.id.dayofnedelia)
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

            // TODO("Not yet implemented")
        }
    }
}
