package com.example.swudical

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.swudical.DTO.UserInfoDTO
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.unimi.dsi.fastutil.ints.Int2ReferenceAVLTreeMap
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_userinfo.*

class HomeActivity : AppCompatActivity() {

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    val RC_SIGN_IN = 1000

    val user = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = user.currentUser?.uid.toString()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        logoutbtn.setOnClickListener{
            signOut()
            val t1 = Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
            t1.show()
            val intent = Intent(this, MainActivity::class.java) // 메인 화면으로 이동
            startActivity(intent)
        }

        editinfobtn.setOnClickListener{
            val intent = Intent(this, UserInfoActivity::class.java) // 메인 화면으로 이동
            startActivity(intent)
        }



        // 하단 바
        btn_staffvali.setOnClickListener{
//            val t1 = Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
//            t1.show()
            val intent = Intent(this, StaffValiActivity::class.java)
            startActivity(intent)
        }
        btn_home.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        btn_rcdvali.setOnClickListener {
            val intent = Intent(this, RecordsValiActivity::class.java)
            startActivity(intent)
        }


        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)
                home_name.text = userInfoDTO?.name
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception)
            }

        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)

                val birthdayFromDTO = userInfoDTO?.birthday
                val rangeYear = IntRange(0, 1)
                val rangeMonth = IntRange(2, 3)
                val rangeDay = IntRange(4, 5)
                val birthday = birthdayFromDTO?.slice(rangeYear) + "년 " + birthdayFromDTO?.slice(rangeMonth) + "월 " + birthdayFromDTO?.slice(rangeDay) + "일, "

                var sexkind = userInfoDTO?.sex // 1, 2, 3, 4
                if (sexkind != null) {
                    sexkind = if( sexkind.toInt() >= 3){
                        "여자"
                    } else{
                        "남자"
                    }
                }
                if (birthdayFromDTO != null){
                    home_id.text = birthday + sexkind
                }
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception)
            }

        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)
                home_mail.text = userInfoDTO?.email
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception)
            }
    }

    private fun signOut() { // 로그아웃
        // Firebase sign out
        firebaseAuth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }

        // facebook log out
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val accessToken: AccessToken = AccessToken.getCurrentAccessToken()
        if(user!=null) {
            val isLoggedIn:Boolean = accessToken != null && !accessToken.isExpired
            if(isLoggedIn) {
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
            }
        }
    }
}