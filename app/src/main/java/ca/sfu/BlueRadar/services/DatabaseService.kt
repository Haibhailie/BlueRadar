package ca.sfu.BlueRadar.services


import android.app.Service
import android.content.Intent
import android.os.IBinder

class DatabaseService : Service() {

//    private lateinit var database: DeviceDatabase
//    private lateinit var databaseDao: DeviceDatabaseDao

    companion object {
        val STOP_SERVICE_ACTION = "stop service action"
//        lateinit var viewModelFactory: DeviceViewModelFactory
    }

    override fun onCreate() {
        super.onCreate()
        println("debug: onCreate called")
//        setupDatabase()
    }

//    private fun setupDatabase() {
//        database = DeviceDatabase.getInstance(this)
//        databaseDao = database.deviceDatabaseDao
//        viewModelFactory = DeviceViewModelFactory(databaseDao)
//    }

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
