package com.example.stendhal_1

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.stendhal_1.datamodel.Utente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_newaccount.*


class newaccount : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val TAG = "MainActivity"
    private val database = FirebaseDatabase.getInstance().reference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_newaccount, container, false)
    }

    //Questa funzione rende invisibile il menu nel fragment newaccount
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Creazione account")

        okbtn.setOnClickListener{
            if (verificacampi()) {
                createAccount(email.text.toString(),nome.text.toString(),password.text.toString())
            }
            else{
                Toast.makeText(activity,"Inserire correttamente tutti i campi",Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Memorizza il nuovo utente sul database
    private fun memorizzautente(user : String?, usr : Utente) {
        database.child("Utenti").child(user.toString()).child("Dati").child("Account").setValue(usr)
    }

    //verifica se i campi sono stati riempiti correttamente
    private fun verificacampi() : Boolean{
        return email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty() && nome.text.toString().isNotEmpty()
    }

    fun createAccount(email : String, nome : String,password : String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser?.uid
                    val usr = Utente(nome,email)
                    memorizzautente(user, usr) //memorizza sul database
                    Toast.makeText(context, "Utente registrato con successo", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view!!).navigateUp()
                    Navigation.findNavController(view!!).navigateUp()
                }
                else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Errore nella registrazione", Toast.LENGTH_SHORT).show()
                }
            }

    }




}
