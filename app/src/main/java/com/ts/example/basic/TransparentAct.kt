package com.ts.example.basic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ts.example.R

class TransparentAct: AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, TransparentAct::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_transparent)
    }

}