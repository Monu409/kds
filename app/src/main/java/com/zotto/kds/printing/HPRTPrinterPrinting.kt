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
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


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

    fun saveLogToFile(context: Context, logData: String, fileName: String = "KDS_logfile.txt") {
        try {
            // Create or open the log file in internal storage
            val file = File(context.filesDir, fileName)

            // Open file output stream in append mode
            FileOutputStream(file, true).use { fos ->
                fos.write((logData + "\n").toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
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
//        Toast.makeText(context,"Enter in permission",Toast.LENGTH_LONG).show()
//        saveLogToFile(context,"Enter in permission block")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPermissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_MUTABLE)
        } else {
            mPermissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_ONE_SHOT)
        }
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        context.registerReceiver(mUsbReceiver, filter)
    }

    fun openUSBPrintingPort(context: Context){
//        Toast.makeText(context,"Enter in openUSBPrintingPort",Toast.LENGTH_LONG).show()
//        saveLogToFile(context,"Enter in openUSBPrintingPort")
        try {
            //USB not need call "iniPort"
//            Toast.makeText(context,"Enter in openUSBPrintingPort try block",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort try block")
            mUsbManager = (mcontext!!.getSystemService(Context.USB_SERVICE) as UsbManager?)!!
            val deviceList = mUsbManager!!.deviceList
            val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()

            var HavePrinter = false
            while (deviceIterator.hasNext()) {
//                Toast.makeText(context,"Enter in openUSBPrintingPort while",Toast.LENGTH_LONG).show()
//                saveLogToFile(context,"Enter in openUSBPrintingPort while")
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
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch1 ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch1 ${e.printStackTrace()}")
        }catch (e: Exception){
            e.printStackTrace()
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch2 ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch2 ${e.printStackTrace()}")
        }catch (e: IOException){
            e.printStackTrace()
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch3 ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch3 ${e.printStackTrace()}")
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

     fun kitchenReciept(productlists:ArrayList<Product>, orderid:String, context: Context){
        openUSBPrintingPort(context)
//         var productlist=ArrayList<Product>()
        receipt = ReceiptBuilder(600)
        receipt!!.setMargin(10, 10)
            .setAlign(Paint.Align.CENTER)
            .setColor(Color.BLACK)
            .setTextSize(25f)
//            .setTypeface(typeface)
        try {

            if (Print.IsOpened()){
//            if (true){
                    companyDetails(orderid,context)
                    for (product in productlists) {

                            if (product.name!!.length < 23) {
                                receipt!!.setAlign(Paint.Align.LEFT)
                                    .setTextSize(25f)
//                                    .setTypeface(mcontext, "font/roboto_bold.ttf")
                                    .addText(product.quantity.toString()+" X "+product.name, true)

                            }
                            else {
                                receipt!!.setAlign(Paint.Align.LEFT)
                                    .setTextSize(25f)
//                                    .setTypeface(mcontext, "font/roboto_bold.ttf")
                                    .addText(product.quantity.toString()+" X "+product.name, true)

                                if (product.name!!.length > 23) {
                                    receipt!!.setAlign(Paint.Align.LEFT)
                                        .setTextSize(25f)
//                                        .setTypeface(mcontext, "font/roboto_bold.ttf")
                                        .addText(product.name!!.substring(23, product!!.name!!.length), true)

                                }

                            }
                        if((product.fm?.isEmpty()) != true){
//                            for (item in product.fm!!){
//                                receipt!!.setAlign(Paint.Align.LEFT)
//                                    .setTextSize(20f)
////                                    .setTypeface(mcontext, "font/roboto_bold.ttf")
//                                    .addText("   ${item.fm_cat_name}: ${item.fm_item_name}", true)
//                            }
                            val groupedItems = product.fm!!
                                .groupBy { it.fm_cat_name }
                                .mapValues { entry -> entry.value.joinToString(", ") { it.fm_item_name.toString() } }

                            for ((fmCatName, fmItemNames) in groupedItems) {
                                receipt!!.setAlign(Paint.Align.LEFT)
                                    .setTextSize(20f)
                                    .addText("   $fmCatName: $fmItemNames", true)
                            }
                        }
                        if((product.om?.isEmpty()) != true){
                            val groupedItems = product.om!!
                                .groupBy { it.om_cat_name }
                                .mapValues { entry -> entry.value.joinToString(", ") { it.om_item_name.toString() } }

                            for ((omCatName, omItemNames) in groupedItems) {
                                receipt!!.setAlign(Paint.Align.LEFT)
                                    .setTextSize(20f)
                                    .addText("   $omCatName: $omItemNames", true)
                            }
//                            for (item in product.om!!){
//                                receipt!!.setAlign(Paint.Align.LEFT)
//                                    .setTextSize(20f)
////                                    .setTypeface(mcontext, "font/roboto_bold.ttf")
//                                    .addText("   ${item.om_cat_name}: ${item.om_item_name}", true)
//                            }
                        }
                        receipt!!.setAlign(Paint.Align.LEFT)
                            .setTextSize(25f)
//                                .setTypeface(mcontext, "font/roboto_bold.ttf")
                            .addText("", true)
                    }

                    receipt!!.setAlign(Paint.Align.CENTER)
                        .setTextSize(25f)
//                        .setTypeface(mcontext, "font/roboto_bold.ttf")
                        .addText("#"+orderid!!.substring(orderid.length-4,orderid.length), true)

                var mBitmap = receipt!!.build()

                try {
                    val file: File = File(mcontext.getExternalFilesDir(null), "receipt.png")
                    val out = FileOutputStream(file)
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    out.flush()
                    out.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                    Print.PrintBitmap(receipt!!.build(), 0 , 0)
                    Print.CutPaper(Print.PARTIAL_CUT.toInt(), 40)

            }
        }catch (e:NullPointerException){
            e.printStackTrace()
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch4 ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch4 ${e.printStackTrace()}")
        }catch (e:Exception){
            e.printStackTrace()
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch5 ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch5 ${e.printStackTrace()}")
        }catch (e:IOException){
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch6 ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch6 ${e.printStackTrace()}")
            e.printStackTrace()
        }finally {
            Print.PortClose()
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch7",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch7")
        }
    }

     fun companyDetails(orderid: String, context: Context){
        appDatabase= DatabaseClient.getInstance(mcontext!!)!!.getAppDatabase()
        restaurantDao=appDatabase!!.restaurantDao()
         var restaurant=restaurantDao!!.getRestaurant(SessionManager.getRestaurantId(mcontext))
        val date1 = Date()
        var date = DateFormat.format("yyyy-MM-dd", date1).toString()
        var time = DateFormat.format("HH:mm", date1).toString()


        try {

            receipt!!.setAlign(Paint.Align.LEFT)
//                .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                .addText(restaurant!!.restaurantname,true)


            receipt!!.setAlign(Paint.Align.LEFT)
//                .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                .addText(mcontext.resources.getString(R.string.date_txt)+date,false)
                .setAlign(Paint.Align.RIGHT)
                .addText(mcontext.resources.getString(R.string.time_txt)+time+"  ",true)

                receipt!!.setAlign(Paint.Align.LEFT)
//                    .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                    .addText(mcontext.resources.getString(R.string.order_id_txt)+orderid,true)



            receipt!!.setAlign(Paint.Align.CENTER)
//                .setTextSize(25f).setTypeface(mcontext, "robotomono_regular.ttf")
                .addText("---------------------------------------------------------",true)

        }catch (e: IOException){
            e.printStackTrace()
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch8 ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch8 ${e.printStackTrace()}")
        }catch (ex:Exception){
            ex.printStackTrace()
//            Toast.makeText(context,"Enter in openUSBPrintingPort catch1 ${ex.printStackTrace()}",Toast.LENGTH_LONG).show()
//            saveLogToFile(context,"Enter in openUSBPrintingPort catch1 ${ex.printStackTrace()}")
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