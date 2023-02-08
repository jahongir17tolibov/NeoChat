package com.jt17.neochat.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jt17.neochat.view.adapters.MessageAdapter
import com.jt17.neochat.databinding.FragmentChatBinding
import com.jt17.neochat.view.models.MessageModel
import com.jt17.neochat.utils.DataUtils
import com.jt17.neochat.utils.PrefUtils
import java.util.Date

class ChatsFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQ_CODE = 100
    }

    /** adapter and args row **/
    private val messageAdapter: MessageAdapter by lazy { MessageAdapter() }
    private val messageList = ArrayList<MessageModel>()
    private val args: ChatsFragmentArgs by navArgs()

    /** database row **/
    private val database = Firebase.database
    private val myRef = database.getReference("")

    /** room types row **/
    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    /** register types row **/
    private val senderUid: String = PrefUtils.firstRegister

    /** storage types row **/
    private lateinit var imgUri: Uri
    private val storageRef = FirebaseStorage.getInstance().getReference("images/")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUID()/* save registers and others */
        initRecyc()/* recyclerview and adapter usages */
        initToolBar()/* toolBar usages */
        initClicks()/* Usage for all clicks button, cardview and others */

    }

    private fun initUID() {
        senderRoom = PrefUtils.firstRegister.trim() + "_" + args.chatsModel!!.user.trim()
        receiverRoom = args.chatsModel!!.user.trim() + "_" + PrefUtils.firstRegister.trim()
    }

    private fun initOnlineOrOfflineMode() {
        args.chatsModel?.user?.let {
            myRef.child("presence").child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {/*snapshot null bo'lmasa true yani snapshot mavjud bo'lsa*/
                        val status = snapshot.getValue(Boolean::class.java)
                        status?.let { position ->
                            binding.statusTxt.text = if (position) "Online" else "Offline"
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun initToolBar() {
        initOnlineOrOfflineMode()
        binding.usersName.text = args.chatsModel?.user
        binding.materialToolBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

    }

    private fun initRecyc() {
        binding.charRecyc.run {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = messageAdapter
        }

        myRef.child("chats").child("$senderRoom").child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (oneMsg in snapshot.children) {
                        val message: MessageModel? = oneMsg.getValue(MessageModel::class.java)
                        message!!.junatuvciUnId = oneMsg.key!!
                        messageList.add(message)
                    }
                    messageAdapter.addMessage(messageList)
                    try {
                        binding.charRecyc.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    } catch (_: Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    private fun initClicks() {
        binding.sendMsg.setOnClickListener {
            val editText = binding.txtMessage.text.toString()
            if (!binding.imgResultBac.isVisible) {
                if (editText.isNotEmpty()) {
                    val time = Date().time
                    /* upload data to model */
                    sendMessage(
                        MessageModel(
                            PrefUtils.firstRegister,
                            binding.txtMessage.text.toString(),
                            time,
                            null,
                            senderUid
                        )
                    )
                    binding.charRecyc.scrollToPosition(messageAdapter.itemCount - 1)
                    binding.txtMessage.text = null
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Message should not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                uploadImage() { pos, imgPath ->
                    if (!pos) return@uploadImage
                    val time = Date().time
                    /* upload data to model */
                    sendMessage(
                        MessageModel(
                            PrefUtils.firstRegister,
                            binding.txtMessage.text.toString(),
                            time,
                            imgPath,
                            senderUid
                        )
                    )
                    binding.charRecyc.scrollToPosition(messageAdapter.itemCount - 1)
                    binding.txtMessage.text = null
                }
            }
        }

        binding.uploadImage.setOnClickListener {
            selectImg()
        }

        binding.imgResultBac.setOnClickListener {
            imgBckgVisibility(false)
            progressVisiblity(false)
        }
    }

    private fun selectImg() {
        Intent().run {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            startActivityForResult(this, REQ_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && data != null && data.data != null) {
            imgUri = data.data!!
            binding.imgResult.setImageURI(imgUri)
            imgBckgVisibility(true)
        }
    }

    private fun uploadImage(upLoadStatus: ((Boolean, String?) -> Unit)? = null) {
        progressVisiblity(true)
        val nowData = Date()
        val getFileName = DataUtils.getUpLoadImgFileName(nowData)
        storageRef.child(getFileName)
            .putFile(imgUri)
            .addOnSuccessListener {
                imgBckgVisibility(false)
                Toast.makeText(requireContext(), "Rasm yuklandi", Toast.LENGTH_SHORT).show()
                progressVisiblity(false)
                storageRef.child(getFileName).downloadUrl.addOnSuccessListener {
                    val filePath = it.toString()
                    upLoadStatus?.invoke(true, filePath)
                }
            }
            .removeOnFailureListener {
                Toast.makeText(requireContext(), "Rasm yuklashda xatolik", Toast.LENGTH_SHORT)
                    .show()
                progressVisiblity(false)
                imgBckgVisibility(false)
                upLoadStatus?.invoke(false, null)
            }
    }

    private fun progressVisiblity(pos: Boolean) {
        binding.progress.isVisible = pos
    }

    private fun imgBckgVisibility(pos: Boolean) {
        binding.imgResultBac.isVisible = pos
        if (pos) return//agar true bolsa rekursiya iwlaydi
        binding.imgResult.setImageDrawable(null)
    }

    private fun sendMessage(messageModel: MessageModel) {
        val randomKey = myRef.child("chats").push().key

        val lastMsg = HashMap<String, Any>()
        lastMsg["lastMsg"] = messageModel
        lastMsg["lastTime"] = messageModel.time

        myRef.child("chats").child(senderRoom!!).updateChildren(lastMsg)
        myRef.child("chats").child(receiverRoom!!).updateChildren(lastMsg)

        myRef.child("chats").child(senderRoom!!)
            .child("messages").child(randomKey!!)
            .setValue(messageModel)
            .addOnSuccessListener {
                myRef.child("chats").child(receiverRoom!!)
                    .child("messages")
                    .child(randomKey)
                    .setValue(messageModel)
                    .addOnSuccessListener {}
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}