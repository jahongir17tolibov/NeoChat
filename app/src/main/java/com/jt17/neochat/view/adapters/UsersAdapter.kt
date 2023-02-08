package com.jt17.neochat.view.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jt17.neochat.databinding.UsersItemLyBinding
import com.jt17.neochat.view.models.MessageModel
import com.jt17.neochat.utils.DataUtils

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ItemHolder>() {

    inner class ItemHolder(val b: UsersItemLyBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(itemData: MessageModel) {
            b.usersName.text = itemData.user
            b.timeUsers.text = DataUtils.fromMillsToTimeString(itemData.time!!)
        }
    }

    var baseList = emptyList<MessageModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onItemClickListener: ((MessageModel) -> Unit)? = null
    fun setOnItemClickListener(listener: ((MessageModel) -> Unit)? = null) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            UsersItemLyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val itemData = baseList[position]
        holder.bind(itemData)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(itemData)
        }

    }

    override fun getItemCount(): Int = baseList.size
}