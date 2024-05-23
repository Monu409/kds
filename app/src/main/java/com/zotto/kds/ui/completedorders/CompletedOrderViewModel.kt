package com.zotto.kds.ui.completedorders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zotto.kds.database.table.Order
import com.zotto.kds.repository.CompletedOrdersRepository
import com.zotto.kds.ui.home.OrderBoundaryCallback
import com.zotto.kds.utils.Singleton

class CompletedOrderViewModel(var completedOrdersRepository: CompletedOrdersRepository):ViewModel() {
    var orderlivedata: LiveData<PagedList<Order>>?= MutableLiveData<PagedList<Order>>()
    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(0)
            //.setInitialLoadSizeHint(4)
            .setPageSize(100)
            .build()

    if (Singleton.ordertype.equals("completed")){
            val data = completedOrdersRepository.orderDao.getAllDatasourceCompletedOrder()
            orderlivedata = LivePagedListBuilder(data, config)
                .build()
        }

    }

}