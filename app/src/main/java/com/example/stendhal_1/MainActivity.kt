package com.example.stendhal_1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // Imposta il menu dal file di risorse
        menuInflater.inflate(R.menu.button_login, menu)

        return true
    }

    /**
     * Processa le voci del menu
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            R.id.fragment_login -> Navigation.findNavController(this, R.id.navHost).navigate(R.id.action_to_login)
            else -> return false    // Voce non processata
        }

        return true
    }
}


