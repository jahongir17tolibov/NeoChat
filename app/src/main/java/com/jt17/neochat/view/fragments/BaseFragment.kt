package com.jt17.neochat.view.fragments

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jt17.neochat.R
import com.jt17.neochat.view.adapters.UsersAdapter
import com.jt17.neochat.databinding.AppBarMainBinding
import com.jt17.neochat.databinding.FragmentBaseBinding
import com.jt17.neochat.view.models.MessageModel
import com.jt17.neochat.utils.PrefUtils

class BaseFragment : Fragment() {
    private var _binding: FragmentBaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var appBarMainBinding: AppBarMainBinding

    private val database = Firebase.database
    private val myRef = database.getReference("")
    private val navigation by lazy { findNavController() }
    private val args: BaseFragmentArgs by navArgs()
    private val usersList = ArrayList<MessageModel>()
    private lateinit var uidUser: String
    private val usersAdapter by lazy { UsersAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseBinding.inflate(inflater, container, false)
        appBarMainBinding = binding.appBarId
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uidUser = PrefUtils.firstRegister

        initDrawerLy()
        initFirebaseUsers()
        initRecyc()

    }

    private fun initDrawerLy() {
        appBarMainBinding.mToolbar.setNavigationOnClickListener {
            binding.drawerLy.open()
        }

        binding.navView.setNavigationItemSelectedListener {
            it.isChecked = true
            binding.drawerLy.close()
            true
        }
        val headerView = binding.navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.header_users_name).text = args.namesIntoLoginSignUp
        headerView.findViewById<TextView>(R.id.header_users_name).text = args.namesIntoSignUpFragment

    }

    private fun checkCurrentUser() {
        val user = Firebase.auth.currentUser
        if (user == null) {
            navigation.navigate(R.id.signInFragment)
        }
    }

    private fun initFirebaseUsers() {
        myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (dataSnapshot in snapshot.children) {
                    if (dataSnapshot.key!! != PrefUtils.firstRegister) {
                        val getName = dataSnapshot.child("name").value.toString()
                        val lastMessage = ""
                        val unreadMessagesCount = 0
                        usersList.add(MessageModel(getName, lastMessage, 123456, null, ""))
                    }
                }
                usersAdapter.baseList = usersList
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun initRecyc() {
        appBarMainBinding.usersRecycler.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter
        }

        usersAdapter.setOnItemClickListener {
            val action = BaseFragmentDirections.actionBaseFragmentToChatsFragment(it)
            navigation.navigate(action)
        }

//        usersAdapter.setOnItemClickListener {
//        }

    }

    private fun Toast.showCustomToast(message: String, activity: Activity) {
        val customLayout = activity.layoutInflater.inflate(
            R.layout.custom_toast,
            activity.findViewById(R.id.toast_container)
        )
        this.apply {
            view = customLayout
            setGravity(Gravity.BOTTOM, 0, 40)
            setText(message)
            duration = Toast.LENGTH_SHORT
            show()
        }
    }

    private fun initButton() {
//        binding.buttonTap.setOnClickListener {
//            val action = BaseFragmentDirections.actionBaseFragmentToChatsFragment(
//                MessageModel(
//                    "jytfixed2332",
//                    "why your'e inspected",
//                    8555
//                )
//            )
//            findNavController().navigate(action)
//            val bundle =
//                bundleOf("str_txt" to "Why are you know impossible\nIt's just not\n-how are you know stupid? paster!!!")
//            view?.findNavController()?.navigate(R.id.chatsFragment, bundle)
//        }
    }

    override fun onStart() {
        super.onStart()
        checkCurrentUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}