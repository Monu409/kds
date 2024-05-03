package com.zotto.kds.model

import android.graphics.drawable.Drawable

class Language {
    var languagename=""
    var countryimage:Drawable?=null
    var lang_code=""
    var countryname=""

    override fun toString(): String {
        return "Language(languagename='$languagename', countryimage='$countryimage', lang_code='$lang_code', countryname='$countryname')"
    }


}