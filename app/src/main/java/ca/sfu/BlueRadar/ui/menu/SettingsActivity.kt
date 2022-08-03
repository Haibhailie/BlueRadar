package ca.sfu.BlueRadar.ui.menu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceFragmentCompat
import androidx.appcompat.widget.Toolbar
import ca.sfu.BlueRadar.R


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if(savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        showUpButton()
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