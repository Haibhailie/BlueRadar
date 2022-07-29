package ca.sfu.BlueRadar.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ca.sfu.BlueRadar.MainActivity
import ca.sfu.BlueRadar.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng

class LocationTrackingService: LifecycleService() {

    private var notificationID = 1
    private var channelID = "notification channel"
    private var channelName = "tracking channel"
    private lateinit var myBroadcastReceiver: MyBroadCastReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val STOP_NOTIFY_SERVICE = "Stop Notification Service"
        val isTracking = MutableLiveData<Boolean>()
        val currentPoint = MutableLiveData<LatLng>()
    }

    override fun onCreate() {
        super.onCreate()
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            isTracking.postValue(false)
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            startNotification()

            fusedLocationProviderClient = FusedLocationProviderClient(this@LocationTrackingService)
            isTracking.observe(this@LocationTrackingService, Observer {
                updateLocationTracking(it)
            })

            myBroadcastReceiver = MyBroadCastReceiver()
            val intentFilter = IntentFilter()
            intentFilter.addAction(STOP_NOTIFY_SERVICE)
            registerReceiver(myBroadcastReceiver, intentFilter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        println("debug: onStartCommand called")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(notificationID)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            val request = LocationRequest().apply {
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
                    //println("debug: New Location ${location.latitude}, ${location.longitude}")
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

    private fun startNotification() {
        currentPoint.value?.apply {
            currentPoint.postValue(this)
        }
        isTracking.postValue(true)

        if (Build.VERSION.SDK_INT > 26) {
            val notificationChannel = NotificationChannel(
                channelID, channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainActivityIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.blueradar_logo)
            .setContentTitle("BlueRadar")
            .setContentText("App is tracking your location")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
        val notification = notificationBuilder.build()
        notificationManager.notify(notificationID, notification)
    }

    inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            notificationManager.cancel(notificationID)
            stopSelf()
            unregisterReceiver(myBroadcastReceiver)
        }
    }
}