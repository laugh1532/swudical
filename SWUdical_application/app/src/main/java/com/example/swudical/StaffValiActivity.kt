package com.example.swudical

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.swudical.DTO.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_staff_vali.*


class StaffValiActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vali)

        //OO님의 수술실
        val user = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = user.currentUser?.uid.toString()

        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)

                if (userInfoDTO != null) {
                    txt_title.text = userInfoDTO.name.toString() + "님의 수술실"
                }
                else{
                    txt_title.text = "환자님의 수술실"
                }
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception)
            }

        //진료기록 조회
        Common.ReadMedicalConfirm(rv_medicalList, R.layout.activity_staff_vali, this)

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)


    }

    override fun onBackPressed() {
        val intent = Intent(this, RecordsValiActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }


}


