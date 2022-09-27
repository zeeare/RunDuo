package com.example.runduo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runduo.misc.utilTracks
import kotlinx.android.synthetic.main.run_stuffs.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunViews : RecyclerView.Adapter<RunViews.HolderRuns>() {

    inner class HolderRuns(viewingItem: View) : RecyclerView.ViewHolder(viewingItem)
     val callbackDiff = object : DiffUtil.ItemCallback<DataRun>()
     {
         override fun areItemsTheSame(oldItem: DataRun, newItem: DataRun): Boolean {
             return oldItem.id == newItem.id
         }

         override fun areContentsTheSame(oldItem: DataRun, newItem: DataRun): Boolean {
             return oldItem.hashCode() == newItem.hashCode()
         }
     }

    val notSame = AsyncListDiffer(this, callbackDiff)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRuns {
        return HolderRuns(
            LayoutInflater.from(parent.context).inflate(
                R.layout.run_stuffs,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return notSame.currentList.size
    }

    fun listSubmitted(list: List<DataRun>) = notSame.submitList(list)

    override fun onBindViewHolder(holder: HolderRuns, position: Int) {
        val Datarun = notSame.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(Datarun.img).into(stuffsImage)
            val currTime = Calendar.getInstance().apply {
                timeInMillis = Datarun.timeStamps
            }
            val distKM = "${Datarun.distanceInMeters / 1000f}km"
            stuffsDistance.text = distKM

            val speedAvg = "${Datarun.averageSpeedInKMH / 1000f}km/h"
            stuffsSpeedAvg.text = speedAvg

            val formatDate = SimpleDateFormat("dd.MM.yy",Locale.getDefault())
            stuffsDate.text = formatDate.format(currTime.time)

            val burnedCalories = "${Datarun.caloriesBurned}kcal"
            stuffsCalories.text = burnedCalories

            stuffsTiming.text = utilTracks.formatTimingStopWatch(Datarun.timingInMillis)
        }
    }

}