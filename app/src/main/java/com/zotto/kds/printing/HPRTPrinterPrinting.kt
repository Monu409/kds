package com.zotto.kds.printing

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder
import com.zotto.kds.R
import com.zotto.kds.database.AppDatabase
import com.zotto.kds.database.DatabaseClient
import com.zotto.kds.database.dao.OrderDao
import com.zotto.kds.database.dao.RestaurantDao
import com.zotto.kds.database.table.Product
import com.zotto.kds.utils.SessionManager
import print.Print
import java.io.File
import java.io.IOException
import java.lang.Error
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class HPRTPrinterPrinting(var mcontext: Context) {
    companion object{
        private var HPRTPrinter = Print()
        private var mUsbManager: UsbManager? = null
        private var device: UsbDevice? = null
        private val ACTION_USB_PERMISSION = "com.mypay.mypos"
        private var mPermissionIntent: PendingIntent? = null
        private var receipt: ReceiptBuilder? = null
        private var appDatabase: AppDatabase?=null
        private var orderDao: OrderDao?=null
        private var restaurantDao: RestaurantDao?=null
    }
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val action = intent.action
                if (ACTION_USB_PERMISSION == action) {
                    synchronized(this) {
                        device = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null && Print.PortOpen(context, device) != 0) {
                                if (context != null)
                                    Toast.makeText(context, "port closed", Toast.LENGTH_SHORT).show()
                                return
                            } else {
                                //    Toast.makeText(context, "connected successfully", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            return
                        }
                    }
                }
                if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                    device =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (device != null) {
                        val count = device!!.interfaceCount
                        for (i in 0 until count) {
                            val intf = device!!.getInterface(i)
                            //Class ID 7代表打印机
                            if (intf.interfaceClass == 7) {
                                Print.PortClose()
                                if (context != null) Toast.makeText(
                                    context,
                                    "port closed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HPRTSDKSample", StringBuilder("Activity_Main --> mUsbReceiver ").append(e.message).toString())
            } catch (error: Error) {
                Log.e("HPRTSDKSampleError", StringBuilder("Activity_Main --> mUsbReceiver ").append(error.message).toString())
            }
        }
    }

    fun printerPermission(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPermissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_MUTABLE)
        } else {
            mPermissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_ONE_SHOT)
        }
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        context.registerReceiver(mUsbReceiver, filter)
    }

    fun openUSBPrintingPort(){
        try {
            //USB not need call "iniPort"
            mUsbManager = (mcontext!!.getSystemService(Context.USB_SERVICE) as UsbManager?)!!
            val deviceList = mUsbManager!!.deviceList
            val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()

            var HavePrinter = false
            while (deviceIterator.hasNext()) {
                device = deviceIterator.next()
                val count = device!!.interfaceCount
                for (i in 0 until count) {
                    val intf = device!!.getInterface(i)
                    if (intf.interfaceClass == 7) {
                        Log.d("PRINT_TAG", "vendorID--" + device!!.vendorId + "ProductId--" + device!!.productId)
                        HavePrinter = true
                        if (mUsbManager != null)
                            mUsbManager!!.requestPermission(device, mPermissionIntent)
                        try {
                            Print.PortOpen(mcontext, device)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            if (!HavePrinter) {
                if (mcontext != null)
                    Toast.makeText(mcontext, "Please connect usb printer.", Toast.LENGTH_LONG).show()
            }
        }catch (e:NullPointerException){
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }

    }

    fun openIPPrintingPort(printerip:String){
        val portname = ("WiFi," +printerip + ",9100")
        try {
            Print.PortOpen(mcontext, portname)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

     fun kitchenReciept(productlists:ArrayList<Product>, orderid:String){
        openUSBPrintingPort()
         var productlist=ArrayList<Product>()
        receipt = ReceiptBuilder(600)
        receipt!!.setMargin(10, 10)
            .setAlign(Paint.Align.CENTER)
            .setColor(Color.BLACK)
            .setTextSize(25f)
            .setTypeface(mcontext, "robotomono_regular.ttf")
        try {

            if (Print.IsOpened()){
                    companyDetails(orderid)
                    for (product in productlist) {

                            if (product.name!!.length < 23) {
                                receipt!!.setAlign(Paint.Align.LEFT)
                                    .setTextSize(25f)
                                    .setTypeface(mcontext, "robotomono_bold.ttf")
                                    .addText(product.quantity.toString()+" X "+product.name, true)

                            }
                            else {
                                receipt!!.setAlign(Paint.Align.LEFT)
                                    .setTextSize(25f)
                                    .setTypeface(mcontext, "robotomono_bold.ttf")
                                    .addText(product.quantity.toString()+" X "+product.name, true)

                                if (product.name!!.length > 23) {
                                    receipt!!.setAlign(Paint.Align.LEFT)
                                        .setTextSize(25f)
                                        .setTypeface(mcontext, "robotomono_bold.ttf")
                                        .addText(product.name!!.substring(23, product!!.name!!.length), true)

                                }

                            }
                            receipt!!.setAlign(Paint.Align.LEFT)
                                .setTextSize(25f)
                                .setTypeface(mcontext, "robotomono_bold.ttf")
                                .addText("", true)


                    }

                    receipt!!.setAlign(Paint.Align.CENTER)
                        .setTextSize(25f)
                        .setTypeface(mcontext, "robotomono_bold.ttf")
                        .addText("#"+orderid!!.substring(orderid.length-4,orderid.length), true)
                    Print.PrintBitmap(receipt!!.build(), 0 , 0)
                    Print.CutPaper(Print.PARTIAL_CUT.toInt(), 40)

            }
        }catch (e:NullPointerException){
            e.printStackTrace()
        }catch (e:Exception){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }finally {
            Print.PortClose()
        }
    }

     fun companyDetails(orderid: String){
        appDatabase= DatabaseClient.getInstance(mcontext!!)!!.getAppDatabase()
        restaurantDao=appDatabase!!.restaurantDao()
         var restaurant=restaurantDao!!.getRestaurant(SessionManager.getRestaurantId(mcontext))
        val date1 = Date()
        var date = DateFormat.format("yyyy-MM-dd", date1).toString()
        var time = DateFormat.format("HH:mm", date1).toString()


        try {

            receipt!!.setAlign(Paint.Align.LEFT)
                .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                .addText(restaurant!!.restaurantname,true)


            receipt!!.setAlign(Paint.Align.LEFT)
                .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                .addText(mcontext.resources.getString(R.string.date_txt)+date,false)
                .setAlign(Paint.Align.RIGHT)
                .addText(mcontext.resources.getString(R.string.time_txt)+time+"  ",true)

                receipt!!.setAlign(Paint.Align.LEFT)
                    .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                    .addText(mcontext.resources.getString(R.string.order_id_txt)+orderid,true)



            receipt!!.setAlign(Paint.Align.CENTER)
                .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                .addText("---------------------------------------------------------",true)

        }catch (e: IOException){
            e.printStackTrace()

        }catch (ex:Exception){
            ex.printStackTrace()
        }catch (ex:IllegalStateException){
            ex.printStackTrace()
        }

    }
    fun loadCategoryImagefromServer(pictureName: String?, a: Int, b: Int): Bitmap? {
        var bitmap: Bitmap? = null

        if (pictureName != null && pictureName.length > 0) {
            val imgFile: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/mypos"+"/"+pictureName)
            Log.e("imgFile :", imgFile.toString())
            if (imgFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                if (bitmap != null) bitmap = Bitmap.createScaledBitmap(bitmap, a, b, true)
            }
        }
        return bitmap
    }
}