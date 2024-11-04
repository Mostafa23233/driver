package com.darbaast.driver.data.sharedPreferences

import android.content.Context
import android.content.SharedPreferences

class DarbastSharedPreference(private val context: Context) {
    fun saveBooleanDark(b: Boolean?) {
        val sharedPreferences = context.getSharedPreferences("DARK", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("DARK", b!!)
        editor.apply()
    }

    fun getBooleanDark(): Boolean {
        val sharedPreferences = context.getSharedPreferences("DARK", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("DARK", false)
    }
    fun savePhone(phone:String){
        val sharedPreferences = context.getSharedPreferences("PHONE",Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putString("PHONE",phone)
        edit.apply()
    }

    fun getPhone():String{
        val sharedPreferences = context.getSharedPreferences("PHONE",Context.MODE_PRIVATE)
        return sharedPreferences.getString("PHONE","").toString()
    }







}