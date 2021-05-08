package com.example.swudical

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home.*
import com.bumptech.glide.Glide

class HomeActivity : AppCompatActivity() {
    private val GET_GALLERY_IMAGE = 200
    private var imageview: ImageView? = null
    private var currentImageUrl: Uri? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    val user = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = user.currentUser?.uid.toString()

    val storageRef = FirebaseStorage.getInstance().reference.child("images/" + uid + ".jpg")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        //region 프로필사진 불러오기
        storageRef.getDownloadUrl().addOnCompleteListener { task ->
            if (task.isSuccessful()) {
                // Glide 이용하여 이미지뷰에 로딩
                Glide.with(this).load(task.getResult()).override(1024, 980).into(imageView3)
            }
        }
        //endregion

        //region 사용자정보 조회
        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)

                if(userInfoDTO?.name!=null) {
                    home_name.text = userInfoDTO.name
                }
                val birthdayFromDTO = userInfoDTO?.birthday
                val rangeYear = IntRange(0, 1)
                val rangeMonth = IntRange(2, 3)
                val rangeDay = IntRange(4, 5)

                var sexkind = userInfoDTO?.sex // 1, 2, 3, 4
                var birthday = "0"
                if (sexkind != null){
                    birthday = if(sexkind.toInt() <= 2){
                        "19" + birthdayFromDTO?.slice(rangeYear) + "년 " + birthdayFromDTO?.slice(rangeMonth) + "월 " + birthdayFromDTO?.slice(rangeDay) + "일"
                    } else{
                        "20" + birthdayFromDTO?.slice(rangeYear) + "년 " + birthdayFromDTO?.slice(rangeMonth) + "월 " + birthdayFromDTO?.slice(rangeDay) + "일"
                    }
                }
                if (sexkind != null) {
                    sexkind = if( sexkind.toInt() % 2 == 0){
                        "여성"
                    } else {
                        "남성"
                    }
                }


                if (birthdayFromDTO != null){
                    home_id.text = birthday
                    home_sex.text = sexkind
                }

                if(userInfoDTO?.email!=null) {
                    home_mail.text = userInfoDTO.email
                }
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception) }
        //endregion

        //region 사진 클릭
        imageview = findViewById<View>(R.id.imageView3) as ImageView
        imageview!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GET_GALLERY_IMAGE)
        }
        //endregion

        //region 편집 버튼
        editinfobtn.setOnClickListener{
            val intent = Intent(this, UserInfoActivity::class.java)
            intent.putExtra("extra_name", home_name.toString())
            startActivity(intent)
        }
        //endregion

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
        //endregion

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
    }

    //갤러리에서 사진 가져오기
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resolver = contentResolver
        if(resultCode == Activity.RESULT_OK){
            if(requestCode==GET_GALLERY_IMAGE){
                currentImageUrl = data!!.getData()
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(resolver,currentImageUrl)
                    imageView3.setImageBitmap(bitmap)
                    UploadImage()
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            else{
                Log.d("ActivityResult","something wrong")
            }
        }
    }

    //region 사진 업로드
    private fun UploadImage() {
        if (currentImageUrl != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("업로드중...")
            progressDialog.show()

            //storage
            storageRef.putFile(currentImageUrl!!) //성공시
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "사진 업로드 완료", Toast.LENGTH_SHORT).show()

                } //실패시
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
                } //진행중
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount.toDouble()
                    //dialog에 진행률을 퍼센트로 출력해 준다
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "% ...")
                }
        }
    }
    //endregion

    //로그아웃
    private fun signOut() {
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