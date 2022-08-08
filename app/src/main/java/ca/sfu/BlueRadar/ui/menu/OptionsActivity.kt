package ca.sfu.BlueRadar.ui.menu

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ca.sfu.BlueRadar.R


class OptionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set theme based on user preferences
        setCustomTheme()
        setContentView(R.layout.activity_options)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.options, OptionsFragment())
                .commit()
        }
    }

    override fun onResume() {
        setCustomTheme()
        super.onResume()
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

    class OptionsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
        private val OPTIONS_COLOURS = "options_colours"
        private lateinit var prefChangeListener: OnSharedPreferenceChangeListener

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.options_settings)

            prefChangeListener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key.equals(OPTIONS_COLOURS)) {
                    AlertDialog.Builder(context)
                        .setTitle("Change Theme")
                        .setMessage("An app restart is required to properly apply the theme. Restart now or later?")
                        .setPositiveButton("now") { _, _ ->
                            // exitProcess restarts the application
                            val ctx: Context? = context
                            val pm = ctx?.packageManager
                            val intent = pm?.getLaunchIntentForPackage(ctx.packageName)
                            val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
                            ctx.startActivity(mainIntent)
                            Runtime.getRuntime().exit(0)
                        }
                        .setNegativeButton("later", null)
                        //No listener needed for negative button
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?,
        ) {

        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(
                prefChangeListener
            )
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(
                prefChangeListener
            )
        }
    }
}