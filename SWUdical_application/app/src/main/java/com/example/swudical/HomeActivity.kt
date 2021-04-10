package com.example.swudical

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private val GET_GALLERY_IMAGE = 200
    private var imageview: ImageView? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    val user = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = user.currentUser?.uid.toString()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        // 사용자정보 기본값
        val basicNotice = "\'EDIT\'을 눌러주세요!"
        home_name.text = basicNotice
        home_id.text = basicNotice
        home_mail.text = basicNotice

        //region 사진 업로드
        imageview = findViewById<View>(R.id.imageView3) as ImageView
        imageview!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            startActivityForResult(intent, GET_GALLERY_IMAGE)
        }

//        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
//                val selectedImageUri = data.data
//                imageview!!.setImageURI(selectedImageUri)
//            }
//        }
        //endregion

        //region 사용자정보 조회
        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)

                if(userInfoDTO?.name==null || userInfoDTO.sex ==null || userInfoDTO.email ==null || userInfoDTO.birthday ==null){
                    val intent = Intent(this, UserInfoActivity::class.java)
                    startActivity(intent)
                }

                if(userInfoDTO?.name!=null) {
                    home_name.text = userInfoDTO.name
                }
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
                if(userInfoDTO?.email!=null) {
                    home_mail.text = userInfoDTO.email
                }
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception) }
        //endregion

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)

        //region 로그아웃 버튼
        if(FirebaseAuth.getInstance().currentUser!=null){
            logoutbtn.setOnClickListener{
                signOut()
                val t1 = Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
                t1.show()
                // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) //기존에 쌓인 task 삭제 (기존에 쌓인 스택 삭제)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //task를 새로 생성
                startActivity(intent)
                finish()
            }
        }
        // endregion

        //region 편집 버튼
        editinfobtn.setOnClickListener{
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
        //endregion
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

    override fun onBackPressed() {
        val intent = Intent(this, RecordsValiActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
