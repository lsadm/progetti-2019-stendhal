package com.example.stendhal_1

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.stendhal_1.datamodel.Quadro
import com.example.stendhal_1.datamodel.QuadroEmergente
import com.example.stendhal_1.datamodel.bidirezione
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_dettaglio_quadro.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class dettaglio_quadro : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("Quadri")
    private val database_quadriemergenti = database.child("Quadri_emergenti")
    private val nodo = FirebaseDatabase.getInstance().reference
    private val id = auth.currentUser?.uid.toString()
    private var quadro: Quadro?=null
    private var quadroemer: QuadroEmergente?=null
    private val TAG = "MainActivity"
    private val storageRef = FirebaseStorage.getInstance().getReference()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dettaglio_quadro, container, false)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.Cestino  -> {
                //finestra di dialog per eliminare gioco
                val builder = AlertDialog.Builder(activity as AppCompatActivity)
                builder.setTitle(R.string.AlertMessage)
                builder.setNegativeButton(R.string.NegativeButton, DialogInterface.OnClickListener { _, which -> }) //chiude la finestra
                builder.setPositiveButton(R.string.PositiveButton, DialogInterface.OnClickListener { _, which ->
                    deleteGame() //elimina il gioco
                    Navigation.findNavController(view!!).navigateUp() //e torna indietro
                })
                builder.show() //mostra la finestra
            }
            R.id.Modifica -> {
                //crea un bundle e lo passa al fragment inserimento, poi lì verrà modificato
                val b = Bundle()
                b.putParcelable("quadro",quadroemer)
                Navigation.findNavController(view!!).navigate(R.id.action_to_add_quadroemergente,b)
            }
            else -> return false
        }
        return true
    }



    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

            super.onCreateOptionsMenu(menu, inflater)
            menu?.clear()
            if (quadroemer?.id == id) {
                inflater?.inflate(R.menu.menu_modifica, menu) //viene mostrato solo il menu modifica


    }}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        fun getItemCount(array: ArrayList<QuadroEmergente?>): Int {
            return array.size
        }


        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")
        setHasOptionsMenu(true)
        activity?.requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //impedisce la rotazione dello schermo
        val quadremer = ArrayList<QuadroEmergente?>()
        // Estraggo il parametro (quadro) dal bundle e lo visualizzo
        //visualizzo il dettaglio
        arguments?.let {
            quadro = it.getParcelable("quadro")
            quadro?.let {
                nome_quadro.text = it.nome
                autore_quadro.text = it.autore
                spiegazione.text = it.spiegazione
                val annoquadro = String.format("%d", it.anno)
                anno_quadro.text = annoquadro
                val imagRef = storageRef.child("Quadri_storici/").child(quadro?.periodoapp.toString() + "/").child(quadro?.nome.toString() + ".jpg")
                downloadFoto(imagRef)
                }

            quadroemer = it.getParcelable("quadroemer")
            quadroemer?.let {
                nome_quadro.text = it.nome
                autore_quadro.text = it.autore
                spiegazione.text = it.spiegazione
                val annoquadro = String.format("%d", it.anno)
                anno_quadro.text = annoquadro
                val imagRef = storageRef.child("Quadri_emergenti/").child(quadroemer?.key.toString()).child("Image")
                downloadFoto(imagRef)
            }

            }

        //PER QUADRO EMERGENTE CAUSALE
        val quadroemergenteListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Leggo dal database tutti i quadri emergenti e li metto in quadremer che è un ArrayList
                quadremer.clear()
                snapshot.children.forEach {
                    val quadrosingolo:QuadroEmergente = it.getValue(QuadroEmergente::class.java)!!
                    quadremer.add(quadrosingolo)
                }

                val numeroelementi = getItemCount(quadremer)
                val ran = (1..numeroelementi).random()
                val quadro:QuadroEmergente?=quadremer.get(ran)
                nome_quadro.text = quadro?.nome
                autore_quadro.text = quadro?.autore
                spiegazione.text = quadro?.spiegazione
                val annoquadro = String.format("%d", quadro?.anno)
                anno_quadro.text = annoquadro
                val imagRef = storageRef.child("Quadri_emergenti").child(quadro?.key.toString()).child("Image")
                downloadFoto(imagRef)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show()
            }
        }
        if (bidirezione==true){
            database_quadriemergenti.addValueEventListener(quadroemergenteListener)
            bidirezione=false
        }
        picture.setOnClickListener {
            zoomFoto(picture)
        }

    }





    private fun downloadFoto(imagRef : StorageReference) {
            imagRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                // Use the bytes to display the image
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                picture.setImageBitmap(bitmap)
            }.addOnFailureListener {
                // Handle any errors
            }
        }



    private fun deleteGame() {
        database_quadriemergenti.child(quadroemer!!.key.toString()).removeValue()
        nodo.child("Utenti").child(auth.currentUser!!.uid).child("Mie_opere").child(quadroemer!!.key.toString()).removeValue()
        storageRef.child("Quadri_emergenti").child(quadroemer!!.key.toString()).child("Image").delete()

    }

    private fun zoomFoto(img : ImageView) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.foto, null)
        dialogBuilder.setView(dialogView)
        val imageview = dialogView.findViewById(R.id.imageView) as ImageView
        val bitmap = (img.drawable as? BitmapDrawable)?.bitmap
        imageview.setImageBitmap(bitmap)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


}


