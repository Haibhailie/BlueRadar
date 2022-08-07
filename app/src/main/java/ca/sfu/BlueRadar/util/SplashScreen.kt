package ca.sfu.BlueRadar.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < 29) return
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ),
                0
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Handler().postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 3000)
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT)
                    finish()
                }
            }
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