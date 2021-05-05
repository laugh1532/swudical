package com.example.swudical

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_blockchain_vali.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import kotlin.jvm.Throws

class BlockchainValiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockchain_vali)

        //region 진료기록 블록체인
        val jsoupAsyncTask = TransactionAsyncTask()
        jsoupAsyncTask.execute()
        //endregion
    }

    @SuppressLint("StaticFieldLeak")
    inner class TransactionAsyncTask : AsyncTask<Void?, Void?, Void?>() {
        private var htmlContentInStringFormat: String? = null

        @Throws(JSONException::class)
        fun JSONParser(jsonStr: String?) {
            val stringBuilder = StringBuilder()
            val jsonObject = JSONObject(jsonStr)
            val result = jsonObject.getString("result")     //result 파싱
            val jsonArray = JSONArray(result)                      //result 값 array로 받아오기

            for (i in 0 until jsonArray.length()) {
                val jsonObject2 = jsonArray.getJSONObject(i)
                val blockNumber = jsonObject2.getString("blockNumber")  //blockNumber 파싱
                val input = jsonObject2.getString("input")              //input 파싱
                stringBuilder.append("블록번호: ").append(blockNumber).append("해시 값: ").append(input).append("\n") //형식지정
            }
            htmlContentInStringFormat = stringBuilder.toString()
        }

        override fun onPostExecute(result: Void?) {
            //textviewHtmlDocument_2.setText(htmlContentInStringFormat)
            tmp.text = htmlContentInStringFormat.toString()
            //Log.d("result", htmlContentInStringFormat.toString())
        }

//        override fun onPreExecute() {
//            super.onPreExecute()
//        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val htmlPageUrl = "https://api-ropsten.etherscan.io/api?module=account&action=txlist&address=0x84754e49bb890628ed9fabf4ea188d0ab7cc310c&startblock=0&endblock=99999999&sort=asc&apikey=6J7H7XR6ZW7AXKB4CZABIJBCZNNPR1KFV3"
                val doc: Document = Jsoup.connect(htmlPageUrl).ignoreContentType(true).get()
                val links: Elements = doc.select("body")

                for (link in links) {
                    htmlContentInStringFormat += link.text().trim().toString() + "\n"
                }

                htmlContentInStringFormat = htmlContentInStringFormat?.substring(4, htmlContentInStringFormat!!.length)
                JSONParser(htmlContentInStringFormat)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}

