package com.example.stendhal_1

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.storage.FirebaseStorage
import com.example.stendhal_1.datamodel.Periodo
import kotlinx.android.synthetic.main.fragment_singolo_periodo.view.*

class PeriodiAdapter(val dataset: ArrayList<Periodo?>, val context: Context) : RecyclerView.Adapter<rigaperiodoviewholder>() {

    // Invocata per creare un ViewHolder
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): rigaperiodoviewholder {
        // Crea e restituisce un viewholder, effettuando l'inflate del layout relativo alla riga
        return rigaperiodoviewholder(LayoutInflater.from(context).inflate(R.layout.riga_periodo, viewGroup, false))
    }

    // Invocata per conoscere quanti elementi contiene il dataset
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Invocata per visualizzare all'interno del ViewHolder il dato corrispondente alla riga
    override fun onBindViewHolder(viewHolder: rigaperiodoviewholder, position: Int) {
        val periodo = dataset.get(position)
        val anno_string = periodo?.anno.toString()

        viewHolder.tvNome.text = periodo?.nome
        viewHolder.tvAnno.text = anno_string

        viewHolder.itemView.setOnClickListener {
            val b = Bundle()
            b.putParcelable("periodo",periodo)
            Navigation.findNavController(it).navigate(R.id.action_to_SingoloPeriodo, b)
        }
    }
}