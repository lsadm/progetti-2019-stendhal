package com.example.stendhal_1

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ImageView
import com.example.stendhal_1.datamodel.Quadro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_dettaglio_quadro.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class dettaglio_quadro : Fragment() {


    private var quadro: Quadro? = null
    private val storageRef = FirebaseStorage.getInstance().getReference()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dettaglio_quadro, container, false)
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
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")
        setHasOptionsMenu(true)
        activity?.requestedOrientation =
            (ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //impedisce la rotazione dello schermo

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
                val imagRef = storageRef.child("Quadri_storici/").child(quadro?.periodoapp.toString() + "/")
                    .child(quadro?.nome.toString() + ".jpg")

                //legge i valori del quadro dal database utilizzando l'oggetto passato col bundle
                // val imagRef = storageRef.child(quadro?.periodoapp.toString() + "/").child(gioco?.key.toString() + "/")
                //val myRef = FirebaseDatabase.getInstance().getReference("Quadri")

                downloadFoto(imagRef)
            }
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


