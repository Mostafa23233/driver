package com.darbaast.driver.server.retrofit

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // بررسی اتصال به اینترنت
        if (!isOnline(context)) {
            throw NoConnectivityException()
        }

        // ادامه دادن درخواست و دریافت پاسخ
        val request = chain.request()
        val response = chain.proceed(request)

        // گرفتن status code
        val statusCode = response.code

        // چک کردن status code
        if (statusCode == 401) {
            // خطای احراز هویت
            throw AuthenticationException("Unauthorized")
        } else if (statusCode == 500) {
            // خطای سرور
            throw ServerException("Internal Server Error")
        }

        // برگرداندن پاسخ به بقیه بخش‌ها
        return response
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}

// تعریف کلاس‌های خطا
class NoConnectivityException : IOException("No internet connection")
class AuthenticationException(message: String) : IOException(message)
class ServerException(message: String) : IOException(message)
