package com.darbaast.driver.feature.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import coil.load
import com.darbaast.driver.R
import com.darbaast.driver.custom.DarbastActivity
import com.darbaast.driver.data.model.CarouselData
import com.darbaast.driver.data.sharedPreferences.DarbastSharedPreference
import com.darbaast.driver.data.token.TokenViewModel
import com.darbaast.driver.feature.authentication.AuthenticationActivity
import com.darbaast.driver.feature.history.HistoryFragment
import com.darbaast.driver.feature.home.HomeFragment
import com.darbaast.driver.feature.message.MessageFragment
import com.darbaast.driver.feature.setting.SettingFragment
import com.darbaast.driver.socket.SocketManager
import com.darbaast.driver.socket.SocketService
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.button.MaterialButtonToggleGroup
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.SmoothBottomBar
import org.neshan.common.model.LatLng
import javax.inject.Inject
import com.google.android.material.navigation.NavigationView as NavigationView1

@AndroidEntryPoint
class MainActivity : DarbastActivity() {
    private lateinit var navigationView: NavigationView1
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnDrawerMenu :ImageView
    private lateinit var btnCloseDrawer:ImageView
    private lateinit var bottomBar:SmoothBottomBar
    private lateinit var themeToggleGroup: MaterialButtonToggleGroup
    @Inject
    lateinit var socketManager: SocketManager

    @Inject
    lateinit var darbastSharedPreference: DarbastSharedPreference
    private val  tokenViewModel:TokenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replace(R.id.container_main,HomeFragment())
        setContentView(R.layout.activity_main)
        init()
        checkLocationPermission()
        manageDarkMode()
        drawerMenu()
        click()
        manageBottomBar()
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
    private fun getLocation() {

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

                }

        }
    }
            private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }
    private fun manageDarkMode() {

        if (darbastSharedPreference.getBooleanDark()){
            themeToggleGroup.check(R.id.buttonDark)
        }else{
            themeToggleGroup.check(R.id.buttonLight)
        }


        themeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.buttonLight) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darbastSharedPreference.saveBooleanDark(false)
                    window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
                } else if (checkedId == R.id.buttonDark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darbastSharedPreference.saveBooleanDark(true)
                    window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
                }
            }
        }
    }

    private fun manageBottomBar() {
        bottomBar.setOnItemSelectedListener {
            when(it){
                0 -> replace(R.id.container_main,HomeFragment())
                1 -> replace(R.id.container_main,MessageFragment())
                2 -> replace(R.id.container_main,HistoryFragment())
                3 -> replace(R.id.container_main,SettingFragment())
            }
        }
    }



    private fun click() {
        btnCloseDrawer.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.LEFT)
        }
    }

    private fun drawerMenu() {

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {

                    true
                }
                R.id.nav_logout -> {

                    PopupDialog.getInstance(this)
                        .setStyle(Styles.STANDARD)
                        .setPopupDialogIcon(R.drawable.exit)
                        .setPositiveButtonText(getString(R.string.yes))
                        .setNegativeButtonText(getString(R.string.cancel2))
                        .setHeading(resources.getString(R.string.exitFromSystem))
                        .setDescription(resources.getString(R.string.exitMessage))
                        .setCancelable(false)
                        .showDialog(object : OnDialogButtonClickListener() {
                            override fun onPositiveClicked(dialog: Dialog) {
                                super.onPositiveClicked(dialog)
                                tokenViewModel.deleteAllToken()
                                startActivityDarbast(AuthenticationActivity())
                            }

                            override fun onNegativeClicked(dialog: Dialog) {
                                super.onNegativeClicked(dialog)
                            }
                        })


                    true
                }
                else -> false
            }

        }
        btnDrawerMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

    private fun init(){
        drawerLayout = findViewById(R.id.drawer_layout)
        btnDrawerMenu = findViewById(R.id.btnMenu)
        navigationView = findViewById(R.id.nav_view)
        val view = navigationView.getHeaderView(0)
        btnCloseDrawer = view.findViewById(R.id.img_close_drawer)
        bottomBar = findViewById(R.id.bottomBar)
        themeToggleGroup = findViewById(R.id.themeToggleGroup)

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
}