package com.jt17.neochat.utils

import android.app.Application
import com.orhanobut.hawk.Hawk

class App : Application() {
    companion object {
        var user1: String? = "null1"
        var user2: String? = "null2"

        var userPosition: Boolean = true
    }

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
    }
}