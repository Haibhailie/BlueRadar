package ca.sfu.BlueRadar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.services.DatabaseService
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase

class LoadingActivity : AppCompatActivity() {


    private lateinit var locationTrackingServiceIntent: Intent
    private lateinit var databaseService: Intent
    private lateinit var bluetoothService: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        setupServices()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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