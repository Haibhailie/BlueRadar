package ca.sfu.BlueRadar.ui.devices

import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.databinding.FragmentHomeBinding
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao
import com.google.android.gms.maps.model.LatLng

class DevicesFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var deviceNameList: ArrayList<String> = ArrayList()
    private lateinit var bluetoothManager:BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothDevices: ArrayList<String> = ArrayList()
    private var deviceAddresses: ArrayList<String> = ArrayList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var database: DeviceDatabase
    private lateinit var databaseDao: DeviceDatabaseDao
    private lateinit var viewModelFactory: DeviceViewModelFactory
    private lateinit var deviceViewModel: DeviceViewModel

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice
                        .EXTRA_DEVICE)
                    val temp = deviceViewModel.allEntriesLiveData.value
                    if (temp?.isNotEmpty() == true && device != null) {
                        for(i in temp) {
                            if(i.deviceName == device.name) {
                                i.deviceConnected = true
                                Log.d("check me again bitch", "haha")
                            }
                        }
                    }
                    Log.d("BluetoothReceiver", "BluetoothDevice ${device?.name} connected")
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val g:ArrayList<LatLng?> = ArrayList()
                    g.add(LocationTrackingService.currentPoint.value)
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice
                        .EXTRA_DEVICE)
                    val dbList = deviceViewModel.allEntriesLiveData.value
                    var lastLoc = LatLng(0.0,0.0)
                    LocationTrackingService.currentPoint.observe(viewLifecycleOwner) {
                        lastLoc = it
                    }
                    if (dbList?.isNotEmpty() == true && device != null) {
                        for(i in dbList) {
                            if(i.deviceName == device.name) {
                                i.deviceConnected = false
                                i.deviceLastLocation = lastLoc
                                Log.d("check me again bitch", i.deviceLastLocation.toString())
                            }
                        }
                    }
                    for(i in deviceViewModel.allEntriesLiveData.value!!) {
                        Log.d("check me again bitch", i.toString())
                    }
                    Log.d("BluetoothReceiver", "BluetoothDevice ${device?.name} disconnected")

                }
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    val rssi: String = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short
                        .MIN_VALUE).toString()
                    if(!deviceAddresses.contains(deviceHardwareAddress)) {
                        if (deviceHardwareAddress != null) {
                            deviceAddresses.add(deviceHardwareAddress)
                        }

                        val deviceString = if(deviceName.isNullOrEmpty()) {
                            "$deviceHardwareAddress RSSI $rssi dBm"
                        } else {
                            "$deviceName RSSI $rssi dBm"
                        }
                        bluetoothDevices.add(deviceString)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = getSystemService(requireContext(), BluetoothManager::class.java)!!
        bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}.launch(enableBtIntent)
        }

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter().apply{
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        }

        requireActivity().registerReceiver(receiver, filter)
        database = DeviceDatabase.getInstance(requireActivity())
        databaseDao = database.deviceDatabaseDao
        viewModelFactory = DeviceViewModelFactory(databaseDao)
        deviceViewModel = ViewModelProvider(requireActivity(),viewModelFactory)[DeviceViewModel::class.java]



        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach{ device ->
            val deviceName = device.name
            deviceNameList.add(deviceName)
            val btDevice = Device()
            btDevice.deviceName = deviceName
            btDevice.deviceType = device.type.toString()
            btDevice.deviceTracking = true
            if(deviceViewModel.allEntriesLiveData.value?.contains(btDevice) == false){
                deviceViewModel.insert(btDevice)
            }

            val deviceHardwareAddress = device.address // MAC Address
            Log.d("bonded-device-name", btDevice.deviceName)
            Log.d("bonded-device-address", deviceHardwareAddress)
        }

        // checking status
        val toast: Toast
        if (bluetoothAdapter.isEnabled) {
            Log.d("bluetooth-checker", "bluetooth enabled")
            toast = Toast.makeText(requireContext(), "bluetooth enabled", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            Log.d("bluetooth-checker", "bluetooth disabled")
            toast = Toast.makeText(requireContext(), "bluetooth disabled", Toast.LENGTH_SHORT)
            toast.show()
        }
        bluetoothAdapter.startDiscovery()

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val listView: ListView = binding.myListView

        val arrayList: ArrayList<Device> = ArrayList()
        val arrayAdapter = DeviceListAdapter(requireActivity(),arrayList)
        listView.adapter = arrayAdapter
        val check = bluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) ==
                BluetoothHeadset
            .STATE_CONNECTED
        Log.d("checking connection", check.toString())
        val li = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        Log.d("Connected Devices: ", li.size.toString())
        deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        deviceViewModel.deleteAll()
        requireActivity().unregisterReceiver(receiver)
    }
}