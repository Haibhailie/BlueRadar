package ca.sfu.BlueRadar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.sfu.BlueRadar.about.AboutApplication
import ca.sfu.BlueRadar.about.AboutDevelopers
import ca.sfu.BlueRadar.databinding.ActivityMainBinding
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.ui.menu.OptionsActivity
import ca.sfu.BlueRadar.ui.menu.SettingsActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var burgerToggle: ActionBarDrawerToggle
    lateinit var burgerMenuLayout: DrawerLayout
    lateinit var burgerView: NavigationView
    lateinit var navView: BottomNavigationView
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var locationTrackingServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupBurgerMenuContents()
        setupBurgerMenuNavigation()
        checkPermissions()
        startLocationTrackingService()
    }

    private fun setupBurgerMenuNavigation() {
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_devices, R.id.navigation_map
            ), burgerMenuLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setupBurgerMenuContents() {
        burgerMenuLayout = findViewById(R.id.burger_layout)
        burgerView = findViewById(R.id.burger_view)

        burgerToggle = ActionBarDrawerToggle(this, burgerMenuLayout, R.string.open, R.string.close)
        burgerMenuLayout.addDrawerListener(burgerToggle)
        burgerToggle.syncState()

        burgerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.burger_settings -> {
                    Toast.makeText(applicationContext, "Settings Page", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.burger_options -> {
                    Toast.makeText(applicationContext, "Options Page", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OptionsActivity::class.java))
                }

                R.id.burger_logout ->
                    Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT).show()

                R.id.burger_about_devs -> {
                    startActivity(Intent(this, AboutDevelopers::class.java))
                }

                R.id.bueger_about_app -> {
                    startActivity(Intent(this, AboutApplication::class.java))
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (burgerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT < 29) return
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ),
                0
            )
        }
    }

    private fun startLocationTrackingService() {
        locationTrackingServiceIntent = Intent(this, LocationTrackingService::class.java)
        this.startService(locationTrackingServiceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent()
        intent.action = LocationTrackingService.STOP_TRACKING_SERVICE
        sendBroadcast(intent)
        LocationTrackingService.isTracking.value = false
    }
}