package com.example.swudical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.swudical.DTO.MedicalConfirmDTO
import com.example.swudical.DTO.UserInfoDTO
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_userinfo.*
import kotlinx.android.synthetic.main.item_list.*
import org.jetbrains.anko.longToast

class HomeActivity : AppCompatActivity() {

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 1000

    // Access a Cloud Firestore instance from your Activity
    // private val db = Firebase.firestore
    val user = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = user.currentUser?.uid.toString()

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

        editinfobtn.setOnClickListener{
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }

        logoutbtn.setOnClickListener{
            signOut()
            val t1 = Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
            t1.show()
            val intent = Intent(this, MainActivity::class.java) // 메인 화면으로 이동
            startActivity(intent)
        }

        // 하단 바
        btn_staffvali.setOnClickListener{
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
                val userInfoDTO =  result.toObject(UserInfoDTO::class.java)
                home_name.text = userInfoDTO?.name
            }
            .addOnFailureListener { exception ->
                Log.w("error", "Error getting documents: ", exception)
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