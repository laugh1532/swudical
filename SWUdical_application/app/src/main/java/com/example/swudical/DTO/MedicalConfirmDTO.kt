package com.example.swudical.DTO

data class MedicalConfirmDTO (
    val date: String?=null,
    val diagnosis: String?=null,
    val doc_path: String?=null,
    val doctor_name: String?=null,
    val hospital_id: String?=null,
    val hospital_name: String?=null,
    val surgery: String?=null,
    val user_id: String?=null,
    val voice_path: String?=null,
    val doctor_id: String?=null
)