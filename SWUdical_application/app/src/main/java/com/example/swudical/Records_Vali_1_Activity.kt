package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.swudical.DTO.MedicalConfirmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_records_vali_1_.*
import kotlinx.android.synthetic.main.activity_staff_vali.*

class Records_Vali_1_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_vali_1_)

        val item:MedicalConfirmDTO = intent.getSerializableExtra("item") as MedicalConfirmDTO


        dept.text = item.dept
        writer.text = item.writer
        diag_num.text = item.diag_num.toString()
        name.text = item.name
        sex.text = item.sex
        age.text = item.age.toString()
        regident_num.text = item.regident_num
        tel.text = item.tel
        address.text = item.address
        treatment_period.text = item.treatment_period
        content.text = item.content
        hospital_name.text = item.hospital
        doctor_license.text = item.doctor_license.toString()
        doctor_name.text = item.doctor_name
        diagnosis.text = item.diagnosis
        write_date.text = item.date
    }

    override fun onBackPressed() {
        var intent = Intent(this, RecordsValiActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}