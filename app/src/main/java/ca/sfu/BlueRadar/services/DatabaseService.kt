package ca.sfu.BlueRadar.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.DevicesFragment
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao
import java.text.SimpleDateFormat
import java.util.*

class DatabaseService : Service() {

    private lateinit var database: DeviceDatabase
    private lateinit var databaseDao: DeviceDatabaseDao

    companion object {
        val STOP_SERVICE_ACTION = "stop service action"
        lateinit var viewModelFactory: DeviceViewModelFactory
    }

    override fun onCreate() {
        super.onCreate()
        println("debug: onCreate called")
        setupDatabase()
    }

    private fun setupDatabase() {
        database = DeviceDatabase.getInstance(this)
        databaseDao = database.deviceDatabaseDao
        viewModelFactory = DeviceViewModelFactory(databaseDao)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("debug: onStartCommand called")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        println("debug: onDestroy called")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}
