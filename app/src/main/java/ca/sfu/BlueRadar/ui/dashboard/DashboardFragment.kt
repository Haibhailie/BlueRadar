package ca.sfu.BlueRadar.ui.dashboard

import android.bluetooth.BluetoothDevice
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.databinding.FragmentDashboardBinding
import ca.sfu.BlueRadar.ui.devices.DeviceRecyclerAdapter
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.DevicesFragment
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao
import ca.sfu.BlueRadar.util.Util

class DashboardFragment : Fragment() {
    private lateinit var database: DeviceDatabase
    private lateinit var databaseDao: DeviceDatabaseDao
    private lateinit var viewModelFactory: DeviceViewModelFactory
    private lateinit var deviceViewModel: DeviceViewModel

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

        database = DeviceDatabase.getInstance(requireActivity())
        databaseDao = database.deviceDatabaseDao
        viewModelFactory = DeviceViewModelFactory(databaseDao)
        deviceViewModel = ViewModelProvider(requireActivity(),viewModelFactory)[DeviceViewModel::class.java]
        if(!deviceViewModel.allEntriesLiveData.value.isNullOrEmpty()){
//            if(!deviceViewModel.activeEntriesLiveData.value.isNullOrEmpty()) {
//                setupRecyclerView()
//                for(i in deviceViewModel.activeEntriesLiveData.value!!) {
//                    Log.d("check_from_dash_active", i.toString())
//                }
//            }
            setupRecyclerView()
            for(i in deviceViewModel.allEntriesLiveData.value!!) {
                Log.d("check_from_dash", i.toString())
            }
        }

        return root
    }

    private fun setupRecyclerView() {
        recyclerView = binding.devicesRecyclerDashboard
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        arrayList = ArrayList()
        recyclerAdapter = DashboardRecyclerAdapter(requireActivity(), arrayList, deviceViewModel)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        arrayList = ArrayList()
        recyclerAdapter = DashboardRecyclerAdapter(requireActivity(), arrayList, deviceViewModel)
        Util.removeDuplicates(arrayList)
        deviceViewModel.activeEntriesLiveData.observe(viewLifecycleOwner) {
            recyclerAdapter.replace(it)
            recyclerAdapter.notifyDataSetChanged()
        }
        recyclerView.adapter = recyclerAdapter

        recyclerView.addItemDecoration(
            MarginItemDecoration(25)
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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