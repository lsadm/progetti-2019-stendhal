package com.example.stendhal_1

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_login.*



class login : Fragment() {

    var auth = FirebaseAuth.getInstance()
    var user: FirebaseUser? = null
    private val PREF_NAME = "Stendhal"
    private val PREF_USERNAME = "Username"
    private val PREF_PASSWORD = "Password"
    private val PREF_AUTOLOGIN = "AutoLogin"
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    //Dopo aver effettuato il login..
    private fun updateUI(usr: FirebaseUser?) {
        if (usr != null) {
            Toast.makeText(activity, "Utente loggato", Toast.LENGTH_LONG).show()
            Navigation.findNavController(view!!).navigateUp() //torno alla schermata precedente
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation=(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) //Impedisce la rotazione dello schermo

        //Setto titolo e colore dell'ActionBar
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004097")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Login")

        sharedPref = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        traiinfo()

        //Provo a effettuare il login
        okbtn.setOnClickListener {
            if (validcamp()) {
                saveinfo()
                signIn(Username.text.toString(), password.text.toString())
            } else {
                Toast.makeText(activity, "Mancanza di alcuni campi", Toast.LENGTH_SHORT).show()
            }
        }

        newaccountbtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_login_to_newaccount)
        }

    }


    private fun validcamp() : Boolean{
        return (Username.text.toString().isNotEmpty() && password.text.toString().isNotEmpty())
    }

    //Salva i campi inseriti dall'utente
    private fun saveinfo() {
        val editor = sharedPref.edit()

        //Prende i valori dalle textview
        val username = Username.text.toString() //Username è l'e-mail
        val password = password.text.toString()
        val autoLogin = chkAutoLogin.isChecked

        editor.putString(PREF_USERNAME, username)
        editor.putString(PREF_PASSWORD,password)
        editor.putBoolean(PREF_AUTOLOGIN, autoLogin)
        editor.apply()    // Salva le modifiche
    }

    //visualizza le informazioni inserite in precedenza dall'utente
    private fun traiinfo() {
        val username = sharedPref.getString(PREF_USERNAME, "")
        val pass = sharedPref.getString(PREF_PASSWORD,"")
        val autoLogin = sharedPref.getBoolean(PREF_AUTOLOGIN, false)

        Username.setText(username)
        password.setText(pass)
        chkAutoLogin.isChecked = autoLogin
    }

    //questo metodo permette di nascondere il menu log in
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    private fun signIn(email: String, password: String) { //Email=username
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity", "signInWithEmail:success")
                    user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Autenticazione fallita", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }


}
