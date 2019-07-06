package com.example.stendhal_1.datamodel
//per la recycler view degli artisti emergenti utilizzerò questa classe perchè avrò un campo in più rispetto a quadro:Keys
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuadroEmergente(var nome:String? = null, var autore:String? = null, var anno:Int? = null,var spiegazione:String? = null, var key : String? = null,var id : String? = null): Parcelable
