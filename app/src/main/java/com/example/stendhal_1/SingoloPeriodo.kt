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
import android.view.*
import android.widget.Toast
import com.example.stendhal_1.datamodel.Periodo
import com.example.stendhal_1.datamodel.Quadro
import com.example.stendhal_1.datamodel.choosequery
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_singolo_periodo.*

class SingoloPeriodo : Fragment() {


    private val TAG = "MainActivity"
    private val database = FirebaseDatabase.getInstance().getReference("Quadri/Quadri_storici")
    private var periodoquery : Periodo?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        choosequery=1
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_singolo_periodo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")

        val quadr=ArrayList<Quadro?>()
        val adapter = QuadriAdapter(quadr,requireContext())
        list_quadri.adapter = adapter

        arguments?.let {
            val periodo: com.example.stendhal_1.datamodel.Periodo? = it.getParcelable("periodo") //Estrazione dal bundle - ottengo periodo per il nome del sottonodo
            periodo?.let {
                periodoquery=periodo
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

                databasemio.addValueEventListener(quadrilistener)

                list_quadri.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                // Imposto il layout manager a lineare per avere scrolling in una direzione
                list_quadri.layoutManager = LinearLayoutManager(activity)


                }
            }

            }

    fun domyquery2(query: String) {
        val quadr = ArrayList<Quadro?>()
        val adapter = QuadriAdapter(quadr, requireContext())
        list_quadri.adapter = adapter

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(Quadro::class.java)
                quadr.add(g)

                adapter.notifyItemInserted(quadr.indexOf(g))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                adapter.notifyDataSetChanged()

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val g = dataSnapshot.getValue(Quadro::class.java)
                val index = quadr.indexOf(g)
                quadr.remove(g)
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

        val database_query= database.child(periodoquery!!.nome!!)
        database_query.orderByChild("nome").startAt(query).endAt(query+"\uf8ff").addChildEventListener(childEventListener)  //la query effettuerà la ricerca a partire dal nome
        Log.d("TAG","il nome è"+ (periodoquery?.nome))
    }

    }

