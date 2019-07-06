package com.example.stendhal_1

import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.stendhal_1.datamodel.QuadroEmergente
import com.example.stendhal_1.datamodel.Utente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_fragment__artista.*


class Fragment_Artista : Fragment() {

        //attributi
        private val database = FirebaseDatabase.getInstance().getReference("Utenti")
        private val auth = FirebaseAuth.getInstance()
        private val user = FirebaseAuth.getInstance().currentUser?.uid //Questo è l'id dell'utente
        private var singoloutente:Utente? = null

        //metodi

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setHasOptionsMenu(true) //avvisa che deve essere invocata la funzione onCreateOptionsMenu
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_fragment__artista, container, false)
        }

        override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
            super.onCreateOptionsMenu(menu, inflater)
            menu?.removeItem(R.id.app_bar_search) //rimuove l'action bar dall'area personale
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //impedisce la rotazione dello schermo
            super.onViewCreated(view, savedInstanceState)
            //setto colore e titolo dell'action bar
            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
            (activity as AppCompatActivity).supportActionBar?.title="Diventa un artista"

            //divide le varie righe della recycleView
            lista_quadriemergenti.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

            val emerg=ArrayList<QuadroEmergente?>()
            val keys = ArrayList<String>()
            val adapter = AdapterQuadri_emergenti(emerg,requireContext())
            lista_quadriemergenti.adapter = adapter
            var cont=0 //contatore di righe inserite nella recycleView

            //setta la textView annunci col valore di cont, inizialmente a 0
            n_opere.text=cont.toString()

            //Vari listener (di Firebase) per aggiornare dinamicamente la recyclerView
            val childEventListener = object : ChildEventListener {
                //inserimento elemento
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                    // A new comment has been added, add it to the displayed list
                    val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                    emerg.add(g)
                    keys.add(dataSnapshot.key.toString()) //aggiungo le varie key in un vettore
                    adapter.notifyItemInserted(emerg.indexOf(g))
                    cont++
                    try { n_opere.text=cont.toString() }catch(e:Exception) {}
                }
                //modifica elemento
                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                    val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                    val index = keys.indexOf(dataSnapshot.key.toString()) //ottengo l'indice del gioco aggiornato
                    emerg.set(index,g)
                    adapter.notifyDataSetChanged()
                }
                //rimozione elemento
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                    val g = dataSnapshot.getValue(QuadroEmergente::class.java)
                    val index = emerg.indexOf(g)
                    emerg.remove(g)
                    adapter.notifyItemRemoved(index)
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

            // Imposto il layout manager a lineare per avere scrolling in una direzione
            lista_quadriemergenti.layoutManager = LinearLayoutManager(activity)
            floatingActionButton.setOnClickListener {
                if (auth.currentUser != null) {
                    Navigation.findNavController(it).navigate(R.id.action_to_add_quadroemergente)
                } else {
                    Navigation.findNavController(it).navigate(R.id.action_to_login)
                }
            }

            if(user!=null) {
                //scarico dal database le informazioni del singolo utente e le aggiungo in una lista
                dataUser()
                //chiamata al listener per caricare e modificare la recycleView
                database.child(user.toString()).child("Mie_opere").addChildEventListener(childEventListener) //il database da cui chiamo il listener fa variare il sottonodo del database che vado a leggere
            }
            //l'utente non è loggato quindi viene reindirizzato al login
            else {
                Toast.makeText(activity, "Non sei loggato", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view).navigateUp()
                Navigation.findNavController(view).navigate(R.id.action_to_login)
            }
        }

        //quando lascio il fragment abilito la rotazione
        override fun onDestroyView() {
            super.onDestroyView()
            activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
        }

        //scarica i dati dell'utente dal database
        private fun dataUser() {
            val leggoemail = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ut:Utente? = snapshot.getValue(Utente::class.java)
                    singoloutente=ut
                    emailpersonale.text = singoloutente?.email
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                    Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show()
                }
            }
            database.child(user.toString()).child("Dati/Account").addValueEventListener(leggoemail)

        }
    }

