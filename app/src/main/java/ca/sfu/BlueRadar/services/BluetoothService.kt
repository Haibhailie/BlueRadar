package ca.sfu.BlueRadar.services


import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.IBinder
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel


class BluetoothService : Service() {

    companion object {
        val STOP_SERVICE_ACTION = "stop service action"
        lateinit var deviceViewModel: DeviceViewModel
        lateinit var bluetoothManager: BluetoothManager
        lateinit var bluetoothAdapter: BluetoothAdapter
    }

    override fun onCreate() {
        super.onCreate()
        println("debug: onCreate called")

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
