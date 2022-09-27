package com.example.runduo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runduo.misc.fixValues.TRACKING_SHOW
import com.example.runduo.misc.utilTracks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main2.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        goToTrackingFragWhenAsked(intent)
        setSupportActionBar(layoutToolBar)
        navViewBottom.setupWithNavController(navBaseFrag.findNavController())
        navViewBottom.setOnNavigationItemReselectedListener {  }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener{
                if (it.isComplete) {
                    val token = it.result.toString()
                    val databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(utilTracks.retriveUserID()!!)
                    val map: MutableMap<String, Any> = HashMap()
                    map["tokenId"] = token!!
                    databaseReference.updateChildren(map)
                }
            }
    }

    private fun goToTrackingFragWhenAsked(intent: Intent?)
    {
        if(intent?.action == TRACKING_SHOW) {
            navBaseFrag.findNavController().navigate(R.id.global_trackingFrag)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        goToTrackingFragWhenAsked(intent)
    }
}