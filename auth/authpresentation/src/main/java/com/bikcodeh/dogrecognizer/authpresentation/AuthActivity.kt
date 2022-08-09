package com.bikcodeh.dogrecognizer.authpresentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        findNavController(R.id.authNavHostFragment)
            .setGraph(R.navigation.auth_nav_graph)
    }
}