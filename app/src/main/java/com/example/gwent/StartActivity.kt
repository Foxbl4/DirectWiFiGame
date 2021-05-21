package com.example.gwent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gwent.recycler_view.ImageAndTextRecycler
import kotlin.system.exitProcess

class StartActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
    }

    fun chooseCard(view: View?){
        val intent = Intent(this, ImageAndTextRecycler::class.java)
        startActivity(intent)
    }

    fun chatTest(view: View?){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun exit(view: View?){
        this.finish()
        exitProcess(0)
    }

    override fun onBackPressed(){
        this.finish()
        exitProcess(0)
    }
}

