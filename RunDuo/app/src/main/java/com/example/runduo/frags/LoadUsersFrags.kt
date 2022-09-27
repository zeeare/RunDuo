package com.example.runduo.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runduo.LoadUsersView
import com.example.runduo.databinding.LoadUsersFragsBinding
import com.example.runduo.models.ViewModelUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoadUsersFrags : Fragment() {
    private lateinit var fragUsersBinding: LoadUsersFragsBinding
    private lateinit var loadingUsers: ArrayList<ViewModelUsers>
    private var userLoadedView: LoadUsersView? = null
    private lateinit var fireAuthi: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragUsersBinding = LoadUsersFragsBinding.inflate(inflater, container, false)
        fireAuthi = FirebaseAuth.getInstance()
        getListUsers()

        fragUsersBinding.searchingUsers.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (userLoadedView != null)
                    userLoadedView!!.filter.filter(newText)
                return false
            }
        })

        return fragUsersBinding.root
    }

    private fun getListUsers() {

        val dataRef = FirebaseDatabase.getInstance().getReference("Users")
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    loadingUsers = ArrayList()
                    for (data in snapshot.children) {
                        val viewModelUsers = data.getValue(ViewModelUsers::class.java)
                        loadingUsers.add(viewModelUsers!!)
                    }
                    fragUsersBinding.listOfUsers.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(true)
                        userLoadedView = LoadUsersView(loadingUsers)
                        adapter = userLoadedView
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }
}

