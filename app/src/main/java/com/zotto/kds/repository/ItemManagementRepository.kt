package com.zotto.kds.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zotto.kds.database.dao.CategoryDao
import com.zotto.kds.database.table.CategoryTable
import com.zotto.kds.restapi.ApiServices
import com.zotto.kds.restapi.GenericResponse
import com.zotto.kds.utils.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ItemManagementRepository(
    var context: Context,
    var apiServices: ApiServices,
    var categoryDao: CategoryDao
) {

    var categoryListMutableLiveData: MutableLiveData<List<CategoryTable>>? =
        MutableLiveData<List<CategoryTable>>()
    val categorylivedata: LiveData<List<CategoryTable>> get() = categoryListMutableLiveData!!

    fun getAvailableCategoryLocal() {
        categoryListMutableLiveData!!.postValue(categoryDao.getAllAvailableCategory())
    }

    fun getUnavailableCategoryLocal() {
        categoryListMutableLiveData!!.postValue(categoryDao.getAllUnavailableCategory())
    }

    fun getAllCategories() {
        if (categoryDao.isCategoryEmpty()) {
            Log.e("getAllCategories", "  run now")
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(
                apiServices.getCategories(
                    SessionManager.getToken(context), SessionManager.getRestaurantId(context)
                )
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                    .subscribe({ response -> onResponse(response) },
                        { t -> onFailure(t) })
            )
        } else {
            Log.e("data  from db ", "here run")
            categoryListMutableLiveData!!.postValue(categoryDao.getAllCategory())
        }
    }

    private fun onResponse(response: GenericResponse<List<CategoryTable>>) {
        Log.e("categories onResponse", "${response}")
        try {
            if (response.getStatus().equals("200")) {
                Log.e("order Responce== ", "" + response.getData().toString())
                if (response.getData()!!.size > 0) {
                    categoryListMutableLiveData!!.postValue(response.getData()!!)
                    categoryDao.deleteAllCategory()
                    categoryDao.insertCategoryList(response.getData()!!)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onFailure(t: Throwable?) {
        Log.e("onFailure categories", "${t!!.message}")
    }
}