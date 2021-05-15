package com.example.swudical

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.swudical.DTO.UserInfoDTO
import com.facebook.appevents.internal.AppEventUtility.bytesToHex
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_records_vali.rv_medicalList
import kotlinx.android.synthetic.main.activity_staff_vali.*
import kotlinx.android.synthetic.main.item_list.*
import java.security.DigestException
import java.security.MessageDigest
import kotlin.system.exitProcess

class RecordsValiActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_vali)

        //region 수술실 이름 가져오기
        val user = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = user.currentUser?.uid.toString()

        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)

                if(userInfoDTO?.name==null || userInfoDTO.sex ==null || userInfoDTO.email ==null || userInfoDTO.birthday ==null){
                    val intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("where", "main")
                    ContextCompat.startActivity(this, intent, null)
                }

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
        //endregion

        //진료기록 조회
        Common.ReadMedicalConfirm(rv_medicalList, R.layout.activity_records_vali, this)

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)

    }


    //뒤로가기 연속 클릭 대기 시간
    private var mBackWait:Long = 0

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >= 2000 ) {
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