package ca.sfu.BlueRadar.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng

class LocationTrackingService: LifecycleService() {

    private lateinit var myBroadcastReceiver: MyBroadCastReceiver
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val STOP_TRACKING_SERVICE = "Stop Tracking Service"
        val isTracking = MutableLiveData<Boolean>()
        val currentPoint = MutableLiveData<LatLng>()
    }

    override fun onCreate() {
        super.onCreate()
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            currentPoint.value?.apply {
                currentPoint.postValue(this)
            }
            isTracking.postValue(true)

            fusedLocationProviderClient = FusedLocationProviderClient(this@LocationTrackingService)
            isTracking.observe(this@LocationTrackingService, Observer {
                updateLocationTracking(it)
            })

            myBroadcastReceiver = MyBroadCastReceiver()
            val intentFilter = IntentFilter()
            intentFilter.addAction(STOP_TRACKING_SERVICE)
            registerReceiver(myBroadcastReceiver, intentFilter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        unregisterReceiver(myBroadcastReceiver)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            val request = LocationRequest.create().apply {
                interval = 2000L
                fastestInterval = 2000L
                priority = PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.let { locations ->
                for (location in locations) {
                    updateCurrentPoint(location)
                }
            }
        }
    }

    private fun updateCurrentPoint(location: Location?) {
        location?.let {
            val newPoint = LatLng(location.latitude, location.longitude)
            currentPoint.value = newPoint
        }
    }

    inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            stopSelf()
            unregisterReceiver(myBroadcastReceiver)
        }
    }
}