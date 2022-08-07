package ca.sfu.BlueRadar.ui.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.databinding.FragmentDevicesBinding
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.util.Util
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup

class DevicesFragment : Fragment() {

    //Global Variables
    private var _binding: FragmentDevicesBinding? = null
    private var viewDevices = 0
    private val binding get() = _binding!!

    //Global Lateinits
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayList: ArrayList<Device>
    private lateinit var recyclerAdapter: DeviceRecyclerAdapter
    private lateinit var buttonGroup: ThemedToggleButtonGroup

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

        requireActivity().registerReceiver(BluetoothService.receiver, Util.filter)
        deviceViewModel = BluetoothService.deviceViewModel

    }

    override fun onResume() {
        super.onResume()
        updateRecyclerView()
    }

    private fun setupRecyclerView() {

        recyclerView = binding.devicesRecyclerActive
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        arrayList = ArrayList()
        recyclerAdapter =
            DeviceRecyclerAdapter(
                requireActivity(), arrayList, deviceViewModel
            )

        when (viewDevices) {
            0 -> {
                //requireActivity().viewModelStore.clear()
                deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
                    recyclerAdapter.replace(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
            1 -> {
                //Change this once Mongo Migration is complete
                //requireActivity().viewModelStore.clear()
                deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
                    recyclerAdapter.replace(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
            else -> {
                //Change this once Mongo Migration is complete
                //requireActivity().viewModelStore.clear()
                deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
                    recyclerAdapter.replace(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }

        recyclerView.adapter = recyclerAdapter
        recyclerView.addItemDecoration(
            MarginItemDecoration(25)
        )
    }

    private fun updateRecyclerView() {

        when (viewDevices) {
            0 -> {
                //requireActivity().viewModelStore.clear()
                deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
                    recyclerAdapter.replace(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
            1 -> {
                //Change this once Mongo Migration is complete
                //requireActivity().viewModelStore.clear()
                deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
                    recyclerAdapter.replace(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
            else -> {
                //Change this once Mongo Migration is complete
                //requireActivity().viewModelStore.clear()
                deviceViewModel.allEntriesLiveData.observe(viewLifecycleOwner) {
                    recyclerAdapter.replace(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }
        recyclerView.adapter = recyclerAdapter
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