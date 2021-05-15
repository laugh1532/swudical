package com.example.swudical

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.swudical.DTO.MedicalConfirmDTO
import com.example.swudical.DTO.UserInfoDTO
import com.facebook.appevents.internal.AppEventUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_blockchain_vali.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.security.DigestException
import java.security.MessageDigest
import kotlin.jvm.Throws

class BlockchainValiActivity : AppCompatActivity() {
    var dbHash:String = String()
    var resHash:String = "불일치"
    var resBlockNum:String = "불일치"
    var isMatch : String = "불일치"

    val user = FirebaseAuth.getInstance()
//    val db = FirebaseFirestore.getInstance()
//    val uid = user.currentUser?.uid.toString()

    private var htmlContentInStringFormat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockchain_vali)

        val go_intent = findViewById(R.id.btn_Detail) as Button
        go_intent.setOnClickListener {
            val intent = Intent(this, BlockchainListActivity::class.java)
            startActivity(intent)
        }

        //region 블록체인 주소 조회
//        db.collection("user_info").document(uid)
//            .get().addOnSuccessListener { result ->
//                val userInfoDTO = result.toObject(UserInfoDTO::class.java)
//                if (userInfoDTO != null) {
//                    blockAddr = userInfoDTO.blockAddr!!
//                    Log.d("test", blockAddr)
//                }
//            }
//            .addOnFailureListener() { exception ->
//                Log.w("ERR", "err getting documents: ", exception)
//            }
        //endregion

        //region DB 해시 값 조회
        val item: MedicalConfirmDTO = intent.getSerializableExtra("item") as MedicalConfirmDTO
        val directDTOtoString = item.toString()

        Log.d("result", directDTOtoString)

        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(directDTOtoString.toByteArray())
            val hash = md.digest()

            dbHash = "0x" + AppEventUtility.bytesToHex(hash)

            Log.d("result", dbHash)

        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }
        //endregion

        GlobalScope.launch(Dispatchers.IO) {
            async(Dispatchers.IO){
                try {
                    //val htmlPageUrl = "https://api-ropsten.etherscan.io/api?module=account&action=txlist&address="+blockAddr+"&startblock=0&endblock=99999999&sort=asc&apikey=1Y9HJ1PYCVA31EXJ3S4TNUK9CYBA27N5RT"
                    val htmlPageUrl = "https://api-ropsten.etherscan.io/api?module=account&action=txlist&address=0x84754e49Bb890628eD9faBF4ea188d0ab7CC310c&startblock=0&endblock=99999999&sort=asc&apikey=6J7H7XR6ZW7AXKB4CZABIJBCZNNPR1KFV3"
                    val doc: Document = Jsoup.connect(htmlPageUrl).ignoreContentType(true).get()
                    val links: Elements = doc.select("body")

                    for (link in links) {
                        htmlContentInStringFormat += link.text().trim().toString() + "\n"
                    }

                    htmlContentInStringFormat = htmlContentInStringFormat?.substring(4, htmlContentInStringFormat!!.length)

                    //region 파싱
                    val jsonObject = JSONObject(htmlContentInStringFormat!!)
                    val jsonArray = JSONArray(jsonObject.getString("result"))  //result 값 array로 받아오기

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject2 = jsonArray.getJSONObject(i)
                        val blockNumber = jsonObject2.getString("blockNumber")  //blockNumber 파싱
                        val hash = jsonObject2.getString("input")              //input 파싱(hash)

                        if(dbHash==hash){
                            Log.d("test", dbHash)
                            resHash = hash
                            resBlockNum = blockNumber
                            isMatch =  "일치"
                            break
                        }
                    }
                    //endregion

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }.await()

            async(Dispatchers.Main){
                txt_dbhash.text = dbHash
                txt_blockNum.text = resBlockNum
                txt_resHash.text = resHash
                txt_result.text = isMatch
            }

        }

    }
}
