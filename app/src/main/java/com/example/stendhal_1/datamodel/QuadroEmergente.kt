package com.example.stendhal_1.datamodel

//Per la recycler view degli artisti emergenti utilizzerò questa classe perchè avrò due campi in più rispetto a quadro:Key e ID
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuadroEmergente(var nome:String? = null, var autore:String? = null, var anno:Int? = null,var spiegazione:String? = null, var key : String? = null,var id : String? = null): Parcelable

//L'id serve a capire l'autore di ciascun quadro in quanto coincide con l'uid di quest'ultimo (E' un utente registrato)