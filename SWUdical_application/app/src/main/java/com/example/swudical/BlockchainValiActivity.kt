package com.example.swudical

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
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
    var resHash:String = String()
    var resBlockNum:String = "String()"
    var blockAddr:String = String()
    var isMatch : String = String()

    val user = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = user.currentUser?.uid.toString()

    var _hashList = ArrayList<String>()
    var _blockList = ArrayList<String>()

    private var htmlContentInStringFormat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockchain_vali)

        //region 블록체인 주소 조회
        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)
                val directDTOtoString = userInfoDTO.toString()
                if (userInfoDTO != null) {
                    blockAddr = userInfoDTO.blockAddr!!
                }
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception)
            }
        //endregion

        //region DB 해시 값 조회
        val item: MedicalConfirmDTO = intent.getSerializableExtra("item") as MedicalConfirmDTO
        val directDTOtoString = item.toString()

        Log.d("result", directDTOtoString)

        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(directDTOtoString.toByteArray())
            val hash = md.digest()

            dbHash = AppEventUtility.bytesToHex(hash)

            Log.d("result", dbHash)

        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }
        //endregion

        GlobalScope.launch(Dispatchers.IO) {
            async(Dispatchers.IO){
                try {
                    val htmlPageUrl = "https://api-ropsten.etherscan.io/api?module=account&action=txlist&address="+blockAddr+"&startblock=0&endblock=99999999&sort=asc&apikey=6J7H7XR6ZW7AXKB4CZABIJBCZNNPR1KFV3"
                    val doc: Document = Jsoup.connect(htmlPageUrl).ignoreContentType(true).get()
                    val links: Elements = doc.select("body")

                    for (link in links) {
                        htmlContentInStringFormat += link.text().trim().toString() + "\n"
                    }

                    htmlContentInStringFormat = htmlContentInStringFormat?.substring(4, htmlContentInStringFormat!!.length)

//                    var x = JSONParser(htmlContentInStringFormat)
                    resHash = "불일치"
                    resBlockNum = "불일치"
                    isMatch =  "불일치"

                    val jsonObject = JSONObject(htmlContentInStringFormat)
                    //val jResult = jsonObject.getString("result")     //result 파싱
                    val jsonArray = JSONArray(jsonObject.getString("result"))  //result 값 array로 받아오기

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject2 = jsonArray.getJSONObject(i)
                        val blockNumber = jsonObject2.getString("blockNumber")  //blockNumber 파싱
                        val hash = jsonObject2.getString("input")              //input 파싱(hash)
                        _hashList.add(hash)
                        _blockList.add(blockNumber)

                        if(dbHash.equals(hash)){
                            resHash = hash
                            resBlockNum = blockNumber
                            isMatch =  "일치"
                            break
                        }
                    }

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

