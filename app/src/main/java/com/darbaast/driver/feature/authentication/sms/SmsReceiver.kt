package com.darbaast.driver.feature.authentication.sms


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class SMSReceiver : BroadcastReceiver() {
    private var otpListener: OTPReceiveListener? = null


    fun setOTPListener(otpListener: OTPReceiveListener?) {
        this.otpListener = otpListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status?

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent = extras!!.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    val newString = messageIntent.filter {
                        it.isDigit()
                    }
                    otpListener?.onOTPReceived(newString)
                }
                CommonStatusCodes.TIMEOUT -> {
                    otpListener?.onOTPTimeOut()
                }
            }
        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
        fun onOTPReceivedError(error: String?)
    }
}