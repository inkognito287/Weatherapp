package com.example.wea

import android.annotation.SuppressLint
import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import android.widget.EditText


class MainActivity : AppCompatActivity() ,GestureDetector.OnGestureListener,View.OnClickListener{
    var locationManager: LocationManager? = null

    val API: String = "19980e00b25d8dd054c9cbf6233c0fb2" // Use API key
    var k:Int = 0
    lateinit var  myArray: kotlin.Array<String>
    lateinit var latitude : String
    lateinit var longitude: String
    lateinit var cities:EditText
    lateinit var button: Button
    //lateinit var text: TextView
   public  lateinit var city: String
    lateinit var  gestureDetector: GestureDetector
    var x2:Float =0.0f
    var x1:Float =0.0f
    var y2:Float =0.0f
    var y1:Float =0.0f
    var temperatura:String="metric"

    var list= listOf("moscow","babruisk","novopolotsk")
    companion object{
        const val MIN_DISTANCE=150
    }
var z=0



//private var cities:EditText?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cities=findViewById(R.id.cities)
        button=findViewById(R.id.button)
        //text=findViewById(R.id.textView)
        button.setOnClickListener(this)

gestureDetector=GestureDetector(this,this   )
        //weatherTask().execute()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        weatherTask().execute()




        //kegs()

    }
    override fun onClick(view: View?) {
         city= cities.text.toString()
        //text.text=city
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
                    if(y2>y1)
                        refresh()
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

                    //temp.text=latitude
                }

    }
    @SuppressLint("ClickableViewAccessibility")
    private fun kegs(){
        ere.setOnTouchListener { v: View, m: MotionEvent ->
            // Perform tasks here
            //temp.text="kaka"

            refresh()
            true
        }
    }
    fun swipe(e: MotionEvent){
        //temp.text="kaka"
        refresh()
    }
    private fun refresh(){
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                try {
                    recreate()
                } catch (e: Exception) {}
                //handler.postDelayed(this, 4000)
            }
        }

       findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        findViewById<ProgressBar>(R.id.Loader).visibility = View.VISIBLE
        handler.postDelayed(runnable, 500)
       // findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
       // findViewById<ConstraintLayout>(R.id.ere).visibility = View.VISIBLE
    }


    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            // temp.text=latitude
            findViewById<ProgressBar>(R.id.Loader).visibility = View.GONE
            findViewById<ImageView>(R.id.imageView).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE

        }

        override fun onProgressUpdate(vararg values: Void?) {


            super.onProgressUpdate(*values)
        }
        override fun doInBackground(vararg params: String?): String? {

            city=cities.text.toString()
            //city= cities.text
            //cities.get
            var response:String?
            try{
                //if(switch1.isChecked)


                //temp.text=latitude
                //myArray[0]="https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=19980e00b25d8dd054c9cbf6233c0fb2"
                //myArray[1]="https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API"
                response=""
                if (switch1.isChecked==false)
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$temperatura&appid=$API&lang=ru").readText(
                    Charsets.UTF_8
                )
                    //if(k==1)
                if (switch1.isChecked==true)
                    response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=$temperatura&appid=19980e00b25d8dd054c9cbf6233c0fb2&lang=ru").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }





        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

                try {
                    val jsonObj = JSONObject(result)
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                    val updatedAt: Long = jsonObj.getLong("dt")
                    val updatedAtText = "Updated at: " + SimpleDateFormat(
                        "dd/MM/yyyy hh:mm a",
                        Locale.ENGLISH
                    ).format(Date(updatedAt * 1000))
                    var temp = main.getString("temp") + "°C"
                    var tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                    var tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")

                    val sunrise: Long = sys.getLong("sunrise")
                    val sunset: Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")

                    val address = jsonObj.getString("name") + ", " + sys.getString("country")



                    //findViewById<ProgressBar>(R.id.Loader).visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.temp).text=temp
                    findViewById<TextView>(R.id.address).text = address;
                    findViewById<TextView>(R.id.update).text = updatedAtText
                    findViewById<TextView>(R.id.temp_max).text = tempMax
                    findViewById<TextView>(R.id.temp_min).text = tempMin
                    findViewById<TextView>(R.id.windspeed).text = windSpeed
                    findViewById<TextView>(R.id.description).text = "Описание погоды: "+weatherDescription
                    findViewById<TextView>(R.id.pressure).text = "Давление: "+pressure
                    //if (switch1.isChecked==true) findViewById<TextView>(R.id.temp).text="lalak"

                    if(weatherDescription.toString()=="ясно") {
                        //findViewById<ImageView>(R.id.description).setImageDrawable(Drawable.createFromPath("/app/src/main/res/drawable/obl.png"))
                        val imageView: ImageView = findViewById(R.id.imageView)
                        imageView.setImageResource(R.drawable.sun)
                        findViewById<ImageView>(R.id.imageView).visibility = View.VISIBLE

                    }
                    if(weatherDescription.toString()=="облачно")
                    {
                        //findViewById<ImageView>(R.id.description).setImageDrawable(Drawable.createFromPath("/app/src/main/res/drawable/obl.png"))
                        val imageView: ImageView = findViewById(R.id.imageView)
                        imageView.setImageResource(R.drawable.obl)
                        findViewById<ImageView>(R.id.imageView).visibility = View.VISIBLE

                    }
                    if(weatherDescription.toString()=="дождь")
                    {
                        //findViewById<ImageView>(R.id.description).setImageDrawable(Drawable.createFromPath("/app/src/main/res/drawable/obl.png"))
                        val imageView: ImageView = findViewById(R.id.imageView)
                        imageView.setImageResource(R.drawable.rain)
                        findViewById<ImageView>(R.id.imageView).visibility = View.VISIBLE

                    }









                } catch (e: Exception) {
                    //findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

                }


        }
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
