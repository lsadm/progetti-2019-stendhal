package com.example.stendhal_1.datamodel


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize                             //con parcelize riesco a passare un oggetto da un fragment all'altro
@Parcelize
//forma e campi che avrà ogni periodo artistico
data class Periodo(var nome:String,var anno:Int) : Parcelable