package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_records_vali.*

class RecordsValiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_records_vali)
        super.onCreate(savedInstanceState)

        // 하단 바
        btn_staffvali.setOnClickListener{
            val intent = Intent(this, StaffValiActivity::class.java)
            startActivity(intent)
        }
        btn_home.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        btn_rcdvali.setOnClickListener {
            val intent = Intent(this, RecordsValiActivity::class.java)
            startActivity(intent)
        }
    }

    fun RecordValirowNum4(view: View) {
        val doc = RecordVali_row_4_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()
    }
    fun RecordValirowNum3(view: View) {
        val doc = RecordVali_row_3_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()
    }
    fun RecordValirowNum2(view: View) {
        val doc = RecordVali_row_2_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()
    }
    fun RecordValirowNum1(view: View) {
        val doc = RecordVali_row_1_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()}
}