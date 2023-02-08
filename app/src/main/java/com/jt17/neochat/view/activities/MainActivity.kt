package com.jt17.neochat.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.jt17.neochat.R
import com.jt17.neochat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.fragment_container_view_tag)

        auth = FirebaseAuth.getInstance()

        // Write a message to the database
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//        val key1: String? = myRef.push().key
//
//        myRef.child("$key1").setValue("Are you serious?").addOnSuccessListener {
//        }.addOnCompleteListener {
//        }

    }



}