package com.jt17.neochat.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jt17.neochat.R
import com.jt17.neochat.databinding.FragmentSignUpBinding
import com.jt17.neochat.ui.activities.MainActivity
import com.jt17.neochat.ui.activities.SignInActivity

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.signUpBtn.setOnClickListener {
            signUpWithEmail()
        }

        binding.signWithGoogle.setOnClickListener {
            signWithGoogle()
        }

        binding.alreadySignTxt.setOnClickListener {

        }
    }

    private fun signUpWithEmail() {
        val email = binding.emailInput.text.toString()
        val pass = binding.passwordInput.text.toString()
        val repass = binding.passwordRetypeInput.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty() && repass.isNotEmpty()) {

            if (pass == repass) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {

                    if (it.isSuccessful) {
                        val intent = Intent(activity, SignInActivity::class.java)
                        activity?.startActivity(intent)
                        activity?.finish()
                    } else {
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Password is not matching !!", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(requireContext(), "Empty Fields are not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                upDateUI(account)
            }
        } else {
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun upDateUI(account: GoogleSignInAccount) {
        val credintal = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credintal).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                activity?.startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}