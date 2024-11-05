package com.zotto.kds.printing;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;

import print.Print;

public class PrintingOther {
    boolean usbRegistered = false;
    private static final String ACTION_USB_PERMISSION = "com.zotto.kds";
    private  Context context;

    public PrintingOther(Context context){
        this.context = context;
    }

    //setup receipt usb printer
    public void connectReceiptUsb() {
        try {
            boolean HavePrinter = false;

            UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            assert mUsbManager != null;
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

            if (!deviceList.isEmpty()) {
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                while (deviceIterator.hasNext()) {
                    UsbDevice usbDevice1 = deviceIterator.next();
                    int count = usbDevice1.getInterfaceCount();

                    for (int i = 0; i < count; i++) {
                        UsbInterface intf = usbDevice1.getInterface(i);
                        if (intf.getInterfaceClass() == 7) {
                            HavePrinter = true;

                            PendingIntent mPermissionIntent = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
                                mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
                            else
                                mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                            context.registerReceiver(mUsbReceiver, filter);
                            usbRegistered = true;
                            mUsbManager.requestPermission(usbDevice1, mPermissionIntent);
                        }
                    }
//          }
                }
            }

            if (!HavePrinter) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Please connect receipt printer.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                //printer found
            }
        } catch (Exception e) {
            e.printStackTrace();
//      checkKitchenPrinting();
        }
    }

    //BroadcastReceiver for port open
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (Print.PortOpen(context, device) != 0) {
                                Toast.makeText(context,"No Printer Found",Toast.LENGTH_SHORT).show();
                                return;
                            } else {
//                                setupReceiptBitmap();
                                Toast.makeText(context,"Check Bitmap",Toast.LENGTH_SHORT).show();
                            }
                        } else
                            return;
                    }
                }

                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        int count = device.getInterfaceCount();
                        for (int i = 0; i < count; i++) {
                            UsbInterface intf = device.getInterface(i);
                            //Class ID 7
                            if (intf.getInterfaceClass() == 7) {
                                Print.PortClose();
                                Toast.makeText(context,"Please connect to receipt printer!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
