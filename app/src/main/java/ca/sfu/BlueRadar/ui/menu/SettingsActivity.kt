package ca.sfu.BlueRadar.ui.menu

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import ca.sfu.BlueRadar.R
import java.io.File


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
            val cachePreference: Preference? = findPreference("preference_data")
            cachePreference?.setOnPreferenceClickListener {
                createDialog()
                true
            }
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

        private fun createDialog() {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Clear Cache?")
            builder.setPositiveButton("OK") { _, _ ->
                File(requireContext().cacheDir.path).deleteRecursively()
                File(requireActivity().externalCacheDir?.path).deleteRecursively()
//                requireContext().cacheDir.deleteRecursively()
//                requireActivity().externalCacheDir?.deleteRecursively()
                Log.d("clearing_cache", "cleared")
            }
            builder.setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
                Log.d("clearing_cache", "cancelled")
            }
            builder.show()
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