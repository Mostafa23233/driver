package com.darbaast.driver.feature.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.darbaast.driver.feature.authentication.AuthenticationActivity
import com.darbaast.driver.custom.DarbastActivity
import com.darbaast.driver.data.sharedPreferences.DarbastSharedPreference
import com.darbaast.driver.feature.main.MainActivity
import com.darbaast.driver.R
import com.darbaast.driver.custom.annotation.AppVersion
import com.darbaast.driver.data.token.TokenViewModel
import com.darbaast.driver.feature.authentication.AuthViewModel
import com.darbaast.driver.feature.authentication.sms.AppSignatureHelper
import com.darbaast.driver.utils.DarbastAnimations
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : DarbastActivity() {

    private lateinit var updateView: LinearLayout
    private lateinit var btnUpdate : AppCompatButton
    private lateinit var btnLater: AppCompatButton
    @Inject
    lateinit var darbastSharedPreference:DarbastSharedPreference
    @AppVersion
    @Inject
    lateinit var appVersion:String
    private val tokenViewModel:TokenViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var animation: DarbastAnimations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
        manageDarkMod()
        checkVersion()
        exceptionHandler()




    }
    private fun manageDarkMod() {

        val booleanDark = darbastSharedPreference.getBooleanDark()
        if(booleanDark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun init(){
        updateView = findViewById(R.id.view_update)
        btnLater = findViewById(R.id.btn_later)
        btnUpdate = findViewById(R.id.btn_update)
    }

    private fun exceptionHandler() {
        authViewModel.exceptionLiveData.observe(this){
            if (it is HttpException){
                if (it.code() == 401){
                    tokenViewModel.deleteAllToken()
                    startActivityDarbast(AuthenticationActivity())
                }
            }
        }
    }

    private fun checkVersion() {
        authViewModel.getVersion()
        authViewModel.appVersionLiveData.observe(this){app->
            if (appVersion == app.last_version ){
                checkAuth()
            }else{
                btnUpdate.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(app.store_update_url)
                    startActivity(intent)
                }
                visibleItem(updateView)
                animation.animateFromBottomToTop(updateView)
                btnLater.setOnClickListener {
                    checkAuth()
                }
            }
        }
    }

    private fun checkAuth() {

        tokenViewModel.getToken()
        tokenViewModel.tokenLiveData.observe(this){
            if (it.isNotEmpty()){
                authViewModel.authUser()


            }else{
                startActivityDarbast(AuthenticationActivity())
            }
        }
        authViewModel.userLiveData.observe(this){
            startActivityDarbast(MainActivity())
        }

    }
}