package com.example.swudical

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.swudical.DTO.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_blockchain_list.*
import kotlinx.android.synthetic.main.activity_blockchain_vali.*
import kotlinx.android.synthetic.main.activity_staff_vali.*
import kotlinx.android.synthetic.main.blockchain_item_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali

class BlockchainListActivity : AppCompatActivity() {
    private var htmlContentInStringFormat: String? = null
    var _hashList = ArrayList<String>()
    var _blockNumList = ArrayList<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockchain_list)

        //region 수술실 이름 가져오기
        val user = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = user.currentUser?.uid.toString()

        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)

                if(userInfoDTO?.name==null || userInfoDTO.sex ==null || userInfoDTO.email ==null || userInfoDTO.birthday ==null){
                    val intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("where", "main")
                    ContextCompat.startActivity(this, intent, null)
                }

                if (userInfoDTO != null) {
                    txt_title.text = userInfoDTO.name.toString() + "님의 수술실"
                }
                else{
                    txt_title.text = "환자님의 수술실"
                }
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception)
            }
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)


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

                        _hashList.add(hash)
                        _blockNumList.add(blockNumber)
                    }
                    //endregion

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }.await()

            async(Dispatchers.Main){
                //리사이클러뷰 어댑터 연결
                val adapter = RecyclerViewAdapter(_blockNumList, _hashList)
                rv_hashList.adapter = adapter

//                val dividerItemDecoration = DividerItemDecoration(rv_hashList.getContext(), LinearLayoutManager(context).orientation)
//                rv_hashList.addItemDecoration(dividerItemDecoration)
            }

        }
    }

    inner class RecyclerViewAdapter(private val blockNumList: ArrayList<String>, private val hashList: ArrayList<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
        override fun getItemCount() = blockNumList.size

        //리사이클러뷰 생성 이벤트
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("LOG", "onCreateViewHolder")

            val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.blockchain_item_list, parent, false)
            return ViewHolder(inflateView)
        }

        //바인딩 이벤트
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val blockNum = blockNumList[position]
            val hash = hashList[position]
            holder.apply{
                bindViewHolder(blockNum, hash)
            }
        }

        //뷰 홀더 클래스
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var view: View = itemView

            fun bindViewHolder(blockNum: String, hash: String) {
                view.blockNum.text = blockNum
                view.hashVal.text = hash
            }

        }
    }
}