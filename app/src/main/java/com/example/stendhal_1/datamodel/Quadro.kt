package com.example.stendhal_1.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quadro(var nome:String? = null, var autore:String? = null, var anno:Int? = null,var spiegazione:String? = null,var periodoapp:String? = null): Parcelable