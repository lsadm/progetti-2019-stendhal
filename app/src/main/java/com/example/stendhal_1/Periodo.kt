package com.example.stendhal_1

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_periodi.*


class Periodo : Fragment() {
    //attributi

    private val database = FirebaseDatabase.getInstance().getReference("Periodo")
    private val TAG = "MainActivity"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_periodi, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*val v: View? = activity?.findViewById(R.id.bottomNavigation)
        v?.visibility=View.VISIBLE*/
        val period=ArrayList<com.example.stendhal_1.datamodel.Periodo?>()
        val adapter = PeriodiAdapter(period,requireContext())
        list_periodi.adapter = adapter

        //lettura periodi dal database (nodo periodi)
        val periodiListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                period.clear()
                snapshot.children.forEach {
                    val periodo : com.example.stendhal_1.datamodel.Periodo = it.getValue(com.example.stendhal_1.datamodel.Periodo::class.java)!!
                    period.add(periodo)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show()
            }
        }

        database.addValueEventListener(periodiListener)

        // Imposto il layout manager a lineare per avere scrolling in una direzione
        list_periodi.layoutManager = LinearLayoutManager(activity)
    }

}
