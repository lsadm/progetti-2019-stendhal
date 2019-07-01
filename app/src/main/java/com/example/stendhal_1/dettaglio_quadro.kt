package com.example.stendhal_1

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.stendhal_1.datamodel.Quadro
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_dettaglio_quadro.*

class dettaglio_quadro : Fragment() {

    private var quadro : Quadro? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dettaglio_quadro, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //impedisce la rotazione dello schermo

        // Estraggo il parametro (quadro) dal bundle ed eventualmente lo visualizzo
    //visualizzo il dettaglio
        arguments?.let {
            quadro = it.getParcelable("quadro")
            quadro?.let {

                nome_quadro.text = it.nome
                autore_quadro.text = it.autore
                spiegazione.text = it.spiegazione
                val annoquadro= String.format("%d", it.anno)
                anno_quadro.text = annoquadro
                //legge i valori del quadro dal database utilizzando l'oggetto passato col bundle
               // val imagRef = storageRef.child(quadro?.periodoapp.toString() + "/").child(gioco?.key.toString() + "/")
                //val myRef = FirebaseDatabase.getInstance().getReference("Quadri")
                //val databasemio = myRef.child("Quadri_storici/"+quadro.periodoapp!!)

            }
        }



        //picture0.setOnClickListener {
            //zoomFoto(picture0)
        }
}

