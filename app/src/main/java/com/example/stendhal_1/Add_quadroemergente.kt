package com.example.stendhal_1

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.stendhal_1.datamodel.Quadro
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
    var mod = 0 //indica se il quadro deve essere modificato
    val database = FirebaseDatabase.getInstance().reference
    val storageRef = FirebaseStorage.getInstance().getReference()
    var autore : String? = null
    var foto : ImageButton?= null
    var quadro_emergente : QuadroEmergente? =null
    var foto_fatte : Int =0
    var foto_caricate : Int = 0
    val REQUEST_IMAGE_CAPTURE = 1


    //metodi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //aggiungo questa riga per aggiungere un riferimento al menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_add_quadroemergente, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?){
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.tick_button, menu) //visualizzo solo la spunta per l'inserimento
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //impedisce la rotazione dello schermo

        //Nel caso in cui si voglia modificare un quadro già esistente:
        arguments?.let{
            //modifico il gioco
            mod = 1
            //estraggo il gioco dal bundle
            quadro_emergente = it.getParcelable("quadro")
            quadro_emergente?.let {
                //e mostro nel dettaglio i suoi attributi
                autore=quadro_emergente?.autore
                Nome.setText(quadro_emergente?.nome)
                anno.setText(quadro_emergente?.anno.toString())
                Spiegazione.setText(quadro_emergente?.spiegazione)
                download_foto() //scarico le foto e le inserisco negli imageButton
                (activity as AppCompatActivity).supportActionBar?.setTitle("Modifica la tua opera")
            }
        }


        //Nel caso in cui debba inserire un nuovo quadro:
        if(mod!=1) {
            (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
            (activity as AppCompatActivity).supportActionBar?.setTitle("Aggiungi la tua opera")
        }

        // Imposta il funzionamento del pulsante per l'acqisizione dell'immagine
        val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        foto_quadro.setOnClickListener {
            // Creo un intent di tipo implicito per acquisire l'immagine
            takePhoto.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    //Quando si clicca il tick_button:
        override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val nome = Nome.text.toString()
        val auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid

        ///
        val leggiautore = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Utente? = snapshot.getValue(Utente::class.java)
                val autore_temp = user?.nome
                autore = autore_temp
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
                        QuadroEmergente(
                            nome,
                            autore,
                            anno.toInt(),
                            spiegazione,
                            key,
                            id
                        )
                    )

                    ////INSERISCO IL QUADRO ALL'INTERNO DEL DATABASE - SEZIONE SINGOLOUTENTE/MIE_OPERE
                    database.child("Utenti").child(id).child("Mie_opere").child(key.toString()).setValue(
                        QuadroEmergente(
                            nome,
                            autore,
                            anno.toInt(),
                            spiegazione,
                            key,
                            id
                        )
                    )

                    Toast.makeText(activity, "Caricamento in corso", Toast.LENGTH_SHORT).show()

                    if (foto_fatte != 0) caricaFoto(key.toString()) //le carico
                    else Navigation.findNavController(view!!).navigateUp() //altrimenti torno semplicemente indietro
                }
                //se alcuni campi sono vuoti non posso caricare il gioco
                else Toast.makeText(activity, "Riempire campi", Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show()
            }
        }
        //
        //
        database.child("Utenti/" + id + "/Dati/Account/").addValueEventListener(leggiautore)

        if (mod == 1) {

        val anno = anno.text.toString()
        val spiegazione = Spiegazione.text.toString()
        var key: String?
        quadro_emergente?.nome = nome
        quadro_emergente?.spiegazione = spiegazione
        quadro_emergente?.anno = anno.toInt()

        //inserisco il quadro nel catologo dei quadri degli artisti emergenti

            if (nome.length > 0 && spiegazione.length > 0 && anno.toInt() > 0 && id != null) {
                key = get_key()
                database.child("Quadri/Quadri_emergenti").child(key.toString()).setValue(
                    QuadroEmergente(
                        nome,
                        autore,
                        anno.toInt(),
                        spiegazione,
                        key,
                        id
                    )
                ) //e nell'area personale dell'utente
                //in quel percorso con identificativo unico inserisco il gioco. Rappresenta la lista di giochi visibile a tutti
                database.child("Utenti").child(id).child("Mie_opere").child(key.toString()).setValue(
                    QuadroEmergente(
                        nome,
                        autore,
                        anno.toInt(),
                        spiegazione,
                        key,
                        id
                    )
                )
                Toast.makeText(activity, "Caricamento in corso", Toast.LENGTH_SHORT).show()
                //se ci sono foto da caricare
                if (foto_fatte != 0) caricaFoto(key.toString()) //le carico
                else Navigation.findNavController(view!!).navigateUp() //altrimenti torno semplicemente indietro
            }
            //se alcuni campi sono vuoti non posso caricare il gioco
            else Toast.makeText(activity, "Riempire campi", Toast.LENGTH_SHORT).show()

        }

        return super.onOptionsItemSelected(item)
    }


    //scarica foto dal database e le inserisce negli ImageButton
    private fun download_foto() {
            val imagRef = storageRef.child("Quadri_emergenti").child(quadro_emergente?.key.toString() + "/")
            imagRef.child("Image").downloadUrl.addOnSuccessListener {
                GlideApp.with(this).load(it).into(foto_quadro)
            }.addOnFailureListener {
                // Handle any errors
            }
        }
      //  foto_fatte = x[0] + x[1] + x[2] //tiene conto degli imageButton usati


    //carica le foto sul database
    private fun caricaFoto(key : String){
            val mountainsRef = storageRef.child("Quadri_emergenti").child(key).child("Image")
            val bitmap = (foto_quadro?.drawable as? BitmapDrawable)?.bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG,100, baos)
            val data = baos.toByteArray()
            if(data.isNotEmpty()) {
                //l'utente ha caricato delle foto, quindi bisogna caricarle
                var uploadTask = mountainsRef.putBytes(data) //carica i byte della foto
                uploadTask.addOnCompleteListener {
                    foto_caricate++
                    //se è l'ultima
                    if(it.isSuccessful && foto_caricate==foto_fatte) Navigation.findNavController(view!!).navigateUp()
                }.addOnFailureListener {
                    Toast.makeText(activity, "Foto non inserita correttamente", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {}
            }

    }

    //Questo metodo viene invocato per gestire il risultato al ritorno da una activity, occorre determinare chi aveva generato la richiesta
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {     // Acquisizione immagine
            val immagineCatturata = data?.extras?.get("data") as Bitmap
                foto_quadro.setImageBitmap(immagineCatturata) //tiene conto di quante foto caricate


            }
            if(foto_fatte<1) foto_fatte=1
        }


    //quando esco dall'inserimento ritorna la scritta Sthendal
    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")
    }

    //funzione usata per distinguere i due casi: creazione e modifica
    private fun get_key() : String? {
        if(mod==0) {
            return database.child("Quadri/Quadri_emergenti")
                .push().key  //questa push mi restituisce un identificativo unico del percorso creato
        }
        else {
            return quadro_emergente?.key
        }
    }
}
