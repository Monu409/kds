package com.zotto.kds.utils

import androidx.fragment.app.FragmentManager

class Singleton {
    companion object{
        var ordertype="active"
        var isactiveclicked=false
         var fragmentManager:FragmentManager?=null
    }
}