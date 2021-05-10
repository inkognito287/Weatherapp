package com.example.wea

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.gson.Gson
import jsonresponce.Response
import jsonresponce.Response2
import jsonresponce.Response5
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener, View.OnClickListener {
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var Daytodaybutton: Button
    lateinit var mLastLocation: Location
    lateinit var Cities: SearchView
    lateinit var gestureDetector: GestureDetector
    lateinit var mLocationRequest: LocationRequest
    val API: String = "19980e00b25d8dd054c9cbf6233c0fb2" // Use API key
    var city: String = storage.citty
    var pref: SharedPreferences? = null
    var code = "city"
    var k: Int = 0
    var temperaturadnem = arrayOfNulls<String>(6)
    var temperaturanochy = arrayOfNulls<String>(6)
    var heplmassiv = arrayOfNulls<String>(6)
    var images = intArrayOf(1, 2, 3, 4, 5, 6)
    var x2: Float = 0.0f
    var x1: Float = 0.0f
    var y2: Float = 0.0f
    var y1: Float = 0.0f
    var temperatura: String = "metric"
    var multiply: Boolean = storage.multiply
    var layoutManager: RecyclerView.LayoutManager? = null
    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var names = arrayOf("")
    val vrblInterval = 2000
    val fastInterval: Long = 1000
    companion object {
        const val MIN_DISTANCE = 150
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        relativeLayout2.visibility = View.GONE
        mainContainer.visibility = View.GONE
        cities.visibility = View.GONE
        startLocationUpdates()
        pref = getSharedPreferences(code, Context.MODE_PRIVATE)
        city = pref?.getString(code, "")!!
        Cities = findViewById<SearchView>(R.id.cities)
        mLocationRequest = LocationRequest()
        switch1.isEnabled = false
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Daytodaybutton = findViewById(R.id.Daytoday)
        Daytodaybutton.setOnClickListener(this)
        gestureDetector = GestureDetector(this, this)
        layoutManager = LinearLayoutManager(this)
        weatherTask().execute()
        switch1.setOnClickListener {
            cities.clearFocus()
            switch1.isEnabled = false
            multiply = false
            if (switch1.isChecked) {
                saveData(city)
                Loader.visibility = View.VISIBLE

            } else {
                city = pref?.getString(code, "")!!
                cities.isClickable = true
                city = pref?.getString(code, "")!!

            }
            weatherTask().execute()
            checkInternet()
        }
    }
    fun saveData(res: String) {
        val editor = pref?.edit()
        editor?.putString(code, city)
        editor?.apply()
    }
    fun deleteData() {
        val editor = pref?.edit()
        editor?.clear()
        editor?.apply()
    }
    override fun onResume() {
        super.onResume()
        city = pref?.getString(code, "")!!
    }
    override fun onStop() {
        super.onStop()
        saveData(city)
    }
    override fun onDestroy() {
        super.onDestroy()
        saveData(city)
    }
    override fun onClick(view: View?) {
        if (view == this.Daytodaybutton) {

            multiply = true
            storage.citty = city
            if (k == 0) {
                k = 1
                weatherTask().execute()
            }
        }
        if (view == this.cities) {
            city = Cities.query.toString()
            weatherTask().execute()
        }
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            listviewhint.visibility=View.GONE
            setTitle("Подтверждение")
            setMessage("Вы уверены, что хотите выйти из программы?")

            setPositiveButton("Да") { _, _ ->
                super.onBackPressed()
            }

            setNegativeButton("Нет") { _, _ ->
            }
            setCancelable(true)
        }.create().show()
    }

    fun emptySpaceClick(v:View){
        if (v==space){
        cities.clearFocus()
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        listviewhint.visibility=View.GONE
        }
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
                        checkInternet()
                        Loader.visibility = View.VISIBLE
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }
    fun checkInternet(): Boolean {
        val context = this
        var connectivity: ConnectivityManager? = null
        var info: NetworkInfo? = null
        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE)
            as ConnectivityManager
        info = connectivity.activeNetworkInfo
        if (info != null) {
            if (info.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        } else {
            imageView.visibility = View.GONE
            Toast.makeText(this@MainActivity, "Проверьте подключение к интернету", Toast.LENGTH_LONG).show()
            return false
        }
        return false
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
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
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun startLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = vrblInterval.toLong()
        mLocationRequest.fastestInterval = fastInterval
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)
        while (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }
    private fun stopLocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }

    fun onLocationChanged(location: Location) {
        mLastLocation = location
    }
    inner class weatherTask : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            if (!isLocationEnabled()) {
                Toast.makeText(this@MainActivity, "Пожалуйста включите gps", Toast.LENGTH_SHORT).show()
            }
            startLocationUpdates()
            getLastLocation()
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
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }
        override fun doInBackground(vararg params: String?): String? {
            try {
                val test = Gson()
                var allCities = URL("https://nominatim.openstreetmap.org/search?city=${Cities.query}&accept-language=en&format=json").readText(Charsets.UTF_8)
                allCities = "{\"main\":" + allCities + "}"
                var dataCITIES = test.fromJson(allCities, Response5::class.java)
                names[0] = dataCITIES.main!![0]!!.displayName.toString()
                var pokaz = dataCITIES.main!![0]!!.displayName.toString()
                pokaz = pokaz.substring(0, pokaz.indexOf(','))
                names[0] = pokaz
                println(pokaz)
            } catch (e: Exception) {
                println(e.toString())
            }
            var response: String?
            try {
                response = ""
                if (!multiply) {
                    if (!switch1.isChecked) {
                        response = response1(city)
                    }
                    if (switch1.isChecked) {
                        response = response2()
                    }
                }
                if (multiply) {
                    if (!switch1.isChecked) {
                        getLongLat()
                        response = response3()
                    }
                    if (switch1.isChecked) {
                        getLastLocation()
                        response = response3()
                    }
                }
            } catch (e: Exception) {
                response = null
            }
            return response
        }
        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            if(result==null)
                Toast.makeText(this@MainActivity,"Такого города не найдено",Toast.LENGTH_SHORT).show()
            val listview = findViewById<ListView>(R.id.listviewhint)
            val adapter: ArrayAdapter<String?> = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, names)
            listview.adapter = adapter
            listview.setOnItemClickListener { parent, view, position, id ->
                val element = adapter.getItem(position)
                Cities.onActionViewExpanded()
                Cities.setQuery(element, true)
                Cities.clearFocus()
                listview.visibility = View.GONE
                city = cities.query.toString()
                weatherTask().execute()
            }
            Cities.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    Cities.clearFocus()
                    listview.visibility = View.GONE
                    if (names.contains(p0)) {
                        adapter.filter.filter(p0)
                    }
                    if(switch1.isChecked)
                        switch1.toggle()
                    city = cities.query.toString()
                    weatherTask().execute()
                    return false
                }
                override fun onQueryTextChange(p0: String?): Boolean {
                    listview.visibility = View.VISIBLE
                    weatherTask().execute()
                    listview.adapter = adapter
                    adapter.notifyDataSetChanged()
                    return false
                }
            })
            getLastLocation()
            if (result != null) {
                k = 0
                openVisibility()
                switch1.isEnabled = true
                super.onPostExecute(result)
                val gson = Gson()
                try {
                    if (!multiply) {
                        val data = gson.fromJson(result, Response::class.java)
                        val temp = data.main?.temp
                        val tempMin = "Минимум: " + data.main?.tempMin
                        val tempMax = "Максимум: " + data.main?.tempMax
                        val updatedAt: Long? = data.dt
                        val weatherDescription = data.weather!![0]!!.description
                        val pressure = data.main?.pressure
                        val windSpeed = data.wind?.speed.toString()
                        val address = data.name + ", " + data.sys?.country
                        val icon = data.weather[0]?.icon
                        val updatedAtText =
                            "Обновлено: " + SimpleDateFormat(" hh:mm a", Locale.ENGLISH).format(
                                Date(updatedAt?.times(1000)!!)
                            )
                        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.temp).text = temp.toString() + "°c"
                        findViewById<TextView>(R.id.address).text = address
                        findViewById<TextView>(R.id.update).text = updatedAtText.toString()
                        findViewById<TextView>(R.id.temp_max).text = tempMax
                        findViewById<TextView>(R.id.temp_min).text = tempMin.toString()
                        findViewById<TextView>(R.id.windspeed).text = windSpeed + "м/c"
                        findViewById<TextView>(R.id.description).text = "Погода: " + weatherDescription
                        findViewById<TextView>(R.id.pressure).text = "Давление: " + pressure + " гПА"
                        val imageView: ImageView = findViewById(R.id.imageView)
                        var nameimage: Int = 0
                        for (x in icons.values())
                            if ("ic_" + icon == x.name)
                                nameimage = x.ok
                        imageView.setImageResource(nameimage)
                    }
                    if (multiply) {
                        val data2 = gson.fromJson(result, Response2::class.java)
                        val start = Intent(this@MainActivity, weeklyforecast()::class.java)
                        start.putExtra("nightTemperature", tempMin(data2))
                        start.putExtra("images", idImage(data2))
                        start.putExtra("dayTemperature", tempMax(data2))
                        startActivity(start)
                    }
                } catch (e: Exception) {
                }
            } else {
                closeVisibility()
                if (checkInternet() && isLocationEnabled() && switch1.isChecked)
                    Toast.makeText(this@MainActivity, "Попробуйте ещё раз", Toast.LENGTH_SHORT).show()
                switch1.isEnabled = true
            }
            if (checkInternet() && !isLocationEnabled() && switch1.isChecked) {
                relativeLayout2.visibility = View.GONE
                mainContainer.visibility = View.GONE
            }
        }
    }
    fun response1(city: String): String {
        return URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$temperatura&appid=$API&lang=ru").readText(
            Charsets.UTF_8
        )
    }
    fun response2(): String {
        return URL("https://api.openweathermap.org/data/2.5/weather?lat=${storage.latitude}&lon=${storage.longitude}&units=$temperatura&appid=$API&lang=ru").readText(
            Charsets.UTF_8
        )
    }
    fun response3(): String {
        return URL("https://api.openweathermap.org/data/2.5/onecall?lat=${storage.latitude}&lon=${storage.longitude}&exclude=hourly,current,minutely,alerts&units=$temperatura&appid=$API&lang=ru").readText(
            Charsets.UTF_8
        )
    }
    fun tempMax(data2: Response2): Array<String?> {
        for (i in 0..5) {
            val temp2 = data2.daily!![i]?.temp?.day
            temperaturadnem[i] = temp2.toString()
        }
        return temperaturadnem
    }
    fun tempMin(data2: Response2): kotlin.Array<String?> {
        for (i in 0..5) {
            val temp2 = data2.daily!![i]?.temp?.night
            temperaturanochy[i] = temp2.toString()
        }
        return temperaturanochy
    }
    fun idImage(data2: Response2): IntArray {
        for (i in 0..5) {
            val temp2 = data2.daily!![i]?.weather?.get(0)!!.icon
            heplmassiv[i] = temp2.toString()
            for (x in icons.values())
                if ("ic_" + heplmassiv[i] == x.name) {
                    images[i] = x.ok
                    continue
                }
        }
        return images
    }
    fun getLongLat() {
        try {
            val gson = Gson()
            val response: String? =
                URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$temperatura&appid=$API&lang=ru").readText(
                    Charsets.UTF_8
                )
            val data = gson.fromJson(response, Response::class.java)
            storage.longitude = data.coord?.lon.toString()
            storage.latitude = data.coord?.lat.toString()
        } catch (e: Exception) {
        }
    }
    fun openVisibility() {
        Loader.visibility = View.GONE
        Daytoday.visibility = View.VISIBLE
        imageView.visibility = View.VISIBLE
        cities.visibility = View.VISIBLE
        relativeLayout2.visibility = View.VISIBLE

    }
    fun closeVisibility() {
        Loader.visibility = View.GONE
        relativeLayout2.visibility = View.GONE
        mainContainer.visibility = View.GONE
    }
    override fun onShowPress(p0: MotionEvent?) {
    }
    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }
    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }
    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }
    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }
    override fun onLongPress(p0: MotionEvent?) {
    }
}

private fun Switch?.setOnClickListener(onClickListener: View.OnClickListener) {

}

