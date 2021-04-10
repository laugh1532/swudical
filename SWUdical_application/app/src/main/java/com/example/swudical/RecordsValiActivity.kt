package com.example.swudical

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swudical.DTO.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_records_vali.rv_medicalList
import kotlinx.android.synthetic.main.activity_staff_vali.*
import kotlin.system.exitProcess

class RecordsValiActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_records_vali)
        super.onCreate(savedInstanceState)

        //진료기록 조회
        Common.ReadMedicalConfirm(rv_medicalList, R.layout.activity_records_vali, this)

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

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
    }


    //뒤로가기 연속 클릭 대기 시간
    private var mBackWait:Long = 0

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            val t1 = Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
            t1.show()
        } else {
            finish()
            finishAffinity()
            System.runFinalization()
            exitProcess(0)
        }
    }
}