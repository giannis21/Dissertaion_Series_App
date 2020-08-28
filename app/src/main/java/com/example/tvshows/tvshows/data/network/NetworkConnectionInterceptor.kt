package com.example.tvshows.data.network

import android.content.Context
import android.os.Build
import com.example.tvshows.utils.NoInternetException
import com.example.tvshows.data.netMethods
import okhttp3.Interceptor

import okhttp3.Response


class NetworkConnectionIncterceptor(context: Context) : Interceptor {

    private val applicationContext: Context = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        netMethods.hasInternet(applicationContext,false)
        return chain.proceed(chain.request())
    }
}