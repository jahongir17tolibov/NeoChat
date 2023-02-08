package com.jt17.neochat.view.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageModel(
    var user: String = "",
    var message: String = "",
    var time: Long = 0,
    var img: String? = null,
    var junatuvciUnId: String = "",
) : Parcelable
