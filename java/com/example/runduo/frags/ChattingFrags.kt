package com.example.runduo.frags

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runduo.ChattingActs
import com.example.runduo.R
import com.example.runduo.databinding.ChatsViewBinding
import com.example.runduo.databinding.ChattingFragsBinding
import com.example.runduo.misc.utilTracks
import com.example.runduo.models.ViewModelChats
import com.example.runduo.models.ViewModelMsgLogs
import com.example.runduo.models.ViewModelUsers
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChattingFrags : Fragment() {

    private lateinit var ChatListingBinding: ChattingFragsBinding
    private lateinit var firebaseViewsReplace: FirebaseRecyclerAdapter<ViewModelMsgLogs, ViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ChatListingBinding = ChattingFragsBinding.inflate(layoutInflater, container, false)
        readChat()
        return ChatListingBinding.root
    }

    class ViewHolder(val chatsViewBinding: ChatsViewBinding) :
        RecyclerView.ViewHolder(chatsViewBinding.root)

    override fun onPause() {
        super.onPause()
        firebaseViewsReplace.stopListening()
    }

    private fun readChat() {

        val dataLine = FirebaseDatabase.getInstance().getReference("ChatList").child(utilTracks.retriveUserID()!!)
        val firebaseViewsOptions = FirebaseRecyclerOptions.Builder<ViewModelMsgLogs>()
            .setLifecycleOwner(this).setQuery(dataLine, ViewModelMsgLogs::class.java)
            .build()

        firebaseViewsReplace = object : FirebaseRecyclerAdapter<ViewModelMsgLogs, ViewHolder>(firebaseViewsOptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val chatsViewingBind: ChatsViewBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.chats_view, parent, false
                )

                return ViewHolder(chatsViewingBind)
            }
            override fun onBindViewHolder(
                holder: ViewHolder,
                p1: Int,
                chatViews: ViewModelMsgLogs
            ) {
                val dataRef = FirebaseDatabase.getInstance().getReference("Users").child(chatViews.member)
                dataRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val userViews = snapshot.getValue(ViewModelUsers::class.java)
                            val date = utilTracks.formatUserTimeStamp(chatViews.dateNow.toLong())

                            val chatting = ViewModelChats(chatViews.chatroomId,
                                userViews?.nameUser,
                                chatViews.latestMsg,
                                userViews?.imageURL,
                                date
                            )
                            holder.chatsViewBinding.chatView = chatting
                            holder.itemView.setOnClickListener {
                                val screenNext = Intent(context, ChattingActs::class.java)
                                screenNext.putExtra("otherImg", userViews?.imageURL)
                                screenNext.putExtra("otherId", userViews?.userID)
                                screenNext.putExtra("chatroomId", chatViews.chatroomId)
                                startActivity(screenNext)
                            }

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
        ChatListingBinding.chatFragViews.layoutManager = LinearLayoutManager(context)
        ChatListingBinding.chatFragViews.setHasFixedSize(false)
        ChatListingBinding.chatFragViews.adapter = firebaseViewsReplace
    }

    override fun onResume() {
        super.onResume()
        firebaseViewsReplace.startListening()
    }

}