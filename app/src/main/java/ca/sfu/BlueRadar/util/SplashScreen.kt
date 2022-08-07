package ca.sfu.BlueRadar.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.MainActivity
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.services.DatabaseService
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao

class SplashScreen : AppCompatActivity() {

    private lateinit var locationTrackingServiceIntent: Intent
    private lateinit var databaseService: Intent
    private lateinit var bluetoothService: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        checkPermissions()
        setupServices()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT < 29) return
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ),
                0
            )
        }
    }

    private fun setupServices() {
        startLocationTrackingService()
        startDatabaseService()
        startBluetoothService()
    }

    private fun startLocationTrackingService() {
        locationTrackingServiceIntent = Intent(this, LocationTrackingService::class.java)
        this.startService(locationTrackingServiceIntent)
    }

    private fun startDatabaseService() {
        databaseService = Intent(this, DatabaseService::class.java)
        this.startService(databaseService)
    }

    private fun startBluetoothService() {
        BluetoothService.deviceViewModel = ViewModelProvider(
            this,
            DeviceViewModelFactory(DeviceDatabase.getInstance(this).deviceDatabaseDao)
        )[DeviceViewModel::class.java]
        bluetoothService = Intent(this, BluetoothService::class.java)
        this.startService(bluetoothService)

    }
}