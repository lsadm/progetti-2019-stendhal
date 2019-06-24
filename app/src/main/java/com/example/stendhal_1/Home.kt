package com.example.stendhal_1

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_home.*


class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    //vedi bene
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageButton_periodi.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_home_to_periodi)
        }
        imageButton_quadrodelgiorno.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.action_home_to_quadro_giorno)
            }
        imageButton_quadri_artisti_emergenti.setOnClickListener {
                    Navigation.findNavController(view).navigate(R.id.action_home_to_artisti_emergenti)
                }
     }
}

