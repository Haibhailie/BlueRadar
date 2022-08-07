package ca.sfu.BlueRadar.ui.dashboard

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.databinding.FragmentDashboardBinding
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.ui.devices.DevicesFragment
import ca.sfu.BlueRadar.ui.devices.data.Database
import ca.sfu.BlueRadar.ui.devices.data.Device
//import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.util.Util
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
//    private lateinit var database: DeviceDatabase
//    private lateinit var databaseDao: DeviceDatabaseDao
//    private lateinit var viewModelFactory: DeviceViewModelFactory
//    private lateinit var deviceViewModel: DeviceViewModel

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayList: ArrayList<Device>
    private lateinit var recyclerAdapter: DashboardRecyclerAdapter
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        deviceViewModel =
//            BluetoothService.deviceViewModel

        bluetoothManager = ContextCompat.getSystemService(
            requireContext(),
            BluetoothManager::class.java
        )!!
        bluetoothAdapter = bluetoothManager.adapter
        requireActivity().registerReceiver(BluetoothService.receiver, Util.filter)
        setupRecyclerView()
//        deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
//            println("DEVICE: $it\n")
//            if (!deviceViewModel.allEntriesLiveData.value.isNullOrEmpty()) {
//                for (i in deviceViewModel.allEntriesLiveData.value!!) {
//                    Log.d("check_from_dash", i.toString())
//                }
//            } else {
//                val textView: TextView = binding.dashboardTitle
//                dashboardViewModel.text.observe(viewLifecycleOwner) { g ->
//                    textView.text = g
//                    textView.setTextColor(Color.GRAY)
//                }
//            }
//        }
//        val devicesList = Database.getAllEntries()
//        if (!devicesList.isNullOrEmpty()) {
//            for (i in devicesList) {
//                Log.d("check_from_dash", i.toString())
//            }
//        } else {
//            val textView: TextView = binding.dashboardTitle
//            dashboardViewModel.text.observe(viewLifecycleOwner) { g ->
//                textView.text = g
//                textView.setTextColor(Color.GRAY)
//            }
//        }

        val devicesList = Database.realm.query(Device::class)
        CoroutineScope(Dispatchers.Main).launch {
            val devicesFlow = devicesList.asFlow()
            val devicesSubscription = devicesFlow.collect{ changes: ResultsChange<Device> ->
                if(Database.getAllEntries().isNotEmpty()) {
                    for (i in Database.getAllEntries()) {
                        Log.d("check_from_dash", i.toString())
                    }
                }
                else {
                    val textView: TextView = binding.dashboardTitle
                    dashboardViewModel.text.observe(viewLifecycleOwner) { g ->
                        textView.text = g
                        textView.setTextColor(Color.GRAY)
                    }
                }
            }
        }

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
                requireActivity(), arrayList
            )

//        deviceViewModel.activeEntriesLiveData.observe(viewLifecycleOwner) {
//            recyclerAdapter.replace(it)
//            recyclerAdapter.notifyDataSetChanged()
//        }
        val devicesList = Database.realm.query(Device::class)
        CoroutineScope(Dispatchers.Main).launch {
            devicesList.asFlow().collect{
                recyclerAdapter.replace(Database.getAllActiveEntries())
                recyclerAdapter.notifyDataSetChanged()
            }
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
