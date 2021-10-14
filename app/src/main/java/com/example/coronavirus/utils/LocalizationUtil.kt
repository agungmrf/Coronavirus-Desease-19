package com.example.coronavirus.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocalizationUtil {
    fun wrapContext(context: Context): Context {
        val pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val lang = pref?.getString("lang", "en") ?: return context

        val savedLocale = Locale(lang)

        Locale.setDefault(savedLocale)

        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)

        return context.createConfigurationContext(newConfig)
    }
}