package com.example.runduo

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.runduo.databinding.LoadUsersBinding
import com.example.runduo.models.ViewModelUsers
import java.util.*
import kotlin.collections.ArrayList

class LoadUsersView (private var loadingUsers: ArrayList<ViewModelUsers>) :
    RecyclerView.Adapter<LoadUsersView.ViewHolder>(), Filterable {
    private var allUsersInDB: ArrayList<ViewModelUsers> = loadingUsers

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val userLoadingBinding = LoadUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(userLoadingBinding)
    }

    class ViewHolder(val userLoadingBinding: LoadUsersBinding) :
        RecyclerView.ViewHolder(userLoadingBinding.root) {
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewModelUsers = allUsersInDB[position]
        holder.userLoadingBinding.viewModelUsers = viewModelUsers

        holder.userLoadingBinding.otherUserInfo.setOnClickListener {
            val screenNext = Intent(it.context, ShowOtherUsers::class.java)
            screenNext.putExtra("uID", viewModelUsers.userID)
            it.context.startActivity(screenNext)
        }

        holder.itemView.setOnClickListener {
            val screenNext = Intent(it.context, ChattingActs::class.java)
            screenNext.putExtra("otherId", viewModelUsers.userID)
            screenNext.putExtra("otherImg", viewModelUsers.imageURL)
            it.context.startActivity(screenNext)
        }
    }

    override fun getItemCount(): Int {
        return allUsersInDB.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searching = constraint.toString()
                if (searching.isEmpty())
                    allUsersInDB = loadingUsers
                else {
                    val sortUsers = ArrayList<ViewModelUsers>()
                    for (viewModelUsers in loadingUsers) {
                        if (viewModelUsers.nameUser.toLowerCase(Locale.ROOT).trim().contains(searching.toLowerCase(Locale.ROOT).trim()))
                            sortUsers.add(viewModelUsers)
                    }
                    allUsersInDB = sortUsers
                }
                val sortedData = FilterResults()
                sortedData.values = allUsersInDB
                return sortedData
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                allUsersInDB = results?.values as ArrayList<ViewModelUsers>
                notifyDataSetChanged()
            }
        }
    }
}