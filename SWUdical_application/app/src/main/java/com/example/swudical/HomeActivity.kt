package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali

class HomeActivity : AppCompatActivity() {

    //firebase Auth
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        logoutbtn.setOnClickListener{
            signOut()
            val t1 = Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
            t1.show()
            val intent = Intent(this, MainActivity::class.java) // 메인 화면으로 이동
            startActivity(intent)
        }

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
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

//        val accessToken: AccessToken = AccessToken.getCurrentAccessToken()
//        if(user!=null) {
//            val isLoggedIn:Boolean = accessToken != null && !accessToken.isExpired
//            if(isLoggedIn) {
//                FirebaseAuth.getInstance().signOut()
//                LoginManager.getInstance().logOut()
//            }
//        }
    }
}