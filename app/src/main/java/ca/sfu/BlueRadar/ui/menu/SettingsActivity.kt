package ca.sfu.BlueRadar.ui.menu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceFragmentCompat
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import ca.sfu.BlueRadar.R


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set theme based on user preferences
        setCustomTheme()
        setContentView(R.layout.activity_settings)
        if(savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        showUpButton()
    }

    override fun onResume() {
        setCustomTheme()
        super.onResume()
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

    class SettingsFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preference_settings)
        }

        override fun onResume() {
            super.onResume()
            val activity = activity as SettingsActivity?
            activity?.showUpButton()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                android.R.id.home -> {
                    (activity as SettingsActivity?)!!.onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        val fragmentManager: FragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }

    public fun showUpButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    public fun hideUpButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}