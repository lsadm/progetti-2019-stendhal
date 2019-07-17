package com.example.stendhal_1

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import androidx.navigation.Navigation
import com.example.stendhal_1.datamodel.bidirezione
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*


class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")
        setHasOptionsMenu(true)


        imageButton_periodi.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_home_to_periodi)
        }
        imageButton_quadrodelgiorno.setOnClickListener {
                bidirezione=true
                Navigation.findNavController(it).navigate(R.id.action_to_dettaglio_quadro)

            }
        imageButton_quadri_artisti_emergenti.setOnClickListener {
                    Navigation.findNavController(view).navigate(R.id.action_home_to_artisti_emergenti)
                }
     }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        val usr = FirebaseAuth.getInstance().currentUser
        if(usr==null) { //Se non vi è un utente loggato, visualizziamo il button_login
            inflater?.inflate(R.menu.button_login, menu)
        }
        else { //Altrimenti button_logout
            inflater?.inflate(R.menu.button_logout, menu)
        }
    }
}

