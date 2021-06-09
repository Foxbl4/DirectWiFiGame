package com.example.gwent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    //Когда приложение будет готово к использованию - переход на главное активити
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()
    }
}