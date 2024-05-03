package com.zotto.kds.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zotto.kds.R
import com.zotto.kds.repository.SettingRepository

class SettingViewModel(var settingRepository: SettingRepository):ViewModel() {
    var orderdisplayCheckec=MutableLiveData<Int>()
    var orderCookingTimerGrp=MutableLiveData<Int>()
    var robotDeliveryGrp=MutableLiveData<Int>()
    var printingGrp=MutableLiveData<Int>()

    init {
        orderdisplayCheckec.value= R.id.inshop_order_only
        orderdisplayCheckec.value= R.id.online_order_only
        orderdisplayCheckec.value= R.id.all

        orderCookingTimerGrp.value=R.id.automatic_timer
        orderCookingTimerGrp.value=R.id.manual_timer

        robotDeliveryGrp.value=R.id.robot_able
        robotDeliveryGrp.value=R.id.robot_unable

        printingGrp.value=R.id.auto_print
        printingGrp.value=R.id.manual_print
        printingGrp.value=R.id.no_print
    }
}