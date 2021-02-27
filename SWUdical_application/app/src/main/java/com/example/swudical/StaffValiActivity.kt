package com.example.swudical

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.InternalChannelz.id
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_records_vali.*
import kotlinx.android.synthetic.main.activity_staff_vali.*
import kotlinx.android.synthetic.main.activity_staff_vali.view.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class StaffValiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.swudical.R.layout.activity_staff_vali)

        val tflite = getTfliteInterpreter("model.tflite")
//        tflite!!.run(input, output) //?

        val db = FirebaseFirestore.getInstance()

//        **  하단바  **
//        의료진 확인
        btn_staffvali.setOnClickListener{
            val intent = Intent(this, StaffValiActivity::class.java)
            startActivity(intent)
        }
//        홈 화면
        btn_home.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
//        진료 기록 확인
        btn_rcdvali.setOnClickListener {
            val intent = Intent(this, RecordsValiActivity::class.java)
            startActivity(intent)
        }
    }
//        **  [끝] 하단바 [끝]  **

    //    모델 파일 인터프리터를 생성하는 공통 함수
    //    loadModelFile 함수에 예외가 포함되어 있기 때문에 반드시 try, catch 블록이 필요하다.
    private fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return Interpreter(loadModelFile(this, modelPath)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //    모델을 읽어오는 함수로, 텐서플로 라이트 홈페이지에 있다.
    //    MappedByteBuffer 바이트 버퍼를 Interpreter 객체에 전달하면 모델 해석을 할 수 있다.
    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, modelPath: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

//    **  누르면 의사 이름 반환  **
    fun StaffValirowNum1(view: View) {
        val doc = StaffVali_row_1_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()
    }
    fun StaffValirowNum2(view: View) {
        val doc = StaffVali_row_2_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()
    }
    fun StaffValirowNum3(view: View) {
        val doc = StaffVali_row_3_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()
    }
    fun StaffValirowNum4(view: View) {
        val doc = StaffVali_row_4_3.text.toString()
        Toast.makeText(this, doc, Toast.LENGTH_SHORT).show()
    }
//        **  [끝] 누르면 의사 이름 반환 [끝]  **
}
