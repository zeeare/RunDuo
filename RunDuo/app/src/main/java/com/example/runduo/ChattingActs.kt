package com.example.runduo

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.runduo.databinding.ActivityChattingActsBinding
import com.example.runduo.databinding.LeftStyleBinding
import com.example.runduo.databinding.RightStyleBinding
import com.example.runduo.misc.fixValues
import com.example.runduo.misc.utilTracks
import com.example.runduo.models.ViewModelMsg
import com.example.runduo.models.ViewModelMsgLogs
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chatting_acts.*
import org.json.JSONObject


class ChattingActs : AppCompatActivity() {
    private lateinit var activityMessageBinding: ActivityChattingActsBinding
    private lateinit var userImg: String
    private var otherImg: String? = null
    private var chatroomId: String? = null
    private lateinit var sharedPref: SharedPreferences
    private var userName: String? = null
    private lateinit var userId: String
    private var firebaseViewsReplace: FirebaseRecyclerAdapter<ViewModelMsg, ViewHolder>? = null
    private var otherId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityMessageBinding = ActivityChattingActsBinding.inflate(layoutInflater)
        setContentView(activityMessageBinding.root)

        if (intent.hasExtra("chatroomId")) {
            chatroomId = intent.getStringExtra("chatroomId")
            otherId = intent.getStringExtra("otherId")
            otherImg = intent.getStringExtra("otherImg")
            messageReading(chatroomId!!)
        } else {
            otherId = intent.getStringExtra("otherId")
            otherImg = intent.getStringExtra("otherImg")
        }

        userId = utilTracks.retriveUserID()!!
        sharedPref = getSharedPreferences("userData", MODE_PRIVATE)
        userImg = sharedPref.getString("myImage", "").toString()
        userName = sharedPref.getString("myName", "").toString()
        activityMessageBinding.acts = this
        activityMessageBinding.otherImg = otherImg

        activityMessageBinding.sendMsg.setOnClickListener {
            val message = activityMessageBinding.textingMsg.text.toString()
            if (message.isEmpty())
                Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show()
            else {
                messageSending(message)
                getToken(message)
            }
        }
        if (chatroomId == null)
            chatChecking(otherId!!)
    }

    class ViewHolder(var viewDataBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root)

    private fun chatChecking(otherId: String) {
        val dataRef = FirebaseDatabase.getInstance().getReference("ChatList").child(userId)
        val dataLine = dataRef.orderByChild("member").equalTo(otherId)
        dataLine.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val user = ds.child("member").value.toString()
                        if (user == otherId) {
                            chatroomId = ds.key
                            messageReading(chatroomId!!)
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun messageSending(msg: String) {
        if (chatroomId == null)
            chatCreation(msg)
        else {
            var dataRef = FirebaseDatabase.getInstance().getReference("Chat").child(chatroomId!!)
            val msgSaved = ViewModelMsg(userId, otherId!!, msg, System.currentTimeMillis().toString(), "text")
            dataRef.push().setValue(msgSaved)
                .addOnSuccessListener {
                    textingMsg.text.clear()
                    viewingMSG.scrollToPosition(activityMessageBinding.viewingMSG.adapter!!.itemCount - 1)
                }
            val mapping: MutableMap<String, Any> = HashMap()
            mapping["latestMsg"] = msg
            mapping["dateNow"] = System.currentTimeMillis().toString()

            dataRef = FirebaseDatabase.getInstance().getReference("ChatList").child(otherId!!).child(chatroomId!!)
            dataRef.updateChildren(mapping)

            dataRef = FirebaseDatabase.getInstance().getReference("ChatList").child(userId).child(chatroomId!!)
            dataRef.updateChildren(mapping)
        }
    }

    fun userInfo() {
        val intent = Intent(this, ShowOtherUsers::class.java)
        intent.putExtra("uID", otherId)
        startActivity(intent)
    }

    private fun getToken(message: String) {
        val dataRef = FirebaseDatabase.getInstance().getReference("Users").child(otherId!!)
        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tokenId = snapshot.child("tokenId").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("otherId", userId)
                    data.put("otherImg", userImg)
                    data.put("msgHead", userName)
                    data.put("msgBody", message)
                    data.put("chatroomId", chatroomId)

                    to.put("to", tokenId)
                    to.put("data", data)
                    sendNotification(to)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun chatCreation(msg: String) {
        var dataRef = FirebaseDatabase.getInstance().getReference("ChatList").child(userId)
        chatroomId = dataRef.push().key

        val chattingList = ViewModelMsgLogs(chatroomId!!, msg, System.currentTimeMillis().toString(), otherId!!)
        dataRef.child(chatroomId!!).setValue(chattingList)

        dataRef = FirebaseDatabase.getInstance().getReference("ChatList").child(otherId!!)
        val msgLogging = ViewModelMsgLogs(chatroomId!!, msg, System.currentTimeMillis().toString(), userId)
        dataRef.child(chatroomId!!).setValue(msgLogging)

        dataRef = FirebaseDatabase.getInstance().getReference("Chat").child(chatroomId!!)
        val msgSaving = ViewModelMsg(userId, otherId!!, msg, typeMsg = "text")
        dataRef.push().setValue(msgSaving)
            .addOnSuccessListener {
                textingMsg.text.clear()
                viewingMSG.scrollToPosition(activityMessageBinding.viewingMSG.adapter!!.itemCount - 1)
            }
    }

    private fun messageReading(chatroomId: String) {

        val dataLine = FirebaseDatabase.getInstance().getReference("Chat").child(chatroomId)

        val firebaseViewsReplace = FirebaseRecyclerOptions.Builder<ViewModelMsg>().setLifecycleOwner(this).setQuery(dataLine, ViewModelMsg::class.java).build()
        dataLine.keepSynced(true)

        this.firebaseViewsReplace = object : FirebaseRecyclerAdapter<ViewModelMsg, ViewHolder>(firebaseViewsReplace) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

                    var binding: ViewDataBinding? = null

                    if (viewType == 0)
                        binding = RightStyleBinding.inflate(LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    if (viewType == 1)
                        binding = LeftStyleBinding.inflate(LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    return ViewHolder(binding!!)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder,
                    posi: Int,
                    viewModelMsg: ViewModelMsg
                ) {
                    if (getItemViewType(posi) == 0) {
                        holder.viewDataBinding.setVariable(BR.msg, viewModelMsg)
                        holder.viewDataBinding.setVariable(BR.msgImg, userImg)
                    }

                    if (getItemViewType(posi) == 1) {

                        holder.viewDataBinding.setVariable(BR.msg, viewModelMsg)
                        holder.viewDataBinding.setVariable(BR.msgImg, otherImg)
                    }
                }

                override fun getItemViewType(position: Int): Int {
                    val msg = getItem(position)
                    return if (msg.idSendUser == userId)
                        0
                    else
                        1
                }
            }

        activityMessageBinding.viewingMSG.layoutManager = LinearLayoutManager(this)
        activityMessageBinding.viewingMSG.adapter = this.firebaseViewsReplace
        this.firebaseViewsReplace!!.startListening()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (firebaseViewsReplace != null)
            firebaseViewsReplace!!.stopListening()
    }

    private fun sendNotification(to: JSONObject) {

        val msgRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, fixValues.LINK_URL, to,
            Response.Listener { response: JSONObject ->
                Log.d("TAG", "onResponse: $response") },
            Response.ErrorListener {
                Log.d("TAG", "onError: $it") }) {
            override fun getHeaders(): MutableMap<String, String> {
                val mapping: MutableMap<String, String> = HashMap()

                mapping["Authorization"] = "key=" + fixValues.KEY_SERVER
                mapping["Content-type"] = "application/json"
                return mapping
            }
            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val inQueue = Volley.newRequestQueue(this)
        msgRequest.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        inQueue.add(msgRequest)

    }

}