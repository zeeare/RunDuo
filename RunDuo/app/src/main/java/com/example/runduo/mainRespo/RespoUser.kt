package com.example.runduo.mainRespo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.runduo.misc.utilTracks
import com.example.runduo.models.ViewModelUsers
import com.google.firebase.database.*

class RespoUser {
    private var dataNOW: MutableLiveData<ViewModelUsers>? = null
    private lateinit var DataRef: DatabaseReference


    object StaticFunction {
        private var fixInstance: RespoUser? = null
        fun getInstance(): RespoUser {
            if (fixInstance == null)
                fixInstance = RespoUser()

            return fixInstance!!
        }
    }

    fun updateStatus(status: String) {
        val dataRef =
            FirebaseDatabase.getInstance().getReference("Users").child(utilTracks.retriveUserID()!!)
        val edits = mapOf<String, Any>("status" to status)
        dataRef.updateChildren(edits)
    }

    fun updateWeight(weight: String)
    {
        val dataRef =
            FirebaseDatabase.getInstance().getReference("Users").child(utilTracks.retriveUserID()!!)
        val edits = mapOf<String, Any>("weight" to weight)
        dataRef.updateChildren(edits)
    }

    fun getUser(): LiveData<ViewModelUsers> {
        if (dataNOW == null)
            dataNOW = MutableLiveData()
        DataRef = FirebaseDatabase.getInstance().getReference("Users").child(utilTracks.retriveUserID()!!)
        DataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(ViewModelUsers::class.java)
                    dataNOW!!.postValue(userModel)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return dataNOW!!
    }
}