package ca.sfu.BlueRadar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import ca.sfu.BlueRadar.about.AboutApplication
import ca.sfu.BlueRadar.about.AboutDevelopers
import ca.sfu.BlueRadar.databinding.ActivityMainBinding
import ca.sfu.BlueRadar.services.BluetoothService
import ca.sfu.BlueRadar.services.BluetoothService.Companion.deviceViewModel
import ca.sfu.BlueRadar.services.DatabaseService
import ca.sfu.BlueRadar.services.LocationTrackingService
import ca.sfu.BlueRadar.services.NotificationService
import ca.sfu.BlueRadar.ui.devices.DeviceViewModel
import ca.sfu.BlueRadar.ui.devices.DeviceViewModelFactory
import ca.sfu.BlueRadar.ui.devices.data.DeviceDatabase
import ca.sfu.BlueRadar.ui.menu.OptionsActivity
import ca.sfu.BlueRadar.ui.menu.SettingsActivity
import ca.sfu.BlueRadar.util.Util
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var burgerToggle: ActionBarDrawerToggle
    lateinit var burgerMenuLayout: DrawerLayout
    lateinit var burgerView: NavigationView
    lateinit var navView: BottomNavigationView
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var notificationIntent: Intent
    private lateinit var preferences: SharedPreferences
    private var check: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set theme based on preferences
        setCustomTheme()

        setupPreferencesListener()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupBurgerMenuContents()
        setupBurgerMenuNavigation()
    }

    private fun setupPreferencesListener(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key){
                "preference_notifications" -> {
                    val prefs = this.let { PreferenceManager.getDefaultSharedPreferences(it) }
                    val checkBox = prefs?.getBoolean("preference_notifications", true)
                    if (checkBox != null) {
                        check = checkBox
                    }
                    if(checkBox == true){
                        deviceViewModel.activeEntriesLiveData.observe(this) {
                            if (!deviceViewModel.activeEntriesLiveData.value.isNullOrEmpty()) {
                                notificationIntent = Intent(this, NotificationService::class.java)
                                this.startService(notificationIntent)
                            } else {
                                val intent = Intent()
                                intent.action = NotificationService.STOP_SERVICE_ACTION
                                this.sendBroadcast(intent)
                            }
                        }
                    }else{
                        val intent = Intent()
                        intent.action = NotificationService.STOP_SERVICE_ACTION
                        this.sendBroadcast(intent)
                    }
                }
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onRestart() {
        setCustomTheme()
        super.onResume()
    }

    override fun onStart() {
        setCustomTheme()
        super.onStart()
        loadProfileInfo()
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

    private fun loadProfileInfo() {
        burgerView = findViewById(R.id.burger_view)
        val headerView = burgerView.getHeaderView(0)
        val profilePicture: CircleImageView = headerView.findViewById(R.id.profileImg)
        val profileUsername: TextView = headerView.findViewById(R.id.username)
        val profileEmail: TextView = headerView.findViewById(R.id.useremail)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            getString(
                R.string
                    .profile_pref_name
            ), Context.MODE_PRIVATE
        )
        profileUsername.text = sharedPreferences.getString("name", "").toString()
        profileEmail.text = sharedPreferences.getString("email", "").toString()
        val tempImgFileName = "new_img.jpg"
        val imgFile = File(getExternalFilesDir(null), tempImgFileName)
        val tempImgUri: Uri = FileProvider.getUriForFile(this, "ca.sfu.BlueRadar", imgFile)
        if (imgFile.exists()) {
            Log.d("exs_loadPhoto", "imgFile exists, loading previous image")
            val bitmap = Util.getBitmap(this, tempImgUri)
            profilePicture.setImageBitmap(bitmap)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (burgerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }

    private fun setCustomTheme() {
        val theme = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("options_colours", "AppTheme")
        when (theme) {
            "AppTheme" -> setTheme(R.style.AppTheme)
            "AppThemeRed" -> setTheme(R.style.AppThemeRed)
            "AppThemeBlue" -> setTheme(R.style.AppThemeBlue)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val locationIntent = Intent()
        locationIntent.action = LocationTrackingService.STOP_TRACKING_SERVICE
        sendBroadcast(locationIntent)
        LocationTrackingService.isTracking.value = false

        val dbIntent = Intent()
        dbIntent.action = DatabaseService.STOP_SERVICE_ACTION
        sendBroadcast(dbIntent)
    }
}