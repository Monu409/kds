package com.zotto.kds.application;

import android.app.Application;
import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.acra.ACRA;
import org.acra.annotation.AcraMailSender;

@AcraMailSender(mailTo = "monu@opussolutions.io")
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AndroidThreeTen.init(this);
        ACRA.init(this);
    }
}