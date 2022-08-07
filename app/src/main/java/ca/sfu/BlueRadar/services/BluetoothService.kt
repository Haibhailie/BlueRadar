package ca.sfu.BlueRadar.services

import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.data.Database
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.util.DisconnectNotification
import com.google.android.gms.maps.model.LatLng
import kotlin.collections.ArrayList


class BluetoothService() : Service() {

    companion object {
        val STOP_SERVICE_ACTION = "stop service action"
        lateinit var deviceViewModel: DeviceViewModel
        lateinit var bluetoothManager: BluetoothManager
        lateinit var bluetoothAdapter: BluetoothAdapter
        lateinit var receiver: BroadcastReceiver
        lateinit var pairedDevices: Set<BluetoothDevice>

        var deviceNameList: ArrayList<String> = ArrayList()
    }

    private var notificationID = 2
    private var channelID = "notification channel"

    override fun onCreate() {
        super.onCreate()
        println("debug: onCreate called")
        receiver = receiverSkeleton
        bluetoothManager = ContextCompat.getSystemService(
            this,
            BluetoothManager::class.java
        )!!
        bluetoothAdapter = bluetoothManager.adapter
        setupPairedDevices()
    }

    fun setupPairedDevices() {
        pairedDevices = bluetoothAdapter.bondedDevices
        pairedDevices.forEach { device ->
            val deviceName = device.name
            deviceNameList.add(deviceName)
            val btDevice = Device()
            btDevice.deviceName = deviceName
            btDevice.deviceType = device.type.toString()
            btDevice.deviceMacAddress = device.address

//            var liveList = deviceViewModel.allEntriesLiveData.value
            var liveList = Database.getAllEntries()
            var isDuplicate = false

            if (liveList != null) {
                for (check in liveList) {
                    if (check.deviceName == btDevice.deviceName)
                        isDuplicate = true
                }
            }

            if (!isDuplicate) {
//                deviceViewModel.insert(btDevice)
                Database.insert(btDevice)
            }

        }
        bluetoothAdapter.startDiscovery()
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

    fun showDeviceDisconnectionNotification(deviceName: String) {
        var textTitle = "BlueRadar Alert"
        var textContent = "$deviceName has been disconnected."
        var builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.blueradar_logo)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationID, builder.build())
        }
    }

    //Bluetooth broadcast receiver
    private val receiverSkeleton = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {

                BluetoothDevice.ACTION_ACL_CONNECTED -> {

                    //Check if any paired bluetooth devices are available
                    val device: BluetoothDevice? = intent.getParcelableExtra(
                        BluetoothDevice
                            .EXTRA_DEVICE
                    )
                    val temp = Database.getAllEntries()
                    var currentLoc = LatLng(0.0, 0.0)

                    LocationTrackingService.currentPoint.observeForever() {
                        currentLoc = it
                    }

                    //Save device to database on connect if not already available (new device sync)
                    if (temp?.isNotEmpty() == true && device != null) {
                        for (i in temp) {
                            if (i.deviceName == device.name) {

                                i.deviceConnected = true
                                Database.update(i)
                                if (i.deviceTracking) {
                                    i.deviceLastLocation = currentLoc
                                }
                            }
                        }
                    }
                    Log.d("BluetoothReceiver", "BluetoothDevice ${device?.name} connected")
                }

                //Save latlng upon disconnect and update status
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val g: ArrayList<LatLng?> = ArrayList()
                    g.add(LocationTrackingService.currentPoint.value)
                    val device: BluetoothDevice? = intent.getParcelableExtra(
                        BluetoothDevice
                            .EXTRA_DEVICE
                    )
                    val dbList = Database.getAllEntries()
                    var lastLoc = LatLng(0.0, 0.0)
                    LocationTrackingService.currentPoint.observeForever() {
                        lastLoc = it
                    }
                    if (dbList?.isNotEmpty() == true && device != null) {
                        for (i in dbList) {
                            if (i.deviceName == device.name) {
                                i.deviceConnected = false
                                i.deviceLastLocation = lastLoc
                                Database.update(i)

                            }
                        }
                    }
                    for (i in Database.getAllEntries()) {
                        Log.d("check me", i.toString())
                    }
                    Log.d("BluetoothReceiver", "BluetoothDevice ${device?.name} disconnected")
                    device?.name?.let { showDeviceDisconnectionNotification(it) }
                }

                //Add device to arrayList
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    val rssi: String = intent.getShortExtra(
                        BluetoothDevice.EXTRA_RSSI, Short
                            .MIN_VALUE
                    ).toString()
                }
            }
        }
    }
}
