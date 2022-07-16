package ca.sfu.BlueRadar

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.sfu.BlueRadar.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {


    lateinit var burgerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val burgerMenuLayout: DrawerLayout = findViewById(R.id.burger_layout)
        val burgerView: NavigationView = findViewById(R.id.burger_view)

        burgerToggle = ActionBarDrawerToggle(this, burgerMenuLayout, R.string.open, R.string.close)
        burgerMenuLayout.addDrawerListener(burgerToggle)
        burgerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        burgerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.burger_settings ->
                    Toast.makeText(applicationContext, "Settings Page", Toast.LENGTH_SHORT).show()

                R.id.burger_options ->
                    Toast.makeText(applicationContext, "Options Page", Toast.LENGTH_SHORT).show()

                R.id.burger_logout ->
                    Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT).show()
            }

            true
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_devices, R.id.navigation_map
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(burgerToggle.onOptionsItemSelected(item)){
            return true
        }
        return false
    }
}