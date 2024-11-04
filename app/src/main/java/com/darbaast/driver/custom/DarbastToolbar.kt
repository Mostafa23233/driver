package com.darbaast.driver.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.darbaast.driver.R

@SuppressLint("Recycle")
class DarbastToolbar(context: Context, attrs:AttributeSet?):FrameLayout(context,attrs) {

    init {

        val view = inflate(context, R.layout.view_toolbar, this)
        if (attrs!=null){
            val a = context.obtainStyledAttributes(attrs,R.styleable.DarbastToolbar)
            val title = a.getString(R.styleable.DarbastToolbar_dt_title)
            if (!title.isNullOrEmpty()){
               val toolbarTitle:TextView = view.findViewById(R.id.toolbarTitleTv)
               toolbarTitle.text = title
            }
        }

    }

}