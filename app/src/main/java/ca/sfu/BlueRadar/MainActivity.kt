package ca.sfu.BlueRadar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import ca.sfu.BlueRadar.about.AboutApplication
import ca.sfu.BlueRadar.about.AboutDevelopers
import ca.sfu.BlueRadar.databinding.ActivityMainBinding
import ca.sfu.BlueRadar.services.DatabaseService
import ca.sfu.BlueRadar.services.LocationTrackingService
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set theme based on preferences
        setCustomTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupBurgerMenuContents()
        setupBurgerMenuNavigation()
        checkPermissions()
    }

    override fun onRestart() {
        println("debug: onRestart called")
        setCustomTheme()
        super.onResume()
    }

    override fun onStart() {
        println("debug: onStart called")
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
    private fun loadProfileInfo() {
        burgerView = findViewById(R.id.burger_view)
        val headerView = burgerView.getHeaderView(0)
        val profilePicture: CircleImageView = headerView.findViewById(R.id.profileImg)
        val profileUsername: TextView = headerView.findViewById(R.id.username)
        val profileEmail: TextView = headerView.findViewById(R.id.useremail)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(getString(R.string
            .profile_pref_name), Context.MODE_PRIVATE)
        profileUsername.text = sharedPreferences.getString("name","").toString()
        profileEmail.text = sharedPreferences.getString("email","").toString()
        val tempImgFileName = "new_img.jpg"
        val imgFile = File(getExternalFilesDir(null), tempImgFileName)
        val tempImgUri:Uri = FileProvider.getUriForFile(this,"ca.sfu.BlueRadar", imgFile)
        if(imgFile.exists()) {
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
        val theme = PreferenceManager.getDefaultSharedPreferences(this).getString("options_colours", "AppTheme")
        println("debug: theme name $theme")
        when (theme) {
            "AppTheme" -> setTheme(R.style.AppTheme)
            "AppThemeRed" -> setTheme(R.style.AppThemeRed)
            "AppThemeBlue" -> setTheme(R.style.AppThemeBlue)
        }
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
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ),
                0
            )
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