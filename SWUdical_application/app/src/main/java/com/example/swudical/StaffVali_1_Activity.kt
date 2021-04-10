package com.example.swudical

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_staff_vali_1.*
import org.nd4j.linalg.factory.Nd4j
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.jvm.Throws

class StaffVali_1_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vali_1)

        val voice_path = intent.getStringExtra("voice_path")
        val doctor_id = intent.getStringExtra("doctor_id")

        //region 화자인식
        val storage = Firebase.storage("gs://swudical.appspot.com")
        val storageRef: StorageReference = storage.reference
        val pathReference = storageRef.child(voice_path!!)

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

            var max = 0
            var index = -1

            //레이블 출력
            for(i in 0..19){
//                val str = "answer(" + i + ") : " + answer[i] + "\n"
//                Log.e("TAG", str)

                if(answer[i] > max){
                    max = answer[i]
                    index = i
                }
            }

            if(doctor_id == index.toString()){
                test.text = "일치"
            }


        }.addOnFailureListener {
            Log.e("TAG", "error")
        }
        //endregion

        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
    }

    //region 텐서플로우 인터프리터
    private fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return Interpreter(loadModelFile(this, modelPath)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

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
        startActivity(Intent(this, StaffValiActivity::class.java))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
    }
}