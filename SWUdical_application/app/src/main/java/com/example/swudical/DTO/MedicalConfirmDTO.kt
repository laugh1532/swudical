package com.example.swudical.DTO

import java.io.Serializable

data class MedicalConfirmDTO (
//    val date: String?=null,
//    val diagnosis: String?=null,
//    val doc_path: String?=null,
//    val doctor_name: String?=null,
//    val hospital_id: String?=null,
//    val hospital_name: String?=null,
//    val surgery: String?=null,
//    val user_id: String?=null,
//    val voice_path: String?=null,
//    val doctor_id: String?=null

//    val address: String?= null,
//    val age: String?= null,
//    val birth: String?= null,
//    val content: String?= null,
//    val date: String?= null,
//    val diag_number: String?= null,
//    val doctor_id: String?= null,
//    val doctor_name: String?= null,
//    val doctor_path: String?= null,
//    val hospital: String?= null,
//    val patient_name: String?= null,
//    val sex: String?= null,
//    val sick: String?= null,
//    val tel: String?= null,
//    val user_id: String?= null

    val dept: String?= null,
    val writer: String?= null,
    val date: String?= null,
    val diag_num: String?= null,
    val name: String?= null,
    val sex: String?= null,
    val age: String?= null,
    val regident_num: String?= null,
    val tel: String?= null,
    val address: String?= null,
    val diagnosis: String?= null,
    val treatment_period: String?= null,
    val content: String?= null,
    val hospital: String?= null,
    val doctor_license: String?= null,
    val doctor_name: String?= null,
    val user_id: String?= null,
    val voice_path: String?= null,
    val doctor_id: String?= null,
    val surgery_date: String?= null
) : Serializable