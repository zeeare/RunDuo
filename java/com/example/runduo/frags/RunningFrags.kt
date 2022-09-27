package com.example.runduo.frags

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runduo.R
import com.example.runduo.RunViews
import com.example.runduo.misc.TypeSorting
import com.example.runduo.misc.fixValues.REQUEST_PERMISSION_LOCATION
import com.example.runduo.misc.utilTracks
import com.example.runduo.models.viewModelMain
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.running_frags.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunningFrags : Fragment(R.layout.running_frags) , EasyPermissions.PermissionCallbacks {

    private val viewingModel: viewModelMain by viewModels()
    private lateinit var runViews: RunViews

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runViews = RunViews()
        runShowsList()
        permissionRequests()
        when(viewingModel.typeSorting)
        {
            TypeSorting.DATE -> spinningAct.setSelection(0)
            TypeSorting.DISTANCES -> spinningAct.setSelection(1)
            TypeSorting.SPEED_AVG -> spinningAct.setSelection(2)
            TypeSorting.TIME_RUNS -> spinningAct.setSelection(3)
            TypeSorting.BURNED_CALORIES -> spinningAct.setSelection(4)

        }
        spinningAct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adaptView: AdapterView<*>?, v: View?, posi: Int, id: Long) {
                when(posi)
                {
                    0 -> viewingModel.runsSorting(TypeSorting.DATE)
                    1 -> viewingModel.runsSorting(TypeSorting.DISTANCES)
                    2 -> viewingModel.runsSorting(TypeSorting.SPEED_AVG)
                    3 -> viewingModel.runsSorting(TypeSorting.TIME_RUNS)
                    4 -> viewingModel.runsSorting(TypeSorting.BURNED_CALORIES)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
        viewingModel.sortingRuns.observe(viewLifecycleOwner, Observer {
            runViews.listSubmitted(it)
        })
        addNew.setOnClickListener { findNavController().navigate(R.id.action_runningFrags_to_tracksFrags) }
    }

    private val touchingMenuCall = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val posi = viewHolder.layoutPosition
            val runDATA = runViews.notSame.currentList[posi]
            viewingModel.removeRun(runDATA)
            Snackbar.make(requireView(), "Run has been deleted", Snackbar.LENGTH_LONG).apply {
                setAction("undo delete") {
                    viewingModel.addedRun(runDATA)
                }
                show()
            }
        }
    }

    private fun runShowsList() = displayRuns.apply {
        runViews = RunViews()
        adapter = runViews
        layoutManager = LinearLayoutManager(activity)
        ItemTouchHelper(touchingMenuCall).attachToRecyclerView(this)
    }

    private fun permissionRequests()
    {
        if(utilTracks.hasLocationPermissions(requireContext()))
            {
            return
            }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            EasyPermissions.requestPermissions(this,"Location permission is required for this app",REQUEST_PERMISSION_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        }
        else
        {
            EasyPermissions.requestPermissions(this,"Location permission is required for this app",REQUEST_PERMISSION_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        perms: Array<out String>,
        results: IntArray
    ) {
            super.onRequestPermissionsResult(requestCode,perms,results)
            EasyPermissions.onRequestPermissionsResult(requestCode,perms,results,this)
    }

    override fun onPermissionsGranted(requestCode: Int,perms: MutableList<String>)
    {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms))
        {
            AppSettingsDialog.Builder(this).build().show()
        } else
        {
            permissionRequests()
        }
    }

}