package ca.sfu.BlueRadar.ui.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ca.sfu.BlueRadar.R


class OptionsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set theme based on user preferences
        setCustomTheme()
        setContentView(R.layout.activity_options)
        if(savedInstanceState == null) {
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
        val theme = PreferenceManager.getDefaultSharedPreferences(this).getString("options_colours", "AppTheme")
        println("debug: theme name $theme")
        when (theme) {
            "AppTheme" -> setTheme(R.style.AppTheme)
            "AppThemeRed" -> setTheme(R.style.AppThemeRed)
            "AppThemeBlue" -> setTheme(R.style.AppThemeBlue)
        }
    }

    class OptionsFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.options_settings)
        }
    }
}