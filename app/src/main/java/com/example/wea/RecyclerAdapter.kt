package com.example.wea

import android.app.Application
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
    companion object{}
    private var nedelia= arrayOf("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("EEEE")
 var DENnedeli= arrayOf("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday")

  public var  titles= arrayOf("Item one details","Item two details","Item three details","","","")
  //val titles: kotlin.Array<String> =MainActivity().titles
var text3= arrayOf("","","","","","")
    private var details= arrayOf("Item one details","Item two details","Item three details","","","")
    private var images= intArrayOf(R.drawable.ic_10d,R.drawable.ic_10d,R.drawable.obl,R.drawable.obl,R.drawable.rain,R.drawable.rain)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
        return ViewHolder(v)
        //TODO("Not yet implemented")

    }

    override fun getItemCount(): Int {
        return details.size
        //TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
//        var intent=MainActivity().intent
//        val stroka=intent.getStringExtra("Name")
        //Toast.makeText(MainActivity(),stroka,Toast.LENGTH_SHORT).show()



    //startActivity(intent)
    titles = storage.token1
    details = storage.token2
    images = storage.token3

    holder.itemTitle.text = titles[position]
    holder.itemDetail.text = details[position]



            holder.itemImage.setImageResource(images[position])//TODO("Not yet implemented")
        try {

           var i=0
            for (i in 0..5)
                if(nedelia[i]==current.format(formatter)) {
                    DENnedeli[0] = nedelia[i + 1]
                    DENnedeli[1] = nedelia[i + 2]
                    DENnedeli[2] = nedelia[i + 3]
                    DENnedeli[3] = nedelia[i + 4]
                    DENnedeli[4] = nedelia[i + 5]
                    DENnedeli[5] = nedelia[i + 6]
                    holder.itemtext3.text = DENnedeli[position]
                    break
                }



        }catch (v:Exception){
            //Toast.makeText(MainActivity(),v.toString(),Toast.LENGTH_SHORT)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView
        var itemtext3: TextView


        init{
            itemImage=itemView.findViewById(R.id.item_image)
            itemTitle=itemView.findViewById(R.id.item_title)
            itemDetail=itemView.findViewById(R.id.item_detail)
            itemtext3= itemView.findViewById(R.id.textView3)
            //itemView.findViewById<TextView>(R.id.textView3).text=current.format(formatter)



        }
    }}


