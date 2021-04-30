package com.example.wea

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.gson.Gson
import jsonresponce.Response
import jsonresponce.Response2
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener, View.OnClickListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val API: String = "19980e00b25d8dd054c9cbf6233c0fb2" // Use API key
    var city: String = storage.citty
    var k: Int = 0
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var cities: EditText
    lateinit var button: Button
    lateinit var Daytodaybutton: Button
    var titles = arrayOfNulls<String>(6)
    var details = arrayOfNulls<String>(6)
    var images = intArrayOf(1, 2, 3, 4, 5, 6)
    var heplmassiv = arrayOf("er", "", "", "", "", "")
    lateinit var gestureDetector: GestureDetector
    var x2: Float = 0.0f
    var x1: Float = 0.0f
    var y2: Float = 0.0f
    var y1: Float = 0.0f
    var temperatura: String = "metric"
    var multiply: Boolean = storage.multiply
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var hasGps = false
    private var hasNetwork = false
    private val mGoogleApiClient: GoogleApiClient? = null
    lateinit var mLastLocation: Location

    companion object {
        const val MIN_DISTANCE = 150
    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    internal lateinit var mLocationRequest: LocationRequest
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLocationRequest = LocationRequest()
        startLocationUpdates()
        //stoplocationUpdates()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        switch1.isEnabled = false
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        relativeLayout2.visibility = View.GONE
        Daytoday.visibility = View.INVISIBLE
        findViewById<ImageView>(R.id.imageView).visibility = View.INVISIBLE
        findViewById<Button>(R.id.button).visibility = View.INVISIBLE

        cities = findViewById(R.id.cities)
        button = findViewById(R.id.button)
        Daytodaybutton = findViewById(R.id.Daytoday)
        button.setOnClickListener(this)
        Daytodaybutton.setOnClickListener(this)
        gestureDetector = GestureDetector(this, this)



        layoutManager = LinearLayoutManager(this)
        weatherTask().execute()

        switch1.setOnClickListener(View.OnClickListener {
            switch1.isEnabled = false
            multiply = false
            if (switch1.isChecked) {
                findViewById<EditText>(R.id.cities).isEnabled = false

            } else cities.isEnabled = true
            getLastLocation()
            weatherTask().execute()
        })
    }

    override fun onClick(view: View?) {
        if (view == this.button) {
            switch1.isEnabled=false
            multiply = false
            city = cities.text.toString()
            storage.citty = city
            weatherTask().execute()
        }
        if (view == this.Daytodaybutton) {

            multiply = true
            storage.citty = city
            if (k == 0) {
                k = 1
                weatherTask().execute()
            }

        }
        //TODO("Not yet implemented")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        when
            (event?.action) {
            0 -> {
                x1 = event.x
                y1 = event.y
            }
            1 -> {

                x2 = event.x
                y2 = event.y

                val valueX: Float = x2 - x1
                val valueY: Float = y2 - y1

                if (abs(valueY) > MIN_DISTANCE) {
                    if (y2 > y1) {
                        multiply = false


                        weatherTask().execute()

                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        val client = LocationServices.getFusedLocationProviderClient(this)
        client.lastLocation.addOnSuccessListener { location: Location? ->
            this.latitude = location?.latitude.toString()
            this.longitude = location?.longitude.toString()
            storage.latitude = this.latitude
            storage.longitude = this.longitude
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper())
    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined

        mLastLocation = location
        if (mLastLocation != null) {

// Update the UI from here
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun kegs() {
        ere.setOnTouchListener { v: View, m: MotionEvent ->
            refresh()
            true
        }
    }

    private fun refresh() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                try {
                    recreate()
                } catch (e: Exception) {
                }
            }
        }
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {

            if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ActivityCompat.requestPermissions(this@MainActivity, permissions, 0)
            }

            super.onPreExecute()

            findViewById<ProgressBar>(R.id.Loader).visibility = View.VISIBLE
            relativeLayout2.visibility = View.GONE
            imageView.visibility = View.GONE
            Daytodaybutton.visibility = View.GONE
            button.visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)

        }

        override fun doInBackground(vararg params: String?): String? {

            if (multiply)


                city = cities.text.toString()
            var response: String?
            try {
                response = ""
                if (!multiply) {
                    if (switch1.isChecked == false)
                        response = response1(response, city)
                    if (switch1.isChecked == true) {
                        // response= URL("https://www.google.by/maps/search/er/@40.4959367,50.899725,4z/data=!3m1!4b1").readText(
                        //Charsets.UTF_8)
                        getLastLocation()
                        response = response2(response, city)
                    }
                }
                if (multiply) {
                    if (switch1.isChecked == false) {
                        metodlonglat()
                        response = response3(response, city)
                    }
                    if (switch1.isChecked == true) {
                        getLastLocation()
                        response = response4(response, city)
                    }
                }
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {

            k = 0
            if (switch1.isChecked == false) {
                Handler().postDelayed({
                    relativeLayout2.visibility = View.VISIBLE
                    Loader.visibility = View.GONE
                    Daytoday.visibility = View.VISIBLE
                    findViewById<Button>(R.id.button).visibility = View.VISIBLE
                    imageView.visibility = View.VISIBLE
                    switch1.isEnabled = true
                }, 4000)
            } else {
                Handler().postDelayed({
                    relativeLayout2.visibility = View.VISIBLE
                    Loader.visibility = View.GONE
                    Daytoday.visibility = View.VISIBLE
                    findViewById<Button>(R.id.button).visibility = View.VISIBLE
                    imageView.visibility = View.VISIBLE
                    switch1.isEnabled = true
                }, 4000)
            }

            super.onPostExecute(result)
            var gson = Gson()
            try {
                if (!multiply) {
                    var data = gson.fromJson(result, Response::class.java)
                    var temp = data.main?.temp
                    var tempMin = "Минимум: " + data.main?.tempMin
                    var tempMax = "Максимум: " + data.main?.tempMax
                    var updatedAt: Long? = data.dt
                    var weatherDescription = data.weather!![0]!!.description
                    var pressure = data.main?.pressure
                    var windSpeed = data.wind?.speed.toString()
                    var address = data.name + ", " + data.sys?.country
                    var ICON = data.weather!![0]?.icon
                    val updatedAtText =
                            "Обновлено: " + SimpleDateFormat(" hh:mm a", Locale.ENGLISH).format(
                                    Date(updatedAt?.times(1000)!!)
                            )
                    findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.temp).text = temp.toString() + "°c"
                    findViewById<TextView>(R.id.address).text = address;
                    findViewById<TextView>(R.id.update).text = updatedAtText.toString()
                    findViewById<TextView>(R.id.temp_max).text = tempMax
                    findViewById<TextView>(R.id.temp_min).text = tempMin.toString()
                    findViewById<TextView>(R.id.windspeed).text = windSpeed + "м/c"
                    findViewById<TextView>(R.id.description).text = "Погода: " + weatherDescription
                    findViewById<TextView>(R.id.pressure).text = "Давление: " + pressure + " гПА"
                    val imageView: ImageView = findViewById(R.id.imageView)
                    var nameimage: Int = 0
                    for (x in icons.values())
                        if ("ic_" + ICON == x.name)
                            nameimage = x.ok
                    imageView.setImageResource(nameimage ?: error(""))
                }
                if (multiply) {

                    // var gson=Gson()
                    var data2 = gson.fromJson(result, Response2::class.java)
                    val start = Intent(this@MainActivity, kek()::class.java)
                    start.putExtra("details", TempMIN(data2))
                    start.putExtra("images", IDIMAGE(data2))
                    start.putExtra("titles", TempMAX(data2))
                    startActivity(start)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun response1(response2: String?, city: String): String {
        var response2 =
                URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$temperatura&appid=$API&lang=ru").readText(
                        Charsets.UTF_8
                )
        return response2
    }

    fun response2(response2: String?, city: String): String {
        var response2 = ""
        while (response2 == "") {
            response2 =
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=${storage.latitude}&lon=${storage.longitude}&units=$temperatura&appid=19980e00b25d8dd054c9cbf6233c0fb2&lang=ru").readText(
                            Charsets.UTF_8
                    )
        }
        return response2
    }

    fun response3(response2: String?, city: String): String {
        var response2 =
                URL("https://api.openweathermap.org/data/2.5/onecall?lat=${storage.latitude2}&lon=${storage.longitude2}&exclude=hourly,current,minutely,alerts&units=$temperatura&appid=19980e00b25d8dd054c9cbf6233c0fb2").readText(
                        Charsets.UTF_8
                )
        return response2
    }

    fun response4(response2: String?, city: String): String {
        var response2 =
                URL("https://api.openweathermap.org/data/2.5/onecall?lat=${storage.latitude}&lon=${storage.longitude}&exclude=hourly,current,minutely,alerts&units=$temperatura&appid=19980e00b25d8dd054c9cbf6233c0fb2").readText(
                        Charsets.UTF_8
                )
        return response2
    }

    fun TempMAX(data2: Response2): Array<String?> {

        for (i in 0..5) {
            var temp2 = data2.daily!![i]?.temp?.day
            titles[i] = temp2.toString()
        }
        return titles
    }

    fun TempMIN(data2: Response2): kotlin.Array<String?> {
        for (i in 0..5) {
            var temp2 = data2.daily!![i]?.temp?.night
            details[i] = temp2.toString()
        }
        return details
    }

    fun IDIMAGE(data2: Response2): IntArray {
        for (i in 0..5) {
            var temp2 = data2.daily!![i]?.weather?.get(0)!!.icon
            heplmassiv[i] = temp2.toString()
            for (x in icons.values())
                if ("ic_" + heplmassiv[i] == x.name) {
                    images[i] = x.ok
                    continue
                }
        }
        return images
    }

    fun metodlonglat() {
        try {
            var gson = Gson()
            var response: String? =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$temperatura&appid=$API&lang=ru").readText(
                            Charsets.UTF_8
                    )
            var data = gson.fromJson(response, Response::class.java)
            storage.longitude2 = data.coord?.lon.toString()
            storage.latitude2 = data.coord?.lat.toString()
        } catch (e: Exception) {
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



