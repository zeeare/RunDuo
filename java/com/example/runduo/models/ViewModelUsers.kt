package com.example.runduo.models

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

data class ViewModelUsers(
    val imageURL: String = "",
    val userID: String = "",
    var nameUser: String = "",
    val status: String = "",
    val weight: String = ""
) {

    companion object {

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(v: CircleImageView, imageUrl: String?) {
            imageUrl?.let {
                Glide.with(v.context).load(imageUrl).into(v)
            }
        }
    }

}