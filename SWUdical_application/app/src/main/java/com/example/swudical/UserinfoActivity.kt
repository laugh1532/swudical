package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_userinfo.*

class UserInfoActivity : AppCompatActivity() {

    private lateinit var emailId: EditText
    private lateinit var nameId: EditText
    private lateinit var registerNumId1: EditText
    private lateinit var registerNumId2: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        emailId = findViewById(R.id.editTextTextEmailAddress)
        nameId = findViewById(R.id.userinfo_name)
        registerNumId1 = findViewById(R.id.userinfo_birth)
        registerNumId2 = findViewById(R.id.userinfo_sex)

        //next 버튼 클릭
        userinfo_next.setOnClickListener{
            if(ValidateEmail()){ //이메일 유효성검사
                if(ValidateName()){ //이름 유효성검사
                    if(ValidRegisterNum()){//주민등록번호 유효성검사
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    //region 이메일 유효성검사
    private fun ValidateEmail(): Boolean {
        var emailPattern = "[a-zA-Z0-9.-]+@[a-z]+\\.+[a-z]+"

        if (emailId.text.toString().isEmpty()) { //공백검사
            Toast.makeText(applicationContext, "enter email address", Toast.LENGTH_SHORT).show()
            return false
        }
        else { //형식검사
            if (emailId.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {//이메일 유효성검사 성공
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
        if(nameId.text.toString().isEmpty()){ //공백검사
            Toast.makeText(applicationContext, "enter name", Toast.LENGTH_SHORT).show()
            return false
        }
        else
            return true
    }
    //endregion

    //region 주민등록번호 유효성검사
    private fun ValidRegisterNum(): Boolean{
        if(registerNumId1.text.toString().length != 6){ //주민번호 앞자리 공백검사
            Toast.makeText(applicationContext, "enter register number", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(registerNumId2.text.toString().isEmpty()){ //주민번호 뒷자리 공백검사
            Toast.makeText(applicationContext, "enter register number", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            return true
        }
    }
    //endregion
}