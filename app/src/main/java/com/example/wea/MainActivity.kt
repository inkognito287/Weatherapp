package com.example.wea

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() ,GestureDetector.OnGestureListener,View.OnClickListener{
    var locationManager: LocationManager? = null
 val API: String = "19980e00b25d8dd054c9cbf6233c0fb2" // Use API key
    var city: String=storage.citty
    var k:Int = 0
    var kek:String="erer"
    lateinit var  myArray: kotlin.Array<String>
    lateinit var latitude : String
    lateinit var longitude: String
    lateinit var cities:EditText
    lateinit var button: Button

     var titles= arrayOf("","","","","","")
    var details= arrayOf("","","","","","")
    var images= intArrayOf(1,2,3,4,5,6)
    var heplmassiv= arrayOf("er","","","","","")


    val  b= mapOf("01d" to R.drawable.ic_01d,
            "02d" to R.drawable.ic_02d,
            "03d" to R.drawable.ic_03d,
            "04d" to R.drawable.ic_04d,
            "09d" to R.drawable.ic_09d,
            "10d" to R.drawable.ic_10d,
            "50d" to R.drawable.ic_50d,
            "11d" to R.drawable.ic_11d,
            "01n" to R.drawable.ic_01n,
            "02n" to R.drawable.ic_02n,
            "03n" to R.drawable.ic_03d,
            "04n" to R.drawable.ic_04d,
            "09n" to R.drawable.ic_09d,
            "10n" to R.drawable.ic_10n,
            "50n" to R.drawable.ic_50d,
            "11n" to R.drawable. ic_11d,
            "13n" to R.drawable. ic_13d,
            "13d" to R.drawable. ic_13d)

    lateinit var  gestureDetector: GestureDetector
    var x2:Float =0.0f
    var x1:Float =0.0f
    var y2:Float =0.0f
    var y1:Float =0.0f
    var temperatura:String="metric"

    var multiply:Boolean=storage.multiply
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
if(multiply) {





    city = cities.text.toString()
    storage.citty = city

     storage.multiply = false





    storage.citty = city

    refresh()



        return false}
        else{
        finish()
return true}
    }
private  var layoutManager:RecyclerView.LayoutManager?=null
    private var adapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>?=null
    var list= listOf("moscow","babruisk","novopolotsk","")
    companion object{
        const val MIN_DISTANCE=150
    }
var z=0





    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        val secactiv=Intent (this,SecondActivity::class.java)
        startActivity(secactiv)


        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        relativeLayout2.visibility=View.GONE
        Daytoday.visibility=View.INVISIBLE
        findViewById<ImageView>(R.id.imageView).visibility=View.INVISIBLE
        findViewById<Button>(R.id.button).visibility=View.INVISIBLE
        Handler().postDelayed({relativeLayout2.visibility=View.VISIBLE
            Daytoday.visibility=View.VISIBLE
            findViewById<Button>(R.id.button).visibility=View.VISIBLE
            imageView.visibility=View.VISIBLE},1000)



        cities=findViewById(R.id.cities)

        button=findViewById(R.id.button)

        button.setOnClickListener(this)

gestureDetector=GestureDetector(this,this   )



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        layoutManager=LinearLayoutManager(this)
        weatherTask().execute()
        switch1.setOnClickListener(View.OnClickListener {



            relativeLayout2.visibility=View.GONE
            Daytoday.visibility=View.INVISIBLE
            Loader.visibility=View.INVISIBLE
            findViewById<ImageView>(R.id.imageView).visibility=View.INVISIBLE
            findViewById<Button>(R.id.button).visibility=View.INVISIBLE
            Handler().postDelayed({relativeLayout2.visibility=View.VISIBLE
                Daytoday.visibility=View.VISIBLE
                findViewById<Button>(R.id.button).visibility=View.VISIBLE
                imageView.visibility=View.VISIBLE},1000)
           refresh()

        })




    }
    override fun onClick(view: View?) {

            city = cities.text.toString()
            storage.citty = city

            weatherTask().execute()

        //TODO("Not yet implemented")
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        when
            (event?.action){
           0->
            {
                x1=event.x
                y1=event.y
            }
            1->
            {

                x2=event.x
                y2=event.y

                val valueX:Float=x2-x1
                val valueY:Float=y2-y1

                if (abs(valueY)> MIN_DISTANCE){
                    if(y2>y1){

                        refresh()}
                }
            }

        }


        return super.onTouchEvent(event)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                     this.latitude = location?.latitude.toString()
                     this.longitude = location?.longitude.toString()
                    storage.latitude=this.latitude
                    storage.longitude=this.longitude

                }

    }
    @SuppressLint("ClickableViewAccessibility")
    private fun kegs(){
        ere.setOnTouchListener { v: View, m: MotionEvent ->


            refresh()
            true
        }
    }
    fun swipe(e: MotionEvent){

        refresh()
    }

    private fun refresh(){

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                try {
                    recreate()
                } catch (e: Exception) {}
                //handler.postDelayed(this, 1000)
            }
        }

       findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE

        handler.postDelayed(runnable, 100)

    }


    inner class weatherTask() : AsyncTask<String, Void, String>() {



        override fun onPreExecute() {
            super.onPreExecute()

            findViewById<ProgressBar>(R.id.Loader).visibility = View.GONE

            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE

        }

        override fun onProgressUpdate(vararg values: Void?) {


            super.onProgressUpdate(*values)
        }
        override fun doInBackground(vararg params: String?): String? {

            Daytoday.setOnClickListener(){
                try {



                    storage.multiply = true
                    storage.citty=city
                    refresh()

                }catch (e:java.lang.Exception){}


            }
            city=cities.text.toString()

            var response:String?

            try{


                response=""

              if(!multiply){

                if (switch1.isChecked==false)
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=${storage.citty}&units=$temperatura&appid=$API&lang=ru").readText(
                    Charsets.UTF_8
                )
                    //if(k==1)
                if (switch1.isChecked==true){
                    metodlonglat()
                    response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=$temperatura&appid=19980e00b25d8dd054c9cbf6233c0fb2&lang=ru").readText(
                    Charsets.UTF_8)
                }
                }
                if(multiply){
                    if (switch1.isChecked==false) {
                                metodlonglat()
                                        //refresh()
                        response = URL("https://api.openweathermap.org/data/2.5/onecall?lat=${storage.latitude2}&lon=${storage.longitude2}&exclude=hourly,current,minutely,alerts&units=$temperatura&appid=19980e00b25d8dd054c9cbf6233c0fb2").readText(
                                Charsets.UTF_8)

                    }
                    if (switch1.isChecked==true) {
                        getLastLocation()
                                //refresh()
                        response = URL("https://api.openweathermap.org/data/2.5/onecall?lat=${storage.latitude}&lon=${storage.longitude}&exclude=hourly,current,minutely,alerts&units=$temperatura&appid=19980e00b25d8dd054c9cbf6233c0fb2").readText(
                                Charsets.UTF_8)
                    }

                }
            }catch (e: Exception){

                response = null




            }
            return response
        }


fun proweather():String{
    var response2=""
    try {
         response2 =
            URL(" https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&appid=19980e00b25d8dd054c9cbf6233c0fb2").readText(
                Charsets.UTF_8
            )
    }catch (e:Exception){

    }
    return response2
}


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

                try {val jsonObj = JSONObject(result)

                    if(!multiply) {

                        val main = jsonObj.getJSONObject("main")
                        val sys = jsonObj.getJSONObject("sys")
                        val wind = jsonObj.getJSONObject("wind")
                        val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                        val updatedAt: Long = jsonObj.getLong("dt")
                        val updatedAtText = "Обновлено: " + SimpleDateFormat(
                            " hh:mm a",
                            Locale.ENGLISH
                        ).format(Date(updatedAt * 1000))
                        var temp = main.getString("temp") + "°c"
                        var tempMin = "Минимум: " + main.getString("temp_min") + "°c"
                        var tempMax = "Максимум: " + main.getString("temp_max") + "°c"
                        val pressure = main.getString("pressure")
                        val humidity = main.getString("humidity")

                        val sunrise: Long = sys.getLong("sunrise")
                        val sunset: Long = sys.getLong("sunset")
                        val windSpeed = wind.getString("speed")
                        val weatherDescription = weather.getString("description")
                        val ICON=weather.getString("icon")

                        val address = jsonObj.getString("name") + ", " + sys.getString("country")



                        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.temp).text = temp
                        findViewById<TextView>(R.id.address).text = address;
                        findViewById<TextView>(R.id.update).text = updatedAtText
                        findViewById<TextView>(R.id.temp_max).text = tempMax
                        findViewById<TextView>(R.id.temp_min).text = tempMin
                        findViewById<TextView>(R.id.windspeed).text = windSpeed+ "м/c"
                        findViewById<TextView>(R.id.description).text =
                            "Погода: " + weatherDescription
                        findViewById<TextView>(R.id.pressure).text = "Давление: " + pressure+" гПА"



                            val imageView: ImageView = findViewById(R.id.imageView)
                        var nameimage=b[ICON]
                            imageView.setImageResource(nameimage?: error(""))




                    }
                    if(multiply){




                            TempMAX(result)
                            TempMIN(result)
                            IDIMAGE(result)


                            recyclerView.layoutManager = layoutManager

                            adapter = RecyclerAdapter(details,titles,images)

                            recyclerView.adapter = adapter

                        }
                           } catch (e: Exception) {
                                  }
                 val mmdms=titles




        }

    }




    fun TempMAX(result: String?) : kotlin.Array<String> {
        val jsonObj = JSONObject(result)
        var main:JSONArray
        main =jsonObj.getJSONArray("daily")

        var valuesofarray:JSONArray
        // valuesofarray=jsonObj.getJSONArray("{}")
        for ( i in 0..5) {
            var item=main.getJSONObject(i)
            var temp2=item.getJSONObject("temp")
            titles[i]=temp2.getString("max")

        }

        return  titles
    }
    fun TempMIN(result: String?) : kotlin.Array<String> {
        val jsonObj = JSONObject(result)
        var main:JSONArray
        main =jsonObj.getJSONArray("daily")

        var valuesofarray:JSONArray

        for ( i in 0..5) {
            var item=main.getJSONObject(i)
            var temp2=item.getJSONObject("temp")
            details[i]=temp2.getString("min")

        }

        return  details
    }
    fun IDIMAGE(result: String?) : IntArray {

        val jsonObj = JSONObject(result)
        var main:JSONArray
        main =jsonObj.getJSONArray("daily")

        var valuesofarray:JSONArray

        for ( i in 0..5) {
            var item=main.getJSONObject(i)
            var temp2=item.getJSONArray("weather").getJSONObject(0)
             heplmassiv[i]=temp2.getString("icon")

           images[i]= b[heplmassiv[i]] ?: error("")

        }

        return  images



    }
    fun metodlonglat(){
       var response2 = URL("https://api.openweathermap.org/data/2.5/forecast?q=${storage.citty}&units=$temperatura&appid=$API").readText(
                Charsets.UTF_8)
        val jsonObj = JSONObject(response2)
        var main=jsonObj.getJSONObject("city")
        var coord=main.getJSONObject("coord")
        var lat= coord.getString("lat")
        var long=coord.getString("lon")
        storage.longitude2=long
        storage.latitude2=lat

    }

    override fun onShowPress(p0: MotionEvent?) {
        //TODO("Not yet implemented")
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
       // TODO("Not yet implemented")
        return false

    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
        //TODO("Not yet implemented")
    }


}

operator fun String?.invoke(s: String) {

}


