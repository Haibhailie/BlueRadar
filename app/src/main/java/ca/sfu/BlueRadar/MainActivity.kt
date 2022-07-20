package ca.sfu.BlueRadar

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.sfu.BlueRadar.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var burgerToggle: ActionBarDrawerToggle
    lateinit var burgerMenuLayout: DrawerLayout
    lateinit var burgerView: NavigationView
    lateinit var navView: BottomNavigationView
    lateinit var navController:NavController
    lateinit var appBarConfiguration:AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupBurgerMenuContents()
        setupBurgerMenuNavigation()

    }

    private fun setupBurgerMenuNavigation(){
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

    private fun setupBurgerMenuContents(){
        burgerMenuLayout= findViewById(R.id.burger_layout)
        burgerView= findViewById(R.id.burger_view)

        burgerToggle = ActionBarDrawerToggle(this, burgerMenuLayout, R.string.open, R.string.close)
        burgerMenuLayout.addDrawerListener(burgerToggle)
        burgerToggle.syncState()

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(burgerToggle.onOptionsItemSelected(item)){
            return true
        }
        return false
    }
}