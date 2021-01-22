package com.example.tabatatimer


import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.tabatatimer.viewmodels.TimerViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

//! обычная activity с PrefferenceFragment внутри
class SettingsActivity : AppCompatActivity() {

    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        //context = this
        sp = PreferenceManager.getDefaultSharedPreferences(this)

        if (sp.getBoolean("theme", true)) {
            setTheme(R.style.AppTheme)
        }

        val font = sp.getString("text_style", "")
        val listValue = sp.getString("text_lang", "не выбрано")
        val locale: Locale
        locale = if (listValue == "English" || listValue == "Английский") {
            Locale("en")
        } else {
            Locale("ru")
        }
        Locale.setDefault(locale)
//
        val configuration = Configuration()
        configuration.locale = locale
        if (font == "Малый" || font == "Small") {
            configuration.fontScale = 0.85.toFloat()
        } else if (font == "Нормальный" || font == "Normal") {
            configuration.fontScale = 1.toFloat()
        } else {
            configuration.fontScale = 1.15.toFloat()
        }

        baseContext.resources.updateConfiguration(configuration, null)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }


    class SettingsFragment: PreferenceFragmentCompat() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val isNight = prefs.getBoolean("theme", false)
            val themedContext = ContextThemeWrapper(
                requireActivity(), if (isNight) {
                    R.style.AppTheme_Dark
                } else {
                    R.style.AppTheme
                }
            )
            val themeInflater = inflater.cloneInContext(themedContext)
            val view = super.onCreateView(themeInflater, container, savedInstanceState)
            val colorValue = TypedValue()
            themedContext.theme.resolveAttribute(R.attr.listBackgroundColor, colorValue, true)
            view?.setBackgroundColor(colorValue.data)



            return view
        }
        @InternalCoroutinesApi
        private lateinit var timerViewModel: TimerViewModel
        @InternalCoroutinesApi
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

            val button = findPreference<Preference>("DeleteAll")
            val lang = findPreference<ListPreference>("test_lang")
            val theme = findPreference<SwitchPreference>("theme")
            val font = findPreference<ListPreference>("fontSize")

            theme?.setOnPreferenceChangeListener { _, value ->
                value as Boolean
                if (value) {
                    //setTheme(R.style.AppTheme_Dark)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    //setTheme(R.style.AppTheme)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                true
            }

            lang?.setOnPreferenceChangeListener { _, value ->
                if (value.toString() == "English" || value.toString() == "Английский") {
                    val locale = Locale("en")
                    Locale.setDefault(locale)
                    val configuration = Configuration()
                    configuration.locale = locale
                    activity?.resources?.updateConfiguration(configuration, null)
                } else {
                    val locale = Locale("ru")
                    Locale.setDefault(locale)
                    val configuration = Configuration()
                    configuration.locale = locale
                    activity?.resources?.updateConfiguration(configuration, null)
                }
                true
            }

            font?.setOnPreferenceChangeListener { _, value ->
                val configuration = requireActivity().baseContext.resources.configuration
                if (value.toString() == "Малый" || value.toString() == "Small") {
                    configuration.fontScale = 0.85.toFloat()
                } else if (value.toString() == "Нормальный" || value.toString() == "Normal") {
                    configuration.fontScale = 1.toFloat()
                } else {
                    configuration.fontScale = 1.15.toFloat()
                }
                val metrics = DisplayMetrics()
                activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
                metrics.scaledDensity = configuration.fontScale * metrics.density
                activity?.baseContext?.resources
                    ?.updateConfiguration(configuration, metrics)
                true
            }

            button?.setOnPreferenceClickListener {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.settings_clear_warning)
                    .setPositiveButton(R.string.Yes) { _, _ -> timerViewModel.deleteAll() }
                    .setNegativeButton(R.string.No) { _, _ -> }
                    .create()
                    .show()
                true
            }
        }

    }




}
