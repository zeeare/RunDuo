package com.example.runduo.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.runduo.mainRespo.RespoUser

class ViewModelProfile: ViewModel() {
    private var respoUser = RespoUser.StaticFunction.getInstance()

    fun updateStatus(status: String) {
        respoUser.updateStatus(status)
    }

    fun getUser(): LiveData<ViewModelUsers> {
        return respoUser.getUser()
    }

    fun updateWeight(weight: String) {
        respoUser.updateWeight(weight)
    }
}