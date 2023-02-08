package com.jt17.neochat.view.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jt17.neochat.databinding.FragmentLoginBinding
import com.jt17.neochat.utils.PrefUtils

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val database = Firebase.database
    private val myRef = database.getReference("users")
    private var selectImage: Uri? = null
    private val navigation by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersFirebaseAuth()
        startNeoChat()

    }

    private fun usersFirebaseAuth() {
        val getUserName = PrefUtils.firstRegister

        if (getUserName.isNotEmpty()) {
            val action =
                LoginFragmentDirections.actionLoginFragmentToBaseFragment(getUserName, null)
            navigation.navigate(action)
        }

    }

    private fun startNeoChat() {

        binding.startNeoChat.setOnClickListener {
            val name = binding.nameUsersEmail.text.toString()

            if (name.isNotEmpty()) {
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.hasChild(name)) {
                            Toast.makeText(
                                requireContext(),
                                "User already exists",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            myRef.child(name).child("name").setValue(name)
                        }
                        PrefUtils.firstRegister = name
                        val action =
                            LoginFragmentDirections.actionLoginFragmentToBaseFragment(/*user's name*/
                                name,
                                null
                            )
                        navigation.navigate(action)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            } else {
                Toast.makeText(requireContext(), "Please, enter a your name", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}