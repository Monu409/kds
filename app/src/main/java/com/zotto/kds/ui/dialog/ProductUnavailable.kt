package com.zotto.kds.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import com.zotto.kds.R
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.SessionManager

class ProductUnavailable: DialogFragment() {
    private var rootView: View? = null
    private var alertmsg: TextView?=null
    private var buttontype=""


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setGravity(Gravity.CENTER)
        setMargins( dialog, 0, 0, 0, 0 )
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                (getScreenWidth(activity) * .3).toInt(),
                (getScreenHeight(activity) * .7).toInt()
            )
        }
        dialog?.window?.decorView?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    fun getScreenWidth(activity: Activity?): Int {
        val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        return size.x
    }
    fun getScreenHeight(activity: Activity?): Int {
        val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        return size.y
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.product_unavailable, container, false)
        alertmsg=rootView!!.findViewById(R.id.alert_msg)
        if (arguments != null){
            alertmsg!!.text=requireArguments().getString("TITLE")
            buttontype= requireArguments().getString("BUTTON")!!
        }
        rootView!!.findViewById<Button>(R.id.yes_btn).setOnClickListener {

            dismiss()
        }
        rootView!!.findViewById<Button>(R.id.cancel_btn).setOnClickListener {

            dismiss()
        }
        return rootView
    }
    fun setMargins(
        dialog: Dialog,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ): Dialog? {
        val window = dialog.window
            ?: // dialog window is not available, cannot apply margins
            return dialog
        val context = dialog.context

        // set dialog to fullscreen
        val root = RelativeLayout(context)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        // set background to get rid of additional margins
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // apply left and top margin directly
        window.setGravity(Gravity.CENTER)
        val attributes: WindowManager.LayoutParams = window.attributes
        attributes!!.x = marginLeft
        attributes!!.y = marginTop
        window.attributes = attributes

        // set right and bottom margin implicitly by calculating width and height of dialog
        val displaySize: Point = getDisplayDimensions(context)
        val width = displaySize.x - marginLeft - marginRight
        val height = displaySize.y - marginTop - marginBottom
        window.setLayout(width, height)
        return dialog
    }

    @NonNull
    fun getDisplayDimensions(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val screenWidth = metrics.widthPixels
        var screenHeight = metrics.heightPixels

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics(metrics)
        val physicalHeight = metrics.heightPixels
        val statusBarHeight = getStatusBarHeight(context)
        val navigationBarHeight = getNavigationBarHeight(context)
        val heightDelta = physicalHeight - screenHeight
        if (heightDelta == 0 || heightDelta == navigationBarHeight) {
            screenHeight -= statusBarHeight
        }
        return Point(screenWidth, screenHeight)
    }

    fun getStatusBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }
}