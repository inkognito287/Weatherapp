package com.example.wea


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.appcompat.app.AppCompatActivity


class kek: AppCompatActivity() {
    companion object {}

    init {


    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_layout)



    }
}

    class RecyclerAdapter(details1:Array<String>, titles1:Array<String>, images1: IntArray) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        val details: Array<String>
        val titles: Array<String>
        val images: IntArray
        init{
            images=images1
            titles=titles1
            details = details1
        }
        val da = kek()

        val name2 = "erere"







        //kek().na
        // finish()
        private var nedelia = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        private var nedeliarus = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница","Суббота")

        @RequiresApi(Build.VERSION_CODES.O)
        val current = LocalDateTime.now()


        @RequiresApi(Build.VERSION_CODES.O)
        val formatter = DateTimeFormatter.ofPattern("EEEE")
        var DENnedeli = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        var text3 = arrayOf("", "", "", "", "", "")



        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
            return ViewHolder(v)
            //TODO("Not yet implemented")

        }

        override fun getItemCount(): Int {
            return titles.size
            //TODO("Not yet implemented")

        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {





            val ogo: kek = kek()

            val name: String


            holder.itemTitle.text = titles[position]

            holder.itemDetail.text = details[position]



            holder.itemImage.setImageResource(images[position])//TODO("Not yet implemented")
            try {


                for (i in 0..6)
                    if (nedelia[i] == current.format(formatter)) {
                        DENnedeli[0] = nedeliarus[i + 1]
                        DENnedeli[1] = nedeliarus[i + 2]
                        DENnedeli[2] = nedeliarus[i + 3]
                        DENnedeli[3] = nedeliarus[i + 4]
                        DENnedeli[4] = nedeliarus[i + 5]
                        DENnedeli[5] = nedeliarus[i + 6]
                        holder.itemtext3.text = DENnedeli[position]
                        break
                    }


            } catch (v: Exception) {

            }

        }


        @RequiresApi(Build.VERSION_CODES.O)
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var itemImage: ImageView
            var itemTitle: TextView
            var itemDetail: TextView
            var itemtext3: TextView


            init {
                itemImage = itemView.findViewById(R.id.item_image)
                itemTitle = itemView.findViewById(R.id.item_title)
                itemDetail = itemView.findViewById(R.id.item_detail)
                itemtext3 = itemView.findViewById(R.id.textView3)



            }
        }
    }



