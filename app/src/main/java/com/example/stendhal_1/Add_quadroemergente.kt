package com.example.stendhal_1

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.stendhal_1.datamodel.QuadroEmergente
import com.example.stendhal_1.datamodel.Utente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_add_quadroemergente.*
import java.io.ByteArrayOutputStream


class Add_quadroemergente : Fragment() {
    var mod = 0 //Indica se il quadro deve essere modificato oppure è un nuovo inserimento
    val database = FirebaseDatabase.getInstance().reference
    val storageRef = FirebaseStorage.getInstance().getReference()
    var autore : String? = null
    var quadro_emergente : QuadroEmergente? =null
    var foto_fatte : Int =0
    var foto_caricate : Int = 0
    val REQUEST_IMAGE_CAPTURE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_add_quadroemergente, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?){
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.tick_button, menu) //Visualizzo solo la spunta per la conferma dell'inserimento
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo

        //Nel caso in cui si voglia modificare un quadro già esistente:
        // (In questo caso arriviamo in questo fragment a partire dal fragment dettaglio_quadro)
        arguments?.let{
            mod = 1
            //Estraggo il quadro dal bundle _ Il put si trova in Dettaglio_quadro - Nella parte della modifica in onOptionsItemSelected
            quadro_emergente = it.getParcelable("quadro")
            quadro_emergente?.let {
                //Nella schermata di modifica usciranno nelle varie textview, i valori del quadro emergente che si vuole modificare
                autore=quadro_emergente?.autore
                Nome.setText(quadro_emergente?.nome)
                anno.setText(quadro_emergente?.anno.toString())
                Spiegazione.setText(quadro_emergente?.spiegazione)

                //Scarica la foto del quadro dal database e la inserisce nell' ImageButton foto_quadro del fragment add_quadroemergente
                download_foto()
                (activity as AppCompatActivity).supportActionBar?.setTitle("Modifica la tua opera")
            }
        }


        //Nel caso in cui debba inserire un nuovo quadro:
        // (In questo caso arriviamo in questo fragment a partire dal fragment_artista mediante il floating button)
        if(mod!=1) {
            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
            (activity as AppCompatActivity).supportActionBar?.setTitle("Aggiungi la tua opera")
        }

        // Imposta il funzionamento del pulsante per l'acquisizione dell'immagine - Sia per la modifica che per l'inserimento
        val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        foto_quadro.setOnClickListener {
            takePhoto.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE)
            }
        }

    }

    //Quando si clicca il tick_button:
        override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid

        //Nel caso di inserimento, leggo dal database l'autore corrente
        val leggiautore = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Utente? = snapshot.getValue(Utente::class.java)
                val autore_temp = user?.nome
                autore = autore_temp

                //NB: sto in ''Quando si clicca il tick_button''
                val nome = Nome.text.toString()
                val anno = anno.text.toString()
                val spiegazione = Spiegazione.text.toString()
                var key: String?
                quadro_emergente?.nome = nome
                quadro_emergente?.spiegazione = spiegazione
                quadro_emergente?.anno = anno.toInt()

                //INSERISCO IL QUADRO ALL'INTERNO DEL DATABASE - SEZIONE QUADRI_EMERGENTI
                if (nome.length > 0 && spiegazione.length > 0 && anno.toInt() > 0 && id != null) {

                    key = get_key()
                    database.child("Quadri/Quadri_emergenti").child(key.toString()).setValue(
                        QuadroEmergente(nome,autore,anno.toInt(),spiegazione,key,id)
                    )

                    ////INSERISCO IL QUADRO ALL'INTERNO DEL DATABASE - SEZIONE SINGOLOUTENTE/MIE_OPERE
                    database.child("Utenti").child(id).child("Mie_opere").child(key.toString()).setValue(
                        QuadroEmergente(nome,autore,anno.toInt(),spiegazione,key,id)
                    )

                    Toast.makeText(activity, "Inserimento di un nuovo quadro", Toast.LENGTH_SHORT).show()

                    if (foto_fatte != 0) caricaFoto(key.toString()) //Carico la foto
                    else Navigation.findNavController(view!!).navigateUp() //altrimenti torno semplicemente indietro
                }

                //Se alcuni campi sono vuoti non posso caricare il quadro
                else Toast.makeText(activity, "Riempire tutti i campi", Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show()
            }
        }


        //Solo se devo inserire un nuovo quadro effettuo l'addValueEventListener di leggiautore
        if (mod!=1) database.child("Utenti/" + id + "/Dati/Account/").addValueEventListener(leggiautore)


        else{
        val nome = Nome.text.toString()
        val anno = anno.text.toString()
        val spiegazione = Spiegazione.text.toString()
        var key: String?
        quadro_emergente?.nome = nome
        quadro_emergente?.spiegazione = spiegazione
        quadro_emergente?.anno = anno.toInt()

            if (nome.length > 0 && spiegazione.length > 0 && anno.toInt() > 0 && id != null) {
                key = get_key()

                //INSERISCO IL QUADRO ALL'INTERNO DEL DATABASE - SEZIONE QUADRI_EMERGENTI
                database.child("Quadri/Quadri_emergenti").child(key.toString()).setValue(
                    QuadroEmergente(nome,autore,anno.toInt(),spiegazione,key,id)
                )

                ////INSERISCO IL QUADRO ALL'INTERNO DEL DATABASE - SEZIONE SINGOLOUTENTE/MIE_OPERE
                database.child("Utenti").child(id).child("Mie_opere").child(key.toString()).setValue(
                    QuadroEmergente(nome,autore,anno.toInt(),spiegazione,key,id)
                )

                Toast.makeText(activity, "Modifica in corso", Toast.LENGTH_SHORT).show()

                //Foto da caricare
                if (foto_fatte != 0) caricaFoto(key.toString()) //le carico
                else Navigation.findNavController(view!!).navigateUp() //altrimenti torno semplicemente indietro
            }
            //se alcuni campi sono vuoti non posso caricare il quadro
            else Toast.makeText(activity, "Mancanza di alcuni campi", Toast.LENGTH_SHORT).show()

        }

        return super.onOptionsItemSelected(item)
    }


    //Scarica la foto del quadro dal database e la inserisce nell' ImageButton foto_quadro
    private fun download_foto() {
            val imagRef = storageRef.child("Quadri_emergenti").child(quadro_emergente?.key.toString() + "/")

            imagRef.child("Image").downloadUrl.addOnSuccessListener {
                GlideApp.with(this).load(it).into(foto_quadro)
            }.addOnFailureListener {
                // Handle any errors
            }
        }


    //Carica la foto sul database
    private fun caricaFoto(key : String){
            //Path su su Firebase_storage per il caricamento della foto
            val mountainsRef = storageRef.child("Quadri_emergenti").child(key).child("Image")
            //Conversione da drawable a bitmap
            val bitmap = (foto_quadro?.drawable as? BitmapDrawable)?.bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG,100, baos)
            val data = baos.toByteArray()

            if(data.isNotEmpty()) {
                var uploadTask = mountainsRef.putBytes(data) //carica i byte della foto
                uploadTask.addOnCompleteListener {
                    foto_caricate++
                    if(it.isSuccessful && foto_caricate==foto_fatte) Navigation.findNavController(view!!).navigateUp()
                }.addOnFailureListener {
                    Toast.makeText(activity, "Foto non inserita correttamente", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {}
            }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {// Acquisizione immagine
            val immagineCatturata = data?.extras?.get("data") as Bitmap
                foto_quadro.setImageBitmap(immagineCatturata)
            }
            if(foto_fatte<1) foto_fatte=1
        }


    //Quando esco dall'inserimento ritorna la scritta Stendhal
    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")
    }

    //get_key effettua operazioni differenti in base al valore assunto dalla variabile mod (per distinguere i due casi: creazione e modifica)
    private fun get_key() : String? {
        if(mod==0) {
            return database.child("Quadri/Quadri_emergenti").push().key  //Questa push mi restituisce un identificativo unico
        }
        else {
            return quadro_emergente?.key
        }
    }

}
