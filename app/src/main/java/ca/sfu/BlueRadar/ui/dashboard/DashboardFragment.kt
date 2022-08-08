package ca.sfu.BlueRadar.ui.dashboard

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.databinding.FragmentDashboardBinding
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.services.NotificationService
import ca.sfu.BlueRadar.ui.devices.DeviceRecyclerAdapter
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.DevicesFragment
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao
import ca.sfu.BlueRadar.util.Util
import com.mikepenz.iconics.Iconics.applicationContext

class DashboardFragment : Fragment() {
    private lateinit var database: DeviceDatabase
    private lateinit var databaseDao: DeviceDatabaseDao
    private lateinit var viewModelFactory: DeviceViewModelFactory
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var preferences: SharedPreferences

    private lateinit var notificationIntent: Intent

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayList: ArrayList<Device>
    private lateinit var recyclerAdapter: DashboardRecyclerAdapter
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var check: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        deviceViewModel =
            BluetoothService.deviceViewModel

        bluetoothManager = ContextCompat.getSystemService(
            requireContext(),
            BluetoothManager::class.java
        )!!
        bluetoothAdapter = bluetoothManager.adapter
        requireActivity().registerReceiver(BluetoothService.receiver, Util.filter)
        setupRecyclerView()
        deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
            println("DEVICE: $it\n")
            if (!deviceViewModel.allEntriesLiveData.value.isNullOrEmpty()) {
                for (i in deviceViewModel.allEntriesLiveData.value!!) {
                    Log.d("check_from_dash", i.toString())
                }
            } else {
                val textView: TextView = binding.dashboardTitle
                dashboardViewModel.text.observe(viewLifecycleOwner) { g ->
                    textView.text = g
                    textView.setTextColor(Color.GRAY)
                }
            }
        }

//        var status: Boolean = false
//
//        deviceViewModel.activeEntriesLiveData.observe(viewLifecycleOwner) {
//            if (!deviceViewModel.activeEntriesLiveData.value.isNullOrEmpty()) {
//                notificationIntent = Intent(context, NotificationService::class.java)
//                context?.startService(notificationIntent)
//            } else {
//                val intent = Intent()
//                intent.action = NotificationService.STOP_SERVICE_ACTION
//                context?.sendBroadcast(intent)
//            }
//        }
//
//        preferences = PreferenceManager.getDefaultSharedPreferences(context!!)
//        val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
//            when (key){
//                "preference_notifications" -> {
//                    val prefs = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
//                    val checkBox = prefs?.getBoolean("preference_notifications", true)
//                    if (checkBox != null) {
//                        check = checkBox
//                    }
//                    if(checkBox == true && status){
////                        notificationIntent = Intent(context, NotificationService::class.java)
////                        context?.startService(notificationIntent)
//                    }else{
//                        val intent = Intent()
//                        intent.action = NotificationService.STOP_SERVICE_ACTION
//                        context?.sendBroadcast(intent)
//                    }
//                }
//            }
//        }
//        preferences.registerOnSharedPreferenceChangeListener(listener)

//        if(check && status){
//            notificationIntent = Intent(context, NotificationService::class.java)
//            context?.startService(notificationIntent)
//        }else{
//            val intent = Intent()
//            intent.action = NotificationService.STOP_SERVICE_ACTION
//            context?.sendBroadcast(intent)
//        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (this::recyclerAdapter.isInitialized) {
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    fun setupRecyclerView() {
        recyclerView = binding.devicesRecyclerDashboard
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        arrayList = ArrayList()

        recyclerAdapter =
            DashboardRecyclerAdapter(
                requireActivity(), arrayList, deviceViewModel
            )

        deviceViewModel.activeEntriesLiveData.observe(viewLifecycleOwner) {
            recyclerAdapter.replace(it)
            recyclerAdapter.notifyDataSetChanged()
        }

        recyclerView.adapter = recyclerAdapter
        recyclerView.addItemDecoration(
            DevicesFragment.MarginItemDecoration(25)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
