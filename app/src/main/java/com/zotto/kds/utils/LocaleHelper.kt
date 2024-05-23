package com.zotto.kds.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

class LocaleHelper {
    companion object{
        private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
        fun setLocale(context: Context, languageCode: String?) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val resources: Resources = context.resources
            val config: Configuration = resources.getConfiguration()
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            resources.updateConfiguration(config, resources.getDisplayMetrics())
        }
    }
}