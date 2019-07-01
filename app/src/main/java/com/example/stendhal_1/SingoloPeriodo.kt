package com.example.stendhal_1

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.stendhal_1.datamodel.Quadro
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_singolo_periodo.*

class SingoloPeriodo : Fragment() {


    private val TAG = "MainActivity" ///////////Rivedi, che cosa è?

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_singolo_periodo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val quadr=ArrayList<Quadro?>()
        val adapter = QuadriAdapter(quadr,requireContext())
        list_quadri.adapter = adapter

        arguments?.let {
            val periodo: com.example.stendhal_1.datamodel.Periodo? = it.getParcelable("periodo") //Estrazione dal bundle - ottengo periodo per il nome
            periodo?.let {
                val quadrilistener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        quadr.clear()
                        snapshot.children.forEach {
                            val quadro : Quadro = it.getValue(Quadro::class.java)!!
                            quadr.add(quadro)
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                        Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show()
                    }
                }

                val database = FirebaseDatabase.getInstance().getReference("Quadri")
                val databasemio = database.child("Quadri_storici/"+periodo.nome!!) //In base al periodo selezionato verrà mostrato un sottonodo diverso del database

                databasemio.addValueEventListener(quadrilistener) //////////////RIVEDIIIII

                // Imposto il layout manager a lineare per avere scrolling in una direzione
                list_quadri.layoutManager = LinearLayoutManager(activity)

            }

                //Log.v("Prova", periodo.nome);
            }
        }

    }

