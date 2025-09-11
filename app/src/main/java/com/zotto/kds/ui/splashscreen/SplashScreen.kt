package com.zotto.kds.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zotto.kds.BuildConfig
import com.zotto.kds.R
import com.zotto.kds.databinding.SplashScreenBinding
import com.zotto.kds.ui.SyncDataActivity
import com.zotto.kds.ui.language.LanguageActiviity
import com.zotto.kds.ui.login.LoginActivity
import com.zotto.kds.ui.main.MainActivity
import com.zotto.kds.utils.SessionManager

class SplashScreen:AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= SplashScreenBinding.inflate(layoutInflater)
        Log.e("flavor",BuildConfig.FLAVOR)
        if(BuildConfig.FLAVOR.equals("opus")){
            binding.imageView2.setImageResource(R.drawable.logo_op)
        }
        else{
            binding.imageView2.setImageResource(R.drawable.logo_sy)
        }
        setContentView(binding.root)
        Handler().postDelayed({
            if (SessionManager.isLoggedIn(this)){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, LanguageActiviity::class.java)
                startActivity(intent)
                finish()
            }

        }, 3000)
    }

}