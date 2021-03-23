package com.example.swudical

import androidx.recyclerview.widget.RecyclerView
import com.example.swudical.DTO.MedicalConfirmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Common {
    private val firestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance()
    private var uid = user.currentUser?.uid.toString()
    private var medicalList:ArrayList<MedicalConfirmDTO> = ArrayList<MedicalConfirmDTO>()

    //region 진료기록 조회
    fun ReadMedicalConfirm(rv_medicalList: RecyclerView){
        firestore.collection("medical_confirmation")
            .whereEqualTo("user_id", uid)
            .get()
            .addOnSuccessListener { result ->
                var medicalConfirmDTO: MedicalConfirmDTO

                for (document in result) {
                    medicalConfirmDTO = document.toObject(MedicalConfirmDTO::class.java)
                    medicalList.add(medicalConfirmDTO)
                }

                val adapter = RecyclerViewAdapter(medicalList)
                rv_medicalList.adapter = adapter
            }
    }
    //endregion

}