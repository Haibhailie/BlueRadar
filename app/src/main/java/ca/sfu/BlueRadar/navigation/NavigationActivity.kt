package ca.sfu.BlueRadar.navigation

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer

import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.services.LocationTrackingService
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONObject

class NavigationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userPoint: LatLng
    private var deviceLastLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Initialize map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.navigation_map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get device last location and user's current location
        deviceLastLocation = intent.extras!!.getParcelable<LatLng>("deviceLocation")
        LocationTrackingService.currentPoint.observe(this, Observer {
            userPoint = it
        })


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val markerUserLocation = MarkerOptions().position(userPoint).title("You are here")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        val markerDeviceLocation = MarkerOptions().position(deviceLastLocation!!)
            .title("Your device")
        mMap.addMarker(markerUserLocation)
        mMap.addMarker(markerDeviceLocation)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLastLocation!!,17f))

        val fromLat = userPoint.latitude
        val fromLng = userPoint.longitude
        val toLat = deviceLastLocation!!.latitude
        val toLng = deviceLastLocation!!.longitude

        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=$fromLat,$fromLng&destination=$toLat,$toLng&key=AIzaSyDEbPM7EHRUeO-MWEMSVhXcvNZGN-yRJvw"
        val directionsRequest = object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)

            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                this.mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
            }
        }, Response.ErrorListener {
                _ ->
        }){}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }
}