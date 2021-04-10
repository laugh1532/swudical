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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

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
        firebaseAuth = FirebaseAuth.getInstance()

        //region 로그아웃 버튼
        if(FirebaseAuth.getInstance().currentUser!=null){
            logoutbtn.setOnClickListener{
                signOut()
                val t1 = Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
                t1.show()
                // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        // endregion

        // 기본값
        val basicNotice = "\'EDIT\'을 눌러주세요!"
        home_name.text = basicNotice
        home_id.text = basicNotice
        home_mail.text = basicNotice

        //region 편집 버튼
        editinfobtn.setOnClickListener{
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
        //endregion

        //region 사용자정보 조회
        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)
                home_name.text = userInfoDTO?.name

                val birthdayFromDTO = userInfoDTO?.birthday
                val rangeYear = IntRange(0, 1)
                val rangeMonth = IntRange(2, 3)
                val rangeDay = IntRange(4, 5)
                val birthday = birthdayFromDTO?.slice(rangeYear) + "년 " + birthdayFromDTO?.slice(rangeMonth) + "월 " + birthdayFromDTO?.slice(rangeDay) + "일, "
                var sexkind = userInfoDTO?.sex // 1, 2, 3, 4
                if (sexkind != null) {
                    sexkind = if( sexkind.toInt() % 2 == 0){
                        "여자"
                    } else{
                        "남자"
                    }
                }
                if (birthdayFromDTO != null){
                    home_id.text = birthday + sexkind
                }

                home_mail.text = userInfoDTO?.email
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception) }
        //endregion

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
    }

    private fun signOut() { // 로그아웃
        firebaseAuth.signOut()
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut()

        // facebook
        if(AccessToken.getCurrentAccessToken()!=null){
            LoginManager.getInstance().logOut()
        }
    }
}
