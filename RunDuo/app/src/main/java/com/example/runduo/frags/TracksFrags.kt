package com.example.runduo.frags

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runduo.DataRun
import com.example.runduo.R
import com.example.runduo.misc.fixValues.PAUSE_SERVICE
import com.example.runduo.misc.fixValues.PolyL_COLOR
import com.example.runduo.misc.fixValues.PolyL_WIDTH
import com.example.runduo.misc.fixValues.STARTING_OR_RESUME_SERVICE
import com.example.runduo.misc.fixValues.STOP_SERVICE
import com.example.runduo.misc.fixValues.ZOOM_OPS
import com.example.runduo.misc.utilTracks
import com.example.runduo.servicesTracks
import com.example.runduo.models.viewModelMain
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.tracks_frags.*
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class TracksFrags : Fragment(R.layout.tracks_frags) {

    private val viewingModel: viewModelMain by viewModels()
    private var map:GoogleMap? = null
    private var nowTracking = false
    private var pathingPath = mutableListOf<MutableList<LatLng>>()
    private var timingInMillis = 0L
    private var menu: Menu? = null
    private lateinit var sharedPref: SharedPreferences

   // @set:Inject
    //var userWeight: Float = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPref = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewingMap.onCreate(savedInstanceState)
        startRunning.setOnClickListener{
            runChanges()
        }
        stopRunning.setOnClickListener{
            seeAllTracks()
            savingRuns()
        }
        viewingMap.getMapAsync{
            map = it
            addEverything()
        }
        observersSub()
    }

    private fun updateNewPolyLine()
    {
        if(pathingPath.isNotEmpty() && pathingPath.last().size > 1)
        {
            val secondLast = pathingPath.last()[pathingPath.last().size - 2]
            val theLast = pathingPath.last().last()
            val polySettings = PolylineOptions().color(PolyL_COLOR).width(PolyL_WIDTH)
                .add(secondLast).add(theLast)
            map?.addPolyline(polySettings)
        }
    }

    private fun addEverything()
    {
        for(Pline in pathingPath){
            val polySettings = PolylineOptions().color(PolyL_COLOR).width(PolyL_WIDTH)
                .addAll(Pline)
            map?.addPolyline(polySettings)

        }
    }

    private fun seeAllTracks() {
        val position = LatLngBounds.Builder()
        for(PLine in pathingPath){
            for(type in PLine) {
                position.include(type)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                position.build(),
                viewingMap.width,
                viewingMap.height,
                (viewingMap.height * 0.05f).toInt()
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tracking_tool, menu)
        this.menu = menu
    }

    private fun panCameras()
    {
        if(pathingPath.isNotEmpty() && pathingPath.last().isNotEmpty())
        {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathingPath.last().last(), ZOOM_OPS
                )
            )
        }
    }

    private fun savingRuns()
    {
        val userWeight = sharedPref.getFloat("weight",80f)
       map?.snapshot { bmp ->
            var totalDistMeter = 0
           for (poly in pathingPath)
           {
               totalDistMeter += utilTracks.compiledLength(poly).toInt()
           }
           val speedAvg = kotlin.math.round((totalDistMeter/1000f) / (timingInMillis/1000f/60/60) * 10 / 10f)
           val timing = Calendar.getInstance().timeInMillis
           val burnedCalories = ((totalDistMeter)/1000f * userWeight).toInt()
           val userRun = DataRun(bmp,speedAvg, timing ,timingInMillis,totalDistMeter,burnedCalories)
           viewingModel.addedRun(userRun)
           Snackbar.make(
               requireActivity().findViewById(R.id.baseView),
               "Succesfully Saved Run",
                Snackbar.LENGTH_LONG
           ).show()
           runStopped()
       }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(timingInMillis > 0L)
        {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun trackingForUpdate(nowTracking: Boolean)
    {
        this.nowTracking = nowTracking
        if(!nowTracking && timingInMillis > 0L)
        {
            startRunning.text = "Start Running"
            stopRunning.visibility = View.VISIBLE
        } else if (nowTracking)
        {
            startRunning.text = "Stop Running"
            menu?.getItem(0)?.isVisible = true
            stopRunning.visibility = View.GONE
        }
    }

    private fun runChanges()
    {
        if (nowTracking)
        {
            menu?.getItem(0)?.isVisible = true
            servicesPaused()
        } else
        {
            servicesStartORResume()
            Timber.d("Services started")
        }
    }

    private fun servicesStartORResume() =
    Intent(requireContext(), servicesTracks::class.java).also {
        it.action = STARTING_OR_RESUME_SERVICE
        requireContext().startService(it)
    }

    private fun servicesPaused()  =
        Intent(requireContext(), servicesTracks::class.java).also {
            it.action = PAUSE_SERVICE
            requireContext().startService(it)
        }

    private fun servicesStop() =
    Intent(requireContext(), servicesTracks::class.java).also {
        it.action = STOP_SERVICE
        requireContext().startService(it)
    }

    private fun dialogForCancel()
    {
        val cancelDialog = MaterialAlertDialogBuilder(requireContext(), R.style.Alertlog)
        .setMessage("Current Run Data Will Be Wiped")
            .setTitle("Cancel This Run?")
            .setIcon(R.drawable.remove_icon)
            .setPositiveButton("Yes") { _, _ ->
                runStopped()
            }
            .setNegativeButton("No") { dialogUI, _ ->
                dialogUI.cancel()
            }
            .create()
        cancelDialog.show()
    }

    private fun runStopped() {
        messageTimer.text = "00:00:00:00"
        servicesStop()
        findNavController().navigate(R.id.action_tracksFrags_to_runningFrags)
    }

    override fun onOptionsItemSelected(items: MenuItem): Boolean {
        when(items.itemId)
        {
            R.id.miCancelTracking -> {
                dialogForCancel()
            }
        }
        return super.onOptionsItemSelected(items)
    }

    private fun observersSub()
    {
        servicesTracks.nowTracking.observe(viewLifecycleOwner, Observer {
            trackingForUpdate(it)
        })

        servicesTracks.pathingPath.observe(viewLifecycleOwner, Observer {
            pathingPath = it
            panCameras()
            updateNewPolyLine()
        })

        servicesTracks.timingMiliRun.observe(viewLifecycleOwner, Observer {
            timingInMillis = it
            val timeFormat = utilTracks.formatTimingStopWatch(timingInMillis,true)
            messageTimer.text = timeFormat
        })
    }

    override fun onStart()
    {
        super.onStart()
        viewingMap?.onStart()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        viewingMap?.onLowMemory()
    }
    override fun onPause(){
        super.onPause()
        viewingMap?.onPause()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewingMap?.onSaveInstanceState(outState)
    }
    override fun onResume() {
        super.onResume()
        viewingMap?.onResume()
    }
    override fun onDestroy() {
        super.onDestroy()
        viewingMap?.onDestroy()
    }
    override fun onStop() {
        super.onStop()
        viewingMap?.onStop()
    }

}