package com.darbaast.driver.custom

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.darbaast.driver.R


class DarbastToast:java.io.Serializable {


    fun toastFragment(fragment: DarbastFragment , text:String){

        val layout: View = fragment.layoutInflater.inflate(R.layout.toast,
            fragment.view?.findViewById(R.id.toast_layout_root)
        )
        val txtToast: TextView = layout.findViewById(R.id.txt_toast)
        txtToast.text = text
        val toast = Toast(layout.context)
        toast.setGravity(Gravity.BOTTOM,0,130)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
    fun toastActivity(activity:Activity, text:String){
        val layout: View = activity.layoutInflater.inflate(R.layout.toast,activity.findViewById(R.id.toast_layout_root))
        val txtToast: TextView = layout.findViewById(R.id.txt_toast)
        txtToast.text = text
        val toast = Toast(activity)
        toast.setGravity(Gravity.BOTTOM,0,130)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}