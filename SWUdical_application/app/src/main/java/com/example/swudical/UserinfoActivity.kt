package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_userinfo.*

class UserInfoActivity : AppCompatActivity() {

    private lateinit var emailId: EditText
    private var emailPattern = "[a-zA-Z0-9.-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        emailId = findViewById(R.id.editTextTextEmailAddress)

        userinfo_next.setOnClickListener{
            //이메일 유효성검사
            if (emailId.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "enter email address", Toast.LENGTH_SHORT).show()
            } else {
                if (emailId.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {//유효성검사 성공
                    Toast.makeText(applicationContext, "valid email address", Toast.LENGTH_SHORT).show()

                    //다음 화면
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}