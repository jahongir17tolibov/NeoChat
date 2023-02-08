package com.jt17.neochat.utils

import java.text.SimpleDateFormat
import java.util.*

object DataUtils {
    fun fromMillsToTimeString(mills: Long): String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(mills)
    }

    fun getUpLoadImgFileName(mills: Date): String {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        return formatter.format(mills)
    }
}