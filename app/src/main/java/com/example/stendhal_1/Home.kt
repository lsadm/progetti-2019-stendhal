package com.example.stendhal_1

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import androidx.navigation.Navigation
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


    //vedi bene
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")

        //con questo metodo nascondo search bar
        setHasOptionsMenu(true)



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
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        val usr = FirebaseAuth.getInstance().currentUser
        if(usr==null) { //se non è loggato esce login
            inflater?.inflate(R.menu.button_login, menu)

        }
        else { //altrimenti logout
            inflater?.inflate(R.menu.button_logout, menu)
            //inflater?.inflate(R.menu.search, menu)
        }
    }
}

