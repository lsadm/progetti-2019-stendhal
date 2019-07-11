package com.example.stendhal_1

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.example.stendhal_1.datamodel.QuadroEmergente



    class AdapterQuadri_emergenti(val dataset: ArrayList<QuadroEmergente?>, val context: Context) : RecyclerView.Adapter<rigaquadroviewholder>() {
        //Riferimento allo storage di Firebase per il caricamento della foto
        val storageRef = FirebaseStorage.getInstance().getReference()

        // Invocata per creare un ViewHolder
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): rigaquadroviewholder {
            // Crea e restituisce un viewholder
            return rigaquadroviewholder(LayoutInflater.from(context).inflate(R.layout.riga_quadro, viewGroup, false))
        }

        // Invocata per conoscere quanti elementi contiene il dataset
        override fun getItemCount(): Int {
            return dataset.size
        }

        // Invocata per visualizzare all'interno del ViewHolder il dato corrispondente alla riga
        override fun onBindViewHolder(viewHolder: rigaquadroviewholder, position: Int) {
            val quadro = dataset.get(position)
            val imagRef = storageRef.child("Quadri_emergenti").child(quadro?.key.toString()).child("Image")
            val anno_string = quadro?.anno.toString()


            viewHolder.tvNome.text = quadro?.nome
            viewHolder.tvAutore.text = quadro?.autore
            viewHolder.tvAnno.text = anno_string

            //Scarica la foto dal database e la setta nella riga
            imagRef.downloadUrl.addOnSuccessListener {
                GlideApp.with(context).load(it).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(viewHolder.Immagine)
            }

            //Nel momento in cui selezione una view passo attraverso un bundle un quadro emergente
                viewHolder.itemView.setOnClickListener {
                    val b = Bundle()
                    b.putParcelable("quadroemer", quadro)
                    Navigation.findNavController(it).navigate(R.id.action_to_dettaglio_quadro, b)
                }

            }
        }