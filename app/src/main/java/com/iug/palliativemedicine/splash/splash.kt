package com.iug.palliativemedicine.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iug.palliativemedicine.R
import com.iug.palliativemedicine.auth.login
import com.iug.palliativemedicine.databinding.ActivitySignupBinding
import com.iug.palliativemedicine.databinding.ActivitySplashBinding

class splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val check = getSharedPreferences("splash" , MODE_PRIVATE).getBoolean("check" , false)
        if (check){
            startActivity(Intent(this , login::class.java))
            finish()
        }
 binding.btnlogin.setOnClickListener {
     startActivity(Intent(this , login::class.java))
     val sheard = getSharedPreferences("splash" , MODE_PRIVATE).edit().putBoolean("check" , true).apply()
     finish()
 }
    }
}