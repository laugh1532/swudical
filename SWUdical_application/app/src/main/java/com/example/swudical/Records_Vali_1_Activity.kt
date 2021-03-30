package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_staff_vali.*

class Records_Vali_1_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_vali_1_)

        //하단 바
        //Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, RecordsValiActivity::class.java))
        finish()
    }
}