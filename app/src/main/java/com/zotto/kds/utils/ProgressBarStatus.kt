package com.zotto.kds.utils

import android.view.View
import android.widget.ProgressBar

class ProgressBarStatus(var progressBar: ProgressBar) {
    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar!!.visibility = View.INVISIBLE
    }
}