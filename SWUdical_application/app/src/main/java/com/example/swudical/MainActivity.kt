package com.example.swudical

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.swudical.DTO.UserInfoDTO
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list.*
import org.bytedeco.librealsense.context
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    //facebook auth
    private var callbackManager: CallbackManager? = null
    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    //private const val TAG = "GoogleActivity"
    private val RC_SIGN_IN = 99
    private val TAG = "facebooklogin"

    val user = FirebaseAuth.getInstance()
    val uid = user.currentUser?.uid.toString()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val facebookButton = findViewById<Button>(R.id.login_button)
        val newfacebookButton = findViewById<Button>(R.id.new_login_button)

        facebookButton.setOnClickListener { facebookLogin() }
        newfacebookButton.setOnClickListener{
            facebookButton.performClick()
        }

        callbackManager = CallbackManager.Factory.create()

        val newgoogleButton = findViewById<Button>(R.id.new_google_sign_btn)

        // google_sign_btn.setOnClickListener {signIn()}
        newgoogleButton.setOnClickListener{
            signIn()
        }
        //Google ????????? ?????? ??????. requestIdToken ??? Email ??????
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //firebase auth ??????
        firebaseAuth = FirebaseAuth.getInstance()
    }

    // onStart. ????????? ?????? ?????? ?????? ???????????? ????????? ??????
    public override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser !=null) {
            startActivity(Intent(this, RecordsValiActivity::class.java))
            finish()
        }
    } //onStart End

    // onActivityResult
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                Log.w("?????????", "??????", task.exception)
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("MainActivity", "Google sign in failed", e)
            }
        }
    } // onActivityResult End

    // firebaseAuthWithGoogle
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("MainActivity", "firebaseAuthWithGoogle:" + acct.id!!)

        //Google SignInAccount ???????????? ID ????????? ???????????? Firebase Auth??? ???????????? Firebase??? ??????
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.w("MainActivity", "firebaseAuthWithGoogle ??????", task.exception)
                if(firebaseAuth.currentUser !=null) { // ?????? firebaseAuth??? ????????? ??? ?????????
                    startActivity(Intent(this, RecordsValiActivity::class.java))
                    finish()
                }
            } else {
                Log.w("MainActivity", "firebaseAuthWithGoogle ??????", task.exception)
                Snackbar.make(findViewById(R.id.google_sign_btn), "???????????? ?????????????????????.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }// firebaseAuthWithGoogle END

    // facebookLogin start
    private fun facebookLogin(){
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile","email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFBToken(result?.accessToken)
                }
                override fun onCancel() {}
                override fun onError(error: FacebookException?) {}
            })
    }

    // token
    private fun handleFBToken(token : AccessToken?){
        val credential = FacebookAuthProvider.getCredential(token?.token!!)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(firebaseAuth.currentUser !=null) { // ?????? firebaseAuth??? ????????? ??? ?????????
                    startActivity(Intent(this, RecordsValiActivity::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //???????????? ?????? ?????? ?????? ??????
    private var mBackWait:Long = 0

    override fun onBackPressed() {
        // ???????????? ?????? ??????
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            val t1 = Toast.makeText(this, "???????????? ????????? ?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT)
            t1.show()
        } else {
            finish()
            finishAffinity()
            System.runFinalization()
            exitProcess(0)
        }
    }


}
