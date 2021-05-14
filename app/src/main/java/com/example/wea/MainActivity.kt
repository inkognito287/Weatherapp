package com.example.wea

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
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
    var latitude = ""
    lateinit var longitude: String
    lateinit var Daytodaybutton: Button
    lateinit var mLastLocation: Location
    lateinit var Cities: SearchView
    lateinit var gestureDetector: GestureDetector
    lateinit var mLocationRequest: LocationRequest
    val API: String = "19980e00b25d8dd054c9cbf6233c0fb2" // Use API key
    var city: String = ""
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
    var multiply: Boolean = false
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
        mLocationRequest = LocationRequest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        switch1.isEnabled = false
        Daytodaybutton = findViewById(R.id.Daytoday)
        Daytodaybutton.setOnClickListener(this)
        gestureDetector = GestureDetector(this, this)
        layoutManager = LinearLayoutManager(this)
        weatherTask().execute()
        switch1.setOnClickListener {
            cities.clearFocus()
            switch1.isEnabled = false
            //multiply = false
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
        deleteData()
        saveData(city)
    }
    override fun onDestroy() {
        super.onDestroy()
        deleteData()
        saveData(city)
    }
    override fun onClick(view: View?) {
        if (view == this.Daytodaybutton) {
            multiply = true
            if (k == 0) {
                k = 1
                weatherTask().execute()
            }
        }
        if (view == this.cities) {
            city = cities.query.toString()
            weatherTask().execute()
        }
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
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

    fun emptySpaceClick(v: View) {
        if (v == space) {
            cities.clearFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            listviewhint.visibility = View.GONE
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
                        //multiply = false
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
            Toast.makeText(this@MainActivity, "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show()
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
        }
        override fun doInBackground(vararg params: String?): String? {
            closeHints()
            try {
                val test = Gson()
                var allCities = URL("https://nominatim.openstreetmap.org/search?city=${Cities.query}&accept-language=en&format=json").readText(Charsets.UTF_8)
                allCities = "{\"main\":" + allCities + "}"
                var dataCITIES = test.fromJson(allCities, Response5::class.java)
                names[0] = dataCITIES.main!![0]!!.displayName.toString()
                var hintListView = dataCITIES.main!![0]!!.displayName.toString()
                hintListView = hintListView.substring(0, hintListView.indexOf(','))
                names[0] = hintListView
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
                        while (latitude == "")
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
            if (result == null && !switch1.isChecked && checkInternet() && isLocationEnabled()) {
                cities.visibility = View.VISIBLE
                if (listviewhint.visibility==View.GONE && cities.query!="")
                Toast.makeText(this@MainActivity, "Такого города не найдено", Toast.LENGTH_SHORT).show()
            }
            val listview = findViewById<ListView>(R.id.listviewhint)
            val adapter: ArrayAdapter<String?> = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, names)
            listview.adapter = adapter
            listview.setOnItemClickListener { parent, view, position, id ->
                val element = adapter.getItem(position)
                cities.onActionViewExpanded()
                cities.setQuery(element, true)
                cities.clearFocus()
                listview.visibility = View.GONE
                city = cities.query.toString()
                weatherTask().execute()
            }
            cities.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(p0: String?): Boolean {
                    cities.clearFocus()
                    listview.visibility = View.GONE
                    if (names.contains(p0)) {
                        adapter.filter.filter(p0)
                    }
                    if (switch1.isChecked)
                        switch1.toggle()
                    city = cities.query.toString()
                    weatherTask().execute()
                    return false
                }
                override fun onQueryTextChange(p0: String?): Boolean {
                   // multiply = false
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
                        performNowPrognoses(result, gson)
                    }
                    if (multiply) {
                        val data2 = gson.fromJson(result, Response2::class.java)
                        val start = Intent(this@MainActivity, WeeklyForecast()::class.java)
                        start.putExtra("nightTemperature", tempMin(data2))
                        start.putExtra("images", idImage(data2))
                        start.putExtra("dayTemperature", tempMax(data2))
                        startActivity(start)
                        multiply=false
                    }
                } catch (e: Exception) {
                }
            } else {
                closeVisibility()
                if (checkInternet() && isLocationEnabled() && switch1.isChecked) {
                    weatherTask().execute()
                }
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
        return URL("https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=$temperatura&appid=$API&lang=ru").readText(
            Charsets.UTF_8
        )
    }
    fun response3(): String {
        return URL("https://api.openweathermap.org/data/2.5/onecall?lat=${latitude}&lon=${longitude}&exclude=hourly,current,minutely,alerts&units=$temperatura&appid=$API&lang=ru").readText(
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
            longitude = data.coord?.lon.toString()
            latitude = data.coord?.lat.toString()
        } catch (e: Exception) {
        }
    }
    private fun getScreenOrientation(): Int? {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0 else 0
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
    fun performNowPrognoses(result: String?, gson: Gson) {

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
    @RequiresApi(Build.VERSION_CODES.M)
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun closeHints() {
        space.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            space.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = space.rootView.height
            val keypadHeight: Int = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                // keyboard is opened
            } else {
                if (listviewhint.visibility == View.VISIBLE)
                    cities.clearFocus()
                listviewhint.visibility = View.GONE
                // keyboard is closed
            }
        }
    }
}
