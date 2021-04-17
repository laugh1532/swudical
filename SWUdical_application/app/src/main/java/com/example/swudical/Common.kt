package com.example.swudical

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swudical.DTO.MedicalConfirmDTO
import com.example.swudical.DTO.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_records_vali.*
import java.util.*
import kotlin.collections.ArrayList


class Common {
    companion object{ // =static

        //region 진료기록 조회함수
        fun ReadMedicalConfirm(rv_medicalList: RecyclerView, layout_id: Int, context: Context){
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance()
            val uid = user.currentUser?.uid.toString()
            val medicalList:ArrayList<MedicalConfirmDTO> = ArrayList<MedicalConfirmDTO>()

            firestore.collection("medical_confirmation")
                .whereNotEqualTo( if(layout_id == R.layout.activity_staff_vali) "voice_path" else "user_id", "")
                .whereEqualTo("user_id", uid)
//                .orderBy("user_id")    //surgery_date 정렬하려면 필요
//                .orderBy("voice_path") //surgery_date 정렬하려면 필요
//                .orderBy("surgery_date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    var medicalConfirmDTO: MedicalConfirmDTO

                    for (document in result) {
                        medicalConfirmDTO = document.toObject(MedicalConfirmDTO::class.java)
                        medicalList.add(medicalConfirmDTO)
                    }

                    medicalList.sortDescending() //surgery_date 내림차순 정렬

                    //리사이클러뷰 어댑터 연결
                    val adapter = RecyclerViewAdapter(medicalList, layout_id)
                    rv_medicalList.adapter = adapter

                    val dividerItemDecoration = DividerItemDecoration(rv_medicalList.getContext(),
                        LinearLayoutManager(context).orientation)
                    rv_medicalList.addItemDecoration(dividerItemDecoration)
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

        //region 서브타이틀 불러오기

        //endregion

    }

}