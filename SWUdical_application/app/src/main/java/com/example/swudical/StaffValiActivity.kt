package com.example.swudical

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_staff_vali.btn_home
import kotlinx.android.synthetic.main.activity_staff_vali.btn_rcdvali
import kotlinx.android.synthetic.main.activity_staff_vali.btn_staffvali
import kotlinx.android.synthetic.main.activity_staff_vali.rv_medicalList


class StaffValiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vali)

        //진료기록 조회
        Common.ReadMedicalConfirm(rv_medicalList, R.layout.activity_staff_vali, this)

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, RecordsValiActivity::class.java))
        finish()
    }


}


