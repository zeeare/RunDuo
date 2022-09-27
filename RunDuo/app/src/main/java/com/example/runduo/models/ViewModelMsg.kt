package com.example.runduo.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class ViewModelMsg constructor(
    var idSendUser: String = "",
    var idReceiveUser: String = "",
    var msg: String = "",
    var dateNow: String = "",
    var typeMsg: String = ""

) {
    companion object {

        @JvmStatic
        @BindingAdapter("imageMessage")
        public fun loadImage(imageView: ImageView, imageURL: String?) {
            if (imageURL != null) {
                Glide.with(imageView.context).load(imageURL).into(imageView)
            }
        }
    }
}