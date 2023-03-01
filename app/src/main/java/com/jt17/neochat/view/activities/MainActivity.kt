package com.jt17.neochat.view.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jt17.neochat.R
import com.jt17.neochat.databinding.ActivityMainBinding
import com.jt17.neochat.utils.PrefUtils

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

    }

    private fun initWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        statusBarHeight = getStatusBarHeight()
    }

    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private val database = Firebase.database
    private val myRef = database.getReference("")
    private val user = PrefUtils.firstRegister

    override fun onResume() {
        super.onResume()
        if (user.isNotBlank()) {
            myRef.child("presence").child("user").setValue(true)/* online in chat */
        }
    }

    override fun onPause() {
        super.onPause()
        if (user.isNotBlank()) {
            myRef.child("presence").child("user").setValue(false)/* offline chat */
        }
    }

    companion object {
        var statusBarHeight = 0
    }

}