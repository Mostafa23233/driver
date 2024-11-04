package com.darbaast.driver.custom

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.darbaast.driver.data.sharedPreferences.DarbastSharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class DarbastActivity : AppCompatActivity(){

    val LOCATION_PERMISSION_REQUEST_CODE = 300

    @Inject
    lateinit var darbastToast: DarbastToast

    @Inject
    lateinit var darbastSharedPreferences: DarbastSharedPreference

    fun toast(text:String){
        darbastToast.toastActivity(this,text)
    }
    fun replace(fragmentContainer:Int,fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(fragmentContainer,fragment)
        }.commit()
    }
    fun visibleItem(view: View){
        view.visibility = View.VISIBLE
    }
    fun invisibleItem(view: View){
        view.visibility = View.GONE
    }
    fun startActivityDarbast(activity: DarbastActivity){
        startActivity(Intent(this, activity::class.java)).apply {
            finish()
        }
    }
}

@AndroidEntryPoint
abstract class DarbastFragment : Fragment() {
    @Inject
    lateinit var darbastToast: DarbastToast
    fun startActivity(activity: DarbastActivity, context: Context) {
        startActivity(Intent(context, activity::class.java)).apply {
            requireActivity().finish()
        }

    }
   fun visibleItem(view: View){
       view.visibility = View.VISIBLE
   }
    fun invisibleItem(view: View){
        view.visibility = View.GONE
    }
    fun toast(text:String){
        darbastToast.toastFragment(this,text)
    }
    fun replace(fragmentContainer: Int, fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(fragmentContainer, fragment)
        }.commit()

    }


}