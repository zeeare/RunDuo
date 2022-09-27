package com.example.runduo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.runduo.databinding.ActivityShowOtherUsersBinding
import com.example.runduo.models.ViewModelUsers
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_show_other_users.*

class ShowOtherUsers : AppCompatActivity() {
    private lateinit var showUsersActBinding: ActivityShowOtherUsersBinding
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUsersActBinding = ActivityShowOtherUsersBinding.inflate(layoutInflater)
        setContentView(showUsersActBinding.root)
        userID = intent.getStringExtra("uID")
        retrieveDataUsers(userID)

        achieve1a.setOnClickListener{
            val achievement = Toast.makeText(this,"Login to RunDuo for 14 days in a row",
                Toast.LENGTH_SHORT)
            achievement.show()
        }

        achieve2a.setOnClickListener{
            val achievement = Toast.makeText(this,"Accumulate a total timing of 24 hours",
                Toast.LENGTH_SHORT)
            achievement.show()
        }

        achieve3a.setOnClickListener{
            val achievement = Toast.makeText(this,"Accumulate a total running distance of 306.7km",
                Toast.LENGTH_SHORT)
            achievement.show()
        }
    }
    private fun retrieveDataUsers(userId: String?) {
        val dataRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val viewingUsers = snapshot.getValue(ViewModelUsers::class.java)
                    showUsersActBinding.viewModelUsers = viewingUsers

                    if (viewingUsers!!.nameUser.contains(" ")) {
                        val split = viewingUsers.nameUser.split(" ")
                        showUsersActBinding.showFirstName.text = split[0]
                        showUsersActBinding.showLastName.text = split[1]
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}