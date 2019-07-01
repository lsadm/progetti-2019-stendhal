package com.example.stendhal_1

import android.content.Context
import android.content.SharedPreferences
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
    private val PREF_NAME = "Stendhal"      // Nome del file
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


    //invocata appena viene effettuato il login
    private fun updateUI(usr: FirebaseUser?) {
        if (usr != null) {
            Toast.makeText(activity, "Utente loggato", Toast.LENGTH_LONG).show()
            Navigation.findNavController(view!!).navigateUp() //torno alla schermata precedente
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setto titolo e colore dell'actionbar
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
        (activity as AppCompatActivity).supportActionBar?.setTitle("Login")

        //nascondo il bottomNavigation
        //val v: View? = activity?.findViewById(R.id.bottomNavigation)
        //v?.visibility = View.GONE

        sharedPref = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        traiinfo()

        //provo a effettuare il login
        okbtn.setOnClickListener {
            if (validcamp()) {
                saveinfo()
                signIn(email.text.toString(), password.text.toString())
            } else {
                Toast.makeText(activity, "Non hai inserito email o password", Toast.LENGTH_SHORT).show()
            }
        }
        newaccountbtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_login_to_newaccount)
        }
    }


    private fun validcamp() : Boolean{
        return (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty())
    }

    //salva i campi inseriti dall'utente
    private fun saveinfo() {
        val editor = sharedPref.edit()
        val username = email.text.toString()
        val password = password.text.toString()
        val autoLogin = chkAutoLogin.isChecked

        editor.putString(PREF_USERNAME, username)
        editor.putString(PREF_PASSWORD,password)
        editor.putBoolean(PREF_AUTOLOGIN, autoLogin)
        editor.apply()    // Salva le modifiche
    }

    //visualizza le informazioni inserite in precedenza dall'utente per l'autologin
    private fun traiinfo() {
        val username = sharedPref.getString(PREF_USERNAME, "")
        val pass = sharedPref.getString(PREF_PASSWORD,"")
        val autoLogin = sharedPref.getBoolean(PREF_AUTOLOGIN, false)

        email.setText(username)
        password.setText(pass)
        chkAutoLogin.isChecked = autoLogin
    }
    //questo metodo permette di nascondere il menu log in
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    private fun signIn(email: String, password: String) {
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
