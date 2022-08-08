package ca.sfu.BlueRadar.navigation

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
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
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import org.json.JSONObject


class NavigationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userPoint: LatLng
    private var deviceLastLocation: LatLng? = null
    private var deviceName: String? = null
    private lateinit var mapButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Initialize map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.navigation_map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapButton = this.findViewById(R.id.open_google_map_button)

        // Get device last location and user's current location
        deviceLastLocation = intent.extras!!.getParcelable<LatLng>("deviceLocation")
        deviceName = intent.getStringExtra("deviceName")
        LocationTrackingService.currentPoint.observe(this, Observer {
            userPoint = it
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        LocationTrackingService.currentPoint.observe(this, Observer {
            userPoint = it
        })

        if (deviceLastLocation == LatLng(0.0, 0.0)) {
            println("Tracking service: default location")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Tracking Service Alert")
            builder.setIcon(R.drawable.blueradar_logo)
            builder.setMessage("No location has been registered for this device")
            builder.setPositiveButton("OK") { dialog, which ->
                finish()
            }
            builder.show()

        } else {
            mMap = googleMap
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val toggle = prefs.getBoolean("options_map_dark_mode", true)
            if(toggle) {
                val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
                mMap.setMapStyle(mapStyleOptions)
            }

            val markerUserLocation = MarkerOptions().position(userPoint).title("You are here")
            val markerDeviceLocation = MarkerOptions().position(deviceLastLocation!!)
                .title("$deviceName").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            mMap.addMarker(markerUserLocation)
            mMap.addMarker(markerDeviceLocation)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLastLocation!!, 17f))

            val fromLat = userPoint.latitude
            val fromLng = userPoint.longitude
            val toLat = deviceLastLocation!!.latitude
            val toLng = deviceLastLocation!!.longitude

            mapButton.setOnClickListener {
                val uri =
                    "http://maps.google.com/maps?saddr=$fromLat,$fromLng&daddr=$toLat,$toLng"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    try {
                        val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(unrestrictedIntent)
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(this, "Google Maps application is not installed", Toast.LENGTH_LONG).show();
                    }
                }
            }

            val path: MutableList<List<LatLng>> = ArrayList()
            val urlDirections =
                "https://maps.googleapis.com/maps/api/directions/json?origin=$fromLat,$fromLng&destination=$toLat,$toLng&key=AIzaSyDEbPM7EHRUeO-MWEMSVhXcvNZGN-yRJvw"
            val directionsRequest = object : StringRequest(
                Request.Method.GET,
                urlDirections,
                Response.Listener<String> { response ->
                    val jsonResponse = JSONObject(response)

                    // Get routes
                    val routes = jsonResponse.getJSONArray("routes")
                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val steps = legs.getJSONObject(0).getJSONArray("steps")
                    for (i in 0 until steps.length()) {
                        val points =
                            steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                        path.add(PolyUtil.decode(points))
                    }
                    for (i in 0 until path.size) {
                        this.mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
                    }
                },
                Response.ErrorListener { _ ->
                }) {}
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(directionsRequest)
        }
    }
}