package com.example.swudical

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
//import kotlinx.android.synthetic.main.activity_home.btn_home
//import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
//import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_staff_vali.*
import org.nd4j.linalg.factory.Nd4j
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.jvm.Throws

class StaffValiActivity : AppCompatActivity() {

    private var Common = Common()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vali)

        //진료기록 조회
        Common.ReadMedicalConfirm(rv_medicalList)

        //region 화자인식
        val storage = Firebase.storage("gs://swudical.appspot.com")
        val storageRef: StorageReference = storage.getReference()
        val pathReference = storageRef.child("정유경(여)40_byte.npy")

        val ONE_MEGABYTE: Long = 1024 * 1024

        val tflite = getTfliteInterpreter("model40.tflite") //모델 불러오기

        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {

            val data = Nd4j.createNpyFromByteArray(it) //firebase에서 .npy 받아오기
            val input = data.toFloatMatrix() //(input) INDArray -> FloatArray
            val output = Array<FloatArray>(data.rows(), {FloatArray(20)})  //output 초기화

            tflite!!.run(input, output)  //화자인식 run

            val answer = IntArray(20, {0}) //레이블 초기화

            //레이블 생성
            for (i in 0..data.rows()-1){
                answer[output[i].indexOf(output[i].max()!!)] += 1
            }

            //레이블 출력
            for(i in 0..19){
                val str = "answer(" + i + ") : " + answer[i] + "\n"
                Log.e("TAG", str)
            }

        }.addOnFailureListener {
            Log.e("TAG", "error")
        }
        //endregion


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

    //region interpreter
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
    //endregion

    override fun onBackPressed() {
        startActivity(Intent(this, RecordsValiActivity::class.java))
        finish()
    }
}
