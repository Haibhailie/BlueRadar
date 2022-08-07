package ca.sfu.BlueRadar.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.databinding.FragmentNotificationsBinding
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.data.Device
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabaseDao
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var mapSettingsBtn: ImageView

    private lateinit var mMap: GoogleMap
    private var isCenter = false
    private lateinit var markerOptions: MarkerOptions
    private lateinit var userPoint: LatLng

    private lateinit var database: DeviceDatabase
    private lateinit var databaseDao: DeviceDatabaseDao
    private lateinit var viewModelFactory: DeviceViewModelFactory
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var devicesList: ArrayList<Device>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the database
        database = DeviceDatabase.getInstance(requireActivity())
        databaseDao = database.deviceDatabaseDao
        viewModelFactory = DeviceViewModelFactory(databaseDao)
        deviceViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DeviceViewModel::class.java]
        devicesList = ArrayList()

        // Initialize the Map Setting button
        mapSettingsBtn = binding.mapSettings
        mapSettingsBtn.setOnClickListener {
            val mapDialog = MapDialogFragment()
            mapDialog.show(childFragmentManager, "Map Setting Dialog")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.clear()
        markerOptions = MarkerOptions()

        val devicesList = deviceViewModel.allEntriesLiveData.value
        if (devicesList?.size == 0) {
            Toast.makeText(requireContext(), "No device is being connected", Toast.LENGTH_SHORT).show()

        } else {
            for (device in devicesList!!) {
                if (device.deviceLastLocation != LatLng(0.0,0.0)) {
                    markerOptions.position(device.deviceLastLocation!!)
                        .title("${device.deviceName!!}")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    mMap.addMarker(markerOptions)
                }
            }
        }

        LocationTrackingService.currentPoint.observe(viewLifecycleOwner, Observer {
            userPoint = it
            println("debug onMapReady: New Location ${userPoint.latitude}, ${userPoint.longitude}")
            zoomCurrentLocation(userPoint)
        })
    }

    private fun zoomCurrentLocation(userPoint: LatLng?) {
        if (userPoint == null) return
        if (!isCenter) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userPoint, 17f)
            mMap.animateCamera(cameraUpdate)

            markerOptions.position(userPoint).title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            mMap.addMarker(markerOptions)
            isCenter = true
        }
    }
}