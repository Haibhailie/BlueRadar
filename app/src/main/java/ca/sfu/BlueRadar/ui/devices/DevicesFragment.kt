package ca.sfu.BlueRadar.ui.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.databinding.FragmentDevicesBinding
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao
import com.google.android.gms.maps.model.LatLng
import ca.sfu.BlueRadar.util.Util.removeDuplicates


class DevicesFragment : Fragment() {
    private var _binding: FragmentDevicesBinding? = null
    private var deviceNameList: ArrayList<String> = ArrayList()
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothDevices: ArrayList<String> = ArrayList()
    private var deviceAddresses: ArrayList<String> = ArrayList()

    private val binding get() = _binding!!
    private lateinit var database: DeviceDatabase
    private lateinit var databaseDao: DeviceDatabaseDao
    private lateinit var viewModelFactory: DeviceViewModelFactory
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayList: ArrayList<Device>
    private lateinit var recyclerAdapter: DeviceRecyclerAdapter

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {

                    val device: BluetoothDevice? = intent.getParcelableExtra(
                        BluetoothDevice
                            .EXTRA_DEVICE
                    )
                    val temp = deviceViewModel.allEntriesLiveData.value
                    if (temp?.isNotEmpty() == true && device != null) {
                        for (i in temp) {
                            if (i.deviceName == device.name) {

                                i.deviceConnected = true
                                deviceViewModel.updateConnected(i)
                                println("CONNECTED DEVICE IS: ${i}")
                                updateRecyclerView()
                            }
                        }
                    }
                    Log.d("BluetoothReceiver", "BluetoothDevice ${device?.name} connected")
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val g: ArrayList<LatLng?> = ArrayList()
                    g.add(LocationTrackingService.currentPoint.value)
                    val device: BluetoothDevice? = intent.getParcelableExtra(
                        BluetoothDevice
                            .EXTRA_DEVICE
                    )
                    val dbList = deviceViewModel.allEntriesLiveData.value
                    var lastLoc = LatLng(0.0, 0.0)
                    LocationTrackingService.currentPoint.observe(viewLifecycleOwner) {
                        lastLoc = it
                    }
                    if (dbList?.isNotEmpty() == true && device != null) {
                        for (i in dbList) {
                            if (i.deviceName == device.name) {
                                i.deviceConnected = false
                                i.deviceLastLocation = lastLoc
                                deviceViewModel.updateConnected(i)
                                println("CONNECTED DEVICE IS: $i")
                                updateRecyclerView()
                                val toast: Toast = Toast.makeText(
                                    requireContext(),
                                    "Last Loc: ${lastLoc.latitude}, ${lastLoc.longitude}",
                                    Toast
                                        .LENGTH_SHORT
                                )
                                toast.show()
                            }
                        }
                    }
                    for (i in deviceViewModel.allEntriesLiveData.value!!) {
                        Log.d("check me", i.toString())
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
                    val rssi: String = intent.getShortExtra(
                        BluetoothDevice.EXTRA_RSSI, Short
                            .MIN_VALUE
                    ).toString()
                    if (!deviceAddresses.contains(deviceHardwareAddress)) {
                        if (deviceHardwareAddress != null) {
                            deviceAddresses.add(deviceHardwareAddress)
                        }
                        val deviceString = if (deviceName.isNullOrEmpty()) {
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
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}.launch(
                enableBtIntent
            )
        }

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        }
        requireActivity().registerReceiver(receiver, filter)
        firstBootSetup()
    }

    private fun firstBootSetup() {
        database = DeviceDatabase.getInstance(requireActivity())
        databaseDao = database.deviceDatabaseDao
        viewModelFactory = DeviceViewModelFactory(databaseDao)
        deviceViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DeviceViewModel::class.java]


        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            deviceNameList.add(deviceName)
            val btDevice = Device()
            btDevice.deviceName = deviceName
            btDevice.deviceType = device.type.toString()
            btDevice.deviceMacAddress = device.address

            var liveList = deviceViewModel.allEntriesLiveData.value
            var isDuplicate = false

            if (liveList != null) {
                for(check in liveList){
                    if(check.deviceName == btDevice.deviceName)
                        isDuplicate = true
                }
            }

            if (!isDuplicate) {
                deviceViewModel.insert(btDevice)
                println(btDevice)
            }

            val deviceHardwareAddress = device.address // MAC Address
            Log.d("bonded-device-name", btDevice.deviceName)
            Log.d("bonded-device-address", deviceHardwareAddress)
        }

        // checking status
        val toast: Toast
        if (!bluetoothAdapter.isEnabled) {
            Log.d("bluetooth-checker", "Bluetooth is Disabled")
            toast = Toast.makeText(requireContext(), "Bluetooth is Disabled", Toast.LENGTH_SHORT)
            toast.show()
        }
        bluetoothAdapter.startDiscovery()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        removeDuplicates(arrayList)
        updateRecyclerView()
    }


    private fun setupRecyclerView() {
        recyclerView = binding.devicesRecycler
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        arrayList = ArrayList()
        recyclerAdapter = DeviceRecyclerAdapter(requireActivity(), arrayList, deviceViewModel)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        arrayList = ArrayList()
        recyclerAdapter = DeviceRecyclerAdapter(requireActivity(), arrayList, deviceViewModel)
        removeDuplicates(arrayList)
        deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
            recyclerAdapter.replace(it)
            recyclerAdapter.notifyDataSetChanged()
        }
        recyclerView.adapter = recyclerAdapter

        recyclerView.addItemDecoration(
            MarginItemDecoration(25)
        )
    }

    private fun updateRecyclerView  () {

        deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
            recyclerAdapter.replace(it)
            recyclerAdapter.notifyDataSetChanged()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDevicesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupRecyclerView()
        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        //deviceViewModel.deleteAll()
        requireActivity().unregisterReceiver(receiver)
    }

    class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceSize
                }
                left = spaceSize
                right = spaceSize
                bottom = spaceSize
            }
        }
    }
}