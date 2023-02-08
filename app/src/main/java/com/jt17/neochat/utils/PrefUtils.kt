package com.jt17.neochat.utils

import com.orhanobut.hawk.Hawk

object PrefUtils {
    private const val REGISTER_KEY = "register_success"

    var firstRegister = "" //user name
        get() = Hawk.get(REGISTER_KEY, "")
        set(value) {
            Hawk.put(REGISTER_KEY, value)
            field = value
        }
}