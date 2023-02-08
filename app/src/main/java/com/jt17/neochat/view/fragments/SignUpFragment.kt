package com.jt17.neochat.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jt17.neochat.R
import com.jt17.neochat.databinding.FragmentSignUpBinding
import com.jt17.neochat.utils.PrefUtils

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val database = Firebase.database
    private val myRef = database.getReference("users")
    private val navigation by lazy { findNavController() }
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
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        googleUsersFirebaseAuth()/* checking google users name */
        initClicks()

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            navigation.popBackStack(R.id.signInFragment, false)
//        }

    }

    private fun signUpWithEmail() {
        val email = binding.emailInput.text.toString()
        val pass = binding.passwordInput.text.toString()
        val repass = binding.passwordRetypeInput.text.toString()
        val user = FirebaseAuth.getInstance().currentUser

        if (email.isNotEmpty() && pass.isNotEmpty() && repass.isNotEmpty()) {
            if (user != null) {
                Toast.makeText(
                    requireContext(),
                    "This Email is already sign up",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (pass == repass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            navigation.navigate(R.id.loginFragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                it.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Password is not matching !!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Empty Fields are not Allowed !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        firebaseAuth = Firebase.auth
        val credintal = GoogleAuthProvider.getCredential(account.idToken!!, null)
        firebaseAuth.signInWithCredential(credintal).addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                updateUI(user, account)
            } else {
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?, account: GoogleSignInAccount?) {
        if (user != null) {
            Toast.makeText(
                requireContext(),
                "This google account is already sign up",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val googleName = account?.displayName.toString()
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.hasChild(googleName)) {
                        Toast.makeText(
                            requireContext(),
                            "User already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        myRef.child(googleName).child("name").setValue(googleName)
                    }
                    PrefUtils.firstRegister = googleName
                    val action = SignUpFragmentDirections.actionSignUpFragmentToBaseFragment(
                        null,
                        googleName
                    )
                    navigation.navigate(action)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun googleUsersFirebaseAuth() {
        val getUserName = PrefUtils.firstRegister

        if (getUserName.isNotEmpty()) {
            val action =
                LoginFragmentDirections.actionLoginFragmentToBaseFragment(null, getUserName)
            navigation.navigate(action)
        }
    }

    private fun initClicks() {
        binding.signUpBtn.setOnClickListener {
            signUpWithEmail()
        }

        binding.signWithGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.alreadySignTxt.setOnClickListener {
            navigation.navigate(R.id.signInFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

}