package com.darbaast.driver.feature.darbastrequest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.carto.styles.AnimationStyle
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.darbaast.driver.R
import com.darbaast.driver.custom.DarbastActivity
import com.darbaast.driver.custom.SingleLiveEvent
import com.darbaast.driver.socket.SocketManager
import com.darbaast.driver.socket.SocketService
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.hilt.android.AndroidEntryPoint
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.internal.utils.BitmapUtils
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.style.NeshanMapStyle
import javax.inject.Inject

@AndroidEntryPoint
class RequestsActivity : DarbastActivity() {
     lateinit var driverLatLng: LatLng
    private lateinit var intent: Intent
    private lateinit var animSt: AnimationStyle
    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {

                val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    if (!this@RequestsActivity::driverLatLng.isInitialized){
                        Handler(Looper.getMainLooper()).postDelayed({
                            getLocation()
                        },4200)

                    }
                }
            }
        }
    }
    @Inject
    lateinit var socketManager: SocketManager
    private lateinit var map: MapView
    private lateinit var btnOnline:CardView
    private lateinit var imgOnline:ImageView
    private lateinit var txtOnline:TextView
    private var traveling = false
    private var online = false
    private val onLineLiveData = SingleLiveEvent<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requsts)
        init()
        manageDarkMode()
        checkLocationPermission()
        liveData()
        clicks()
        initSomething()


    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    location?.let {
                        driverLatLng = LatLng(it.latitude,it.longitude)
                        val lat = it.latitude
                        val lon = it.longitude
                        map.moveCamera(driverLatLng,0f)

                        map.setZoom(20f, 2f)

                        setMarkerOnCurrentLocation(lat,lon)

                    }
                }


        }


        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // نمایش دیالوگ برای روشن کردن GPS
                try {
                    exception.startResolutionForResult(this, 500)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // مشکل در نمایش دیالوگ
                }
            }
        }


    }



    private fun initSomething() {
        onLineLiveData.value = socketManager.isConnected()
    }

    private fun startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, intent)
        } else {
            startService(intent)
        }
    }

    private fun stopService(){
        stopService(intent)
    }

    private fun clicks() {
        btnOnline.setOnClickListener {
            onLineLiveData.value = !online
        }
    }

    private fun liveData() {
        onLineLiveData.observe(this){
            online = it
            if (it){
                startService()
                txtOnline.text = "خاموش"
                imgOnline.setColorFilter(ContextCompat.getColor(this, R.color.successColor), android.graphics.PorterDuff.Mode.SRC_IN)
            }else {
                stopService()
                txtOnline.text = "روشن"
                imgOnline.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private fun manageDarkMode() {
        if (darbastSharedPreferences.getBooleanDark()){
            map.mapStyle = NeshanMapStyle.NESHAN_NIGHT
        }else{
            map.mapStyle = NeshanMapStyle.STYLE_1
        }
    }
    private fun creteCurrentLocationBitmap():com.carto.graphics.Bitmap{
        val markerView = LayoutInflater.from(this).inflate(R.layout.carlocation, null)
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth,
            markerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)
        return BitmapUtils.createBitmapFromAndroidBitmap(bitmap)
    }
    private fun setMarkerOnCurrentLocation(lat:Double, lng:Double){

        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 3f
        animStBl.phaseOutDuration = 4f
        animSt = animStBl.buildStyle()
        val bitmap = creteCurrentLocationBitmap()
        val markerStyleBuilder = MarkerStyleBuilder()
        markerStyleBuilder.size = 60f
        markerStyleBuilder.bitmap = bitmap
        markerStyleBuilder.animationStyle = animSt
        val markerStyle = markerStyleBuilder.buildStyle()
        map.addMarker(Marker(LatLng(lat,lng), markerStyle))

    }

    private fun init(){
        map = findViewById(R.id.map)
        intent = Intent(this,SocketService::class.java)
        btnOnline = findViewById(R.id.cardOnline)
        imgOnline = findViewById(R.id.imgOnline)
        txtOnline = findViewById(R.id.txtOnline)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(gpsSwitchStateReceiver)
        if (!traveling){
         //   stopService()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation()
                } else {

                    toast("بدون این دسترسی نمی توانید موقعیت خود را در نقشه پیدا کنید!")
                }
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            500 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        getLocation()
                    }
                    Activity.RESULT_CANCELED -> {
                        toast("برای دریافت موقعیت خود باید gps خود را روشن کنید")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(gpsSwitchStateReceiver, intentFilter)
    }

}
