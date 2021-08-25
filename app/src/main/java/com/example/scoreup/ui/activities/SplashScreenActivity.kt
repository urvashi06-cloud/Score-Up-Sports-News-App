package com.example.scoreup.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.example.scoreup.R
import com.example.scoreup.firestore.FirestoreClass

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (FirestoreClass().getCurrentUserId() != ""){
                    startActivity(Intent(this, NewsDisplayActivity::class.java))
                    finish()
                }else {
                    startActivity(Intent(this@SplashScreenActivity, LogInActivity::class.java))
                    finish()
                }

            },
            1500
        )
    }
}