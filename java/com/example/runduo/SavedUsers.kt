package com.example.runduo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SavedUsers(val UserID: String, val NameUser: String, val ImageURL: String): Parcelable {
    constructor() : this("", "", "")
}