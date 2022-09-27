package com.example.runduo.frags

    import android.app.AlertDialog
    import android.content.Context
    import android.content.SharedPreferences
    import android.os.Bundle
    import android.text.TextUtils
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.databinding.DataBindingUtil
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import com.example.runduo.R
    import com.example.runduo.databinding.SetsFragsBinding
    import com.example.runduo.models.ViewModelProfile
    import kotlinx.android.synthetic.main.dialog_layout.view.*
    import kotlinx.android.synthetic.main.dialog_layout.view.editingStatus
    import kotlinx.android.synthetic.main.dialog_layout2.view.*


class SetsFrags : Fragment() {

        private lateinit var profileViewBinds: SetsFragsBinding
        private lateinit var alertMessage: AlertDialog
        private lateinit var viewingProfile: ViewModelProfile
        private lateinit var sharedPref: SharedPreferences

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            sharedPref = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
            profileViewBinds = DataBindingUtil.inflate(inflater, R.layout.sets_frags, container, false)
            viewingProfile = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application).create(ViewModelProfile::class.java)
            viewingProfile.getUser().observe(viewLifecycleOwner, Observer { ViewModelUsers ->
                profileViewBinds.viewModelUsers = ViewModelUsers
                if (ViewModelUsers.nameUser.contains(" ")) {
                    val separate = ViewModelUsers.nameUser.split(" ")
                    profileViewBinds.showFirstName.text = separate[0]
                    profileViewBinds.showLastName.text = separate[1]
                }
            })

            profileViewBinds.changingStatus.setOnClickListener {
                changingStatusMessages()
            }

            profileViewBinds.changeWeight.setOnClickListener{
                changingWeight()
            }

            profileViewBinds.achieve1.setOnClickListener{
                val achievement = Toast.makeText(requireContext(),"Login to RunDuo for 14 days in a row",Toast.LENGTH_SHORT)
                achievement.show()
            }

            profileViewBinds.achieve2.setOnClickListener{
                val achievement = Toast.makeText(requireContext(),"Accumulate a total timing of 24 hours",Toast.LENGTH_SHORT)
                achievement.show()
            }

            profileViewBinds.achieve3.setOnClickListener{
                val achievement = Toast.makeText(requireContext(),"Accumulate a total running distance of 306.7km",Toast.LENGTH_SHORT)
                achievement.show()
            }

            return profileViewBinds.root
        }

        private fun changingWeight() {

            val alertDialog = AlertDialog.Builder(context)
            val weightView = LayoutInflater.from(context).inflate(R.layout.dialog_layout2, null, false)
            alertDialog.setView(weightView)

            weightView.editingWeight.setOnClickListener {
                val newWeight = weightView.changeUserWeight.text.toString()
                if (newWeight.isNotEmpty() && newWeight.isDigitsOnly()) {
                    viewingProfile.updateWeight(newWeight)
                    val editing = sharedPref.edit()
                    editing.putFloat("weight", newWeight.toFloat()).apply()
                    alertMessage.dismiss()
                }
            }
            alertMessage = alertDialog.create()
            alertMessage.show()
        }

    fun String.isDigitsOnly(): Boolean {
        return TextUtils.isDigitsOnly(this) && this.isNotEmpty()
    }
        private fun changingStatusMessages() {
            val alertDialog = AlertDialog.Builder(context)
            val statusView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null, false)
            alertDialog.setView(statusView)

            statusView.editingStatus.setOnClickListener {
                val status = statusView.changeUserStatus.text.toString()
                if (status.isNotEmpty()) {
                    viewingProfile.updateStatus(status)
                    alertMessage.dismiss()
                }
            }
            alertMessage = alertDialog.create()
            alertMessage.show()
        }
    }

