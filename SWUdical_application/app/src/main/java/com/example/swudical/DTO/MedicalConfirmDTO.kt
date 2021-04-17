package com.example.swudical.DTO
import java.io.Serializable

data class MedicalConfirmDTO (
    val dept: String?= null,
    val writer: String?= null,
    val date: String?= null,
    val diag_num: Int?= null,
    val name: String?= null,
    val sex: String?= null,
    val age: Int?= null,
    val regident_num: String?= null,
    val tel: String?= null,
    val address: String?= null,
    val diagnosis: String?= null,
    val treatment_period: String?= null,
    val content: String?= null,
    val hospital: String?= null,
    val doctor_license: Int?= null,
    val doctor_name: String?= null,
    val user_id: String?= null,
    val voice_path: String?= null,
    val doctor_id: Int?= null,
    val surgery_date: String?= null
)  : Serializable, Comparable<MedicalConfirmDTO> {
    override fun compareTo(other: MedicalConfirmDTO): Int {
        return this.surgery_date!!.compareTo(other.surgery_date!!)
    }
}