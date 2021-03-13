package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Records_Vali_1_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_vali_1_)
    }
    override fun onBackPressed() {
        startActivity(Intent(this, RecordsValiActivity::class.java))
        finish()
    }
}