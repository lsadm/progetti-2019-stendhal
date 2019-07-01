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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        arguments?.let {
            quadro = it.getParcelable("quadro")
            quadro?.let {

                //legge i valori del quadro dal database utilizzando l'oggetto passato col bundle
               // val imagRef = storageRef.child(quadro?.periodoapp.toString() + "/").child(gioco?.key.toString() + "/")
                val myRef = FirebaseDatabase.getInstance().getReference("Quadri")
                val databasemio = myRef.child("Quadri_storici/"+quadro.periodoapp!!)

                fun loadList(callback: (list: List<Quadro>) -> Unit) {
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshotError: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list : MutableList<Quadro> = mutableListOf()
                            val children = snapshot!!.children
                            children.forEach {
                                list.add(it.getValue(Quadro::class.java)!!)
                            }
                            callback(list)
                        }
                    })
                }
                loadList {
                    nome_dettaglio.text = it.get(it.indexOf(gioco!!)).nome
                    luogo_dettaglio.text = it.get(it.indexOf(gioco!!)).luogo
                    val prezzoEuro = String.format("%d", it[it.indexOf(gioco!!)].prezzo)+"€"
                    prezzo_dettaglio.text = prezzoEuro
                }

                val childEventListener = object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                        val usr = dataSnapshot.getValue(User::class.java)
                        try {
                            utente_dettaglio.text = usr?.email
                            cellulare_dettaglio.text = usr?.cell
                        }catch(e : Exception) {}

                    }
                    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                        Toast.makeText(context, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                nodoRef.child("users").child(gioco?.id.toString()).child("Dati").addChildEventListener(childEventListener)
                downloadFoto(imagRef)
            }
        }

        //invio email
        utente_dettaglio.setOnClickListener {
            //get input from EditTexts and save in variables
            val recipient = utente_dettaglio.text.toString().trim()
            val subject = "BuyGames".trim()
            val message = "Ciao".trim()
            //method call for email intent with these inputs as parameters
            sendEmail(recipient, subject, message)
        }
        //chiamata cell
        cellulare_dettaglio.setOnClickListener {
            val seller = cellulare_dettaglio.text.toString()
            callCell(seller)
        }
        picture0.setOnClickListener {
            zoomFoto(picture0)
        }
        picture1.setOnClickListener {
            zoomFoto(picture1)
        }
        picture2.setOnClickListener {
            zoomFoto(picture2)
        }
    }
}
