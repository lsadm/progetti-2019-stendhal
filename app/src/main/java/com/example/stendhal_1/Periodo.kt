package com.example.stendhal_1

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
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
        val keys = ArrayList<String>()
        val adapter = PeriodiAdapter(period,requireContext())
        list_periodi.adapter = adapter

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
                val g = dataSnapshot.getValue(com.example.stendhal_1.datamodel.Periodo::class.java)
                period.add(g)
                keys.add(dataSnapshot.key.toString()) //aggiungo le varie key in un vettore
                adapter.notifyItemInserted(period.indexOf(g))


                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val g = dataSnapshot.getValue(com.example.stendhal_1.datamodel.Periodo::class.java)
                val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del gioco aggiornato
                period[index]=g
                adapter.notifyDataSetChanged()
                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(com.example.stendhal_1.datamodel.Periodo::class.java)
                val index = period.indexOf(g)
                period.remove(g)
                adapter.notifyItemRemoved(index)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show()

            }
        }
        database.addChildEventListener(childEventListener)


        // Imposto il layout manager a lineare per avere scrolling in una direzione
        list_periodi.layoutManager = LinearLayoutManager(activity)

    }

}
