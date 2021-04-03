package com.example.swudical

import android.content.Intent
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.swudical.DTO.MedicalConfirmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Common {
    companion object{ //=static
        //region 진료기록 조회함수
        fun ReadMedicalConfirm(rv_medicalList: RecyclerView, layout_id: Int){
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance()
            val uid = user.currentUser?.uid.toString()
            val medicalList:ArrayList<MedicalConfirmDTO> = ArrayList<MedicalConfirmDTO>()

            firestore.collection("medical_confirmation")
                .whereEqualTo("user_id", uid)
                .get()
                .addOnSuccessListener { result ->
                    var medicalConfirmDTO: MedicalConfirmDTO

                    for (document in result) {
                        medicalConfirmDTO = document.toObject(MedicalConfirmDTO::class.java)
                        medicalList.add(medicalConfirmDTO)
                    }

                    //리사이클러뷰 어댑터 연결
                    val adapter = RecyclerViewAdapter(medicalList, layout_id)
                    rv_medicalList.adapter = adapter
                }
        }
        //endregion

        //region 하단 바 함수
        fun BottomBar(btn_staffvali: TextView, btn_home: TextView, btn_rcdvali: TextView){
            //진료기록확인 탭
            btn_rcdvali.setOnClickListener {
                val intent = Intent(it.context, RecordsValiActivity::class.java)
                ContextCompat.startActivity(it.context, intent, null)
            }

            //의료진확인 탭
            btn_staffvali.setOnClickListener{
                val intent = Intent(it.context, StaffValiActivity::class.java)
                ContextCompat.startActivity(it.context, intent, null)
            }

            //마이페이지 탭
            btn_home.setOnClickListener{
                val intent = Intent(it.context, HomeActivity::class.java)
                ContextCompat.startActivity(it.context, intent, null)
            }
        }
        //endregion
    }

}