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
import android.text.method.ScrollingMovementMethod
import android.widget.TextView



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
        return inflater.inflate(R.layout.fragment_dettaglio_quadro, container, false)
    }

    //Cosa fare nel caso in cui si selezioni un item del menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            //Se si seleziona il Cestino:
            R.id.Cestino  -> {
                val builder = AlertDialog.Builder(activity as AppCompatActivity)
                builder.setTitle(R.string.AlertMessage)
                builder.setNegativeButton(R.string.NegativeButton, DialogInterface.OnClickListener { _, which -> }) //chiude la finestra
                builder.setPositiveButton(R.string.PositiveButton, DialogInterface.OnClickListener { _, which ->
                    CancellaQuadro() //Elimina il quadro
                    Navigation.findNavController(view!!).navigateUp()
                })
                builder.show() //mostra la finestra
            }

            //Se si seleziona Modifica:
            R.id.Modifica -> {
                //Crea un bundle e lo passa al fragment add_quadroemergente, poi lì il quadro verrà modificato
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
                inflater?.inflate(R.menu.menu_modifica, menu) //Viene mostrato il menu modifica
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Quanti elementi vi sono in un ArrayList di oggetti di tipo: Quadro emergente
        fun getItemCount(array: ArrayList<QuadroEmergente?>): Int {
            return array.size
        }

        setHasOptionsMenu(true)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo

        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Stendhal")

        val arrayquadremer = ArrayList<QuadroEmergente?>()

        // Estraggo il parametro (quadro) dal bundle e lo visualizzo
        arguments?.let {

            //Caso in cui dal bundle ho ricevuto un quadro storico
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

            //Caso in cui dal bundle ho ricevuto un quadro emergente
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
                //Leggo dal database tutti i quadri emergenti e li metto in arrayquadremer che è un ArrayList
                arrayquadremer.clear()
                snapshot.children.forEach {
                    val quadrosingolo:QuadroEmergente = it.getValue(QuadroEmergente::class.java)!!
                    arrayquadremer.add(quadrosingolo)
                }

                val numeroelementi = getItemCount(arrayquadremer) //Numero di elementi nell'ArrayList
                val ran = (1..numeroelementi).random() //Generazione valore random compreso tra 1 e la dimensione dell'ArrayList
                val quadro:QuadroEmergente?=arrayquadremer.get(ran-1) //Prelevo dall'ArrayList un elemento scelto casualmente
                //E lo visualizzo
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

        //Se bidirezione=true sono arrivato in questo fragment attraverso l'ImageButton_quadro_del_giorno
        //In questo caso devo leggere il quadro dal database, cosa che non succedeva se il quadro lo ottenevo mediante bundle
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
                //Picture è l'id dell'immagine del fragment_dettaglio_quadro.xml
                picture.setImageBitmap(bitmap)
            }.addOnFailureListener {
                // Handle any errors
            }
        }



    private fun CancellaQuadro() {
        database_quadriemergenti.child(quadroemer!!.key.toString()).removeValue()
        nodo.child("Utenti").child(auth.currentUser!!.uid).child("Mie_opere").child(quadroemer!!.key.toString()).removeValue()
        storageRef.child("Quadri_emergenti").child(quadroemer!!.key.toString()).child("Image").delete()
    }

    private fun zoomFoto(img : ImageView) {
        //Istanzio un oggetto della classe AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.foto, null)
        dialogBuilder.setView(dialogView)
        val imageview = dialogView.findViewById(R.id.imageView) as ImageView
        //Converto da drawable a bitmap il parametro di ingresso della funzione zoomFoto che è la ''picture'' prelevata
        //dal fragment Dettaglio_quadro
        val bitmap = (img.drawable as? BitmapDrawable)?.bitmap
        imageview.setImageBitmap(bitmap)
        val alertDialog = dialogBuilder.create()
        //Permette la visualizzazione del alert dialog
        alertDialog.show()
    }

}



