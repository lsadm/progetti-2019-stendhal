package com.example.stendhal_1

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.stendhal_1.datamodel.QuadroEmergente
import com.example.stendhal_1.datamodel.choosequery
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_artisti_emergenti.*

class artisti_emergenti : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("Quadri")
    private val database_emergente = database.child("Quadri_emergenti") //accedo al nodo dei quadri emergenti nel database
    private val TAG = "MainActivity"


//metodi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        choosequery=2
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artisti_emergenti, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")
        val quadroemergente = ArrayList<QuadroEmergente?>()
        //le chiavi mi serviranno per distinguere due eventuali quadri di due artisti diversi aventi lo stesso nome
        val keys = ArrayList<String>()
        val adapter = AdapterQuadri_emergenti(quadroemergente, requireContext())
            lista_quadri_emergenti.adapter = adapter

        //Listener per aggiornare la recycleView
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                // A new comment has been added, add it to the displayed list
                val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                quadroemergente.add(g)
                keys.add(dataSnapshot.key.toString()) //aggiungo le varie key in un vettore
                adapter.notifyItemInserted(quadroemergente.indexOf(g))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del quadro aggiornato
                quadroemergente[index]=g
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                val index = quadroemergente.indexOf(g)
                quadroemergente.remove(g)
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


            database_emergente.addChildEventListener(childEventListener)

        // Imposto il layout manager a lineare per avere scrolling in una direzione
        lista_quadri_emergenti.layoutManager = LinearLayoutManager(activity)


        imageDiventa_Artista.setOnClickListener {
            if (auth.currentUser != null) {
                Navigation.findNavController(it).navigate(R.id.action_to_fragment_Artista)
            } else {
                Navigation.findNavController(it).navigate(R.id.action_to_login)
            }
        }
    }

    //viene invocata nell'activity per effettuare le ricerche (l'activity passa la query come parametro)
    fun domyquery3(query: String) {
        val quadr_emer = ArrayList<QuadroEmergente?>()
        val keys = ArrayList<String>()
        val adapter = AdapterQuadri_emergenti(quadr_emer, requireContext())
        lista_quadri_emergenti.adapter = adapter

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                quadr_emer.add(g)
                keys.add(dataSnapshot.key.toString()) //Aggiungo le varie chiavi in un ArrayList
                adapter.notifyItemInserted(quadr_emer.indexOf(g))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del quadro aggiornato
                quadr_emer[index]= g
                adapter.notifyDataSetChanged()

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                val index = quadr_emer.indexOf(g)
                quadr_emer.remove(g)
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
        database_emergente.orderByChild("nome").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)

    }

}

