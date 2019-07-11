package com.example.stendhal_1.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Periodo(var nome:String? = null,var anno:Int? = null):Parcelable


