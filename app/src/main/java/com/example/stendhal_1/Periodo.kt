package com.example.stendhal_1

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.stendhal_1.datamodel.Periodo
import com.example.stendhal_1.datamodel.choosequery
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_periodi.*


class Periodo : Fragment() {

    private val database = FirebaseDatabase.getInstance().getReference("Periodo")
    private val TAG = "MainActivity"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        choosequery=0
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_periodi, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")

        val period = ArrayList<com.example.stendhal_1.datamodel.Periodo?>()
        val adapter = PeriodiAdapter(period, requireContext())
        list_periodi.adapter = adapter

        //lettura periodi dal database (nodo periodi)

        val periodiListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                period.clear()
                snapshot.children.forEach {
                    val periodo: com.example.stendhal_1.datamodel.Periodo =
                        it.getValue(com.example.stendhal_1.datamodel.Periodo::class.java)!!
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


       list_periodi.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Imposto il layout manager a lineare per avere scrolling in una direzione
        list_periodi.layoutManager = LinearLayoutManager(activity)
    }


    fun domyquery(query: String) {
        val period = ArrayList<Periodo?>()
        val adapter = PeriodiAdapter(period, requireContext())
        list_periodi.adapter = adapter

        val childEventListener = object : ChildEventListener {
           override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
               val g = dataSnapshot.getValue(Periodo::class.java)
               period.add(g)

               adapter.notifyItemInserted(period.indexOf(g))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                adapter.notifyDataSetChanged()

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(Periodo::class.java)
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


            database.orderByChild("nome").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)
            //la query effettuerà la ricerca a partire dal nome

    }
}


