package com.jt17.neochat.view.adapters

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jt17.neochat.databinding.MyMessagesLyItemBinding
import com.jt17.neochat.databinding.OtherMessageLyItemBinding
import com.jt17.neochat.view.models.MessageModel
import com.jt17.neochat.utils.App
import com.jt17.neochat.utils.DataUtils
import com.jt17.neochat.utils.PrefUtils
import com.squareup.picasso.Picasso

class MessageAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    private var messagesList: List<MessageModel> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun addMessage(message: List<MessageModel>) {
        messagesList = message
        notifyDataSetChanged()
    }

    inner class MyMessageViewHolder(b: MyMessagesLyItemBinding) : MessageViewHolder(b.root) {
        private var messageTxt: TextView = b.txtMyMessage
        private var timeTxt: TextView = b.txtMyMessageTime
        private var myImg: ImageView = b.myImg

        override fun messageBind(messageMain: MessageModel) {
            messageTxt.text = messageMain.message
            timeTxt.text = DataUtils.fromMillsToTimeString(messageMain.time)
            Picasso.get().load(messageMain.img).resize(500, 600).onlyScaleDown().into(myImg)
            myImg.isVisible = messageMain.img != null//rasm path kelmasa rasimni berkit
            messageTxt.isVisible = messageMain.message.isNotEmpty()
        }

    }

    inner class OtherMessageViewHolder(b: OtherMessageLyItemBinding) : MessageViewHolder(b.root) {
        private var otherMessageTxt: TextView = b.txtOtherMessage
        private var otherUserTxt: TextView = b.txtOtherUser
        private var otherTimeTxt: TextView = b.txtOtherMessageTime
        private var otherImg: ImageView = b.otherImg

        override fun messageBind(messageMain: MessageModel) {
            otherMessageTxt.text = messageMain.message
            otherUserTxt.text = messageMain.user
            otherTimeTxt.text = DataUtils.fromMillsToTimeString(messageMain.time)
            Picasso.get().load(messageMain.img).resize(500, 600).onlyScaleDown().into(otherImg)
            otherImg.isVisible = messageMain.img != null
            otherMessageTxt.isVisible = messageMain.message.isNotEmpty()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(
                MyMessagesLyItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            OtherMessageViewHolder(
                OtherMessageLyItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val itemData = messagesList[position]
        holder.messageBind(itemData)
    }

    override fun getItemCount(): Int = messagesList.size

    override fun getItemViewType(position: Int): Int {
        val message = messagesList[position]

        return if (PrefUtils.firstRegister == message.user) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    

}


open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun messageBind(messageMain: MessageModel) {}
}