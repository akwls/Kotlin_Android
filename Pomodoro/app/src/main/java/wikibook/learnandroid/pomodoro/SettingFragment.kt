package wikibook.learnandroid.pomodoro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingFragment : PreferenceFragmentCompat() {
    companion object {
        val SETTING_PREF_FILENAME = "pomodoro_setting"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        preferenceManager.sharedPreferencesName = SETTING_PREF_FILENAME

        addPreferencesFromResource(R.xml.pomodoro_preferences)
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }
}