package com.darbaast.driver.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation

class DarbastAnimations {
    fun animateFromBottomToTop(view: View) {
        val translateAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            1f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        translateAnimation.duration = 900
        view.animation = translateAnimation
        Handler(Looper.getMainLooper()).postDelayed({
            view.clearAnimation()
        }, 900)

    }

    fun animateFromTopToBottom(view: View) {
        val translateAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.ZORDER_TOP,
            0f,
            Animation.RELATIVE_TO_SELF,
            1f
        )
        translateAnimation.duration = 900
        view.animation = translateAnimation
        Handler(Looper.getMainLooper()).postDelayed({
            view.clearAnimation()
        }, 900)

    }

    fun animateFromRightToLeft(view: View) {
        val translateAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            1f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.ZORDER_TOP,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        translateAnimation.duration = 900
        view.animation = translateAnimation
        Handler(Looper.getMainLooper()).postDelayed({
            view.clearAnimation()
        }, 900)

    }

    fun shakeAnimation(view: View) {
        val translateAnimation = RotateAnimation(
            0.5f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        translateAnimation.duration = 100
        view.animation = translateAnimation
        Handler(Looper.getMainLooper()).postDelayed({
            view.clearAnimation()
        }, 100)

    }

    fun bottomNavigationAnimation(
        openRatio: Float,
        view: View
    ) {

        if (openRatio > 0.9736264f){
            val translateAnimation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0f,
                Animation.RELATIVE_TO_SELF,
                0f,
                Animation.RELATIVE_TO_SELF,
                0f,
                Animation.RELATIVE_TO_SELF,
                10f
            )
            translateAnimation.duration = 100
            view.animation = translateAnimation
            Handler(Looper.getMainLooper()).postDelayed({
                view.visibility = View.GONE
                view.clearAnimation()
            },100)
        }else{
            if (view.visibility == View.GONE){
                view.visibility = View.VISIBLE
            }
        }
    }

}
