package com.example.swudical

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.swudical.DTO.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_userinfo.*

class UserInfoActivity : AppCompatActivity() {

    private lateinit var et_name: EditText
    private lateinit var et_email: EditText
    private lateinit var et_sex: EditText
    private lateinit var et_birthday: EditText

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        //region 컴포넌트
        et_email = findViewById(R.id.userinfo_email)
        et_name = findViewById(R.id.userinfo_name)
        et_birthday = findViewById(R.id.userinfo_birth)
        et_sex = findViewById(R.id.userinfo_sex)
        //endregion

        //region next 버튼 클릭 이벤트
        userinfo_next.setOnClickListener{
            if(ValidateEmail()){ //이메일 유효성검사
                if(ValidateName()){ //이름 유효성검사
                    if(ValidRegisterNum()){ //주민등록번호 유효성검사
                        setData() //사용자 정보 저장 및 다음 화면 넘어가기
                    }
                }
            }
        }
        //endregion

    }


    //region 사용자 정보 저장 함수
    private fun setData() {
        val name = et_name.text.toString()
        val email = et_email.text.toString()
        val sex = et_sex.text.toString()
        val birthday = et_birthday.text.toString()

        val userInfo = UserInfoDTO(
            name,
            email,
            sex,
            birthday
        )

        db.collection("user_info").document(user?.uid.toString())
            .set(userInfo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //다음 화면 넘어가기
                    val intent = Intent(this, HomeActivity::class.java)
                    // 다음 화면에 값 넣어주기
                    intent.putExtra("email", email)
                    intent.putExtra("name", name)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "저장하지 못했습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("TAG", task.exception.toString())
                }
            }
    }
    //endregion

    //region 이메일 유효성검사
    private fun ValidateEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9.-]+@[a-z]+\\.+[a-z]+"

        if (et_email.text.toString().isEmpty()) { //공백검사
            Toast.makeText(applicationContext, "enter email address", Toast.LENGTH_SHORT).show()
            return false
        }
        else { //형식검사
            if (et_email.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {//이메일 유효성검사 성공
                return true
            }
            else {
                Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
                return false
            }
        }
    }
    //endregion

    //region 이름 유효성검사
    private fun ValidateName(): Boolean{
        if(et_name.text.toString().isEmpty()){ //공백검사
            Toast.makeText(applicationContext, "enter name", Toast.LENGTH_SHORT).show()
            return false
        }
        else
            return true
    }
    //endregion

    //region 주민등록번호 유효성검사
    private fun ValidRegisterNum(): Boolean{

        if(et_birthday.text.toString().length != 6){ //주민번호 앞자리 공백검사
            Toast.makeText(applicationContext, "enter register number", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(et_sex.text.toString().isEmpty()){ //주민번호 뒷자리 공백검사
            Toast.makeText(applicationContext, "enter register number", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            return true
        }
    }
    //endregion

    override fun onBackPressed() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}

//class DataSharedPreferences(context: Context){
//    private val prefsFilename = "prefs"
//    private val prefsKeyEdt = "EditText"
//    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)
//    var myEditText: String?
//        get() = prefs.getString(prefsKeyEdt, "")
//        set(value) = prefs.edit().putString(prefsKeyEdt, value).apply()
//}
//
//class App : Application(){
//    companion object{
//        lateinit var prefs : DataSharedPreferences
//    }
//
//    override fun onCreate() {
//        prefs = DataSharedPreferences(applicationContext)
//        super.onCreate()
//    }
//}