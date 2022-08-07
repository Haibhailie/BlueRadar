package ca.sfu.BlueRadar.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.MainActivity
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.services.DatabaseService
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    private lateinit var locationTrackingServiceIntent: Intent
    private lateinit var databaseService: Intent
    private lateinit var bluetoothService: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
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