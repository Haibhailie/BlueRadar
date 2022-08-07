package ca.sfu.BlueRadar.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.LoadingActivity
import ca.sfu.BlueRadar.MainActivity
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.services.DatabaseService
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        checkPermissions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT < 29) return
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                0
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (verifyPermissions(permissions, grantResults)) {
                    Handler().postDelayed({
                        val intent = Intent(this, LoadingActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 3000)
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT)
                    finish()
                }
            }
        }
    }

    fun verifyPermissions(
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        var permCheck = true
        var permIndex = 0
        for (perm in permissions) {

            println("Permissions: ${perm}")
            println("GrantResults: ${grantResults[permIndex]}")

            if (perm == Manifest.permission.BLUETOOTH_CONNECT
                || perm == Manifest.permission.BLUETOOTH_SCAN
                || perm == Manifest.permission.WRITE_EXTERNAL_STORAGE
                || perm == Manifest.permission.CAMERA
                || perm == Manifest.permission.ACCESS_FINE_LOCATION
            ) {
                if (grantResults[permIndex] != PackageManager.PERMISSION_GRANTED) {
                    permCheck = false
                }
                permIndex++
            }
        }
        return permCheck
    }


}