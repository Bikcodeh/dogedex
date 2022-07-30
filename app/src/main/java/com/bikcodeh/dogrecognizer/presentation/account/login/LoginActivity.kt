package com.bikcodeh.dogrecognizer.presentation.account.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.bikcodeh.dogrecognizer.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findNavController(R.id.loginNavHostFragment)
            .setGraph(R.navigation.login_nav_graph)
    }
}